package tw.codedata;

import static org.junit.Assert.*;
import org.junit.*;

public class HelloTest {
    @Test
    public void sayHelloTo() {
        Hello hello = new Hello();
        assertEquals("Hello, CodeData!", hello.sayHelloTo("CodeData"));
    }
}
