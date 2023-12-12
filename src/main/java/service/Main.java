package service;

import KVServer.KVServer;
import model.Epic;
import model.SubTask;
import model.Task;
import service.auxiliary.Status;
import service.http.HttpTaskManager;
import service.http.HttpTaskServer;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        /*** шаблоны для запуска работы на сервере
         //Запустили KV сервер ***/
         KVServer KVServer = new KVServer();
         KVServer.start();
         KVServer.stop(1);
         //httpTaskManager получает URL или порт 8078 где запустит KVserver
         HttpTaskManager httpTaskManager = new HttpTaskManager(false);// если true то загружаемся из файла
         HttpTaskServer httpTaskServer = new HttpTaskServer(httpTaskManager);

         //Cоздаем задачу
         Task task1 = new Task("Задача", "Описание",
         Status.NEW, "PT15H","2052-08-04T08:11:30");
         httpTaskManager.putTask(task1);

         //Cоздаем задачу
         Task task2 = new Task("Задача2", "Описание2",
         Status.NEW, "PT15H","2052-09-04T08:11:30");
         httpTaskManager.putTask(task2);
         System.out.println(httpTaskManager.showMeTaskById(1));

         Epic someEpic1 = new Epic("Эпик", "Описание");
         SubTask subtask1 = new SubTask("Подзадача1", Status.NEW,
         someEpic1.getId(),4, "PT11H", "2006-08-04T10:11:30");
         SubTask subtask2 = new SubTask("Подзадача1", Status.NEW,
         someEpic1.getId(),5, "PT11H", "2006-09-04T10:11:30");
         httpTaskManager.putTask(someEpic1);
         httpTaskManager.putSubTask(subtask1);
         httpTaskManager.putSubTask(subtask2);


         HttpTaskServer.stop();
         HttpTaskServer.start();
         httpTaskManager.load();

         System.out.println("httpTaskManager.showMeAllTasks()");
         System.out.println(httpTaskManager.showMeAllTasks());

         HttpTaskServer.stop();
         httpTaskManager.load();

         System.out.println("проверка состояния сервера");
         System.out.println(httpTaskManager.showMeAllTasks());

         HttpTaskServer.stop();
         KVServer.stop(5);


    }
}
