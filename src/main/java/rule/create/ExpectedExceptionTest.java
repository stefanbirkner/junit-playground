package rule.create;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class ExpectedExceptionTest {

    @Rule
    public final ExpectedException thrown = new ExpectedException(RuntimeException.class);

    @Test
    public void test() {
        throw new RuntimeException();
    }

    public static class ExpectedException implements TestRule {
        private final Class<?> type;

        public ExpectedException(Class<?> type) {
            this.type = type;
        }

        @Override
        public Statement apply(Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    try {
                        base.evaluate();
                    } catch (Throwable t) {
                        if (!type.isAssignableFrom(t.getClass())) {
                            throw t;
                        }
                    }
                }
            };
        }
    }
}
