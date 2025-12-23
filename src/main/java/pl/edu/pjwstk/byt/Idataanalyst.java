package pl.edu.pjwstk.byt;
import java.util.List;

public interface Idataanalyst {
    int reportsGenerated = 0;
    List<Report> reportList = null;
    public List<Report> getReportList();

    public void addtoReportList(Report report);
    public void delfromReportList(Report report);

    public int getReportsGenerated();
}
