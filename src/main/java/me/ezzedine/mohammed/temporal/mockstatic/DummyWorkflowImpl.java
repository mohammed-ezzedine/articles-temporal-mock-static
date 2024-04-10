package me.ezzedine.mohammed.temporal.mockstatic;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

public class DummyWorkflowImpl implements DummyWorkflow {

    private final DummyActivity activity = Workflow.newActivityStub(DummyActivity.class, ActivityOptions.newBuilder()
                    .setTaskQueue("mock-static").setScheduleToCloseTimeout(Duration.ofMinutes(1)).build());

    private final Logger log = Workflow.getLogger(DummyWorkflowImpl.class);

    private boolean waiting1;

    @Override
    public void execute() {
        log.info("workflow started");

        activity.doSomething1();

        waiting1 = true;
        Workflow.await(() -> !waiting1);

        activity.doSomething2();
    }

    @Override
    public void dummySignal() {
        waiting1 = false;
    }
}
