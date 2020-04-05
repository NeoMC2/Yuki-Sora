package core;

import botApplication.discApplication.core.DiscApplicationEngine;
import botApplication.response.ResponseHandler;
import utils.FileUtils;
import utils.Properties;
import utils.UtilityBase;

public class Engine {

    private final String consMsgDef = "[Engine]";

    FileUtils fileUtils = new FileUtils(this);
    UtilityBase utilityBase = new UtilityBase(this);
    Properties properties;

    DiscApplicationEngine discApplicationEngine = new DiscApplicationEngine(this);
    ResponseHandler responseHandler = new ResponseHandler(this);

    public void boot(String[] args) {
        new ConsoleCommandHandler(this);
        loadProperties();
        handleArgs(args);
        new Thread(new SaveThread(this)).start();
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
        System.exit(0);
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
}
