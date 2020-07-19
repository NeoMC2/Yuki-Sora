package botApplication.discApplication.librarys.transaktion.job;

import java.io.Serializable;

public class Job implements Serializable {

    private static final long serialVersionUID = 42L;

    private int earningTrainee;
    private int earningCoWorker;
    private int earningHeadOfDepartment;
    private int earningManager;
    private String jobName;
    private String doing;
    private String shortName;

    public int getEarningTrainee() {
        return earningTrainee;
    }

    public void setEarningTrainee(int earningTrainee) {
        this.earningTrainee = earningTrainee;
    }

    public int getEarningCoWorker() {
        return earningCoWorker;
    }

    public void setEarningCoWorker(int earningCoWorker) {
        this.earningCoWorker = earningCoWorker;
    }

    public int getEarningHeadOfDepartment() {
        return earningHeadOfDepartment;
    }

    public void setEarningHeadOfDepartment(int earningHeadOfDepartment) {
        this.earningHeadOfDepartment = earningHeadOfDepartment;
    }

    public int getEarningManager() {
        return earningManager;
    }

    public void setEarningManager(int earningManager) {
        this.earningManager = earningManager;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDoing() {
        return doing;
    }

    public void setDoing(String doing) {
        this.doing = doing;
    }
}
