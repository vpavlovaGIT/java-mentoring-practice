package main.java.ru.vpavlova.javacore;

import java.util.ArrayList;
import java.util.List;

/**
 * Починить ConcurrentModificationException в многопоточной среде исполнения
 */
public class CollectionPerformance {
    public static void main(String[] args) {
//        addElementsPerformance();
        failFast();
    }

    private static void addElementsPerformance() {
        var arr = new ArrayList<Integer>();
        var start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            arr.add(0, i);
        }
        System.out.println("ArrayList time taken: " + (System.currentTimeMillis() - start) + " ms");

        var linkedList = new java.util.LinkedList<Integer>();
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            linkedList.add(0, i);
        }
        System.out.println("LinkedList time taken: " + (System.currentTimeMillis() - start) + " ms");
    }

    private static void failFast() {
        List<String> list = new ArrayList<>();
        list.add("Apple");
        list.add("Banana");
        list.add("Cherry");

        Thread t1 = new Thread(() -> {
            for(String s : list) {
                if(s.equals("Cherry")) {
                    list.remove(s);
                }
            }
        });
        t1.start();
    }
}