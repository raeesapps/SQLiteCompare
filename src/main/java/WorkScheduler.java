import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WorkScheduler {
    private final ExecutorService executor = Executors.newWorkStealingPool();

    public Future<?> submitWork(Runnable task) {
        return executor.submit(task);
    }
}
