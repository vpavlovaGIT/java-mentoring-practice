package main.java.ru.vpavlova.concurrency;

import java.util.concurrent.CompletableFuture;

/*
Создать новый пример с использованием асинхронного выполнения задачи (CompletableFuture)
Должны быть использованы след. операторы:
thenApply() - трансформация результата
thenAccept() - вывести в консоль значение результата
thenRun() - запустить другую задачу по завершению текущей
exceptionally() - обработать потенциальное исключение
 */
public class CompletableFutureExample {
    public static void main(String[] args) {
        CompletableFuture.supplyAsync(() -> {
                    double randomNumber = Math.random();
                    int number = (int) (randomNumber * 10) + 1;
                    System.out.println("Сгенерировано число: " + number);
                    // Если число больше 5 - бросаем исключение
                    if (number > 5) {
                        throw new RuntimeException("Слишком большое число: " + number);
                    }
                    return number;
                })
                // Обработка исключения
                .exceptionally(e -> {
                    System.out.println("Ошибка: " + e.getMessage());
                    return -1;
                })
                .thenApply(result -> {
                    int multiplication =  result * 2;
                    System.out.println("Умножаем число на 2: " + multiplication);
                    return multiplication;
                })
                .thenAccept(result -> {
                    System.out.println("Результат: " + result);
                })
                // Завершающее действие
                .thenRun(() -> {
                    System.out.println("Задача завершена!");
                });
        // Ожидание завершения асинхронных операций
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

