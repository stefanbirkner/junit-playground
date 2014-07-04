import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class TestStarter {
    public static void main(String... args) {
        Result result = JUnitCore.runClasses(MathTest.class);
        System.out.println("result: " + result.wasSuccessful());
        System.out.println(result.getFailures().get(0));
    }
}
