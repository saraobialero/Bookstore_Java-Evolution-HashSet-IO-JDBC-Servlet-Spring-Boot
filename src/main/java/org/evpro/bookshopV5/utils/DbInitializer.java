package org.evpro.bookshopV5.utils;

import org.evpro.bookshopV5.model.Role;
import org.evpro.bookshopV5.model.User;
import org.evpro.bookshopV5.model.enums.RoleCode;
import org.evpro.bookshopV5.repository.RoleRepository;
import org.evpro.bookshopV5.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DbInitializer implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if (!roleRepository.existsByRoleCode(RoleCode.ROLE_USER)) {
            roleRepository.save(Role.builder().role(RoleCode.ROLE_USER).build());
        }
        if (!roleRepository.existsByRoleCode(RoleCode.ROLE_ADMIN)) {
            roleRepository.save(Role.builder().role(RoleCode.ROLE_ADMIN).build());
        }
        Role roleAdmin = roleRepository.findByRoleCode(RoleCode.ROLE_ADMIN).orElseThrow();
        if(userRepository.findByEmail("test@mail.it").isEmpty()) {
            userRepository.save(User.builder()
                    .email("test@mail.com")
                    .password(passwordEncoder.encode("psw1234"))
                    .name("Test")
                    .surname("LastName")
                    .roles(List.of(roleAdmin))
                    .build());
        }
    }
}
