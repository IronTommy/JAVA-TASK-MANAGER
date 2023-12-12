package service.http;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    public static HttpClient client = HttpClient.newHttpClient();

    final URI url = URI.create("http://localhost:8078/register"); // передать порт из конструтора
    static String API_KEY = null;

    public KVTaskClient() throws IOException, InterruptedException {
        registration();
    }

    public void registration() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder() // получаем экземпляр билдера
                .GET()// указываем HTTP-метод запроса
                .uri(url)// указываем адрес ресурса
                //.uri(URI.create("http://localhost:8078"))// указываем адрес ресурса
                .version(HttpClient.Version.HTTP_1_1) // указываем версию протокола
                .header("Accept", "application/json")// указываем заголовок Accept
                .build(); // заканчиваем настройку и создаём ("строим") http-запрос
        // отправляем запрос
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler); //и получаем ответ от сервера
        API_KEY = response.body();
        System.out.println("Ключ регистрации теперь у клиента:" + API_KEY);
        System.out.println("Получен ответ от сервера:" + response.statusCode());
    }

    public static void save(String key, String task) throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/save/" + key + "?API_KEY=" + API_KEY);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }

    public static String load(String str) throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/load/" + str + "?API_KEY=" + API_KEY);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static String getApiKey() {
        return API_KEY;
    }
}
