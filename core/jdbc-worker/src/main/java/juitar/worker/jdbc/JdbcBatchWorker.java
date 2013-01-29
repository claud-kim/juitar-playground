package juitar.worker.jdbc;

import juitar.monitoring.spi.config.MonitoredCategory;
import juitar.monitoring.spi.config.MonitoredOperation;
import juitar.worker.queue.Result;
import juitar.worker.queue.Work;
import juitar.worker.queue.Worker;
import org.juitar.monitoring.api.Monitored;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author sha1n
 * Date: 1/20/13
 */
public class JdbcBatchWorker implements Worker {

    private final JdbcTemplate jdbcTemplate;

    public JdbcBatchWorker(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Monitored(threshold = 10, category = MonitoredCategory.DAL, operation = MonitoredOperation.DATABASE_ACCESS)
    @Override
    public Result doWork(Work work) {

        String[] payload = (String[]) work.getPayload();
        int[] ints = jdbcTemplate.batchUpdate(payload);

        Result result = new Result(work.getId());
        result.setResultData(ints);

        return result;
    }

}
