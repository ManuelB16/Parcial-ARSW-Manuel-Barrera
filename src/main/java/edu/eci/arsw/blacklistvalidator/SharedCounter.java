package edu.eci.arsw.blacklistvalidator;

import java.util.concurrent.atomic.AtomicInteger;

public class SharedCounter {

    public static AtomicInteger counter = new AtomicInteger(0);
    
        public static int get() {
        return counter.get();
    }
    public void increment() {
        counter.incrementAndGet();
    }
    
}
