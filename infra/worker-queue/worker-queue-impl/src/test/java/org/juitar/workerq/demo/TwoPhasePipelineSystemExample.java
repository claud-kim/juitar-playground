package org.juitar.workerq.demo;

import org.juitar.workerq.*;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author sha1n
 * Date: 1/19/13
 */
public class TwoPhasePipelineSystemExample {

    public static void main(String... args) throws InterruptedException {

        final TwoPhasePipelineSystemExample twoPhasePipelineSystem = new TwoPhasePipelineSystemExample();

        twoPhasePipelineSystem.start();


        for (int i = 0; i < 2; i++) {
            twoPhasePipelineSystem.phase1Queue.add(
                    new Work(UUID.randomUUID().toString(), "payload", new CompletionCallback() {

                        @Override
                        public void onSuccess(Result result) {
                            twoPhasePipelineSystem.phase2Queue.add(
                                    new Work(UUID.randomUUID().toString(), result.getResultData(), new CompletionCallbackImpl()));
                        }

                        @Override
                        public void onFailure(Result result, Exception e, CompletionStatus status) {
                            throw new RuntimeException(e);
                        }
                    }));
        }

        Thread.sleep(100);

        twoPhasePipelineSystem.stop();
    }

    private WorkerQueueServiceRegistryImpl serviceRegistry = new WorkerQueueServiceRegistryImpl();
    private final WorkQueue phase1Queue = new BlockingWorkQueue();
    private final WorkQueue phase2Queue = new BlockingWorkQueue();

    public void start() {
        // Register and start queue service for PHASE#1 queue.
        WorkerQueueService phase1QueueService = serviceRegistry.registerQueueService(phase1Queue, new WorkerFactoryImpl());
        phase1QueueService.start(1);

        // Register and start queue service for PHASE#2 queue.
        WorkerQueueService phase2QueueService = serviceRegistry.registerQueueService(phase2Queue, new WorkerFactoryImpl());
        phase2QueueService.start(1);
    }

    public void stop() {
        stop(phase1Queue, phase2Queue);
    }

    private void stop(WorkQueue... queues) {
        for (WorkQueue queue : queues) {
            serviceRegistry.getWorkerQueueService(queue).stop(1, TimeUnit.SECONDS);
        }
    }
}
