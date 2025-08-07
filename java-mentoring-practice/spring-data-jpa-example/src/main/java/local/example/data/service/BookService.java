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
        // Решение проблемы N+1 через использование метода findAllWithBooks() с JOIN FETCH
        List<Author> authors = authorRepository.findAllWithBooks();

        authors.forEach(author -> {
            System.out.println("Author: " + author.getName() +
                    ", Books: " + author.getBooks().size());
        });

        return authors;
    }

    // Добавлена аннотация @Transactional иначе возникнет ошибка LazyInitializationException
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
