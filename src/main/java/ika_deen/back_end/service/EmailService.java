package ika_deen.back_end.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationEmail(String to, String token) {
        String verificationLink = "http://localhost:8080/api/v1/auth/verify?token=" + token;
        
        String subject = "Vérification de votre compte Ika Deen";
        String content = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #ddd; padding: 20px; border-radius: 10px;'>" +
                "<h2 style='color: #2E7D32; text-align: center;'>Bienvenue sur Ika Deen</h2>" +
                "<p>Merci de vous être inscrit. Veuillez cliquer sur le bouton ci-dessous pour vérifier votre compte :</p>" +
                "<div style='text-align: center; margin: 30px 0;'>" +
                "<a href='" + verificationLink + "' style='background-color: #2E7D32; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold;'>Vérifier mon compte</a>" +
                "</div>" +
                "<p>Si le bouton ne fonctionne pas, copiez et collez ce lien dans votre navigateur :</p>" +
                "<p style='word-break: break-all; color: #666;'>" + verificationLink + "</p>" +
                "<hr style='border: 0; border-top: 1px solid #eee; margin: 20px 0;'>" +
                "<p style='font-size: 12px; color: #888; text-align: center;'>Ce lien expirera dans 24 heures. Si vous n'avez pas créé de compte, vous pouvez ignorer cet email.</p>" +
                "</div>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }
}
