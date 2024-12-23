package com.example.demo.service;

import com.example.demo.models.Course;
import com.example.demo.models.CourseDTO;
import com.example.demo.models.User;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class UserService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CourseRepository courseRepository;
    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder , CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.courseRepository = courseRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Loading user by username: " + username);
        Optional<User> userDetail = Optional.ofNullable(userRepository.findByUsername(username));

        return userDetail.map(UserDetailsService::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public String addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User added successfully";
    }

    public User getUserProfile(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return user;
    }

    public boolean userIdExists(Long id) {
        return userRepository.existsById(id);
    }

    public boolean userEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // src/main/java/com/example/demo/service/UserService.java
    public boolean userExists(String username) {
        return userRepository.findByUsername(username) != null;
    }

    public Collection<User> listAllUsers() {
        return userRepository.findAll();
    }

    public void updateUser(Long id, User user) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found");
        }
        user.setId(id);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public List<Course> getEnrolledCourses(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        List<CourseDTO> courseDTOs = student.getCourses();
        List<Course> courses = new ArrayList<>();

        for (CourseDTO courseDTO : courseDTOs) {
            courseRepository.findById(courseDTO.getId()).ifPresent(courses::add);
        }

        return courses;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}