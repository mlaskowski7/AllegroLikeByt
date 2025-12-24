package pl.edu.pjwstk.byt;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AnalystAdminTest {

    @Test
    void shouldCreateAnalystAdmin() {
        AnalystAdmin analyst = new AnalystAdmin("analyst1", "analyst@example.com");

        assertNotNull(analyst);
        assertTrue(analyst instanceof Admin); // Check inheritance
        assertTrue(analyst instanceof User);
    }

    @Test
    void shouldGenerateReports() {
        AnalystAdmin analyst = new AnalystAdmin("analyst1", "analyst@example.com");

        int initialCount = analyst.getReportsGenerated();
        analyst.generateReport("Monthly Sales");

        assertEquals(initialCount + 1, analyst.getReportsGenerated());
        assertFalse(analyst.getReportList().isEmpty());
        // Depending on list order, check implementation
    }

    @Test
    void shouldManageReportList() {
        AnalystAdmin analyst = new AnalystAdmin("analyst1", "analyst@example.com");
        Report report = new Report("Data");

        analyst.addtoReportList(report);
        assertTrue(analyst.getReportList().contains(report));
        assertEquals(1, analyst.getReportsGenerated());

        analyst.delfromReportList(report);
        assertFalse(analyst.getReportList().contains(report));
        assertEquals(0, analyst.getReportsGenerated());
    }

    @Test
    void shouldPerformAnalysisMethods() {
        AnalystAdmin analyst = new AnalystAdmin("analyst1", "analyst@example.com");
        assertDoesNotThrow(analyst::analyzeSales);
        assertDoesNotThrow(analyst::viewStatistics);
    }
}
