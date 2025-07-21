package main.java.ru.vpavlova.stream;

import java.util.List;

public class FlatMapReduceExample {
    public static void main(String[] args) {
        List<List<Integer>> numbers = List.of(
                List.of(1, 2, 3),
                List.of(4, 5),
                List.of(6, 7, 8, 9)
        );
        // flatMap: объединение всех списков в один поток
        // reduce: вычисление суммы всех чисел
        int sumNumbers = numbers.stream()
                .flatMap(list -> list.stream())
                .reduce(0, Integer::sum);

        System.out.println("Сумма всех чисел: " + sumNumbers);

    }
}
