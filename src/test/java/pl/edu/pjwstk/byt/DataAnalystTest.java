package pl.edu.pjwstk.byt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataAnalystTest {

    private DataAnalyst dataAnalyst;

    @BeforeEach
    void setUp() {
        dataAnalyst = new DataAnalyst("test", "bobtest@gmail.com");
    }

    @Test
    void shouldThrowExceptionForInvalidEmail() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new DataAnalyst("user", "not_email")
        );
        assertTrue(exception.getMessage().contains("Invalid email"));
    }

    @Test
    void shouldAddReport() {
        Report report = new Report();
        dataAnalyst.addToReportList(report);

        List<Report> reports = dataAnalyst.getReportList();
        assertEquals(1, reports.size());
        assertEquals(1, dataAnalyst.getReportsGenerated());
        assertTrue(reports.contains(report));
    }

    @Test
    void shouldRemoveReport() {
        Report report = new Report();
        dataAnalyst.addToReportList(report);
        dataAnalyst.deleteFromReportList(report);

        List<Report> reports = dataAnalyst.getReportList();
        assertEquals(0, reports.size());
        assertEquals(0, dataAnalyst.getReportsGenerated());
    }

    @Test
    void removingNonExistingReportShouldNotDecreaseCounter() {
        Report report = new Report();
        dataAnalyst.deleteFromReportList(report);

        assertEquals(0, dataAnalyst.getReportsGenerated());
    }
}
