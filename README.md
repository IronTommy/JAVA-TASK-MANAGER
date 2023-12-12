# Task Manager. Трекер задач.
###### Пэт проект в рамках учебной программы.

## Описание:

REST приложение на основе Java Core для организации работы над задачами. Не скромно говоря - аналог Jira

Приложение имеет следующую модель:
![Alt text](https://github.com/Gidrosliv/java-TaskManager/blob/main/schema.png?raw=true)

#### Задачи могут быть трёх типов: 
*  обычные задачи
*  эпики (сложные задачи)
*  подзадачи.

#### Этапы жизни задачи: 
*  NEW — задача только создана, но к её выполнению ещё не приступили. 
*  IN_PROGRESS — над задачей ведётся работа. 
*  DONE — задача выполнена. 
        
## Схема работы API:
    
![Alt text](https://github.com/Gidrosliv/java-TaskManager/blob/main/schema%20API.png?raw=true)

## Функциональность:
*  добавление и хранение данных в памяти;
*  добавление и хранение данных в файле;
*  доступ к данным через локальный сервер, проверяющий ключ доступа;
*  доступ к методам менеджера через HTTP-запросы.

## Шаблоны проектирования

В приложении применяется один из шаблонов проектирования - Фабрика(Factory Design Pattern).

## Как запускать приложение ?
*  клонируем проект на свой пк и открываем его в IntelliJ IDEA
*  запускаем выполнение метода Main 
*  после компиляции и запуска приложением можно пользоваться
*  для проверки работоспособности можно использовать браузер, Postman, или заготовленные в консоли шаблоны действий 


##  Технологический стек:
![Java 11](https://img.shields.io/badge/-Java-green) ![11](https://img.shields.io/badge/-11-orange) ![Spring Boot 2.7.2 ](https://img.shields.io/badge/-Spring%20Boot-blue) ![2.7.2 ](https://img.shields.io/badge/-2.7.2-orange) ![Gson](https://img.shields.io/badge/-Gson%202.9.0-yellowgreen) ![Apache](https://img.shields.io/badge/-Apache%20Maven%204.0.0-blue) ![Junit](https://img.shields.io/badge/-JUnit%205.4.2-green) ![Git](https://badgen.net/badge/icon/github?icon=github&label)     

<a href="#" onClick="scroll(0,0); return false" title="наверх">вверх страницы</a>
