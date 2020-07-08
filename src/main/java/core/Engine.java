package core;

import botApplication.discApplication.core.DiscApplicationEngine;
import botApplication.response.ResponseHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utils.FileUtils;
import utils.Properties;
import utils.UtilityBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class Engine {

    private final String consMsgDef = "[Engine]";

    private FileUtils fileUtils = new FileUtils(this);
    private UtilityBase utilityBase = new UtilityBase(this);
    private Properties properties;

    private DiscApplicationEngine discApplicationEngine = new DiscApplicationEngine(this);
    private ResponseHandler responseHandler = new ResponseHandler(this);

    public java.util.Properties lang;
    public JSONObject pics;

    public void boot(String[] args) {
        loadLanguage();
        loadProperties();
        handleArgs(args);
        new Thread(new SaveThread(this)).start();
        loadPics();
        new ConsoleCommandHandler(this);
    }

    private void handleArgs(String[] args) {
        if (args.length > 0) {
            switch (args[0]) {
                case "test":
                    break;
                case "start":
                    discApplicationEngine.startBotApplication();
                    break;
            }
        }
    }

    public void loadLanguage(){
        getUtilityBase().printOutput(consMsgDef + " !Load language!", false);
        lang = new java.util.Properties();
        String filePath = new File(fileUtils.home + "/lang/lang.prop").getAbsolutePath();
        File file = new File(filePath);
        getUtilityBase().printOutput("[File loader] Loading Object Flile: " + filePath,false);
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            getUtilityBase().printOutput(consMsgDef + " !!!Error loading language, not found!!!", false);
        }
        try {
            lang.load(stream);
        } catch (IOException e) {
            getUtilityBase().printOutput(consMsgDef + " !!!Error loading language!!!", false);
        }
    }

    public void loadPics(){
        getUtilityBase().printOutput(consMsgDef + " !Load pics!", false);
        try {
            pics = fileUtils.loadJsonFile(fileUtils.home + "/pics/pics.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
        utilityBase.printOutput(pics.toJSONString(), true);
    }

    public void loadProperties() {
        utilityBase.printOutput(consMsgDef + " !Loading properties!", false);
        try {
            properties = (Properties) fileUtils.loadObject(fileUtils.home + "/properties.prop");
        } catch (Exception e) {
            e.printStackTrace();
            utilityBase.printOutput(consMsgDef + " !!!Error while loading properties - maybe never created -> creating new file!!!",false);
            properties = new Properties();
        }
    }

    public void saveProperties() {
        utilityBase.printOutput(consMsgDef + " !Saving properties!",false);
        try {
            fileUtils.saveObject(fileUtils.home + "/properties.prop", properties);
        } catch (Exception e) {
            if (properties.debug) {
                e.printStackTrace();
            }
            utilityBase.printOutput(consMsgDef + " !!!Error while saving properties - maybe no permission!!!",false);
        }
    }

    public void shutdown() {
        saveProperties();
        discApplicationEngine.getFilesHandler().saveAllBotFiles();
        System.exit(0);
    }

    public String lang(String phrase, String langg){
        if(langg==null || langg == ""){
            langg="en";
        }
        String t = lang.getProperty(langg + "." + phrase);
        if(t == ""||t==null){
            t = lang.getProperty("en" + "." + phrase);
        }
        if(t == ""||t==null){
            return "```@languageSupportError```";
        }
        return t.replace("\\n", "\n");
    }

    public FileUtils getFileUtils() {
        return fileUtils;
    }

    public UtilityBase getUtilityBase() {
        return utilityBase;
    }

    public Properties getProperties() {
        return properties;
    }

    public DiscApplicationEngine getDiscEngine() {
        return discApplicationEngine;
    }

    public ResponseHandler getResponseHandler() {
        return responseHandler;
    }

    public JSONObject getPics() {
        return pics;
    }
}

