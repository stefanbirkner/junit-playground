package runner.java8;

import static java.util.Arrays.asList;
import static runner.java8.Test.test;

import java.util.List;

import org.junit.Assert;
import org.junit.runner.RunWith;

@RunWith(Java8Runner.class)
public class Java8Test {
    public static List<Test> tests = asList(
            test("true is not false", () -> Assert.assertNotEquals(true, false)),
            test("sets value", () -> {
                int a = 1;
                Assert.assertEquals(a, 1);
            }));
}
