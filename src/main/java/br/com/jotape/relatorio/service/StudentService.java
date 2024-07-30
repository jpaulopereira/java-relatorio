package br.com.jotape.relatorio.service;

import br.com.jotape.relatorio.model.Student;
import br.com.jotape.relatorio.repository.StudentRepository;
import br.com.jotape.relatorio.util.DateUtils;
import br.com.jotape.relatorio.util.ReportUtils;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class StudentService {

    private StudentRepository studentRepository;

    public ByteArrayInputStream reportV1() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos));
        pdfDocument.setDefaultPageSize(PageSize.A4.rotate());

        Document document = new Document(pdfDocument);

        Paragraph title = new Paragraph("Students List")
                .setFontSize(28)
                .setFont(PdfFontFactory.createFont(StandardFonts.COURIER_BOLD))
                .setTextAlignment(TextAlignment.CENTER);

        document.add(title);
        document.add(new Paragraph("\n"));

        Table table = new Table(6)
                .useAllAvailableWidth()
                .setTextAlignment(TextAlignment.CENTER);

        table.addHeaderCell("NAME");
        table.addHeaderCell("EMAIL");
        table.addHeaderCell("AGE");
        table.addHeaderCell("BIRTHDAY");
        table.addHeaderCell("SCHOOL");
        table.addHeaderCell("CREATED AT");

        this.studentRepository.findAll()
                .stream()
                .sorted(Comparator
                        .comparing((Student s) -> s.getSchool().getName())
                        .thenComparing(Student::getName))
                .forEach(student -> {
                    table.addCell(student.getName());
                    table.addCell(student.getEmail());
                    table.addCell(String.valueOf(DateUtils.age(student.getBirthday())));
                    table.addCell(DateUtils.format(student.getBirthday(), "dd/MM/yyyy"));
                    table.addCell(student.getSchool().getName());
                    table.addCell(DateUtils.format(student.getCreatedAt(), "dd/MM/yyyy HH:mm"));
                });

        document.add(table);
        document.close();

        return new ByteArrayInputStream(baos.toByteArray());
    }

    public ByteArrayInputStream reportV2() throws IOException {
        ReportUtils report = ReportUtils.getInstance();
        report.setPageSize(PageSize.A4.rotate());

        report.addParagraph(new Paragraph("Students List")
                .setFontSize(28)
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(PdfFontFactory.createFont(StandardFonts.COURIER_BOLD))
        );

        report.addNewLine();
        report.openTable(6);
        report.addTableHeader("NAME", "EMAIL", "AGE", "BIRTHDAY", "SCHOOL", "CREATED AT");

        this.studentRepository.findAll()
                .stream()
                .sorted(Comparator
                        .comparing((Student s) -> s.getSchool().getName())
                        .thenComparing(Student::getName))
                .forEach(student -> {
                    report.addTableColumn(student.getName());
                    report.addTableColumn(student.getEmail());
                    report.addTableColumn(DateUtils.age(student.getBirthday()));
                    report.addTableColumn(DateUtils.format(student.getBirthday(), "dd/MM/yyyy"));
                    report.addTableColumn(student.getSchool().getName());
                    report.addTableColumn(DateUtils.format(student.getCreatedAt(), "dd/MM/yyyy HH:mm"));
                });

        report.closeTable();
        report.closeDocument();

        return report.getByteArrayInputStream();
    }

    public ByteArrayResource export() throws IOException {
        List<Student> list = studentRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        String nomeDaAba = WorkbookUtil.createSafeSheetName("Relatório");
        Sheet aba = workbook.createSheet(nomeDaAba);

        var headerFields = Arrays.asList(
                "NAME", "EMAIL", "AGE", "BIRTHDAY", "SCHOOL", "CREATED AT"
        );

        Row rowHeader = aba.createRow(0);

        for (int i = 0; i < headerFields.size(); i++) {
            Cell headerCell = rowHeader.createCell(i);
            headerCell.setCellValue(headerFields.get(i));
        }

        int rowIndex = 1;
        for (Student student : list) {
            Row userRow = aba.createRow(rowIndex++);

            Cell idStudent = userRow.createCell(0);
            idStudent.setCellValue(student.getId());

            Cell name = userRow.createCell(1);
            name.setCellValue(student.getName());

            Cell email = userRow.createCell(2);
            email.setCellValue(student.getEmail());

            Cell birthday = userRow.createCell(3);
            birthday.setCellValue(DateUtils.format(student.getBirthday(), "dd/MM/yyyy"));

            Cell school = userRow.createCell(4);
            school.setCellValue(student.getSchool().getName());

            Cell createdAt = userRow.createCell(5);
            createdAt.setCellValue(DateUtils.format(student.getCreatedAt(),"dd/MM/yyyy"));
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        workbook.write(stream);
        workbook.close();

        return new ByteArrayResource(stream.toByteArray());
    }
}
