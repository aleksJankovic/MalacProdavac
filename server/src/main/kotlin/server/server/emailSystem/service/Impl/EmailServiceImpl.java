package server.server.emailSystem.service.Impl;

import com.lowagie.text.DocumentException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import server.server.emailSystem.EmailDetails;
import server.server.emailSystem.pdfConverter.PDFConverter;
import server.server.emailSystem.service.EmailService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

@Service
public class EmailServiceImpl implements EmailService {
    private static int brojac = 0;
    @Value("${spring.mail.username}")
    String sender;
    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.port}")
    private String port;
    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;
    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String mailPropertiesAuth;
    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String mailPropertiesStarttls;

    @Override
    @Async
    public void sendEmail(EmailDetails emailDetails) throws MessagingException, DocumentException, IOException {
        JavaMailSenderImpl javaMailSender = setJavaMailSenderConfig();

        if(javaMailSender == null)
            return;

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        //Setovanje dinamickih vrednosti unutar mail-a
        Context context = new Context();
        context.setVariable("orderNumber", emailDetails.getInoviceOrderResponse().getOrderNumber());

        MimeMessage mimeMailMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMailMessage, true);

        //Setovanje porudzbenice
        byte[] pdfBytes = PDFConverter.generatePDFInvoice(emailDetails.getInoviceOrderResponse());
        ByteArrayResource byteArrayResource = new ByteArrayResource(pdfBytes);


        helper.setTo(emailDetails.getRecipient());
        helper.setSubject(emailDetails.getSubject());
        helper.addAttachment("porudzbenica.pdf", byteArrayResource);
        helper.setText(templateEngine.process("emailTemplate", context), true);
        System.out.println("Setovan attacment");

        if(emailDetails.getInoviceOrderResponse().getPaymentMethodId() == 1L){
            byte[] pdfUplatnica = PDFConverter.generatePDFUplatnica(emailDetails.getInoviceOrderResponse());
            ByteArrayResource byteArrayResource1 = new ByteArrayResource(pdfUplatnica);

            helper.addAttachment("uplatnica.pdf", byteArrayResource1);
        }

        try {
            javaMailSender.send(mimeMailMessage);

            System.out.println("Mail uspesno poslat na " + emailDetails.getRecipient() + " adresu");

        }
        catch (Exception ex){
            ex.getMessage();
        }
    }

    private JavaMailSenderImpl setJavaMailSenderConfig(){
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost("smtp.gmail.com");
        javaMailSender.setPort(587);
        javaMailSender.setUsername("malacprodavac.official@gmail.com");
        javaMailSender.setPassword("ciafiudfndvbwgir"); //2-step verification app password

        Properties properties = javaMailSender.getJavaMailProperties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.starttls.required", "true");

        return javaMailSender;
    }
}
