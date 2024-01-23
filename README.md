<h2> </h2>
<p>Многопользовательское файловое облако. Пользователи сервиса могут использовать его для загрузки и хранения файлов. Источником вдохновения для проекта является Google Drive.</p>
<p>Описание и ТЗ проекта доступно по адресу: </p>
<p>Приложение доступно по адресу: </p> 

<h2>REST API для описания валют и обменных курсов. </h2> Позволяет просматривать и редактировать списки валют и обменных курсов, и совершать расчёт конвертации произвольных сумм из одной валюты в другую. </p>
<p>Веб-интерфейс для проекта не подразумевается.</p>

<h2>Мотивация проекта: </h2>
<ul>
<li>REST API - правильное именование ресурсов, использование HTTP кодов ответа</li>
<li>SQL - базовый синтаксис, создание таблиц</li>
</ul>

<h2>Используемые технологии </h2>
<ul>
<li>Java </li>
Java - коллекции, ООП
Maven/Gradle
Backend
Spring Boot, Spring Security, Spring Sessions
Thymeleaf
Upload файлов, заголовки HTTP запросов, cookies, cессии
Базы данных
SQL
Spring Data JPA
Представление о NoSQL хранилищах
Frontend - HTML/CSS, Bootstrap
Docker - контейнеры, образы, volumes, Docker Compose
Тесты - интеграционное тестирование, JUnit, Testcontainers
Деплой - облачный хостинг, командная строка Linux, Tomcat
</ul>

Мотивация проекта
Использование возможностей Spring Boot
Практика с Docker и Docker Compose
Первый проект, где студент самостоятельно разрабатывает структуру БД
Знакомство с NoSQL хранилищами - S3 для файлов, Redis для сессий
Функционал приложения
Работа с пользователями:

Регистрация
Авторизация
Logout
Работа с файлами и папками:

Загрузка файлов и папок
Создание новой пустой папки (аналогично созданию новой папки в проводнике)
Удаление
Переименование
Интерфейс приложения
Главная страница
Адрес - /?path=$path_to_subdirectory. Параметр $path задаёт путь просматриваемой папки. Если параметр отсутствует, подразумевается корневая папка. Пример - /path=Projects%2FJava%2FCloudFileStorage (параметр закодирован через URL Encode).

Заголовок
Для неавторизованных пользователей - кнопки регистрации и авторизации
Для авторизованных пользователей - логин текущего пользователя и кнопка Logout
Контент (только для авторизованных пользователей)
Форма поиска файлов и папок по названию
Навигационная цепочка (breadcrumbs), содержащая путь из папок до текущей папки. Каждый элемент является ссылкой на свою папку. Пример - цепочка из папок, ведущая к - Projects/Java/CloudFileStorage содержала бы 3 папки - корневую, Projects и Projects/Java
Список файлов в текущей директории. Для каждого файла отображаем имя и кнопку, вызывающее меню действий (удаление, переименование)
Формы (или drop areas) для загрузки файлов и папок
Страница поиска файлов
Адрес - /search/?query=$search_query.

Заголовок
Для неавторизованных пользователей - кнопки регистрации и авторизации
Для авторизованных пользователей - логин текущего пользователя и кнопка Logout
Контент
Форма поиска файлов и папок по названию
Список найденных файлов. Для каждого найденного файла отображаем имя и кнопку для перехода в папку, содержащую данный файл
Неавторизованные пользователя не имеют доступа к данной странице, приложение должно редиректить их на форму авторизации.

Контроллер для доступа к конкретному файлу
Остальное
Страницы с формами регистрации и авторизации
Работа с сессиями, авторизацией, регистрацией
В предыдущем проекте мы управляли сессиями пользователей вручную, в этом проекте воспользуемся возможности экосистемы Spring Boot.

За авторизацию, управление доступом к страницам отвечает Spring Security.

За работу с сессиями отвечает Spring Sessions. По умолчанию Spring Boot хранит сессии внутри приложения, и они теряются после каждого перезапуска приложения. Мы воспользуемся Redis для хранения сессий. Пример - https://www.baeldung.com/spring-session. Redis - NoSQL хранилище, имеющее встроенный TTL (time to live) атрибут для записей, что делает его удобным для хранения сессий - истекшие сессии автоматически удаляются.

SQL база данных
В этом проекте студент самостоятельно разрабатывает структуру базы данных для хранения пользователей (файлы и сессии располагаются в других хранилищах). Предлагаю использовать MySQL.

Ориентироваться стоит на интеграцию с Spring Security. Эта библиотека экосистемы Spring подразумевает определённые атрибуты, которыми должен обладать пользователь, и список которых и станет основой колонок для таблицы Users.

