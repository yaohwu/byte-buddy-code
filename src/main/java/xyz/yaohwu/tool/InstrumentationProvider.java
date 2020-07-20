package xyz.yaohwu.tool;

import java.lang.instrument.Instrumentation;

public interface InstrumentationProvider {
    Instrumentation findInstrumentation();
}