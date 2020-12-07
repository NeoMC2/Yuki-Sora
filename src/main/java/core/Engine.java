package core;

import botApplication.discApplication.core.DiscApplicationEngine;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.item.Item;
import botApplication.discApplication.librarys.item.collectables.gems.*;
import botApplication.discApplication.librarys.item.collectables.metal.*;
import botApplication.discApplication.librarys.item.collectables.stuff.*;
import botApplication.discApplication.utils.NetworkManager;
import botApplication.response.ResponseHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utils.FileUtils;
import utils.Properties;
import utils.UtilityBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class Engine {

    private final String consMsgDef = "[Engine]";
    private final FileUtils fileUtils = new FileUtils(this);
    private final UtilityBase utilityBase = new UtilityBase(this);
    private final DiscApplicationEngine discApplicationEngine = new DiscApplicationEngine(this);
    private final ResponseHandler responseHandler = new ResponseHandler(this);
    public JSONObject lang;
    public JSONObject pics;
    private Properties properties;
    private final NetworkManager networkManager = new NetworkManager(this);

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
                    discApplicationEngine.startBotApplication();
                    Iterator<DiscApplicationUser> t = discApplicationEngine.getFilesHandler().getUsers().values().iterator();

                    DiscApplicationUser us = t.next();
                    if (us.getItems().size() > 0) {
                        Diamond diamond;
                        try {
                            diamond = (Diamond) us.getItems().get(0);
                        } catch (Exception e) {
                            System.out.println("Experiment failed!");
                            return;
                        }
                        us.getItems().remove(diamond);
                        System.out.println("Expermiment done!");
                    } else {
                        Diamond diamond = new Diamond();
                        us.getItems().add(diamond);
                        System.out.println("Started experiment");
                    }
                    break;

                case "reform":
                    JSONObject o = null;
                    try {
                        o = getFileUtils().loadJsonFile(getFileUtils().home + "/transactions/monsters.json");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Object[] ob = o.keySet().toArray();

                    for (Object oS:ob){
                        String s = (String) oS;
                        JSONObject mnster = (JSONObject) o.get(s);

                        for (Object oSS:ob){
                            String ss = (String) oSS;
                            JSONObject mnsterr = (JSONObject) o.get(ss);

                            String ev = (String) mnsterr.get("ev");
                            if(ev!=null){
                                String[] evos = ev.split(",");
                                if(evos.length>1) {
                                    for (String evosS:evos){
                                        if(s.equals(evosS)){
                                            mnster.put("shown", "false");
                                            System.out.println(s);
                                        }
                                    }
                                } else {
                                    if(ev.equals(s)){
                                        mnster.put("shown", "false");
                                        System.out.println(s);
                                    }
                                }
                            }
                        }
                    }

                    getFileUtils().saveJsonFile(getFileUtils().home +"/transactions/monsters.json", o);
                    break;
                case "start":
                    discApplicationEngine.startBotApplication();
                    break;

                case "up":
                    ArrayList<JSONObject> attacks = new ArrayList<>();
                    ArrayList<JSONObject> mnsters = new ArrayList<>();
                    JSONObject obj = null;
                    try {
                        obj = getFileUtils().loadJsonFile(getFileUtils().home + "/transactions/monsters.json");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Object[] obb = obj.keySet().toArray();

                    for (Object oS:obb){
                        String s = (String) oS;
                        JSONObject newMnster = new JSONObject();
                        JSONObject mnster = (JSONObject) obj.get(s);
                        JSONArray atts = (JSONArray) mnster.get("attacks");

                        newMnster.put("name", s);
                        newMnster.put("imageUrl", mnster.get("img"));
                        newMnster.put("baseHp", Integer.parseInt((String) mnster.get("hp")));
                        //do test yk
                        try {
                            int evlvl = Integer.parseInt((String) mnster.get("evlvl"));
                            newMnster.put("evolveLvl", evlvl);
                        } catch (Exception ignored){
                        }
                        try {
                            String ss = (String) mnster.get("shown");
                            if (ss != null)
                                if (ss.equals("false"))
                                    newMnster.put("shown", false);
                                else
                                    newMnster.put("shown", true);
                        } catch (Exception e) {
                            newMnster.put("shown", true);
                        }

                        mnsters.add(newMnster);
                        for (Object at:atts) {
                            JSONObject att = (JSONObject) at;
                            JSONObject newAt = new JSONObject();
                            newAt.put("baseDmg", Integer.parseInt((String) att.get("dmg")));
                            newAt.put("attackName", att.get("name"));
                            newAt.put("level", Integer.parseInt((String) att.get("lvl")));
                            newAt.put("maxUsage", Integer.parseInt((String)att.get("usage")));
                            String st = (String) att.get("state");
                            if(st!=null)
                            newAt.put("statusEffects", st);
                            attacks.add(newAt);
                        }
                    }
                    ArrayList<JSONObject> remove = new ArrayList<>();
                    for (JSONObject attack : attacks) {
                        for (JSONObject attack1: attacks) {
                            if (attack.get("attackName").equals(attack1.get("attackName")) && attack != attack1)
                                remove.add(attack1);
                        }
                    }

                    attacks.removeAll(remove);

                    System.out.println("Attacks: \n");
                    for (JSONObject ao:attacks) {
                        System.out.println(ao.toJSONString());
                        //System.out.println(networkManager.post("https://yuki.mindcollaps.de/api/yuki/attack", ao.toJSONString(), apiToken));
                        System.out.println("\n\n");
                    }
                    for (JSONObject ao:mnsters) {
                        System.out.println(ao.toJSONString());
                        //System.out.println(networkManager.post("https://yuki.mindcollaps.de/api/yuki/monster", ao.toJSONString(), apiToken));
                        System.out.println("\n\n");
                    }
                    break;

                case "up2":
                    ArrayList<Item> gems = new ArrayList<>();

                    gems.add(new Diamond());
                    gems.add(new Emerald());
                    gems.add(new Ruby());
                    gems.add(new Sapphire());
                    ArrayList<Item> metals = new ArrayList<>();

                    metals.add(new AluminiumOre());
                    metals.add(new CopperOre());
                    metals.add(new GoldOre());
                    metals.add(new IronOre());
                    metals.add(new PlatinumOre());
                    metals.add(new SilverOre());
                    ArrayList<Item> stuffs = new ArrayList<>();

                    stuffs.add(new Cable());
                    stuffs.add(new Cobweb());
                    stuffs.add(new Feather());
                    stuffs.add(new Glass());
                    stuffs.add(new MetalScrew());
                    stuffs.add(new Stick());
                    stuffs.add(new Tape());

                    loopStuff(gems);
                    loopStuff(metals);
                    loopStuff(stuffs);
                    break;
            }
        }
    }

    private void loopStuff(ArrayList<Item> list){
        for(Item it : list){
            try {
                //System.out.println(networkManager.post("https://yuki.mindcollaps.de/api/yuki/item", makeJsonFromItem(it).toJSONString(), apiToken));
            } catch (Exception e) {
                System.out.println("Skipping " + it.getItemName());
            }
        }
    }

    private JSONObject makeJsonFromItem(Item it) throws Exception{
        JSONObject o = new JSONObject();
        o.put("imageUrl", it.getImgUrl());
        o.put("itemName", it.getItemName());
        o.put("itemDescription", it.getDescription());
        o.put("itemRarity", it.getItemRarity().name().toLowerCase());
        o.put("isitemCookable", false);
        o.put("itemCanFound", true);
        String type = "";
        if(it instanceof Gem)
            type = "gem";
        else if(it instanceof Metal)
            type = "metal";
        else if (it instanceof Stuff)
            type = "stuff";
        else
            throw new Exception("not working");

        o.put("itemType", type);
        return o;
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
        if (lang == null) {
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

