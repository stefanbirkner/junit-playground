package rule.create;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class LogStdOutTest {
    @Rule
    public final StandardOutputStreamLog log = new StandardOutputStreamLog();

    @Test
    public void test() {
        System.out.println("hello world");
        assertEquals("hello world\n", log.getLog());
    }

    public static class StandardOutputStreamLog implements TestRule {
        private final ByteArrayOutputStream log = new ByteArrayOutputStream();
        private PrintStream originalStream;

        @Override
        public Statement apply(Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    originalStream = System.out;
                    try {
                        System.setOut(new PrintStream(log));
                        base.evaluate();
                    } finally {
                        System.setOut(originalStream);
                    }
                }
            };
        }

        public String getLog() {
            try {
                return log.toString("UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
