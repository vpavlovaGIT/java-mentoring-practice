-- Потерянное обновление
-- Пессимистичная блокировка на запись SELECT FOR UPDATE
-- T1
BEGIN;
SELECT balance FROM accounts WHERE id = 1 FOR UPDATE; -- Блокировка строки
UPDATE accounts SET balance = 800 WHERE id = 1; -- 1000 - 200
COMMIT;
-- T2 (начнёт выполняться только после COMMIT T1)
BEGIN;
SELECT balance FROM accounts WHERE id = 1 FOR UPDATE; -- Ждёт разблокировки
UPDATE accounts SET balance = 500 WHERE id = 1; -- 800 - 300
COMMIT;
-- Повышение уровня изоляции
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
--
BEGIN;
SELECT balance FROM accounts WHERE id = 1; -- Читает 1000
UPDATE accounts SET balance = 800 WHERE id = 1; -- 1000 - 200
COMMIT;
-- Обновление с помощью атомарной операции
-- T1
BEGIN;
UPDATE accounts SET balance = balance - 200 WHERE id = 1;
COMMIT;
-- T2
BEGIN;
UPDATE accounts SET balance = balance - 300 WHERE id = 1;
COMMIT;

--Неповторяющееся чтение
-- Блокировка на чтение SELECT FOR SHARE
-- T1 (чтение с блокировкой)
BEGIN;
SELECT balance FROM accounts WHERE id = 1 FOR SHARE;
-- T2 (пытается изменить баланс, но ждёт разблокировки)
BEGIN;
UPDATE accounts SET balance = 800 WHERE id = 1;
COMMIT;
SELECT balance FROM accounts WHERE id = 1;
COMMIT;
-- Повышение уровня изоляции
-- Устанавливаем уровень изоляции REPEATABLE READ
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
-- T1
BEGIN;
SELECT balance FROM accounts WHERE id = 1; -- Читает 1000
-- T2 (изменяет данные)
BEGIN;
UPDATE accounts SET balance = 800 WHERE id = 1;
COMMIT;
-- T1 (второе чтение всё ещё видит 1000, несмотря на изменения T2)
SELECT balance FROM accounts WHERE id = 1;
COMMIT;

-- Фантомное чтение
-- Блокировка диапазона на чтение SELECT FOR UPDATE
-- T1 (чтение с блокировкой)
BEGIN;
SELECT balance FROM accounts WHERE balance > 1000 FOR UPDATE;
-- T2 (пытается добавить запись, но ждёт)
BEGIN;
INSERT INTO accounts VALUES (4, 'Star', 5000);
COMMIT;
-- T1 продолжает работу
SELECT balance FROM accounts WHERE balance > 1000;
COMMIT;
-- Повышение уровня изоляции
-- Устанавливаем уровень изоляции SERIALIZABLE
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
-- T1
BEGIN;
SELECT balance FROM accounts WHERE balance > 1000; -- Читает 2000, 3000
-- T2 (в другом соединении)
BEGIN;
INSERT INTO accounts VALUES (4, 'Star', 5000);
COMMIT;
-- T1
SELECT balance FROM accounts WHERE balance > 1000; -- Всё ещё читает 2000, 3000
COMMIT;
-- После COMMIT T1 новые запросы увидят 2000, 3000, 5000

