package main.java.ru.vpavlova.concurrency.racecondition;

import java.util.concurrent.locks.ReentrantLock;

//Решение проблемы гонки за разделяемый ресурс
//с использованием реентерабельной блокировки java.util.concurrent.locks.ReentrantLock
public class RaceConditionSync {
    private static int counter = 0;
    private static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            for (int i = 0; i < 10000; i++) {
                lock.lock(); // Захватываем блокировку
                try {
                    counter++;
                } finally {
                    lock.unlock(); // Всегда освобождаем
                }
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

