package com.vicode.api.apirest.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.vicode.api.apirest.Repositories.ListRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import com.lowagie.text.DocumentException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.vicode.api.apirest.Entities.ListEntity;

@RestController
@RequestMapping("/list")
public class ListController {

    @Value("${base.url}")
    private String baseUrl;

    @Autowired
    private ListRepository listRepository;

    @GetMapping
    public List<ListEntity> getAllMembers() {
        return listRepository.findAll();
    }

    @PostMapping
    public ListEntity postMember(@RequestBody ListEntity list) {
        return listRepository.save(list);
    }

    @GetMapping("/{id}")
    public ListEntity getOneMember(@PathVariable Long id) {
        return listRepository.findById(id).orElseThrow(() -> new RuntimeException(id + " - Not Found"));
    }

    @PutMapping("/{id}")
    public ListEntity putMember(@PathVariable Long id, @RequestBody ListEntity listDetail) {
        ListEntity list = listRepository.findById(id).orElseThrow(() -> new RuntimeException(id + " - Not Found"));

        return listRepository.save(list);
    }
    
    @DeleteMapping("/{id}")
    public ListEntity deleteMember(@PathVariable Long id) {
        ListEntity list = listRepository.findById(id).orElseThrow(() -> new RuntimeException(id + " - Not Found"));
        listRepository.delete(list);
        return list;
    }

    @GetMapping("/generate-list/{id}")
    public ResponseEntity<byte[]> generateCredential(@PathVariable Long id) {
        try {
            // Buscar el miembro en la base de datos o donde estén almacenados los datos del
            // miembro
            ListEntity list = listRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException(id + " - Not Found"));

            // Cargar el contenido del archivo HTML
            String htmlTemplate = loadHtmlTemplate("templates/list_template.html");

            // Reemplazar los placeholders con los valores del miembro en el HTML
            htmlTemplate = replacePlaceholders(htmlTemplate, list);

            // Convertir el documento HTML a PDF
            byte[] pdfBytes = generatePDF(htmlTemplate);

            // Devolver el PDF como respuesta
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    private String loadHtmlTemplate(String templatePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(templatePath);
        byte[] htmlBytes = resource.getInputStream().readAllBytes();
        return new String(htmlBytes, StandardCharsets.UTF_8);
    }

    private String replacePlaceholders(String htmlTemplate, ListEntity list) {
        String day = list.getDay() != null ? list.getDay() : "";
        String keyboard1 = list.getKeyboard1() != null ? list.getKeyboard1() : "";
        String keyboard2 = list.getKeyboard2() != null ? list.getKeyboard2() : "";
        String guitar1 = list.getGuitar1() != null ? list.getGuitar1() : "";
        String guitar2 = list.getGuitar2() != null ? list.getGuitar2() : "";
        String bass = list.getBass() != null ? list.getBass() : "";
        String drums = list.getDrums() != null ? list.getDrums() : "";
        String date = list.getDate() != null ? list.getDate().toString() : "";


        return htmlTemplate
                .replace("{{day}}", day)
                .replace("{{keyboard1}}", keyboard1)
                .replace("{{keyboard2}}", keyboard2)
                .replace("{{guitar1}}", guitar1)
                .replace("{{guitar2}}", guitar2)
                .replace("{{bass}}", bass)
                .replace("{{drums}}", drums)
                .replace("{{date}}", date);
    
    }
    
    private byte[] generatePDF(String htmlTemplate) throws DocumentException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlTemplate);
        renderer.layout();
        renderer.createPDF(outputStream);
        renderer.finishPDF();
        outputStream.close();
        return outputStream.toByteArray();
    }

}
