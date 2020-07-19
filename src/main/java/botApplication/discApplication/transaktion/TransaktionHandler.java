package botApplication.discApplication.transaktion;

import botApplication.discApplication.librarys.transaktion.job.Job;
import com.google.gson.JsonObject;
import core.Engine;
import org.json.simple.JSONObject;

import java.nio.IntBuffer;
import java.util.ArrayList;

public class TransaktionHandler {

    Engine engine;

    public TransaktionHandler(Engine engine) {
        this.engine = engine;
    }

    public ArrayList<Job> parseJsonToJobs(JSONObject object){
        ArrayList<Job> jobs = new ArrayList<>();
        Object[] set = object.keySet().toArray();
        for (int i = 0; i < set.length; i++) {
            JSONObject o = (JSONObject) object.get((String) set[i]);
            Job j = new Job();
            j.setJobName((String) o.get("name"));
            j.setShortName((String) set[i]);
            j.setEarningTrainee(Integer.parseInt((String) o.get("etrainee")));
            j.setEarningCoWorker(Integer.parseInt((String) o.get("ecoworker")));
            j.setEarningHeadOfDepartment(Integer.parseInt((String) o.get("ehead")));
            j.setEarningManager(Integer.parseInt((String) o.get("emanager")));
            j.setDoing((String) o.get("doing"));

            jobs.add(j);
        }
        return jobs;
    }
}
