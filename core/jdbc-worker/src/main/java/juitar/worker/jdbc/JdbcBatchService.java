package juitar.worker.jdbc;

import juitar.monitoring.api.Monitored;
import juitar.worker.queue.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PostConstruct;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author sha1n
 * Date: 1/20/13
 */
public class JdbcBatchService {

    @Autowired
    private WorkQueue queue;

    @Autowired
    private WorkerQueueServiceRegistry workerServiceRegistry;

    @Autowired
    @Qualifier("batchWorker")
    private WorkerFactory workerFactory;

    private WorkerQueueService workerQueueService;
    private int workers = 1;
    private Queue<String> statementQueue = new ConcurrentLinkedQueue<>();

    @PostConstruct
    public void init() {
        workerQueueService = workerServiceRegistry.registerQueueService(queue, workerFactory);
        workerQueueService.start(workers);
    }

    public void setWorkers(int workers) {
        this.workers = workers;
    }

    @Monitored
    public void executeUpdate(String updateStatement, ResultChannel resultChannel) {
        statementQueue.add(updateStatement);

        if (statementQueue.size() >= 10) {
            queue.submit(new Work(queue.getId(), statementQueue.toArray(new String[statementQueue.size()]), resultChannel));
        }
    }

}