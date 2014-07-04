package sfbrunner;

import org.junit.runner.RunWith;

@RunWith(SfbRunner.class)
public class SfbRunnerTest {
    public void sfbTestOne() {

    }

    public void notATest() {

    }

    public void sfbTestTwo() {

    }

    public void sfbTestThreeMustFail() {
        throw new RuntimeException("xx");
    }
}
