#JUnit Explained

A presentation about JUnit's internals.

Stefan Birkner

##Setup – Basic Test

    public class MathTest {
      @Test
      public void calculates_minimum() {
        int min = Math.min(3, 5);
        assertEquals(3, min);
      }
    }

##Setup – Test Starter

    public class TestStarter {
      public void main(String... args) {
        Result result = JUnitCore.runClasses(MathTest.class);
        if (result.wasSuccessful())
          System.out.println("Everything is fine.");
        else
          System.out.println("Test failed");
      }
    }

##Output of Simple Test

    Everything is fine.

##Failing Test

    public class MathTest {
      @Test
      public void calculates_minimum() {
        int min = Math.min(3, 5);
        assertEquals(4, min);
      }
    }

Output:

    Test failed

## Result

* wasSuccessful() – `true` if all tests succeeded
* getRunCount() – number of tests run
* getRunTime() – how long did the entire suite run
* getIgnoreCount() – number of ignored tests
* getFailureCount() – number of failed tests
* getFailures() – information about the failed tests

## Failures

    public class TestStarter {
      public void main(String... args) {
        Result result = JUnitCore.runClasses(MathTest.class);
        System.out.println(result.getFailures().get(0));
      }
    }

Output:

    calculates_minimum(MathTest): expected:<4> but was:<3>

##Failure

    +-----------------------------+
    |          Failure            |
    +-----------------------------+
    | description : Description   |
    | thrownException : Throwable |
    +-----------------------------+

##IDE Output

    java.lang.AssertionError: 
    Expected :4
    Actual   :3
    <Click to see difference>

    at org.junit.Assert.fail(Assert.java:88)
    at org.junit.Assert.failNotEquals(Assert.java:789)
    at org.junit.Assert.assertEquals(Assert.java:118)
    at org.junit.Assert.assertEquals(Assert.java:601)
    at org.junit.Assert.assertEquals(Assert.java:588)
    at MathTest.calculates_minimum(MathTest.java:9)
    ...

#RunListener

##Add RunLister

    public class TestStarter {
      public void main(String... args) {
        JUnitCore core = new JUnitCore();
        core.addListener(new YourRunListener());
        core.run(MathTest.class);
      }
    }

##RunListener

The RunListener has seven methods:

* testRunStarted(Description description)
* testRunFinished(Result result)
* testStarted(Description description)
* testFinished(Description description)
* testFailure(Failure failure)
* testAssumptionFailure(Failure failure)
* testIgnored(Description description)

#Runner

##Runner

    +-----------------------------------+
    |              Runner               |
    +-----------------------------------+
    | getDescription() : Description    |
    | run(inout notifier : RunNotifier) |
    | testCount() : int                 |
    +-----------------------------------+

##JUnitCore.runClasses(MathTest.class)

    Request request = createRequestForClasses(MathTest.class):
    Runner runner = request.getRunner();
    RunNotifier runNotifier = createRunNotifier();
    runner.run(runNotifier);

##Create Runners

    AllDefaultPossibilitiesBuilder.runnerForClass(Class<?> testClass) throws Throwable {
        List<RunnerBuilder> builders = Arrays.asList(
                ignoredBuilder(),
                annotatedBuilder(),
                suiteMethodBuilder(),
                junit3Builder(),
                junit4Builder());

        for (RunnerBuilder each : builders) {
            Runner runner = each.safeRunnerForClass(testClass);
            if (runner != null) {
                return runner;
            }
        }
        return null;
    }
    
##IgnoredBuilder

    public class IgnoredBuilder extends RunnerBuilder {
        public Runner runnerForClass(Class<?> testClass) {
            if (testClass.getAnnotation(Ignore.class) != null) {
                return new IgnoredClassRunner(testClass);
            }
            return null;
        }
    }
    
##IgnoredClassRunner

    public class IgnoredClassRunner extends Runner {
        private final Class<?> clazz;
    
        public IgnoredClassRunner(Class<?> testClass) {
            clazz = testClass;
        }
    
        public void run(RunNotifier notifier) {
            notifier.fireTestIgnored(getDescription());
        }
    
        public Description getDescription() {
            return Description.createSuiteDescription(clazz);
        }
    }

##JUnit4Builder

    public class JUnit4Builder extends RunnerBuilder {
        @Override
        public Runner runnerForClass(Class<?> testClass) throws Throwable {
            return new BlockJUnit4ClassRunner(testClass);
        }
    }

##BlockJUnit4ClassRunner – Checks

    protected void collectInitializationErrors(List<Throwable> errors) {
        validatePublicVoidNoArgMethods(BeforeClass.class, true, errors);
        validatePublicVoidNoArgMethods(AfterClass.class, true, errors);
        validateClassRules(errors);
        applyValidators(errors);
        validateNoNonStaticInnerClass(errors);
        validateConstructor(errors);
        validateInstanceMethods(errors);
        validateFields(errors);
        validateMethods(errors);
    }
    
    VALIDATORS = Arrays.asList(new AnnotationsValidator(), new PublicClassValidator());
    
