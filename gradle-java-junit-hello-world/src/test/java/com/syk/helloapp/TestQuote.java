package com.syk.helloapp;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TestQuote {

   @Test
   public void testSetWho() {
      String str = "testwho";
      Quote quote = new Quote();
      quote.setWho(str);

      assertEquals(quote.getWho(),str);
   }
}

