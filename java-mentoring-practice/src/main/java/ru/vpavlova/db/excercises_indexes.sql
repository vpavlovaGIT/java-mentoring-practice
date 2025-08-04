-- 1. Базовый индекс для фильтрации
-- 1.1 Без индекса СУБД будет выполнять полное сканирование таблицы — проверяя каждую строку.
-- Это медленно, особенно если в таблице много записей. Индексы ускоряют поиск, но требуют памяти и замедляют запись.
-- 1.2 Создаем таблицу users
CREATE TABLE users (
    id int primary key, 
    name text, 
    email text, 
    created_at timestamp
);
-- 1.3 Наполняем тестовыми данными (500 записей)
INSERT INTO users 
SELECT 
    generate_series(1, 500) as id,
    'User ' || generate_series(1, 500) as name,
    'user' || generate_series(1, 500) || '@example.com' as email,
    NOW() - (random() * interval '365 days') as created_at;
-- 1.4 Добавляем конкретную запись для поиска
INSERT INTO users VALUES (501, 'Test User', 'user@example.com', NOW());
-- 1.5 Анализируем запрос БЕЗ индекса
EXPLAIN ANALYZE 
SELECT * FROM users WHERE email = 'user@example.com';
-- 1.6 Создаем индекс для ускорения поиска по email
CREATE INDEX idx_users_email ON users(email);
-- 1.7 Анализируем запрос С индексом
EXPLAIN ANALYZE 
SELECT * FROM users WHERE email = 'user@example.com';

-- 2. Составной индекс для сложного условия
-- 2.1 Оптимальный составной индекс для запроса
-- SELECT * FROM orders
-- WHERE status = 'shipped' AND created_at > now()
-- ORDER BY created_at DESC;
CREATE INDEX idx_status_createdat ON orders (status, created_at DESC);
-- 2.3 Проверка работы с разными порядками условий
CREATE INDEX idx_createdat_status ON orders(created_at, status); -- Такой индекс хуже работает для запроса
-- WHERE status = 'shipped' AND created_at > now()
-- потому что created_at — диапазон, а status не может быть эффективно использован.

-- 3. Индекс для сортировки
-- 3.1 Индекс, который оптимизирует и фильтрацию, и сортировку
CREATE INDEX idx_category_price_desc ON products(category_id, price DESC);
-- 3.2 Разница между двумя индексами
--✔ Индекс `(category_id, price DESC)` оптимизирует фильтрацию по категории и сортировку по цене,
--❌ Индекс `(price DESC, category_id)` неэффективен для фильтрации по категории category_id = 5, так как сортирует по цене глобально.

-- 4. Частичный индекс
-- Заполнение 10 млн записей, где 1% — level = 'ERROR'
INSERT INTO logs (level, message, created_at)
SELECT
    CASE
        WHEN RANDOM() < 0.01 THEN 'ERROR'     -- 1% ошибок
        ELSE 'INFO'                           -- 99% обычные логи
        END,
    'Log message ' || gs,
    NOW() - (INTERVAL '30 days' * RANDOM())
FROM generate_series(1, 10000000) AS gs;
-- 4.1 Создание частичного индекса для оптимизации
CREATE INDEX idx_logs_error ON logs(created_at DESC)
    WHERE level = 'ERROR'
-- 4.2 Создание полного индекса для оптимизации
CREATE INDEX idx_logs_full ON logs(level, created_at DESC);
-- 4.3 Сравнение по размеру
SELECT pg_size_pretty(pg_relation_size('idx_logs_error')) AS partial_index_size; -- 2192 kB
SELECT pg_size_pretty(pg_relation_size('idx_logs_full')) AS full_index_size; -- 301 MB

-- 5. Индекс для поиска по шаблону
-- 5.1  Индекс, который ускорит поиск по префиксу
CREATE INDEX idx_articles_title_prefix ON articles(title text_pattern_ops);
-- 5.2 Объяснение, почему обычный B-tree индекс не всегда помогает с LIKE
-- Обычный B-tree индекс не всегда помогает с `LIKE`, потому что PostgreSQL учитывает сортировку по локали (collation)
-- без `text_pattern_ops` не может эффективно использовать индекс для префиксного поиска.

-- 6. Оптимизация JOIN с индексами
-- Индексы для оптимизации с учетом условий соединения и фильтрации
CREATE INDEX idx_orders_user_created ON orders(user_id, created_at);
-- user_id — участвует в JOIN (внешний ключ);
-- created_at — участвует в фильтрации по диапазону;
-- такой составной индекс позволяет сразу находить все заказы нужных пользователей в нужном диапазоне.

-- 7. Индекс для полнотекстового поиска
-- GIN индекс для полнотекстового поиска
CREATE INDEX idx_documents_content_tsv
    ON documents
    USING GIN (to_tsvector('english', content));
-- Проверка эффективности
QUERY PLAN                                                                                                                         |
-----------------------------------------------------------------------------------------------------------------------------------+
Bitmap Heap Scan on documents  (cost=12.97..17.23 rows=1 width=19) (actual time=0.075..0.093 rows=100 loops=1)                     |
  Recheck Cond: (to_tsvector('english'::regconfig, content) @@ '''databas'' & ''optim'''::tsquery)                                 |
  Heap Blocks: exact=2                                                                                                             |
  ->  Bitmap Index Scan on idx_documents_content_tsv  (cost=0.00..12.97 rows=1 width=0) (actual time=0.050..0.050 rows=100 loops=1)|
        Index Cond: (to_tsvector('english'::regconfig, content) @@ '''databas'' & ''optim'''::tsquery)                             |
Planning Time: 2.464 ms                                                                                                            |
Execution Time: 0.146 ms                                                                                                           |









