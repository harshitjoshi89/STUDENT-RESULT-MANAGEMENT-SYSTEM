package com.srms.srms_app.service;

import com.srms.srms_app.entity.Student;
import com.srms.srms_app.repository.StudentRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

// NAYA IMPORT (Phase 5)
import com.srms.srms_app.service.StudentService;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelUploadService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // NAYA INJECTION (Phase 5)
    @Autowired
    private StudentService studentService;

    // Helper method to check if file is Excel
    public static boolean isValidExcelFile(MultipartFile file) {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(file.getContentType());
    }

    // --- METHOD 1: ADMIN - Bulk Student Upload ---
    // (Updated to use StudentService for calculation)
    public List<String> processStudentExcel(MultipartFile file) {
        List<String> errors = new ArrayList<>();
        int savedCount = 0;

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            int rowNumber = 0;

            // Header row skip
            if (rows.hasNext()) {
                rows.next();
                rowNumber++;
            }

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                rowNumber++;

                try {
                    // Extract cells
                    Cell rollnoCell = currentRow.getCell(0);
                    Cell nameCell = currentRow.getCell(1);
                    Cell classCell = currentRow.getCell(2);
                    Cell userCell = currentRow.getCell(3);
                    Cell passCell = currentRow.getCell(4);

                    // Basic validation
                    if (rollnoCell == null || nameCell == null || classCell == null || userCell == null || passCell == null) {
                        errors.add("Row " + rowNumber + ": Kuch cells khaali hain.");
                        continue;
                    }

                    // Get values
                    int rollno = (int) rollnoCell.getNumericCellValue();
                    String name = nameCell.getStringCellValue();
                    String classSection = classCell.getStringCellValue();
                    String username = userCell.getStringCellValue();
                    String password = passCell.getStringCellValue();

                    // Check for duplicates
                    if (studentRepository.findByUsername(username).isPresent() || studentRepository.findByRollno(rollno).isPresent()) {
                        errors.add("Row " + rowNumber + ": Rollno '" + rollno + "' ya Username '" + username + "' pehle se register hai.");
                        continue;
                    }

                    // Create student object
                    Student student = new Student();
                    student.setRollno(rollno);
                    student.setName(name);
                    student.setClassSection(classSection);
                    student.setUsername(username);
                    student.setPassword(passwordEncoder.encode(password));
                    
                    // --- YAHAN CHANGE HAI (PHASE 5) ---
                    // Directly save nahi karna hai
                    // Naye StudentService ka istemaal karein taaki Total/Average/Result (N/A) set ho
                    studentService.saveStudentWithCalculatedResults(student);
                    savedCount++;

                } catch (Exception e) {
                    errors.add("Row " + rowNumber + ": Data format galat hai. (Error: " + e.getMessage() + ")");
                }
            }

            // Final summary message
            if (savedCount > 0) {
                errors.add(0, savedCount + " students safalta se upload ho gaye."); // Add success at the top
            }
            if (errors.isEmpty()) {
                errors.add("File khaali thi ya koi data nahi mila.");
            }

        } catch (Exception e) {
            errors.clear(); // Clear previous errors
            errors.add("File padhte waqt error: " + e.getMessage());
        }

        return errors;
    }


    // --- METHOD 2: TEACHER - Bulk Marks Upload ---
    // (Updated to use StudentService for calculation)
    public List<String> processMarksExcel(MultipartFile file, String subject) {
        List<String> errors = new ArrayList<>();
        int updatedCount = 0;

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            int rowNumber = 0;

            // Header row skip
            if (rows.hasNext()) {
                rows.next();
                rowNumber++;
            }

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                rowNumber++;

                try {
                    Cell rollnoCell = currentRow.getCell(0);
                    Cell marksCell = currentRow.getCell(1);

                    if (rollnoCell == null || marksCell == null) {
                        errors.add("Row " + rowNumber + ": Rollno ya Marks cell khaali hai.");
                        continue;
                    }

                    int rollno = (int) rollnoCell.getNumericCellValue();
                    int marks = (int) marksCell.getNumericCellValue();

                    // --- YAHAN CHANGE HAI (PHASE 5) ---
                    // Poora logic StudentService mein move ho gaya hai
                    try {
                        studentService.updateStudentMarks(rollno, subject, marks);
                        updatedCount++;
                    } catch (RuntimeException e) {
                        // Agar StudentService error deta hai (jaise rollno nahi mila)
                        errors.add("Row " + rowNumber + ": " + e.getMessage());
                    }

                } catch (Exception e) {
                    errors.add("Row " + rowNumber + ": Data format galat hai. (Error: " + e.getMessage() + ")");
                }
            }

            // Final summary message
            if (updatedCount > 0) {
                errors.add(0, updatedCount + " students ke marks safalta se update ho gaye."); // Add success at the top
            }
            if (errors.isEmpty()) {
                errors.add("File khaali thi ya koi data update nahi hua.");
            }

        } catch (Exception e) {
            errors.clear(); // Clear previous errors
            errors.add("File padhte waqt error: " + e.getMessage());
        }

        return errors;
    }
}

