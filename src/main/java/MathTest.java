import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MathTest {
    @Test
    public void calculates_minimum() {
        int min = Math.min(3, 5);
        assertEquals(4, min);
    }
}