Пример интеграции между Spring Security и Spring Data JPA - https://www.baeldung.com/registration-with-spring-mvc-and-spring-security.

Важно помнить о создании необходимых индексов в таблице Users. Например, логин пользователя должен быть уникальным.

Хранилище файлов S3
Для хранения файлов будем пользоваться S3 - simple storage service. Проект, разработанный Amazon Cloud Services, представляет из себя облачный сервис и протокол для файлового хранилища. Чтобы не зависеть от платных сервисов Amazon в этом проекте, воспользуемся альтернативным S3-совместимым хранилищем, которое можно запустить локально - https://min.io/

Докер образ для локального запуска MinIO - https://hub.docker.com/r/minio/minio/
Для работы с протоколом S3 воспользуемся Minio Java SDK
Структура S3 хранилища
В SQL мы оперируем таблицами, в S3 таблиц не существует, вместо этого S3 оперирует бакетами (bucket - корзина) с файлами. Чтобы понять что такое бакет, можно провести аналогию с диском или флешкой.

Внутри бакета можно создавать файлы и папки.

Для хранения файлов всех пользователей в проекте создадим для них бакет под названием user-files. В корне бакета для каждого пользователя будет создана папка с именем в формате user-${id}-files, где id является идентификатором пользователя из SQL базы.

Каждая из таких папок является корнем для хранения папок данного пользователя. Пример - файл docs/test.txt пользователя с id 1 должен быть сохранён в путь user-1-files/docs/test.txt.

Работа с S3 из Java
Как было упомянуто выше, для работы с S3 воспользуемся AWS Java SDK. Необходимо будет научиться пользоваться этой библиотекой, чтобы:

Создавать файлы
Переименовывать файлы
“Переименовывать” папки. Насколько знаю в S3 нет такой операции, переименование папки по сути представляет собой создание папки под новым именем и перенос туда файлов
Удалять файлы
Upload файлов
Для загрузки файлов необходимо воспользоваться HTML file input - https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/file. Распространённый подход оформить это в виде зоны, на которую можно перетягивать файлы из проводника, пример - https://codepen.io/dcode-software/pen/xxwpLQo.

На уровне HTTP, передача файлов осуществляется с помощью multipart/form-data.

Со стороны Spring Boot необходимо будет реализовать контроллер(ы) для обработки загруженных файлов. Важно иметь в виду, что по-умолчанию лимит на загрузку файлов в Spring Boot равен 10 мегабайтам, но его можно увеличить.

Загрузка папок
File input может быть использован для загрузки либо отдельных файлов, либо папок (если у input установлен атрибут webkitdirectory), но не одновременно.

Получается, что необходимо иметь 2 input’а - для файлов, и для папок. Возможно, существуют Javascript библиотеки, которые решают этот вопрос и реализуют единый input для обоих случаев.

Тесты
Интеграционные тесты сервиса по работе с пользователями
Как и в прошлом проекте, покроем тестами связку слоя данных с классами-сервисами, отвечающими за пользователей.

Вместо с H2 предлагаю воспользоваться Testcontainers для запуска тестов в контексте полноценной (а не in-memory) базы данных. Это позволяет приблизить окружение тестов к рабочему окружению, и тестировать нюансы, специфичные для конкретных движков БД.

Примеры тест кейсов:

Вызов метода “создать пользователя” в сервисе, отвечающем за работу с пользователями, приводит к появлению новой записи в таблице users
Создание пользователя с неуникальным username приводит к ожидаемому типу исключения
Интеграционные тесты сервиса по работе с файлами и папками
Опциональное задание повышенной сложности - покрыть тестами взаимодействие с сервисом хранения данных, работающим Minio.

Примеры тест кейсов:

Загрузка файла приводит к его появлению в bucket’е Minio в корневой папке текущего пользователя
Переименование, удаление файлов и папок приводит к ожидаемому результату
Проверка прав доступа - пользователь не должен иметь доступа к чужим файлам
Поиск - пользователь может находить свои файлы, но не чужие
Что потребуется:

Интеграция JUnit и Spring Security
Реализация GenericContainer для интеграции Minio и Testcontainers
Docker
В данном проекте впервые воспользуемся Docker для удобного запуска необходимых приложений - SQL базы, файлового хранилища MinIO и хранилища сессий Redis.

Необходимо:

Найти образы для каждого нужного приложения из списка выше
Написать Docker Compose файл для запуска стека с приложениями (по контейнеру для каждого)
Знать Docker Compose команды для работы со стеком
Как будет выглядеть работа с Docker:

Для работы над проектом запускаем стек из контейнеров
Уничтожаем или останавливаем контейнеры (с сохранением данных на volumes), когда работа не ведётся
По необходимости уничтожаем данные на volumes, если хотим очистить то или иное хранилище, запустить