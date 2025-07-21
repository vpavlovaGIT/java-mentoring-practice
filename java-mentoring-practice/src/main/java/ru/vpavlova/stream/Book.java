package main.java.ru.vpavlova.stream;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Book {
    String title;
    String author;
    int year;
    double price;

    public Book(String title, String author, int year, double price) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.price = price;
    }

    @Override
    public String toString() {
        return title + " (" + author + ", " + year + ") - $" + price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return year == book.year &&
                Double.compare(book.price, price) == 0 &&
                Objects.equals(title, book.title) &&
                Objects.equals(author, book.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, year, price);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        List<Book> books = List.of(
                new Book("1984", "Джордж Оруэлл", 1949, 12.99),
                new Book("Хоббит", "Джон Толкин", 1937, 15.50),
                new Book("Хоббит", "Джон Толкин", 1937, 15.50),
                new Book("Великий Гэтсби", "Фрэнсис Скотт Фицджеральд", 1925, 9.99),
                new Book("Унесённые ветром", "Маргарет Митчелл", 1936, 14.99),
                new Book("Мастер и Маргарита", "Михаил Булгаков", 1967, 11.99),
                new Book("Мастер и Маргарита", "Михаил Булгаков", 1967, 11.99),
                new Book("Сто лет одиночества", "Габриэль Гарсиа Маркес", 1967, 16.99)
        );
        List<String> resultBooks = books.stream()
                .distinct()
                .filter(b -> b.year > 1950)
                .peek(b -> System.out.println("Оставлены книги после 1950 года: " + b))
                .sorted((b1, b2) -> Double.compare(b2.price, b1.price))
                .peek(b -> System.out.println("После сортировки по убыванию цены: " + b))
                .map(b -> b.title.toUpperCase() + " (" + b.author + ", " + b.year)
                .collect(Collectors.toList());

        System.out.println("Вывод финального списка: " + resultBooks);
    }
}
