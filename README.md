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

- Java 21 или выше
- Docker и Docker Compose
- Maven 3.9+ (включен wrapper)

### 1. Настройка базы данных

```bash
docker-compose -f docker/docker-compose.yml up -d
```

Эта команда:
- Создаст и запустит контейнер PostgreSQL 16
- Автоматически создаст базу данных `marketdb`
- Настроит пользователя `postgres` с паролем `postgres`
- База данных будет доступна на порту `5432`

### 2. Запуск приложения

#### С помощью Maven

```bash
./mvnw spring-boot:run
```

#### Сборка JAR и запуск

```bash
./mvnw package
java -jar target/MyMarket-0.0.1-SNAPSHOT.jar
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