package com.example.demo;

import com.example.demo.models.*;
import com.example.demo.repository.*;
import com.example.demo.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LearningManagementSystemApplicationTests {

	@Test
	void contextLoads() {
	}


	/*@Mock
	private LessonRepository lessonRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private CourseRepository courseRepository;

	@Mock
	private CourseRepository attendanceRepository;

	@InjectMocks
	private AttendanceService attendanceService;

	@InjectMocks
	private LessonService lessonService;

	@InjectMocks
	private UserService userService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}


	@Test
	void testGenerateOtp() {
		Long lessonId = 1L;
		String courseId = "course1";
		UserRole role = UserRole.INSTRUCTOR;
		Lesson lesson = new Lesson(lessonId, courseId, "Lesson 1", 1L);

		when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
		when(attendanceRepository.otpExists(lesson)).thenReturn(false);
		when(userRepository.findById(lesson.getInstructorId())).thenReturn(Optional.of(new User()));

		String otp = attendanceService.generateOtp(lessonId, courseId, role);

		assertNotNull(otp);
		verify(attendanceRepository, times(1)).saveOtp(any(Lesson.class), any(User.class), anyString());
	}

	@Test
	void testValidateAndMarkAttendance() {
		Long lessonId = 1L;
		Long studentId = 1L;
		String otp = "123456";
		UserRole role = UserRole.STUDENT;
		Lesson lesson = new Lesson(lessonId, "course1", "Lesson 1", 1L);
		User student = new User(studentId, "student1", "password", "email", UserRole.STUDENT, null, null);

		when(userRepository.findById(studentId)).thenReturn(Optional.of(student));
		when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
		when(attendanceRepository.validateOtp(lesson, otp)).thenReturn(true);
		when(attendanceRepository.findAll()).thenReturn(List.of());

		boolean result = attendanceService.validateAndMarkAttendance(lessonId, studentId, otp, role);

		assertTrue(result);
		verify(attendanceRepository, times(1)).markAttendance(any(Lesson.class), any(User.class));
	}

	@Test
	void testCreateLesson() {
		String courseId = "course1";
		String title = "Lesson 1";
		UserRole role = UserRole.INSTRUCTOR;
		Long instructorId = 1L;
		Course course = new Course(courseId, "Course 1", "Description", 10, instructorId);
		User instructor = new User(instructorId, "instructor1", "password", "email", UserRole.INSTRUCTOR, null, null);

		when(userRepository.findById(instructorId)).thenReturn(Optional.of(instructor));
		when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
		when(lessonRepository.existsByTitleAndCourseId(title, courseId)).thenReturn(false);

		lessonService.createLesson(courseId, title, role, instructorId);

		verify(lessonRepository, times(1)).save(courseId, title, instructorId);
		verify(courseRepository, times(1)).save(any(Course.class));
	}

	@Test
	void testAddUser() {
		User user = new User(1L, "user1", "password", "email", UserRole.STUDENT, null, null);

		when(userRepository.findByUsername(user.getUsername())).thenReturn(null);
		when(userRepository.existsById(user.getId())).thenReturn(false);

		String response = userService.addUser(user);

		assertEquals("User added successfully", response);
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	void testGetUserProfile() {
		String username = "user1";
		User user = new User(1L, username, "password", "email", UserRole.STUDENT, null, null);

		when(userRepository.findByUsername(username)).thenReturn(user);

		User result = userService.getUserProfile(username);

		assertEquals(user, result);
	}*/

}
