package com.vicode.api.apirest.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.vicode.api.apirest.Repositories.MemberRepository;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

import java.io.File;
import java.io.FileOutputStream;

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
    public Member getOneMember(@PathVariable Long id ) {
        return memberRepository.findById(id).orElseThrow(()-> new RuntimeException(id + " - Not Found"));
    }
    

     @PutMapping("/{id}")
      public Member putMember(@PathVariable Long id, @RequestBody Member memberDetail) {  
        Member member = memberRepository.findById(id).orElseThrow(()-> new RuntimeException(id + " - Not Found"));
        member.setFirsName(memberDetail.getFirsName());
        member.setLastName(memberDetail.getLastName());
        member.setMemberNumber(memberDetail.getMemberNumber());

        return memberRepository.save(member);
    } 

    @DeleteMapping("/{id}")
    public String deleteMember(@PathVariable Long id){
         Member member = memberRepository.findById(id).orElseThrow(()-> new RuntimeException(id + " - Not Found"));
         memberRepository.delete(member);
         return "Delete productId - " + id;
    }

    @GetMapping("/generate-credential/{id}")
public ResponseEntity<byte[]> generateCredential(@PathVariable Long id) {
    String QR_CODE_FOLDER = "src/main/resources/static/qr-codes/";

    try {
        // Buscar el miembro en la base de datos o donde estén almacenados los datos del miembro
        Member member = memberRepository.findById(id).orElseThrow(() -> new RuntimeException(id + " - Not Found"));

        // Cargar el contenido del archivo HTML
        ClassPathResource resource = new ClassPathResource("templates/credential_template.html");
        byte[] htmlBytes = resource.getInputStream().readAllBytes();
        String htmlTemplate = new String(htmlBytes, StandardCharsets.UTF_8);

        //Generar QR

            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            System.out.println(baseUrl + "/members/" + member.getId());
            String info = baseUrl + "/members/" + member.getId();
            BitMatrix matrix = new QRCodeWriter().encode(info, BarcodeFormat.QR_CODE, 225, 225, hints);

            // Guardar el código QR en la carpeta especificada
            String qrFileName = "qr_" + member.getId() + ".png";
            File qrFile = new File(QR_CODE_FOLDER + qrFileName);
            MatrixToImageWriter.writeToStream(matrix, "PNG", new FileOutputStream(qrFile));

        // Reemplazar los placeholders con los valores del miembro
        htmlTemplate = htmlTemplate.replace("{{memberNumber}}", String.valueOf(member.getMemberNumber()));
        htmlTemplate = htmlTemplate.replace("{{firstName}}", member.getFirsName());
        htmlTemplate = htmlTemplate.replace("{{lastName}}", member.getLastName());
        htmlTemplate = htmlTemplate.replace("{{memberId}}", String.valueOf(member.getId()));

        // Convertir el documento HTML a PDF
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlTemplate);
        renderer.layout();
        renderer.createPDF(outputStream);
        renderer.finishPDF();
        outputStream.close();
        byte[] pdfBytes = outputStream.toByteArray();

        // Devolver el PDF como respuesta
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(pdfBytes);
    } catch (IOException | DocumentException | WriterException e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError().build();
    }
}

}
