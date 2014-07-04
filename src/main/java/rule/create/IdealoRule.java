package rule.create;

import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class IdealoRule implements TestRule {
    private final TestRule chain = RuleChain.outerRule(new Timeout(1_000));

    @Override
    public Statement apply(Statement base, Description description) {
        return chain.apply(base, description);
    }
}
