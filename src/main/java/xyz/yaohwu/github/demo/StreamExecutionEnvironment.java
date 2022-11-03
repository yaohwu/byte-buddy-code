package xyz.yaohwu.github.demo;

public class StreamExecutionEnvironment {

    public static StreamExecutionEnvironment getExecutionEnvironment() {
        return getExecutionEnvironment(new Configuration());
    }

    public static StreamExecutionEnvironment getExecutionEnvironment(Configuration configuration) {
        return new StreamExecutionEnvironment();
    }


    public String desc() {
        return "fake desc";
    }
}