# My Market App

Веб-приложение интернет-магазина, разработанное на Spring Boot с использованием реактивной архитектуры WebFlux.

## Описание

My Market App — это полнофункциональное приложение электронной коммерции, которое позволяет пользователям:
- Просматривать каталог товаров с поиском и сортировкой
- Добавлять товары в корзину
- Управлять корзиной (добавление, удаление, изменение количества)
- Оформлять заказы
- Просматривать историю заказов

## Технологии

### Backend
- **Spring Boot 4.0.1** - основной фреймворк
- **Spring WebFlux** - реактивный веб-слой
- **Spring Data R2DBC** - реактивная работа с базой данных
- **Project Reactor** - реактивные потоки (Mono/Flux)
- **Thymeleaf** - шаблонизатор для представлений

### База данных
- **PostgreSQL** - production база данных
- **R2DBC PostgreSQL** - реактивный драйвер

### Тестирование
- **JUnit 5** - тестовый фреймворк
- **Mockito** - мокирование зависимостей
- **Reactor Test** - тестирование реактивных потоков (StepVerifier)
- **Testcontainers** - запуск PostgreSQL в Docker для интеграционных тестов
- **WebTestClient** - тестирование WebFlux контроллеров

### Сборка
- **Maven** - система сборки

## Архитектура

### Реактивный стек

Приложение использует полностью реактивный стек:

```
Controller (WebFlux)
    ↓ Mono/Flux
Service Layer
    ↓ Mono/Flux
Repository (R2DBC)
    ↓ Reactive Streams
PostgreSQL
```

### Основные компоненты

#### Контроллеры
- `ItemController` - управление каталогом товаров
- `CartController` - управление корзиной
- `OrderController` - управление заказами

#### Сервисы
- `ItemService` - бизнес-логика работы с товарами
- `CartService` - бизнес-логика корзины (WebSession)
- `OrderService` - бизнес-логика заказов

#### Репозитории
- `ItemRepository` - реактивный доступ к данным товаров
- `OrderRepository` - реактивный доступ к данным заказов
- `OrderItemRepository` - реактивный доступ к позициям заказов

## Установка и запуск

### Требования

- Docker и Docker Compose
- Java 21 (для локальной разработки)
- Maven 3.9+ (включен wrapper)

### Запуск с Docker Compose

Запуск всего стека (приложение + база данных + nginx):

```bash
docker compose -f docker/docker-compose.yml up --build
```

Приложение будет доступно по адресу: **http://localhost:8080**

## Тестирование

### Требования для тестов

- **Docker** - обязательно для запуска тестов (Testcontainers)

### Запуск всех тестов

```bash
./mvnw test
```

### Запуск конкретного теста

```bash
./mvnw test -Dtest=ItemRepositoryTest
./mvnw test -Dtest=OrderControllerTest
```