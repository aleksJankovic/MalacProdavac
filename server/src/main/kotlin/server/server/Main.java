package server.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import server.server.emailSystem.pdfConverter.PDFConverter;
import server.server.service.TopPerformersService;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;

@SpringBootApplication
public class Main {
    @Autowired
    static TopPerformersService topPerformersService;
    public static void main(String[] args) throws IOException {

        //PDFConverter.generatePDF(inputFile, outputFile);
        LocalDate date = LocalDate.now();
        DayOfWeek dayOfTheWeek = date.getDayOfWeek();

        LocalDate currentWeekStartDate = date.minusDays(dayOfTheWeek.getValue() - DayOfWeek.MONDAY.getValue());
        LocalDate currentWeekEndDate = currentWeekStartDate.plusDays(6);

        LocalDate previousWeekStartDate = currentWeekStartDate.minusDays(7);
        LocalDate previousWeekEndDate = currentWeekStartDate.minusDays(1);

        System.out.println(dayOfTheWeek);
        System.out.println(currentWeekStartDate);
        System.out.println(currentWeekEndDate);
        System.out.println(previousWeekStartDate);
        System.out.println(previousWeekEndDate);

        System.out.println("Done!");
    }


}
