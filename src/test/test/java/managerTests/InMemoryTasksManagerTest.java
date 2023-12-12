package test.java.managerTests;

import org.junit.jupiter.api.BeforeEach;
import service.inMemory.InMemoryTasksManager;

public class InMemoryTasksManagerTest  extends TaskManagerTest {

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTasksManager();
    }

}




