package server.server.emailSystem.pdfConverter;

import com.lowagie.text.DocumentException;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;
import server.server.dtos.response.InoviceOrderResponse;
import server.server.utils.FormatDateCustomUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class PDFConverter {
    public static byte[] generatePDFInvoice(InoviceOrderResponse inoviceOrder){
        try {
            //Setovanje template-a

            ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
            templateResolver.setPrefix("templates/");
            templateResolver.setSuffix(".html");
            templateResolver.setTemplateMode(TemplateMode.HTML);
            templateResolver.setCharacterEncoding("UTF-8");

            SpringTemplateEngine templateEngine = new SpringTemplateEngine();
            templateEngine.setTemplateResolver(templateResolver);

            org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
            //Order
            context.setVariable("orderNumber", inoviceOrder.getOrderNumber());
            context.setVariable("orderDate", FormatDateCustomUtil.formatDate(inoviceOrder.getOrderDate()));
            //Buyer
            context.setVariable("buyerName", inoviceOrder.getBuyerName() + " " + inoviceOrder.getBuyerSurname());
            context.setVariable("buyerAddress", inoviceOrder.getBuyerAddress());
            context.setVariable("buyerEmail", inoviceOrder.getBuyerEmail());
            context.setVariable("buyerPhoneNumber", inoviceOrder.getBuyerPhoneNumber());
            //Seller
            context.setVariable("sellerName", inoviceOrder.getSellerName() + " " + inoviceOrder.getSellerSurname());
            context.setVariable("sellerAddress", inoviceOrder.getSellerAddress());
            context.setVariable("sellerEmail", inoviceOrder.getSellerEmail());
            //Items
            context.setVariable("items", inoviceOrder.getOrderItemsList());
            //Total
            context.setVariable("total", inoviceOrder.calculateTotalInvoiceAmount());

            String htmlContent = templateEngine.process("invoice", context);

            //Konvertoranje HTML-a u PDF format
            ITextRenderer textRenderer = new ITextRenderer();

            textRenderer.setDocumentFromString(htmlContent);
            textRenderer.layout();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            textRenderer.createPDF(outputStream);
            outputStream.close();

            System.out.println("PDF je uspešno generisan.");

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] generatePDFUplatnica(InoviceOrderResponse inoviceOrder){
        try {
            //Setovanje template-a
            ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
            templateResolver.setPrefix("templates/");
            templateResolver.setSuffix(".html");
            templateResolver.setTemplateMode(TemplateMode.HTML);

            SpringTemplateEngine templateEngine = new SpringTemplateEngine();
            templateEngine.setTemplateResolver(templateResolver);

            org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
            context.setVariable("orderNumber", inoviceOrder.getOrderNumber());
//            //Buyer
            context.setVariable("buyerName", inoviceOrder.getBuyerName() + " " + inoviceOrder.getBuyerSurname());
            context.setVariable("buyerAddress", inoviceOrder.getBuyerAddress());

//            //Seller
            context.setVariable("sellerName", inoviceOrder.getSellerName() + " " + inoviceOrder.getSellerSurname());
            context.setVariable("sellerAddress", inoviceOrder.getSellerAddress());
//            //Items
            String s = String.valueOf(inoviceOrder.getOrderItemsList().size()) + "221 - " + inoviceOrder.getOrderNumber() + " - " + "849" ;
            context.setVariable("number", s);
//            //Total
            context.setVariable("total", inoviceOrder.calculateTotalInvoiceAmount());
            context.setVariable("account", inoviceOrder.getSellerAccountNumber());

            String htmlContent = templateEngine.process("uplatnica", context);

            ITextRenderer textRenderer = new ITextRenderer();

            textRenderer.setDocumentFromString(htmlContent);
            textRenderer.layout();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            textRenderer.createPDF(outputStream);
            outputStream.close();

            System.out.println("PDF je uspešno generisan.");

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

}
