package com.paperforge.service;

import com.paperforge.config.ResourceLimitsConfig;
import com.paperforge.model.Role;
import com.paperforge.model.User;
import com.paperforge.repository.RoleRepository;
import com.paperforge.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UnlimitedUsersScaleTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ResourceLimitsConfig resourceLimitsConfig;

    @Test
    @Transactional
    void testLargeBatchUserCreationScalability() {
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

        int batchSize = 100;
        List<User> userBatch = new ArrayList<>();

        for (int i = 0; i < batchSize; i++) {
            User user = new User("scale_user_" + i, "scale_user_" + i + "@paperforge.local", "hashedPassword123");
            user.setStorageQuotaBytes(resourceLimitsConfig.getDefaultUserQuotaBytes());
            user.getRoles().add(userRole);
            userBatch.add(user);
        }

        userRepository.saveAll(userBatch);

        assertTrue(userRepository.count() >= batchSize);
        assertTrue(userRepository.findByUsername("scale_user_50").isPresent());
        assertEquals(resourceLimitsConfig.getDefaultUserQuotaBytes(), userRepository.findByUsername("scale_user_50").get().getStorageQuotaBytes());
    }
}
