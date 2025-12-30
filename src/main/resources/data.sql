------------------------------------------------------------
-- SEED DATA: ITEMS
------------------------------------------------------------

-- Insert sample items


INSERT INTO items (title, description, price, count, created_at, updated_at)
SELECT * FROM (VALUES ('Смартфон Samsung Galaxy S23', 'Флагманский смартфон с отличной камерой и производительностью',
                       75990.00, 15, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                      ('Ноутбук Lenovo ThinkPad', 'Надежный бизнес-ноутбук с процессором Intel Core i7', 125000.00, 8,
                       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                      ('Наушники Sony WH-1000XM5', 'Беспроводные наушники с активным шумоподавлением', 32990.50, 25,
                       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                      ('Умные часы Apple Watch Series 9', 'Современные умные часы с множеством функций здоровья',
                       44990.99, 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                      ('Планшет iPad Air', 'Мощный планшет для работы и развлечений', 67990.00, 10, CURRENT_TIMESTAMP,
                       CURRENT_TIMESTAMP),
                      ('Игровая консоль PlayStation 5', 'Консоль нового поколения для захватывающих игр', 54990.00, 5,
                       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                      ('Электронная книга Kindle', 'Компактная читалка с подсветкой экрана', 12990.00, 30,
                       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                      ('Фитнес-браслет Xiaomi Band 8', 'Доступный фитнес-трекер с отличной автономностью', 3990.00, 50,
                       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                      ('Клавиатура Logitech MX Keys', 'Беспроводная клавиатура для профессионалов', 11990.50, 20,
                       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                      ('Мышь Logitech MX Master 3S', 'Эргономичная беспроводная мышь с точным сенсором', 9990.00, 18,
                       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                      ('Монитор Dell UltraSharp 27"', '4K монитор для профессиональной работы', 45990.00, 7,
                       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                      ('Веб-камера Logitech C920', 'Full HD веб-камера для видеоконференций', 7990.00, 22,
                       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                      ('Внешний SSD Samsung T7', 'Портативный SSD накопитель 1TB', 9990.00, 35, CURRENT_TIMESTAMP,
                       CURRENT_TIMESTAMP),
                      ('Роутер TP-Link AX3000', 'Мощный Wi-Fi 6 роутер для дома', 8990.00, 16, CURRENT_TIMESTAMP,
                       CURRENT_TIMESTAMP),
                      ('Powerbank Xiaomi 20000mAh', 'Быстрая зарядка для ваших устройств', 2990.00, 45,
                       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)) AS v(title, description, price, count, created_at, updated_at)
WHERE NOT EXISTS (SELECT 1 FROM items);
