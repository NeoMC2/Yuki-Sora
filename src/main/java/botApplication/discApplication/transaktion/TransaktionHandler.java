package botApplication.discApplication.transaktion;

import botApplication.discApplication.librarys.transaktion.Item;
import botApplication.discApplication.librarys.transaktion.job.Job;
import botApplication.discApplication.librarys.transaktion.monsters.Attack;
import botApplication.discApplication.librarys.transaktion.monsters.Monster;
import core.Engine;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class TransaktionHandler {

    private Engine engine;

    public TransaktionHandler(Engine engine) {
        this.engine = engine;
    }

    public Monster getRandomMonster(Item.Rarity minRarity) {
        int r = ThreadLocalRandom.current().nextInt(0, engine.getDiscEngine().getFilesHandler().getMonsters().size());
        Monster m = engine.getDiscEngine().getFilesHandler().getMonsters().get(r);
        if (Item.rarityToInt(minRarity) >= Item.rarityToInt(m.getItemRarity())) {
            int i = Item.rarityToInt(m.getItemRarity());
            int r2 = ThreadLocalRandom.current().nextInt(0, 50);
            if (i == 0) {
                return m;
            } else if (i == 1) {
                if (r2 > 10) {
                    return m;
                }
            } else if (i == 2) {
                if (r2 > 20) {
                    return m;
                }
            } else if (i == 3) {
                if (r2 > 30) {
                    return m;
                }
            }
        }
        return getRandomMonster(minRarity);
    }

    public static ArrayList<Job> parseJsonToJobs(JSONObject object) {
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

    public static ArrayList<Monster> parseJsonToMonster(JSONObject object) {
        ArrayList<Monster> monsters = new ArrayList<>();
        Object[] set = object.keySet().toArray();
        for (int i = 0; i < set.length; i++) {
            JSONObject o = (JSONObject) object.get((String) set[i]);
            Monster m = new Monster();
            m.setItemName((String) set[i]);
            m.setImgUrl((String) o.get("img"));
            m.setMaxHp(Integer.parseInt((String) o.get("hp")));
            m.setBaseHp(m.getMaxHp());
            m.setHp(m.getMaxHp());
            m.setLevel(Integer.parseInt((String) o.get("lvl")));
            m.setMonsterTypes(getMonsterTypesFromJson(o));
            m.setItemRarity(Item.stringToRarity((String) o.get("rar")));

            JSONArray attacks = (JSONArray) o.get("attacks");
            for (Object att : attacks) {
                JSONObject attack = (JSONObject) att;
                Attack a = new Attack();
                a.setAttackName((String) attack.get("name"));
                a.setBaseDamage(Integer.parseInt((String) attack.get("dmg")));
                a.setMonsterTypes(getMonsterTypesFromJson(attack));
                a.setLvl(Integer.parseInt((String) attack.get("lvl")));
                m.getAttacks().add(a);
            }
            addAttacks(m, m.getAttacks());
            monsters.add(m);
        }
        return monsters;
    }

    private static void addAttacks(Monster m, ArrayList<Attack> a){
        int c = 1;
        for (Attack at:a) {
            if(at.getLvl() <= m.getLevel())
            if(c == 1){
                m.setA1(at);
                c++;
            }else if(c == 2){
                m.setA2(at);
                c++;
            } else if(c == 3){
                m.setA3(at);
                c++;
            } else if(c == 4){
                m.setA4(at);
                c++;
            } else {
                return;
            }
        }
    }

    private static ArrayList<Monster.MonsterType> getMonsterTypesFromJson(JSONObject o) {
        JSONArray types = (JSONArray) o.get("type");
        ArrayList<Monster.MonsterType> m = new ArrayList<>();
        for (Object ob : types) {
            String s = (String) ob;
            try {
                m.add(Monster.stringToMonsterType(s));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return m;
    }
}