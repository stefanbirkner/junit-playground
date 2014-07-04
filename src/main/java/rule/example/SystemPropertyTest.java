package rule.example;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ProvideSystemProperty;

public class SystemPropertyTest {
    @Rule
    public final ProvideSystemProperty myPropertyHasMyValue
            = new ProvideSystemProperty("MyProperty", "MyValue");

    @Test
    public void overrideProperty() {
        assertEquals("MyValue", System.getProperty("MyProperty"));
    }
}
