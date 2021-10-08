import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WorkScheduler {
    private final ExecutorService executor = Executors.newWorkStealingPool();

    public <T> Future<T> submitWork(Callable<T> task) {
        return executor.submit(task);
    }
}
