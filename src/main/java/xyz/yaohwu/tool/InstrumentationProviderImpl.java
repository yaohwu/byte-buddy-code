package xyz.yaohwu.tool;

import net.bytebuddy.agent.ByteBuddyAgent;

import java.lang.instrument.Instrumentation;

/**
 * @author yaohwu
 * created by yaohwu at 2020/7/2 16:14
 */
public enum InstrumentationProviderImpl implements InstrumentationProvider {

    INSTANCE;

    private Instrumentation instrumentation = null;

    public Instrumentation findInstrumentation() {
        if (instrumentation != null) {
            return instrumentation;
        } else {
            try {
                instrumentation = ByteBuddyAgent.getInstrumentation();
                return instrumentation;
            } catch (IllegalStateException e) {
                try {
                    instrumentation = ByteBuddyAgent.install(
                            new ByteBuddyAgent.AttachmentProvider.Compound(
                                    ByteBuddyAgent.AttachmentProvider.DEFAULT));
                    return instrumentation;
                } catch (Throwable t) {
                    return null;
                }
            }
        }
    }
}