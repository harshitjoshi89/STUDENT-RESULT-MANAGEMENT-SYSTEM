package com.srms.srms_app.controller;

import com.srms.srms_app.entity.Student;
import com.srms.srms_app.entity.Teacher;
import com.srms.srms_app.repository.StudentRepository;
import com.srms.srms_app.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/fa")
public class FaController {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository; // Naya Repository Autowire karein

    @GetMapping("/dashboard")
    public String faDashboard(Model model, Authentication authentication) {
        // 1. Login kiye hue FA (teacher) ka username (jaise 'fa_10a') hasil karein
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        // 2. Database se FA (teacher) ki poori details nikaalein
        Teacher fa = teacherRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("FA (Teacher) not found"));

        // 3. FA ko kaunsi class assigned hai, woh pata karein
        String assignedClass = fa.getClassAssigned();

        // 4. Us class ke saare students ko StudentRepository se dhoondein
        // Yahi hai naya method jo humne Step 1 mein banaya tha
        List<Student> students = studentRepository.findByClassSection(assignedClass);

        // 5. Details ko dashboard par bhejein
        model.addAttribute("teacher", fa); // FA ki profile details ke liye
        model.addAttribute("students", students); // Class ke students ki list ke liye
        model.addAttribute("classSection", assignedClass); // Class ka naam dikhane ke liye

        return "fa-dashboard";
    }
}
