# My Market App

Веб-приложение интернет-магазина, разработанное на Spring Boot с использованием реактивной архитектуры WebFlux.

## Описание

My Market App — это полнофункциональное приложение электронной коммерции, которое позволяет пользователям:
- Регистрироваться и входить в систему
- Просматривать каталог товаров с поиском и сортировкой
- Добавлять товары в корзину
- Управлять корзиной (добавление, удаление, изменение количества)
- Оформлять заказы с обработкой платежей
- Просматривать историю заказов

Анонимные пользователи могут просматривать каталог. Корзина, заказы и оплата доступны только авторизованным пользователям.

### Аутентификация и авторизация

- **Пользователь → market-app**: Form login (username/password хранятся в PostgreSQL)
- **market-app → payment-service**: OAuth2 Client Credentials Flow через Keycloak (machine-to-machine)

## Структура проекта

```
my-market-app/
├── market-app/          # Основное веб-приложение магазина
├── payment-service/     # Микросервис обработки платежей
├── payment-api/         # OpenAPI спецификация и сгенерированные клиенты
├── docker/              # Docker конфигурации
│   ├── docker-compose.yml
│   └── keycloak/        # Keycloak realm конфигурация
└── proxy/               # Nginx конфигурация
```

## Технологии

### Backend
- **Spring Boot 4.0.1** - основной фреймворк
- **Spring WebFlux** - реактивный веб-слой
- **Spring Data R2DBC** - реактивная работа с базой данных
- **Spring Security** - аутентификация (form login) и авторизация
- **Spring OAuth2 Client** - Client Credentials Flow для межсервисного взаимодействия
- **Spring OAuth2 Resource Server** - JWT валидация в payment-service
- **Project Reactor** - реактивные потоки (Mono/Flux)
- **Thymeleaf** - шаблонизатор для представлений

### Инфраструктура
- **PostgreSQL** - основная база данных
- **Redis** - кэширование корзины (с настраиваемым TTL)
- **Keycloak** - OAuth2 сервер для service-to-service аутентификации
- **Nginx** - reverse proxy
- **Docker Compose** - оркестрация всех сервисов

### API
- **OpenAPI Generator** - генерация клиентов и серверных интерфейсов
- **WebClient** - реактивный HTTP клиент для межсервисного взаимодействия

### Тестирование
- **JUnit 5** - тестовый фреймворк
- **Mockito** - мокирование зависимостей
- **Reactor Test** - тестирование реактивных потоков (StepVerifier)
- **Testcontainers** - запуск PostgreSQL в Docker для интеграционных тестов
- **WebTestClient** - тестирование WebFlux контроллеров

### Сборка
- **Maven** - система сборки (multi-module)

### Основные компоненты

#### Market App

**Контроллеры:**
- `AuthController` - регистрация и вход пользователей
- `ItemController` - управление каталогом товаров
- `CartController` - управление корзиной
- `OrderController` - управление заказами

**Сервисы:**
- `UserService` - регистрация, аутентификация, программный логин после регистрации
- `ItemService` - бизнес-логика работы с товарами
- `CartService` - бизнес-логика корзины с Redis кэшированием
- `OrderService` - бизнес-логика заказов
- `OrderProcessingService` - оформление заказа с оплатой через payment-service

**Репозитории:**
- `UserRepository` - реактивный доступ к данным пользователей
- `ItemRepository` - реактивный доступ к данным товаров
- `CartRepository` - реактивный доступ к данным корзин
- `CartItemRepository` - реактивный доступ к позициям корзины
- `OrderRepository` - реактивный доступ к данным заказов
- `OrderItemRepository` - реактивный доступ к позициям заказов

#### Payment Service (внутренний микросервис)

Защищён JWT авторизацией — принимает запросы только с валидным OAuth2 токеном и ролью `payment.balance.manage`.

**Контроллеры:**
- `PaymentController` - внутренний API для обработки платежей

**Сервисы:**
- `PaymentService` - бизнес-логика обработки платежей и управления балансом

**Репозитории:**
- `PaymentRepository` - реактивный доступ к данным баланса

## Установка и запуск

### Требования

- Docker и Docker Compose
- Java 21 (для локальной разработки)
- Maven 3.9+ (включен wrapper)

### Запуск с Docker Compose

Запуск всего стека (приложения + база данных + Redis + Keycloak + Nginx):

```bash
docker compose -f docker/docker-compose.yml up --build
```

## Тестирование

### Требования для тестов

- **Docker** - обязательно для запуска интеграционных тестов (Testcontainers)

### Запуск всех тестов

```bash
./mvnw test
```

### Запуск тестов для конкретного модуля

```bash
./mvnw test -pl market-app
./mvnw test -pl payment-service
```

### Запуск конкретного теста

```bash
./mvnw test -pl market-app -Dtest=CartServiceTest
./mvnw test -pl payment-service -Dtest=PaymentServiceTest
```