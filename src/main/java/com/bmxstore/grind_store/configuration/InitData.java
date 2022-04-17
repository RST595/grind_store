package com.bmxstore.grind_store.configuration;

import com.bmxstore.grind_store.data.entity.user.UserEntity;
import com.bmxstore.grind_store.data.entity.user.UserRole;
import com.bmxstore.grind_store.data.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Configuration
@AllArgsConstructor
public class InitData {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepo userRepo;

//    @Bean
    public void initUserDB(){
        userRepo.saveAll(IntStream.rangeClosed(1, 50)
                .mapToObj(i -> new UserEntity("Firstname" + i, "Lastname" + i, "Address" + i,
                        "email" + i, UserRole.USER, bCryptPasswordEncoder.encode("string" + i)))
                .collect(Collectors.toList()));
    }
}
