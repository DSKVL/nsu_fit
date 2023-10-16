package com.example.trainstationtemp;

import com.example.trainstationtemp.entity.User;
import com.example.trainstationtemp.entity.domain.Brigade;
import com.example.trainstationtemp.entity.domain.Department;
import com.example.trainstationtemp.entity.domain.Gender;
import com.example.trainstationtemp.entity.domain.Worker;
import com.example.trainstationtemp.repository.BrigadeRepository;
import com.example.trainstationtemp.repository.DepartmentRepository;
import com.example.trainstationtemp.repository.UserRepository;
import com.example.trainstationtemp.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
    public class DatabaseLoader implements CommandLineRunner {

        @Autowired WorkerRepository repository;
        @Autowired private UserRepository users;
        @Autowired private DepartmentRepository deps;
        @Autowired private BrigadeRepository brigadesRepository;

        public DatabaseLoader() {
        }

        @Override
        public void run(String... strings) throws Exception {
            var worker = new Worker("Василий", "Сережкин",
                    new Date(124124), new Date(134145435),
                    Gender.MALE, 40_000,
                    false, 0);
            var worker2 = new Worker("Василиca", "Сережкинa",
                    new Date(124124), new Date(134145435),
                    Gender.FEMALE, 40_000,
                    false, 0);
            var worker3 = new Worker("Ваааы", "ЫЫЫффааа",
                    new Date(124124), new Date(134145435),
                    Gender.FEMALE, 40_000,
                    false, 0);
            var dep = new Department();
            var brig = new Brigade();
            brig.setBrigadier(worker2);
            brig.setDepartment(dep);
            brig.setWorkers(Arrays.asList(worker, worker2));
            dep.setChief(worker);
            dep.setBrigades(List.of(brig));

            repository.save(worker);
            repository.save(worker2);

            deps.save(dep);

            worker3.setBrigade(brig);
            worker2.setBrigade(brig);
            worker.setBrigade(brig);
            brigadesRepository.save(brig);
            repository.save(worker);
            repository.save(worker2);
            repository.save(worker3);

            String password1 =  new BCryptPasswordEncoder().encode("1234");
            User user1 = new User("user", password1);
            user1.addAuthority("READ_WORKER");
            user1.addAuthority("WRITE_WORKER");
            user1.addAuthority("READ_BRIGADES");
            user1.addAuthority("WRITE_BRIGADES");
            user1.addAuthority("READ_DEPARTMENT");
            user1.addAuthority("WRITE_DEPARTMENT");
            String password2 =  new BCryptPasswordEncoder().encode("1234");
            User user2 = new User("user2", password2);
            users.save(user1);

            users.save(user2);
        }
    }