##BlockJUnit4ClassRunner – run

    public void run(RunNotifier notifier) {
        EachTestNotifier testNotifier = new EachTestNotifier(notifier, getDescription());
        try {
            Statement statement = classBlock(notifier);
            statement.evaluate();
        } catch (AssumptionViolatedException e) {
            testNotifier.addFailedAssumption(e);
        } catch (StoppedByUserException e) {
            throw e;
        } catch (Throwable e) {
            testNotifier.addFailure(e);
        }
    }
    
##Statement

    public abstract class Statement {
        /**
         * Run the action, throwing a {@code Throwable} if anything goes wrong.
         */
        public abstract void evaluate() throws Throwable;
    }

##classBlock

    Statement statement = () -> runChildren(notifier);
    if (!areAllChildrenIgnored()) {
        statement = withBeforeClasses(statement);
        statement = withAfterClasses(statement);
        statement = withClassRules(statement);
    }
    return statement;

##runChildren

    BlockJUnit4ClassRunner extends ParentRunner<FrameworkMethod>

    void runChildren(RunNotifier notifier) {
        for (FrameworkMethod each : getFilteredChildren()) {
            runChild(each, notifier);
        }
    }
    
    List<FrameworkMethod> getFilteredChildren() {
        return getTestClass().getAnnotatedMethods(Test.class);
    }
    
    void runChild(final FrameworkMethod method, RunNotifier notifier) {
        Description description = describeChild(method);
        if (isIgnored(method)) {
            notifier.fireTestIgnored(description);
        } else {
            runLeaf(methodBlock(method), description, notifier);
        }
    }

##runLeaf

    void runLeaf(Statement statement, Description description, RunNotifier notifier) {
        EachTestNotifier eachNotifier = new EachTestNotifier(notifier, description);
        eachNotifier.fireTestStarted();
        try {
            statement.evaluate();
        } catch (AssumptionViolatedException e) {
            eachNotifier.addFailedAssumption(e);
        } catch (Throwable e) {
            eachNotifier.addFailure(e);
        } finally {
            eachNotifier.fireTestFinished();
        }
    }

##methodBlock

    Object test = getTestClass().getOnlyConstructor().newInstance();
    Statement statement = methodInvoker(method, test);
    statement = possiblyExpectingExceptions(method, test, statement);
    statement = withPotentialTimeout(method, test, statement);
    statement = withBefores(method, test, statement);
    statement = withAfters(method, test, statement);
    statement = withRules(method, test, statement);
    return statement;
    
##InvokeMethod

    public class InvokeMethod extends Statement {
        private final FrameworkMethod testMethod;
        private final Object target;
    
        public InvokeMethod(FrameworkMethod testMethod, Object target) {
            this.testMethod = testMethod;
            this.target = target;
        }
    
        @Override
        public void evaluate() throws Throwable {
            testMethod.invokeExplosively(target);
        }
    }

##FrameworkMethod.invokeExplosively

    public Object invokeExplosively(final Object target, final Object... params)
            throws Throwable {
        return new ReflectiveCallable() {
            @Override
            protected Object runReflectiveCall() throws Throwable {
                return method.invoke(target, params);
            }
        }.run();
    }

##More Runners

* Suite
* Parameterized
* Theories
* Enclosed
* Categories

##Suite

    @RunWith(Suite.class )
    @Suite.SuiteClasses({A.class, B.class})
    public class TestSuite {
    }

##Suite

    Suite extends ParentRunner<Runner>

    public Suite(Class<?> klass, RunnerBuilder builder) throws InitializationError {
        this(builder, builder.runners(klass, getAnnotatedClasses(klass)));
    }
    
    protected Suite(Class<?> klass, List<Runner> runners) throws InitializationError {
        super(klass);
        this.runners = Collections.unmodifiableList(runners);
    }

    @Override
    protected List<Runner> getChildren() {
        return runners;
    }

    @Override
    protected Description describeChild(Runner child) {
        return child.getDescription();
    }

    @Override
    protected void runChild(Runner runner, final RunNotifier notifier) {
        runner.run(notifier);
    }

##SfbRunner

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

##SfbRunnerTest

    @RunWith(SfbRunner.class)
    public class SfbRunnerTest {
        public void sfbTestOne() {
    
        }
    
        public void notATest() {
            throw new RuntimeException("never executed");
        }
    
        public void sfbTestTwo() {
    
        }
    
        public void sfbTestThreeMustFail() {
            throw new RuntimeException("xx");
        }
    }
    
#Rules

Method Modification in JUnit

http://prezi.com/kb5pfbj5-iyz/rules-in-junit/