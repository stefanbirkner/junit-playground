package runner.java8;

import java.util.List;

import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

public class Java8Runner extends ParentRunner<Test> {
    public Java8Runner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected List<Test> getChildren() {
        try {
            return (List<Test>) getTestClass().getJavaClass().getField("tests").get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Description describeChild(Test child) {
        return Description.createTestDescription(getTestClass().getJavaClass(), child.name);
    }

    @Override
    protected void runChild(Test child, RunNotifier notifier) {
        Description description = describeChild(child);
        EachTestNotifier eachTestNotifier = new EachTestNotifier(
                notifier, description);
        eachTestNotifier.fireTestStarted();
        try {
            child.statement.evaluate();
        } catch (Throwable t) {
            eachTestNotifier.addFailure(t);
        } finally {
            eachTestNotifier.fireTestFinished();
        }
    }
}
