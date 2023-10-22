## Ссылка на ПР
https://github.com/geo-tat/java-explore-with-me/pull/5

# ExploreWithMe 
#### Бэкенд приложение построенное на микросервисной архитектуре - служит для создания афиши мероприятий и участия в них.


## Стек:
#### JAVA 11, REST, JPA, Maven, Spring-Boot, Hibernate, PostgresSQL и H2, Docker

## Функционал:
- 3 уровня доступа(public/private/admin)
- База данных уникальных пользователей
- База данных событий (создание/обновление) с лимитом на участие пользователей
- Модерация событий админом (публикация/отмена)
- Создание запросов от пользователей на участие в событие
- Создание специальных подборок событий
- Возможность комментировать события

## Структура базы данных основного сервиса:

![ewm-main-service](ewm-main-service.png)
