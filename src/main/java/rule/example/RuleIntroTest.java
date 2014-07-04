package rule.example;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;

public class RuleIntroTest {
    @Rule
    public final TestRule timeout = new Timeout(100);

    @Test
    public void successfulTest() throws Exception {
        //nothing to do
    }

    @Test
    public void failingTest() throws Exception {
        Thread.sleep(200);
    }
}
