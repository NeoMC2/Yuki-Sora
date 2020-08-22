package core;

import botApplication.discApplication.core.DiscApplicationEngine;
import botApplication.discApplication.utils.NetworkManager;
import botApplication.response.ResponseHandler;
import org.json.simple.JSONObject;
import utils.FileUtils;
import utils.Properties;
import utils.UtilityBase;

import java.util.Set;

public class Engine {

    private final String consMsgDef = "[Engine]";
    public JSONObject lang;
    public JSONObject pics;
    private final FileUtils fileUtils = new FileUtils(this);
    private final UtilityBase utilityBase = new UtilityBase(this);
    private Properties properties;
    private final DiscApplicationEngine discApplicationEngine = new DiscApplicationEngine(this);
    private final ResponseHandler responseHandler = new ResponseHandler(this);
    private NetworkManager networkManager = new NetworkManager(this);

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

    private void convertPropertiesToLangJson() {
        JSONObject jsonObject = new JSONObject();
        JSONObject ger = new JSONObject();
        JSONObject eng = new JSONObject();

        jsonObject.put("de", ger);
        jsonObject.put("en", eng);

        //lang should be a prop file
        Set<Object> langKey = lang.keySet();

        for (Object l : langKey) {
            String s = (String) l;
            if (s.startsWith("de")) {
                ger.put(s.substring(3), lang.get(s));
            }

            if (s.startsWith("en")) {
                eng.put(s.substring(3), lang.get(s));
            }
        }

        fileUtils.saveJsonFile(fileUtils.home + "/lang/lang.json", jsonObject);
    }

    public void loadLanguage() {
        getUtilityBase().printOutput(consMsgDef + " !Load language!", false);
        try {
            lang = fileUtils.loadJsonFile(fileUtils.home + "/lang/lang.json");
        } catch (Exception e) {
            getUtilityBase().printOutput(consMsgDef + " !!!Error loading language, not found!!!", false);
        }
    }

    public void loadPics() {
        getUtilityBase().printOutput(consMsgDef + " !Load pics!", false);
        try {
            pics = fileUtils.loadJsonFile(fileUtils.home + "/pics/pics.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadProperties() {
        utilityBase.printOutput(consMsgDef + " !Loading properties!", false);
        try {
            properties = (Properties) fileUtils.loadObject(fileUtils.home + "/properties.prop");
        } catch (Exception e) {
            e.printStackTrace();
            utilityBase.printOutput(consMsgDef + " !!!Error while loading properties - maybe never created -> creating new file!!!", false);
            properties = new Properties();
        }
    }

    public void saveProperties() {
        utilityBase.printOutput(consMsgDef + " !Saving properties!", false);
        try {
            fileUtils.saveObject(fileUtils.home + "/properties.prop", properties);
        } catch (Exception e) {
            if (properties.debug) {
                e.printStackTrace();
            }
            utilityBase.printOutput(consMsgDef + " !!!Error while saving properties - maybe no permission!!!", false);
        }
    }

    public void shutdown() {
        saveProperties();
        discApplicationEngine.getFilesHandler().saveAllBotFiles();
        System.exit(0);
    }

    public String lang(String phrase, String langg, String[] arg) {
        if(lang == null){
            return "```@languageSupportError```";
        }
        if (langg == null || langg.equals("")) {
            langg = "en";
        }
        String t = (String) ((JSONObject) lang.get(langg)).get(phrase);
        if (t != null)
            if (t.equals("")) {
                t = (String) ((JSONObject) lang.get("en")).get(phrase);
            }
        try {
            t = langC(t, arg);
        } catch (Exception e) {
        }
        if (t != null)
            if (t.equals("")) {
                return "```@languageSupportError```";
            }
        if (t == null)
            return "```@languageSupportError```";
        return t.replace("\\n", "\n");
    }

    private String langC(String p, String[] arg) {
        String newString = p;
        char[] pArr = p.toCharArray();
        for (int i = 0; i < pArr.length; i++) {
            char c = pArr[i];
            if (c == '%') {
                int j = Integer.parseInt(String.valueOf(pArr[i + 1]));
                for (int k = i + 1; k < pArr.length; k++) {
                    char cc = pArr[k];
                    if (cc == '%') {
                        try {
                            newString = langC(p.substring(0, i) + arg[j - 1] + p.substring(k + 1), arg);
                        } catch (Exception e) {
                            utilityBase.printOutput("[Language Support] !!!Error in parsing lang variables!!!", true);
                        }
                        break;
                    }
                }
                break;
            }
        }
        return newString;
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

    public NetworkManager getNetworkManager() {
        return networkManager;
    }
}

