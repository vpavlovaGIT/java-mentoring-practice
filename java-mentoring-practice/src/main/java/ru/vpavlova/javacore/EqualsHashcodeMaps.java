package main.java.ru.vpavlova.javacore;

import java.util.Objects;

/*
   Поправить ошибки, связанные с equals/hashCode
 */
public class EqualsHashcodeMaps {
    public static void main(String[] args) {
        var set = new java.util.HashSet<Person>();
        var person = new Person(18, "Petya");
        set.add(person);
        person.setAge(19);
        System.out.println("Person: " + set.contains(person));
    }

    static class Person {
        int age;
        String name;

        public Person(int age, String name) {
            this.age = age;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Person person = (Person) o;
            return age == person.age && Objects.equals(name, person.name);
        }

        @Override
        public int hashCode() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}