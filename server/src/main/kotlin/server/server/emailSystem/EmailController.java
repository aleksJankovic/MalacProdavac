package server.server.emailSystem;

import com.lowagie.text.DocumentException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.server.emailSystem.service.EmailService;

import java.io.IOException;

@RestController
@RequestMapping("/email")
public class EmailController {
    @Autowired
    EmailService emailService;

    @PostMapping("/send")
    public String sendEmail(@RequestBody EmailDetails emailDetails) throws MessagingException, DocumentException, IOException {
        emailService.sendEmail(emailDetails);
        return "Poslat mail";
    }
}
