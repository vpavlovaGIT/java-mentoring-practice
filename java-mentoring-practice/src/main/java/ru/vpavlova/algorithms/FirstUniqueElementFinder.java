package main.java.ru.vpavlova.algorithms;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Найти первый неповторяющийся элемент в массиве целых (положительных) чисел
 * Обработать следующие входные данные:
 * input = {9, 4, 9, 6, 7, 4, 5} - массив корректный
 * input = {} - пустой массив
 * input = {1} - массив с одним элементом
 * input = {5, 5, 5, 5, 5, 5, 5} все элементы одинаковые
 * + еще один кейс
 * input = {1, 1, 2, 2, 3, 3} - массив содержит только пары чисел
 */
public class FirstUniqueElementFinder {
    int getFirstUnique(int[] arr) {
        // Ввод: массив
        // Вывод: целое число
        // создаем цикл и проходимся по элементам массива
        // используем хэш мапу, где ключ - элемент массива
        // значение - счетчик - количество повторяющихся элементов
        // 9-2, 4-2, 6-1, 7-1, 5-1
        Map<Integer, Integer> ewk = new LinkedHashMap<>();
        int result;
        if (arr == null || arr.length == 0) {
            return -1;
        } // Обработка пустого массива
        // Ошиблась в написании length + перечисления в for идут через ";"
        for (int i = 0; i<arr.length; i++) {
            //Integer key = arr[i];
            Integer counter = ewk.getOrDefault(arr[i], 0);
            counter++;
            ewk.put(arr[i], counter);
        }
        // 9-2
        // 4-2
        // 6-1
        // 7-1
        // 5-1
        for (Map.Entry<Integer, Integer> entry : ewk.entrySet()) {
            // не указала в методах getValue() и getKey() скобки () на конце
            if (entry.getValue()==1) {
                // вернула return в if для вывода первого неповторяющегося элемента
               return result = entry.getKey();
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        FirstUniqueElementFinder solution = new FirstUniqueElementFinder();

        int[]  input1 = {9, 4, 9, 6, 7, 4, 5};
        System.out.println("Первый уникальный элемент: " + solution.getFirstUnique(input1));

        int[]  input2 = {};
        System.out.println("Результат обработки пустого массив: " + solution.getFirstUnique(input2));

        int[]  input3 = {1};
        System.out.println("Массив с одним элементом: " + solution.getFirstUnique(input3));

        int[]  input4 = {5, 5, 5, 5, 5, 5, 5};
        System.out.println("Результат обработки массива в котором все элементы одинаковые: " + solution.getFirstUnique(input4));

        int[]  input5 = {1, 1, 2, 2, 3, 3};
        System.out.println("Результат обработки массива только с парами чисел: " + solution.getFirstUnique(input5));
    }
}
