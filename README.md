# Server-side of "Sunshine"

Sunshine (Солнышко) - это приложение для знакомств, отличительная
черта которого не свайпы, а ежедневное подборка 2-5 пар для каждого 
пользователя, при взаимном лайке с которыми можно начать беседу. 
Предусмотрена логика премиум аккаунта.

Server-side предоставляет api для мобильного клиента


## Dev info:
- Spring boot 3, java 21, auth по JWT
- [Диаграмма БД](db_diagram.pdf), [docker-compose](docker-compose.yaml)
- Покрытие интеграционными и Unit тестами (по данным IDEA Coverage):
  - Class - 94% (110/117)
  - Method - 90% (537/594)
  - Line - 87% (1268/1446)
- Prometheus и grafana для сбора и отображения метрик и логов
- Lombok; Liquibase; Socketio; Spring security, MVC, mail
