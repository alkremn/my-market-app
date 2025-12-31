------------------------------------------------------------
-- SEED DATA: ITEMS
------------------------------------------------------------

-- Insert sample items


INSERT INTO items (title, description, img_path, price, count, created_at, updated_at)
SELECT * FROM (VALUES ('Футбольный мяч Adidas', 'Профессиональный футбольный мяч для игры на траве',
                       'images/football.png', 2990.00, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                      ('Баскетбольный мяч Nike', 'Качественный баскетбольный мяч для зала и улицы',
                       'images/basketball.png', 3490.00, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                      ('Волейбольный мяч Mikasa', 'Официальный мяч для волейбола',
                       'images/volleyball.png', 2790.00, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                      ('Бейсбольная кепка Nike', 'Классическая спортивная кепка из хлопка',
                       'images/baseball-cap.png', 1590.00, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                      ('Шапка зимняя Adidas', 'Теплая вязаная шапка для холодной погоды',
                       'images/beanie.png', 1990.00, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                      ('Кроссовки Nike Air', 'Беговые кроссовки с амортизацией',
                       'images/sneakers.png', 8990.00, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                      ('Кеды Converse', 'Классические текстильные кеды',
                       'images/converse.png', 4990.00, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                      ('Ботинки Timberland', 'Прочные кожаные ботинки для походов',
                       'images/boots.png', 12990.00, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                      ('Футбольные бутсы Puma', 'Профессиональные бутсы с шипами для футбола',
                       'images/football-boots.png', 6990.00, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                      ('Туфли классические', 'Мужские кожаные туфли для офиса',
                       'images/dress-shoes.png', 7990.00, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)) AS v(title, description, img_path, price, count, created_at, updated_at)
WHERE NOT EXISTS (SELECT 1 FROM items);
