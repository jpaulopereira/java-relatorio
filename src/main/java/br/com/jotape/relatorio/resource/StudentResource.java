package br.com.jotape.relatorio.resource;

import br.com.jotape.relatorio.model.Student;
import br.com.jotape.relatorio.repository.StudentRepository;
import br.com.jotape.relatorio.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/students")
public class StudentResource {

    private StudentService studentService;
    private StudentRepository studentRepository;

    @GetMapping
    public List<Student> findAll() {
        return this.studentRepository.findAll();
    }

    @GetMapping("/report-pdf")
    public ResponseEntity<InputStreamResource> report() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=students.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(this.studentService.reportV2()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/report-xlsx")
    public ResponseEntity<ByteArrayResource> export() {
        try {
            ByteArrayResource resource = studentService.export();

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students.xlsx");

            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
