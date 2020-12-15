package botApplication.discApplication.utils;

import core.Engine;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ApiManager {

    private String apiToken;
    private Engine engine;
    private final String api = "https://yuki.mindcollaps.de/api/yuki";

    public ApiManager(Engine engine) {
        JSONObject se = null;
        try {
            se = engine.getFileUtils().loadJsonFile(engine.getFileUtils().home + "/secret.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
        apiToken = (String) se.get("secret");
        this.engine = engine;
    }

    public JSONObject getServerById(String id){
        JSONObject req = new JSONObject();
        req.put("sid", id);
        return engine.getFileUtils().convertStringToJson(engine.getNetworkManager().post(api + "/getServer", req.toJSONString(), apiToken));
    }

    public JSONObject getUserBy(String id){
        JSONObject req = new JSONObject();
        req.put("id", id);
        return engine.getFileUtils().convertStringToJson(engine.getNetworkManager().post(api + "/getUser", req.toJSONString(), apiToken));
    }

    public JSONObject getUserInventoryById(String id){
        JSONObject req = new JSONObject();
        req.put("id", id);
        return engine.getFileUtils().convertStringToJson(engine.getNetworkManager().post(api + "/getUserInventory", req.toJSONString(), apiToken));
    }

    public JSONObject getUserMonstersById(String id){
        JSONObject req = new JSONObject();
        req.put("id", id);
        return engine.getFileUtils().convertStringToJson(engine.getNetworkManager().post(api + "/getUserMonsters", req.toJSONString(), apiToken));
    }

    public JSONObject getUserMonsterByIds(String uid, String mid){
        JSONObject req = new JSONObject();
        req.put("id", uid);
        req.put("mid", mid);
        return engine.getFileUtils().convertStringToJson(engine.getNetworkManager().post(api + "/getUserMonster", req.toJSONString(), apiToken));
    }

    public JSONObject dmgOnMonster(String uid, String mid, long dmg){
        JSONObject req = new JSONObject();
        req.put("id", uid);
        req.put("mid", mid);
        req.put("dmg", dmg);
        return engine.getFileUtils().convertStringToJson(engine.getNetworkManager().post(api + "/dmgOnMonster", req.toJSONString(), apiToken));
    }

    public JSONObject xpOnMonster(String uid, String mid, long xp){
        JSONObject req = new JSONObject();
        req.put("id", uid);
        req.put("mid", mid);
        req.put("xp", xp);
        return engine.getFileUtils().convertStringToJson(engine.getNetworkManager().post(api + "/xpOnMonster", req.toJSONString(), apiToken));
    }

    public JSONObject addItemToUser(String uidItem, String id, int amount){
        JSONObject req = new JSONObject();
        req.put("id", id);
        req.put("item", uidItem);
        req.put("amount", amount);
        return engine.getFileUtils().convertStringToJson(engine.getNetworkManager().post(api + "/userItem", req.toJSONString(), apiToken));
    }

    public JSONObject removeItemFromUser(String uidItem, String id, int amount){
        JSONObject req = new JSONObject();
        amount = amount * -1;
        req.put("id", id);
        req.put("item", uidItem);
        req.put("amount", amount);
        return engine.getFileUtils().convertStringToJson(engine.getNetworkManager().post(api + "/userItem", req.toJSONString(), apiToken));
    }

    public JSONObject work(String id){
        JSONObject req = new JSONObject();
        req.put("id", id);
        return engine.getFileUtils().convertStringToJson(engine.getNetworkManager().post(api + "/work", req.toJSONString(), apiToken));
    }

    public JSONObject giveUserAJob(String id, String uidJob){
        JSONObject req = new JSONObject();
        req.put("id", id);
        req.put("job", uidJob);
        return engine.getFileUtils().convertStringToJson(engine.getNetworkManager().post(api + "/userJob", req.toJSONString(), apiToken));

    }

    public JSONObject removeUserAJob(String id){
        JSONObject req = new JSONObject();
        req.put("id", id);
        return engine.getFileUtils().convertStringToJson(engine.getNetworkManager().delete(api + "/userJob", req.toJSONString(), apiToken));
    }

    public JSONObject giveCoinsToUser(String id, int coins){
        JSONObject req = new JSONObject();
        req.put("id", id);
        req.put("coins", coins);
        return engine.getFileUtils().convertStringToJson(engine.getNetworkManager().post(api + "/coins", req.toJSONString(), apiToken));
    }

    public JSONObject removeCoinsFromUser(String id, int coins){
        JSONObject req = new JSONObject();
        coins = coins * -1;
        req.put("id", id);
        req.put("coins", coins);
        return engine.getFileUtils().convertStringToJson(engine.getNetworkManager().post(api + "/coins", req.toJSONString(), apiToken));
    }

    public JSONObject createUser(JSONObject object){
        return engine.getFileUtils().convertStringToJson(engine.getNetworkManager().post(api + "/user", object.toJSONString(), apiToken));

    }

    public JSONObject createUser(User user, String certLvl){
        JSONObject req = new JSONObject();
        req.put("username", user.getName());
        req.put("userID", user.getId());
        req.put("certLevel", certLvl);
        return engine.getFileUtils().convertStringToJson(engine.getNetworkManager().post(api + "/user", req.toJSONString(), apiToken));

    }

    public JSONObject createServer(JSONObject object){
        return engine.getFileUtils().convertStringToJson(engine.getNetworkManager().post(api + "/server", object.toJSONString(), apiToken));
    }

    public JSONObject createUser(JSONObject user, String certLvl){
        return engine.getFileUtils().convertStringToJson(engine.getNetworkManager().post(api + "/user", user.toJSONString(), apiToken));
    }

    public JSONObject createServer(Guild guild){
        JSONObject req = new JSONObject();
        req.put("serverName", guild.getName());
        req.put("serverId", guild.getId());
        return engine.getFileUtils().convertStringToJson(engine.getNetworkManager().post(api + "/server", req.toJSONString(), apiToken));
    }

    public JSONObject getJobs(){
        return engine.getFileUtils().convertStringToJson(engine.getNetworkManager().get(api + "/job", apiToken));
    }

    public JSONObject getMonsters(){
        return engine.getFileUtils().convertStringToJson(engine.getNetworkManager().get(api + "/monster", apiToken));
    }

    public JSONObject userRandomMonster(String id, String minRarity){
        JSONObject req = new JSONObject();
        req.put("rarity", minRarity);
        req.put("id", id);
        return engine.getFileUtils().convertStringToJson(engine.getNetworkManager().post(api + "/userRandomMonster", req.toJSONString(), apiToken));
    }

    public JSONObject patchUser(JSONObject json, String id){
        JSONObject req = new JSONObject();
        req.put("data", json);
        req.put("id", id);
        return engine.getFileUtils().convertStringToJson(engine.getNetworkManager().patch(api + "/user", req.toJSONString(), apiToken));
    }

    public JSONObject patchServer(JSONObject json, String id){
        JSONObject req = new JSONObject();
        req.put("data", json);
        req.put("sid", id);
        return engine.getFileUtils().convertStringToJson(engine.getNetworkManager().patch(api + "/server", req.toJSONString(), apiToken));
    }

    public JSONObject getUsers(){
        return engine.getFileUtils().convertStringToJson(engine.getNetworkManager().get(api + "/user", apiToken));
    }

    public JSONObject addAutochannelToServer(String id, String chanId){
        JSONObject dat = new JSONObject();
        JSONObject server = getServerById(id);
        if((Integer) server.get("status") == 200){
            JSONObject data = (JSONObject) server.get("data");
            JSONArray chans = (JSONArray) data.get("autoChannelIds");

            chans.add(chanId);
            dat.put("autoChannelIds", chans);

            return patchServer(dat, id);
        }
        return getErrorJson();
    }

    public JSONObject removeAutochannel(String id, String chanId){
        JSONObject dat = new JSONObject();
        JSONObject server = getServerById(id);
        if((Integer) server.get("status") == 200){
            JSONObject data = (JSONObject) server.get("data");
            JSONArray chans = (JSONArray) data.get("autoChannelIds");

            chans.removeIf(e -> e.equals(chanId));
            dat.put("autoChannelIds", chans);

            return patchServer(dat, id);
        }
        return getErrorJson();
    }

    private JSONObject getErrorJson(){
        JSONObject o = new JSONObject();
        o.put("status", 400);
        o.put("message", "request failed!");
        return o;
    }
}
