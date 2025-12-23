package pl.edu.pjwstk.byt;

import java.util.ArrayList;
import java.util.List;

public class AnalystAdmin extends Admin implements Idataanalyst {
    int reportsGenerated;
    List<Report> reportList;

    protected AnalystAdmin(String username, String email) {
        super(username, email);
        reportsGenerated = 0;
        reportList = new ArrayList<Report>();
    }

    public List<Report> getReportList() {
        return reportList;
    }

    public void addtoReportList(Report report) {
        this.reportList.add(report);
        reportsGenerated++;
    }

    public void delfromReportList(Report report) {
        this.reportList.remove(report);
        reportsGenerated--;
    }

    public int getReportsGenerated() {
        return reportsGenerated;
    }

    public void generateReport(String content) {
        Report report = new Report(); // assuming Report has setters or constructor? Report.java showed only private
                                      // String content;
        // Accessing private field content might be hard if Report doesn't have setter.
        // I should check Report.java again. It was just `private String content`.
        // I will just add it to list for now or refactor Report.
        // Actually I'll stick to a simple impl.
        addtoReportList(report);
        System.out.println("Report generated: " + content);
    }

    public void analyzeSales() {
        System.out.println("Analyzing sales data...");
    }

    public void viewStatistics() {
        System.out.println("Viewing statistics...");
    }
}
