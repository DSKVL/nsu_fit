package ru.nsu.fit.dskvl.trainstation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseLoader implements CommandLineRunner {
    private final WorkerRepository repository;

    @Autowired
    public DatabaseLoader(WorkerRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        this.repository.save(new Worker("Валерий", "Сережкин"));
    }
}
