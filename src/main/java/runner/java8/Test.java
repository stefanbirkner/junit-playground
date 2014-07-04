package runner.java8;

public class Test {
    public final String name;
    public final TestStatement statement;

    public static Test test(String name, TestStatement statement) {
        return new Test(name, statement);
    }

    public Test(String name, TestStatement statement) {
        this.name = name;
        this.statement = statement;
    }
}
