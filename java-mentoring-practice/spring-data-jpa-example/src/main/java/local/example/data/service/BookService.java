package local.example.data.service;

import local.example.data.model.Author;
import local.example.data.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final AuthorRepository authorRepository;

    @Transactional(readOnly = true)
    public List<Author> getAllAuthorsWithBooks() {
        /*
         * Текущее решение проблемы N+1: JOIN FETCH через кастомный метод findAllWithBooks() в репозитории
         *
         * Альтернативный способ №1:
         * В AuthorRepository добавить аннотацию @EntityGraph(attributePaths = "books") - загружаются книги вместе с авторами
         * List<Author> findAll() и тогда в методе getAllAuthorsWithBooks() можно использовать обычный findAll()
         *
         * Альтернативный способ №2:
         * Использовать @BatchSize;
         * Добавить в сущность Author аннотацию
         * @BatchSize(size = 10)
         * private List<Book> books;
         * или в application.yml hibernate.default_batch_fetch_size: 10
         */
        List<Author> authors = authorRepository.findAllWithBooks();

        authors.forEach(author -> {
            System.out.println("Author: " + author.getName() +
                    ", Books: " + author.getBooks().size());
        });

        return authors;
    }

    /*
     * Решения проблемы LazyInitializationException
     *
     * Текущее решение: Добавлена аннотация @Transactional держит сессию открытой, чтобы избежать LazyInitializationException
     * в дальнейшем применяется метод с JOIN FETCH
     *
     * Можно использовать @EntityGraph - аннотация @EntityGraph(attributePaths = "books")
     *
     * Паттерн Self-Inject
     * Self-Inject служит для разделения транзакций, чтобы
     * не держать одну большую транзакцию во время долгих I/O операций;
     * Реализация:
     * 1. внедряем BookService через Spring Proxy
     * @Lazy
     * private final BookService self;
     * 2. Вызываем метод через self
     * authors.forEach(self::printAuthorWithBooks);
     */
    @Transactional(readOnly = true)
    public void printAuthorsAndBooks() {
        // Используем метод с JOIN FETCH
        authorRepository.findAllWithBooks().forEach(this::printAuthorWithBooks);
    }

    @Transactional
    public void printAuthorWithBooks(Author author) {
        System.out.println("Author: " + author.getName() +
                ", Books: " + author.getBooks().size());
        author.setPrintCounter(author.getPrintCounter() + 1);
    }
}
