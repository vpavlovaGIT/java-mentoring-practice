package main.java.ru.vpavlova.javacore;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Починить ConcurrentModificationException в многопоточной среде исполнения
 */
public class CollectionPerformance {
    public static void main(String[] args) {
        failFast();
    }

    // Использование CopyOnWriteArrayList
    private static void failFast() {
        List<String> list = new CopyOnWriteArrayList<>();
        list.add("Apple");
        list.add("Banana");
        list.add("Cherry");

        // Обычный ArrayList не потокобезопасен.
        // Когда поток main добавлял элементы, другой - t1 пытался их удалять,
        // поэтому и возникла ошибка ConcurrentModificationException
        Thread t1 = new Thread(() -> {
            for (String s : list) {
                if (s.equals("Cherry")) {
                    list.remove(s);
                }
            }
            System.out.println("После удаления: " + list);
        });
        t1.start();
    }
}