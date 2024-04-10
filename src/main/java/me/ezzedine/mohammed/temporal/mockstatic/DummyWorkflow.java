package me.ezzedine.mohammed.temporal.mockstatic;

import io.temporal.workflow.*;

@WorkflowInterface
public interface DummyWorkflow {

    @WorkflowMethod
    void execute();

    @SignalMethod
    void dummySignal();
}
