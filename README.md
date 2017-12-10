
# Chunk-09-2017 [![Build Status](https://travis-ci.org/java-park-mail-ru/Chunk-09-2017.svg?branch=develop)](https://travis-ci.org/java-park-mail-ru/Chunk-09-2017)
## It Is Not Defense
>***Поглощай или будь поглощен<br>***

## [Frontend](https://chunk-frontend.herokuapp.com/)<=>[Backend](https://backend-java-spring.herokuapp.com/)

## Members
* Андрей Савосин
* Игорь Дружинин
* Дима Трубников

## API
| Действие | Тип запроса, URL | Тело запроса | Тело ответа |
| --- | --- | --- | --- |
| Зарегистрироваться | POST, /sign_up | "username", "email", "password" | "username", "email" |
| Авторизоваться | POST, /sign_in | "login", "password" | "username", "email" |
| Изменить профиль текущего пользователя | POST, /update | “username”, ”email”, “password”, “old_password” | "username", "email" |
| Запросить данные пользователя текущей сессии | GET, /whoisit | | "username", "email" | |
| Разлогиниться | GET, /exit |  |  |

