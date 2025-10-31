package com.srms.srms_app.config;

import com.srms.srms_app.entity.Student;
import com.srms.srms_app.entity.Teacher;
import com.srms.srms_app.repository.StudentRepository;
import com.srms.srms_app.repository.TeacherRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor Injection
    public DataLoader(TeacherRepository teacherRepository, StudentRepository studentRepository, PasswordEncoder passwordEncoder) {
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("DataLoader is running...");

        // 1. Create Admin User (Password: adminpass)
        if (teacherRepository.findByUsername("admin").isEmpty()) {
            Teacher admin = new Teacher();
            admin.setTeacherId(1); // Set ID
            admin.setName("Admin User");
            admin.setEmail("admin@srms.edu");
            admin.setContactNo("9000000001");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("adminpass")); // Hashed password
            admin.setRole("ROLE_ADMIN");
            teacherRepository.save(admin);
            System.out.println("Created new ADMIN user: admin");
        } else {
             System.out.println("User 'admin' already exists. Skipping creation.");
        }

        // 2. Create Teacher User (Password: adminpass)
        if (teacherRepository.findByUsername("teacher_phy").isEmpty()) {
            Teacher physicsTeacher = new Teacher();
            physicsTeacher.setTeacherId(2); // Set ID
            physicsTeacher.setName("Physics Teacher");
            physicsTeacher.setEmail("physics.teacher@srms.edu");
            physicsTeacher.setContactNo("9000000002");
            physicsTeacher.setSubject("Physics");
            physicsTeacher.setUsername("teacher_phy");
            physicsTeacher.setPassword(passwordEncoder.encode("adminpass")); // Hashed password
            physicsTeacher.setRole("ROLE_TEACHER");
            teacherRepository.save(physicsTeacher);
            System.out.println("Created new TEACHER user: teacher_phy");
        }

        // 3. Create FA User (Password: adminpass)
        if (teacherRepository.findByUsername("fa_10a").isEmpty()) {
            Teacher fa = new Teacher();
            fa.setTeacherId(3); // Set ID
            fa.setName("FA for 10-A");
            fa.setEmail("fa.10a@srms.edu");
            fa.setContactNo("9000000003");
            fa.setUsername("fa_10a");
            fa.setPassword(passwordEncoder.encode("adminpass")); // Hashed password
            fa.setRole("ROLE_FA");
            fa.setClassAssigned("10-A");
            teacherRepository.save(fa);
            System.out.println("Created new FA user: fa_10a");
        }

        // 4. Create Student User (Password: studentpass)
        if (studentRepository.findByUsername("student1").isEmpty()) {
            Student student = new Student();
            student.setRollno(101); // Set ID
            student.setName("Student One");
            student.setClassSection("10-A");

            // --- YAHAN SE CHANGES HAIN ---
            int maths = 75;
            int physics = 80;
            int chemistry = 85;
            int total = maths + physics + chemistry;
            float average = total / 3.0f; // 3.0f zaroori hai decimal ke liye
            String result = (average >= 33) ? "Pass" : "Fail"; // Simple pass/fail logic

            student.setMaths(maths);
            student.setPhysics(physics);
            student.setChemistry(chemistry);
            student.setTotal(total);
            student.setAverage(average);
            student.setResult(result);
            // --- YAHAN TAK CHANGES HAIN ---

            student.setUsername("student1");
            student.setPassword(passwordEncoder.encode("studentpass")); // Hashed password
            studentRepository.save(student);
            System.out.println("Created new STUDENT user: student1 (with calculated results)"); // Message update kar diya
        } else {
            System.out.println("User 'student1' already exists. Skipping creation.");
        }

        System.out.println("DataLoader finished.");
    }
}

