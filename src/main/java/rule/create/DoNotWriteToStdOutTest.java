package rule.create;

import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runners.model.Statement;

public class DoNotWriteToStdOutTest {
    @Rule
    public final TestRule rule = (statement, description) -> new Statement() {
        private final LogStdOutTest.StandardOutputStreamLog log = new LogStdOutTest.StandardOutputStreamLog();

        @Override
        public void evaluate() throws Throwable {
            log.apply(statement, description).evaluate();
            assertTrue(log.getLog().isEmpty());
        }
    };

    @Test
    public void test() {
        System.out.println("hello world");
    }
}
