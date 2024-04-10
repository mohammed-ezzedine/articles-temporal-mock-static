package me.ezzedine.mohammed.temporal.mockstatic;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DummyActivityImpl implements DummyActivity {

    @Override
    public void doSomething1() {
        log.info("do something 1");
    }

    @Override
    public void doSomething2() {
        log.info("do something 2");
    }
}
