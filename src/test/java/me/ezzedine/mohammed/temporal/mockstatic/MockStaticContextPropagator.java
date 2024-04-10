package me.ezzedine.mohammed.temporal.mockstatic;

import io.temporal.api.common.v1.Payload;
import io.temporal.common.context.ContextPropagator;
import org.mockito.MockedStatic;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mockStatic;

public class MockStaticContextPropagator<T> implements ContextPropagator {

    private final Class<T> targetClass;

    public MockStaticContextPropagator(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    private final ThreadLocal<MockedStatic<T>> workflowMockedStatic = new ThreadLocal<>();
    private final List<Stubbing> stubbings = new ArrayList<>();

    @Override
    public String getName() {
        return "MockStaticContextPropagator";
    }

    @Override
    public Map<String, Payload> serializeContext(Object context) {
        return Map.of();
    }

    @Override
    public Object deserializeContext(Map<String, Payload> header) {
        return new Object();
    }

    @Override
    public Object getCurrentContext() {
        return null;
    }

    @Override
    public void setCurrentContext(Object context) {
        if (workflowMockedStatic.get() != null) {
            workflowMockedStatic.get().close();
            workflowMockedStatic.remove();
        }

        workflowMockedStatic.set(mockStatic(targetClass));
        stubbings.forEach(s -> workflowMockedStatic.get().when(s.verification()).thenAnswer(s.answer()));
    }

    public void addStubbing(MockedStatic.Verification verification, Answer<?> answer) {
        stubbings.add(new Stubbing(verification, answer));
    }

    private record Stubbing(MockedStatic.Verification verification, Answer<?> answer) { }
}
