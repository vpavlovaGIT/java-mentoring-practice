## Оптимизировать запросы путём создания индекса

### 1. Базовый индекс для фильтрации

- таблица - `users (id int primary key, name text, email text, created_at timestamp)`
- запрос - `SELECT * FROM users WHERE email = 'user@example.com';`
- Задача:
    - Объяснить, почему этот запрос может работать медленно без индекса 
    - Создать подходящий индекс 
    - Проверить его эффективность с помощью EXPLAIN ANALYZE

### 2. Составной индекс для сложного условия

- таблица - `orders (id int primary key, user_id int, status text, amount int, created_at timestamp)`
- запрос - `SELECT * FROM orders WHERE status = 'shipped' AND created_at > now() ORDER BY created_at DESC;`
- Задача:
  - Определить оптимальный составной индекс 
  - Учитывать порядок столбцов в индексе 
  - Проверить работу с разными порядками условий

### 3. Индекс для сортировки

- таблица - `products (id int primary key, name text, price int, category_id int)`
- запрос - `SELECT * FROM products WHERE category_id = 5 ORDER BY price DESC LIMIT 100;`
- Задача:
  - Создать индекс, который оптимизирует и фильтрацию, и сортировку 
  - Объяснить разницу между индексами (category_id, price) и (price, category_id)

### 4. Частичный индекс

- таблица - `logs (id int primary key, level text, message text, created_at timestamp)`
- запрос - `SELECT * FROM logs WHERE level = 'ERROR' ORDER BY created_at DESC LIMIT 100;`
- Особенность - В таблице 10 млн записей, но только 1% имеют level = 'ERROR' 
- Задача:
  - Создать частичный индекс для оптимизации 
  - Сравнить размер индекса с полным индексом

### 5. Индекс для поиска по шаблону

- таблица - `articles (id int primary key, title text, content text, author_id int)`
- запрос - `SELECT * FROM articles WHERE title LIKE 'PostgreSQL%';`
- Задача:
  - Создать индекс, который ускорит поиск по префиксу 
  - Объяснить, почему обычный B-tree индекс не всегда помогает с LIKE

### 6. Оптимизация JOIN с индексами

- таблица_1 - `users (id int primary key, name text, email text, created_at timestamp)`
- таблица_2 - `orders (id int primary key, user_id int, status text, amount int, created_at timestamp)`
- запрос - `SELECT u.name, COUNT(o.id), SUM(o.amount) FROM users u JOIN orders o ON u.id = o.user_id WHERE o.created_at BETWEEN '2025-07-30' AND '2025-09-01' GROUP BY u.id;`
- Задача:
  - Определить, какие индексы нужны для оптимизации 
  - Учитывать как условия соединения, так и фильтрации

### 7. Индекс для полнотекстового поиска

- таблица - `documents (id int primary key, title text, content text, author text)`
- запрос - `SELECT id, title FROM documents WHERE to_tsvector('english', content) @@ to_tsquery('english', 'database & optimization');`
- Задача:
  - Создать GIN индекс для полнотекстового поиска 
  - Проверьте его эффективность