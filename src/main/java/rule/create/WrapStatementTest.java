package rule.create;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runners.model.Statement;

public class WrapStatementTest {
    @Rule
    public final TestRule rule = (statement, description) -> new Statement() {
        @Override
        public void evaluate() throws Throwable {
            statement.evaluate();
        }
    };

    @Test
    public void test() {
    }
}
