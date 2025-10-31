package com.srms.srms_app.controller;

// Zaroori imports add kiye hain
import com.srms.srms_app.service.ExcelUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin") // Sab URLs /admin se shuru honge
public class AdminController {

    // Service ko inject kiya
    @Autowired
    private ExcelUploadService excelUploadService;

    // Ye method admin dashboard page ko dikhata hai
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        // (Jo message upload ke baad aayega, woh 'model' mein automatically add ho jaayega)
        return "admin-dashboard";
    }

    // Ye naya method file upload ko handle karega
    @PostMapping("/upload-students")
    public String uploadStudents(
            @RequestParam("file") MultipartFile file, // Form se 'file' naam ka input lega
            RedirectAttributes redirectAttributes) {  // Message wapas page par bhejme ke liye

        // Check karo ki file khaali toh nahi hai
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload.");
            return "redirect:/admin/dashboard";
        }

        try {
            // Service ko call karke file process karwayi
            String summaryMessage = excelUploadService.uploadStudentsFromExcel(file);

            // Service se mila hua summary message check karo
            // Agar message mein "fail" ya "error" hai, toh use error box mein dikhao
            if (summaryMessage.toLowerCase().contains("fail") || summaryMessage.toLowerCase().contains("error")) {
                redirectAttributes.addFlashAttribute("errorMessage", summaryMessage);
            } else {
                // Warna success box mein dikhao
                redirectAttributes.addFlashAttribute("successMessage", summaryMessage);
            }

        } catch (Exception e) {
            // Agar service mein koi unexpected error aaya
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "File upload fail ho gayi: " + e.getMessage());
        }

        // Kaam poora hone ke baad, wapas dashboard par bhej do
        return "redirect:/admin/dashboard";
    }
}

