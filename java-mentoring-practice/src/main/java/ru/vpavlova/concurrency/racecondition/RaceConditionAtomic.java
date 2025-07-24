package main.java.ru.vpavlova.concurrency.racecondition;

import java.util.concurrent.atomic.AtomicInteger;

//Решение проблемы гонки за разделяемый ресурс с использованием AtomicInteger
public class RaceConditionAtomic {
    public static final AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            for (int i = 0; i < 10000; i++) {
                counter.incrementAndGet(); // Атомарная операция
            }
        };

        var thread = new Thread(task);
        var thread1 = new Thread(task);
        var thread2 = new Thread(task);

        thread.start();
        thread1.start();
        thread2.start();

        thread.join();
        thread1.join();
        thread2.join();

        System.out.println("counter =" + counter);
    }
}
