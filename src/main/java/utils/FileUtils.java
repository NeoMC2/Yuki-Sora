package utils;

import core.Engine;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class FileUtils {

    Engine engine;

    public FileUtils(Engine engine) {
        this.engine = engine;
    }

    public String home = System.getProperty("user.dir")+"/homeworkbothome";

    public String getHome() {
        return home;
    }

    public Object loadObject(String path) throws Exception {
        String filePath = new File(path).getAbsolutePath();
        File file = new File(filePath);
        engine.getUtilityBase().printOutput("[File loader] Loading Object Flile: " + filePath,false);
        FileInputStream stream = null;
        ObjectInputStream objStream = null;
        Object obj = null;
        if (!file.exists()) {
            engine.getUtilityBase().printOutput("The File was never created!",false);
            throw new Exception("File was never created!");
        }
        stream = new FileInputStream(file);
        objStream = new ObjectInputStream(stream);
        obj = objStream.readObject();
        objStream.close();
        stream.close();
        return obj;
    }

    public void saveObject(String path, Object obj) throws Exception {
        String filePath = new File(path).getAbsolutePath();
        File file = new File(filePath);
        engine.getUtilityBase().printOutput("[File loader] Save Object File: " + filePath,false);
        FileOutputStream stream = null;
        ObjectOutputStream objStream = null;
        createFileRootAndFile(file);
        stream = new FileOutputStream(file);
        objStream = new ObjectOutputStream(stream);
        objStream.writeObject(obj);
        objStream.flush();
        objStream.close();
        stream.close();

    }

    public JSONObject loadJsonFile(String path) throws Exception {
        File file = new File(path);
        JSONObject object;
        JSONParser parser = new JSONParser();
        try {
            Reader reader = new FileReader(file.getAbsolutePath());
            object = (JSONObject) parser.parse(reader);
            reader.close();
        } catch (Exception e) {
            if(engine.getProperties().debug){
                e.printStackTrace();
            }
            System.out.println("ERRORORORRO!!!!");
            throw new Exception("File load error");
        }
        return object;
    }

    public JSONObject convertStringToJson(String json){
        JSONObject object = null;
        JSONParser parser = new JSONParser();
        try {
            object = (JSONObject) parser.parse(json);
        } catch (ParseException e) {
            if(engine.getProperties().debug){e.printStackTrace();}
            engine.getUtilityBase().printOutput("Invalid Json",false);
        }
        return object;
    }

    public String convertJsonToString(JSONObject object){
        return object.toJSONString();
    }

    public void saveJsonFile(String path, JSONObject object){
        try {
            FileWriter fileWriter = new FileWriter(path);
            fileWriter.write(object.toJSONString());
            fileWriter.close();
        } catch (IOException e) {
            if(engine.getProperties().debug){e.printStackTrace();}
        }
    }

    private void createFileRootAndFile(File file) {
        String pas = file.getAbsolutePath().replace("\\", "/");
        String[] path = pas.split("/");
        String pat = path[0];
        for (int i = 1; i < path.length - 1; i++) {
            pat = pat + "/" + path[i];
        }
        File dir = new File(pat);
        if (!dir.mkdirs()) {
            try {
                dir.mkdirs();
            } catch (Exception e) {
                engine.getUtilityBase().printOutput("[file utils create File Dirs] cant create file dirs",true);
            }
        }
        createFiles(file);
    }

    private void createFiles(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                engine.getUtilityBase().printOutput("[file utils create File Dirs] cant create file dirs", true);
            }
        }
    }
}