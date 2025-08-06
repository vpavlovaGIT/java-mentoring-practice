package local.example.data.service;

import local.example.data.model.Author;
import local.example.data.repository.AuthorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    private final AuthorRepository authorRepository;

    public BookService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Transactional(readOnly = true)
    public List<Author> getAllAuthorsWithBooks() {
        List<Author> authors = authorRepository.findAll();

        authors.forEach(author -> {
            System.out.println("Author: " + author.getName() +
                    ", Books: " + author.getBooks().size());
        });

        return authors;
    }

    public void printAuthorsAndBooks() {
        List<Author> authors = authorRepository.findAll();

        authors.forEach(this::printAutorWithBooks);
    }

    @Transactional
    public void printAutorWithBooks(Author author) {
        System.out.println("Author: " + author.getName() +
                ", Books: " + author.getBooks().size());
        author.setPrintCounter(author.getPrintCounter() + 1);
    }
}
