package me.ezzedine.mohammed.temporal.mockstatic;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestEnvironmentOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.List;

import static org.mockito.Mockito.*;

class DummyWorkflowImplTest {

    public static final String WORKFLOW_ID = "workflowId";
    private static MockStaticContextPropagator<WorkflowLoggerApiWrapper> workflowLoggerMockStatic;

    private Logger mockLogger;
    private TestWorkflowEnvironment testWorkflowEnvironment;
    private WorkflowClient workflowClient;

    @BeforeEach
    void setUp() {
        workflowLoggerMockStatic = new MockStaticContextPropagator<>(WorkflowLoggerApiWrapper.class);
        mockLogger = mock(Logger.class);

        testWorkflowEnvironment = TestWorkflowEnvironment.newInstance(getTestEnvironmentOptions());
        workflowClient = testWorkflowEnvironment.getWorkflowClient();

        Worker worker = testWorkflowEnvironment.newWorker("mock-static");
        DummyActivityImpl dummyActivity = mock(DummyActivityImpl.class);
        worker.registerActivitiesImplementations(dummyActivity);
        worker.registerWorkflowImplementationTypes(DummyWorkflowImpl.class);

        testWorkflowEnvironment.start();
    }

    @AfterEach
    void tearDown() {
        testWorkflowEnvironment.shutdown();
    }

    @Test
    @DisplayName("test context propagation")
    void test_context_propagation() {
        workflowLoggerMockStatic.addStubbing(() -> WorkflowLoggerApiWrapper.getLogger(any()), invocationOnMock -> mockLogger);
        startWorkflow();

        sendFirstSignalToWorkflow();

        verify(mockLogger).info("workflow started");
        verify(mockLogger).info("workflow finished");
    }

    private void sendFirstSignalToWorkflow() {
        workflowClient.newWorkflowStub(DummyWorkflow.class, WORKFLOW_ID).dummySignal();
        testWorkflowEnvironment.sleep(Duration.ofMillis(500));
    }

    private void startWorkflow() {
        WorkflowOptions workflowOptions = WorkflowOptions.newBuilder().setWorkflowId(WORKFLOW_ID).setTaskQueue("mock-static").build();
        DummyWorkflow workflow = workflowClient.newWorkflowStub(DummyWorkflow.class, workflowOptions);
        WorkflowClient.start(workflow::execute);
        testWorkflowEnvironment.sleep(Duration.ofMillis(500));
    }

    private static TestEnvironmentOptions getTestEnvironmentOptions() {
        return TestEnvironmentOptions.newBuilder().setWorkflowClientOptions(getWorkflowClientOptions()).build();
    }

    private static WorkflowClientOptions getWorkflowClientOptions() {
        return WorkflowClientOptions.newBuilder().setContextPropagators(List.of(workflowLoggerMockStatic)).build();
    }
}