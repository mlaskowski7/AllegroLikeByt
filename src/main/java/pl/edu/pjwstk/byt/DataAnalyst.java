package pl.edu.pjwstk.byt;

import java.util.ArrayList;
import java.util.List;

public class DataAnalyst extends User implements Idataanalyst {
    int reportsGenerated;
    List<Report> reportList;

    public DataAnalyst(String username, String email) {
        super(username, validateEmail(email));
        this.reportsGenerated = 0;
        this.reportList = new ArrayList<Report>();
    }
    private static String validateEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$";

        if (!email.matches(regex)) {
            throw new IllegalArgumentException("Invalid email address: " + email);
        }

        return email;
    }

    public List<Report> getReportList() {
        return reportList;
    }

    public void addToReportList(Report report) {
        this.reportList.add(report);
        reportsGenerated++;
    }

    /**
     * @deprecated Use {@link #addToReportList(Report)} instead.
     */
    @Deprecated
    public void addtoReportList(Report report) {
        addToReportList(report);
    }

    public void deleteFromReportList(Report report) {
        if (this.reportList.remove(report)) {
            reportsGenerated--;
        }
    }

    /**
     * @deprecated Use {@link #deleteFromReportList(Report)} instead.
     */
    @Deprecated
    public void delfromReportList(Report report) {
        deleteFromReportList(report);
    }


    public int getReportsGenerated() {
        return reportsGenerated;
    }
}
