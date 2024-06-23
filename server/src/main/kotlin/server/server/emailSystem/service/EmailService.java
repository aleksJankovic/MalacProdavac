package server.server.emailSystem.service;

import com.lowagie.text.DocumentException;
import jakarta.mail.MessagingException;
import server.server.emailSystem.EmailDetails;

import java.io.IOException;

public interface EmailService {

    void sendEmail(EmailDetails emailDetails) throws MessagingException, DocumentException, IOException;
}
