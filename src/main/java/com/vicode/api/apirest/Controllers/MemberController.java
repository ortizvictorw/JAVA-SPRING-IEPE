package com.vicode.api.apirest.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.vicode.api.apirest.Repositories.MemberRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.DocumentException;
import com.vicode.api.apirest.Entities.Member;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/members")
public class MemberController {

    @Value("${base.url}")
    private String baseUrl;

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @PostMapping
    public Member postMember(@RequestBody Member member) {
        return memberRepository.save(member);
    }

    @GetMapping("/{id}")
    public Member getOneMember(@PathVariable Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new RuntimeException(id + " - Not Found"));
    }

    @PutMapping("/{id}")
    public Member putMember(@PathVariable Long id, @RequestBody Member memberDetail) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new RuntimeException(id + " - Not Found"));
    
        member.setFirstName(memberDetail.getFirstName());
        member.setLastName(memberDetail.getLastName());
        member.setMemberNumber(memberDetail.getMemberNumber());
        member.setImageBase64(memberDetail.getImageBase64());
        member.setDateOfBirth(memberDetail.getDateOfBirth()); // Ajuste para fecha de nacimiento
        member.setAddress(memberDetail.getAddress()); // Ajuste para dirección
        member.setPosition(memberDetail.getPosition()); // Ajuste para cargo
        member.setActivity(memberDetail.getActivity()); // Ajuste para actividad
        member.setDateOfJoiningChurch(memberDetail.getDateOfJoiningChurch()); // Ajuste para fecha de ingreso a la iglesia
        member.setDateOfBaptism(memberDetail.getDateOfBaptism()); // Ajuste para fecha de bautismo
        member.setStatus(memberDetail.getStatus()); // Ajuste para estado
    
        return memberRepository.save(member);
    }
    
    @DeleteMapping("/{id}")
    public Member deleteMember(@PathVariable Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new RuntimeException(id + " - Not Found"));
        memberRepository.delete(member);
        return member;
    }

    @GetMapping("/generate-credential/{id}")
    public ResponseEntity<byte[]> generateCredential(@PathVariable Long id) {
        try {
            // Buscar el miembro en la base de datos o donde estén almacenados los datos del
            // miembro
            Member member = memberRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException(id + " - Not Found"));

            // Generar el código QR en memoria
            String info = baseUrl + "/members/" + member.getId();
            byte[] qrBytes = generateQRCode(info);

            // Obtener la imagen en formato base64 desde la base de datos
            String imageBytes = member.getImageBase64();

            // Cargar el contenido del archivo HTML
            String htmlTemplate = loadHtmlTemplate("templates/credential_template.html");

            // Reemplazar los placeholders con los valores del miembro en el HTML
            htmlTemplate = replacePlaceholders(htmlTemplate, member, qrBytes, imageBytes);

            // Convertir el documento HTML a PDF
            byte[] pdfBytes = generatePDF(htmlTemplate);

            // Devolver el PDF como respuesta
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    private byte[] generateQRCode(String info) throws WriterException, IOException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix matrix = new QRCodeWriter().encode(info, BarcodeFormat.QR_CODE, 225, 225, hints);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream);
        return outputStream.toByteArray();
    }

    private String loadHtmlTemplate(String templatePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(templatePath);
        byte[] htmlBytes = resource.getInputStream().readAllBytes();
        return new String(htmlBytes, StandardCharsets.UTF_8);
    }

    private String replacePlaceholders(String htmlTemplate, Member member, byte[] qrBytes, String imageBytes) {
        String memberNumber = member.getMemberNumber() != null ? String.valueOf(member.getMemberNumber()) : "";
        String firstName = member.getFirstName() != null ? member.getFirstName() : "";
        String lastName = member.getLastName() != null ? member.getLastName() : "";
        String memberId = member.getId() != null ? String.valueOf(member.getId()) : "";
        String qrCode = qrBytes != null ? "data:image/png;base64," + Base64.getEncoder().encodeToString(qrBytes) : "";
        String image = imageBytes != null ? imageBytes : "";

        return htmlTemplate
                .replace("{{memberNumber}}", memberNumber)
                .replace("{{firstName}}", firstName)
                .replace("{{lastName}}", lastName)
                .replace("{{memberId}}", memberId)
                .replace("{{qrCode}}", qrCode)
                .replace("{{image}}", image)
                .replace("{{title}}", member.getFirstName() + member.getLastName());

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
