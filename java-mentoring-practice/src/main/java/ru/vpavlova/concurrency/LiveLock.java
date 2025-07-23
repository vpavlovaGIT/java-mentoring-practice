package main.java.ru.vpavlova.concurrency;

/*
Решить проблему LiveLock:

PS: Возможно стоит рассмотреть решение подобных ситуаций на уровне бизнес-логики. Пропускать того, кто старше и\или в алфавитном порядке.
 */
public class LiveLock {

    public static void main(String[] args) {
        var alice = new Person("Alice", true);
        var bob = new Person("Bob", true);

        new Thread(() -> alice.eatWith(bob)).start();
        new Thread(() -> bob.eatWith(alice)).start();
    }

    static class Person {
        private final String name;
        private boolean isHungry;

        public Person(String name, boolean isHungry) {
            this.name = name;
            this.isHungry = isHungry;
        }

        public void eatWith(Person person) {
            while (isHungry) {
                if (person.isHungry) {
                    // Определяем приоритет по алфавитному порядку имен
                    if (this.name.compareTo(person.name) > 0) {
                        System.out.println(name + ": You eat first, " + person.name + "!");
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException exception) {
                            Thread.currentThread().interrupt();
                        }
                        continue;
                    }
                }
                System.out.println(name + ": I'm eating now!");
                isHungry = false;
            }
        }
    }
}