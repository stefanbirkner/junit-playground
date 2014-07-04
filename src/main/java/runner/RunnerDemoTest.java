package runner;

import static junit.framework.Assert.assertTrue;

import org.junit.*;
import org.junit.rules.Timeout;

public class RunnerDemoTest {
    @Rule
    public final Timeout timeout = new Timeout(1_000);

    @Before
    public void setUp() {
    }

    @Test
    public void success() {
    }

    @Test
    public void failure() {
        assertTrue(false);
    }

    @Test
    @Ignore
    public void ignored() {
    }

    @After
    public void tearDown() {
    }
}
