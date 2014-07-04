package basic;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import org.junit.Test;

/**
 * This test shows the different results of a JUnit test.
 */
public class TestResultTest {
    @Test
    public void success() {
        //does not throw an exception
    }

    @Test
    public void failure()  {
        assertTrue(false);//throws AssertionError
    }

    @Test
    public void ignored() {
        assumeTrue(false);//throws AssumptionViolatedException
        assertTrue(false);
    }

    @Test
    public void error() throws Exception {
        throw new Exception();
    }
}
