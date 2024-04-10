package me.ezzedine.mohammed.temporal.mockstatic;

import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

public class WorkflowLoggerApiWrapper {
    public static Logger getLogger(Class<?> clazz) {
        return Workflow.getLogger(clazz);
    }
}
