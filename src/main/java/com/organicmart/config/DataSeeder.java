package com.organicmart.config;

import com.organicmart.entity.Category;
import com.organicmart.entity.Role;
import com.organicmart.entity.User;
import com.organicmart.repository.CategoryRepository;
import com.organicmart.repository.RoleRepository;
import com.organicmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Seeds the four base roles, the 11 product categories, and a default Admin
 * account on first startup, so the application is usable immediately even
 * if schema.sql was never run manually (Hibernate's ddl-auto=update only
 * creates empty tables - it does not run the INSERT seed statements in
 * schema.sql, so this seeder covers that gap).
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        for (String roleName : new String[]{"ROLE_ADMIN", "ROLE_FARMER", "ROLE_CUSTOMER", "ROLE_DELIVERY"}) {
            roleRepository.findByName(roleName).orElseGet(() ->
                    roleRepository.save(Role.builder().name(roleName).build()));
        }

        if (categoryRepository.count() == 0) {
            Map<String, String> categories = new LinkedHashMap<>();
            categories.put("Grains", "fa-wheat-awn");
            categories.put("Pulses", "fa-seedling");
            categories.put("Spices", "fa-pepper-hot");
            categories.put("Dry Fruits", "fa-tree");
            categories.put("Honey", "fa-jar");
            categories.put("Herbal Products", "fa-leaf");
            categories.put("Organic Oils", "fa-bottle-droplet");
            categories.put("Seeds", "fa-seedling");
            categories.put("Tea", "fa-mug-hot");
            categories.put("Coffee", "fa-mug-saucer");
            categories.put("Natural Grocery Products", "fa-basket-shopping");

            for (Map.Entry<String, String> entry : categories.entrySet()) {
                String slug = entry.getKey().toLowerCase().replace(" ", "-");
                categoryRepository.save(Category.builder()
                        .name(entry.getKey())
                        .slug(slug)
                        .iconClass(entry.getValue())
                        .active(true)
                        .build());
            }
        }

        if (!userRepository.existsByEmail("admin@organicmart.com")) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);

            User admin = User.builder()
                    .fullName("OrganicMart Admin")
                    .email("admin@organicmart.com")
                    .mobileNumber("9999999999")
                    .password(passwordEncoder.encode("Admin@123"))
                    .enabled(true)
                    .profileCompleted(true)
                    .roles(roles)
                    .build();
            userRepository.save(admin);
        }
    }
}
