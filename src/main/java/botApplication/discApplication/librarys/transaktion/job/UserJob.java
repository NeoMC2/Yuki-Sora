package botApplication.discApplication.librarys.transaktion.job;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

public class UserJob extends Job implements Serializable {

    private static final long serialVersionUID = 42L;

    private JobRank jobRank;
    private int jobXp;
    private int jobLevel;

    private boolean lvlUp;
    private boolean positionUp;

    public enum JobRank {
        Trainee, CoWorker, HeadOfDepartment, Manager
    }

    public int work() throws Exception {
        if(jobRank == JobRank.Trainee){
            jobXp+=1;
            updateJobLevel();
            return getEarningTrainee();
        }
        if(jobRank == JobRank.CoWorker){
            jobXp+=1;
            updateJobLevel();
            return getEarningCoWorker();
        }
        if(jobRank == JobRank.HeadOfDepartment){
            jobXp+=1;
            updateJobLevel();
            return getEarningHeadOfDepartment();
        }
        if(jobRank == JobRank.Manager){
            jobXp+=1;
            updateJobLevel();
            return getEarningManager();
        }
        throw new Exception("No job rank");
    }

    private void updateJobLevel(){
        if(jobRank == JobRank.Trainee){
            if(jobXp >= 10){
                jobLevel ++;
                jobXp -= 10;
                lvlUp = true;
                if(jobXp >= 10)
                    updateJobLevel();
            }

            if(jobLevel >= 5){
                jobRank = JobRank.CoWorker;
                positionUp = true;
                if(jobLevel >= 5)
                    updateJobLevel();
            }
        }

        if(jobRank == JobRank.CoWorker){
            if(jobXp >= 30){
                jobLevel ++;
                jobXp -= 30;
                lvlUp = true;
                if(jobXp >= 30)
                    updateJobLevel();
            }

            if(jobLevel >= 30){
                jobRank = JobRank.HeadOfDepartment;
                positionUp = true;
                if(jobLevel >= 30)
                    updateJobLevel();
            }
        }

        if(jobRank == JobRank.HeadOfDepartment){
            if(jobXp >= 50){
                jobLevel ++;
                jobXp -= 50;
                lvlUp = true;
                if(jobXp >= 50)
                    updateJobLevel();
            }

            if(jobLevel >= 100){
                jobRank = JobRank.Manager;
                positionUp = true;
            }
        }
    }

    public boolean doTraining(int xp){
        if(ThreadLocalRandom.current().nextInt(0, 5) > 1){
            jobXp += xp;
            updateJobLevel();
            return true;
        } else {
            jobXp += (xp/2);
            updateJobLevel();
            return false;
        }
    }

    public void setJob(Job j){
        setEarningCoWorker(j.getEarningCoWorker());
        setEarningHeadOfDepartment(j.getEarningHeadOfDepartment());
        setEarningManager(j.getEarningManager());
        setEarningTrainee(j.getEarningTrainee());
        setJobName(j.getJobName());
        setShortName(j.getShortName());
        setDoing(j.getDoing());
    }

    public String jobRankToString(JobRank r){
        if(r == JobRank.Trainee){
            return "Trainee";
        }

        if(r == JobRank.CoWorker){
            return "Co Worker";
        }

        if(r == JobRank.HeadOfDepartment){
            return "Head of department";
        }

        if(r == JobRank.Manager){
            return "Manager";
        }
        return null;
    }

    public JobRank stringToJobRank(String s){
        switch (s.toLowerCase().replace(" ", "")){
            case "trainee":
                return JobRank.Trainee;

            case "coworker":
            case "worker":
                return JobRank.CoWorker;

            case "head":
            case "headofdepartment":
            case "departmenthead":
                return JobRank.HeadOfDepartment;

            case "manager":
            case "bos":
                return JobRank.Manager;
        }
        return null;
    }

    public JobRank getJobRank() {
        return jobRank;
    }

    public void setJobRank(JobRank jobRank) {
        this.jobRank = jobRank;
    }

    public int getJobXp() {
        return jobXp;
    }

    public void setJobXp(int jobXp) {
        this.jobXp = jobXp;
    }

    public int getJobLevel() {
        return jobLevel;
    }

    public void setJobLevel(int jobLevel) {
        this.jobLevel = jobLevel;
    }

    public boolean isLvlUp() {
        if (lvlUp) {
            lvlUp = false;
            return true;
        } else {
            return false;
        }
    }

    public boolean isPositionUp() {
        if(positionUp){
            positionUp = false;
            return true;
        } else {
            return false;
        }
    }
}
