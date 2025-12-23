package pl.edu.pjwstk.byt;

import java.util.ArrayList;
import java.util.List;

public class DataAnalyst extends User implements Idataanalyst {
    int reportsGenerated;
    List<Report> reportList;

    public DataAnalyst(String username, String email) {
        super(username, email);
        this.reportsGenerated = 0;
        this.reportList = new ArrayList<Report>();
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
}
