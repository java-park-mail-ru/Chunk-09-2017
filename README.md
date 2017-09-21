# Chunk-09-2017
## Tower Defense
>***Строй башни, уничтожай врагов, оберегай ядро<br>
>(пока только авторизируйся)***
***
## Members
* Андрей aka Лапища
* Игорь Дружинин
* Дима Трубников

## API
|Действие | Тип запроса, URL | Тело запроса | Тело ответа (success) | Тело ответа (error) | 
| :----| -------- | ---| -- | -- |---| 
|Зарегистрироваться | POST, /sign_up | {"username":"Bob","email":"bob@mail.ru", "password":"secret"} | {"username":"Bob","email":"bob@mail.ru"} | {"errorMessage":"too short password"} | 
| Залогиниться | POST, /sign_in | {"login": "Bob", "password": "secret"} | {"username":"Bob","email":"bob@mail.ru"} | {"errorMessage":"wrong password or login"} | 
| Разлогиниться | GET, /exit | | | | 
| Запросить данные пользователя текущей сессии | GET, /whoisit | | {"login": "Bob", "password": "secret"} | | 
| Изменить профиль | POST, /update | {"username":"Bob","email":"bob@mail.ru", "password":"mystery", "old_password": "secret"} | {"username":"Bob","email":"bob@mail.ru"} | {"errorMessage":"wrong password"} |
