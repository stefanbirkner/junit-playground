package sfbrunner;

import org.junit.internal.runners.statements.InvokeMethod;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SfbRunner extends ParentRunner<FrameworkMethod> {
    public SfbRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected List<FrameworkMethod> getChildren() {
        List<FrameworkMethod> testMethods = new ArrayList<>();
        for (Method method : getTestClass().getJavaClass().getMethods()) {
            if (method.getName().startsWith("sfb")) {
                testMethods.add(new FrameworkMethod(method));
            }
        }
        return testMethods;
    }

    @Override
    protected Description describeChild(FrameworkMethod method) {
        return Description.createTestDescription(getTestClass().getJavaClass(),
                method.getName(), method.getAnnotations());
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        try {
            runSingleTest(method, notifier);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void runSingleTest(FrameworkMethod method, RunNotifier notifier) throws IllegalAccessException,
            InvocationTargetException, InstantiationException {
        Object test = getTestClass().getOnlyConstructor().newInstance();
        Statement statement = new InvokeMethod(method, test);
        Description description = describeChild(method);
        notifier.fireTestStarted(description);
        try {
            statement.evaluate();
            if (method.getName().endsWith("MustFail")) {
                notifier.fireTestFailure(new Failure(description, new RuntimeException("Test didn't fail.")));
            }
        } catch (Throwable e) {
            if (!method.getName().endsWith("MustFail")) {
                notifier.fireTestFailure(new Failure(description, e));
            }
        } finally {
            notifier.fireTestFinished(description);
        }
    }
}
