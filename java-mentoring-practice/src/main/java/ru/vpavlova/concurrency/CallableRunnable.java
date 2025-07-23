package main.java.ru.vpavlova.concurrency;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

/*
Отредактировать существующий или создать новый пример с использованием асинхронного выполнения задачи (CompletableFuture)
Должны быть использованы след. операторы:
thenApply() - трансформация результата
thenAccept() - вывести в консоль значение результата
thenRun() - запустить другую задачу по завершению текущей
exceptionally() - обработать потенциальное исключение
 */
public class CallableRunnable {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var threadPool = Executors.newSingleThreadExecutor();
        Runnable runnable = () -> System.out.println("It's runnable task without result");
        Callable<Integer> callable = () -> {
            System.out.println("It's callable task will return summ of numbers from 1 to 100");
            return Stream.iterate(1, i -> i + 1).limit(100).mapToInt(Integer::intValue).sum();
        };
        CompletableFuture.supplyAsync(() -> {
                    System.out.println("Current thread :: " + Thread.currentThread().getName());
                    return Stream.iterate(1, i -> i + 1).limit(100).mapToInt(Integer::intValue).sum();
                }, threadPool)
                .handle((result, ex) -> {
                    if (ex != null) {
                        System.out.println("Smth was wrong = " + ex.getMessage());
                        return Integer.MIN_VALUE;
                    } else {
                        System.out.println("Sum = " + result);
                        return result;
                    }
                });

        new Thread(runnable).start();
        var sum = threadPool.submit(callable).get();
        threadPool.shutdown();
        System.out.println("Sum = " + sum);
    }
}
