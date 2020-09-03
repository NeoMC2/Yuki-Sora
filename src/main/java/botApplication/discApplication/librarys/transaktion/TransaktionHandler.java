package botApplication.discApplication.librarys.transaktion;

import botApplication.discApplication.librarys.item.Item;
import botApplication.discApplication.librarys.item.monsters.Attack;
import botApplication.discApplication.librarys.item.monsters.Monster;
import botApplication.discApplication.librarys.item.monsters.StatusEffect;
import botApplication.discApplication.librarys.job.Job;
import core.Engine;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class TransaktionHandler {

    private final Engine engine;

    public TransaktionHandler(Engine engine) {
        this.engine = engine;
    }

    public static ArrayList<Job> parseJsonToJobs(JSONObject object) {
        ArrayList<Job> jobs = new ArrayList<>();
        Object[] set = object.keySet().toArray();
        for (int i = 0; i < set.length; i++) {
            JSONObject o = (JSONObject) object.get(set[i]);
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
            boolean fail = false;
            JSONObject o = (JSONObject) object.get(set[i]);
            Monster m = new Monster();
            try {
                m.setItemName((String) set[i]);
                m.setImgUrl((String) o.get("img"));
                m.setMaxHp(Integer.parseInt((String) o.get("hp")));
                m.setBaseHp(m.getMaxHp());
                m.setHp(m.getMaxHp());
                m.setLevel(Integer.parseInt((String) o.get("lvl")));
                m.setMonsterTypes(getMonsterTypesFromJson(o));
                m.setItemRarity(Item.stringToRarity((String) o.get("rar")));
            } catch (Exception e) {
                fail = true;
            }

            try {
                m.setEvolveLevel(Integer.parseInt((String) o.get("evlvl")));
            } catch (Exception e) {
                m.setEvolveLevel(-1);
            }

            try {
                String[] ev = ((String) o.get("ev")).split(",");
                if (ev[0].equals("")) {
                    if (!o.get("ev").equals(""))
                        m.getEvolves().add((String) o.get("ev"));
                } else
                    m.getEvolves().addAll(Arrays.asList(ev));
            } catch (Exception e) {
            }

            try {
                String s = (String) o.get("shown");
                if (s != null)
                    if (s.equals("false"))
                        m.setShown(false);
            } catch (Exception e) {
                m.setShown(true);
            }

            JSONArray attacks = (JSONArray) o.get("attacks");
            for (Object att : attacks) {
                JSONObject attack = (JSONObject) att;
                Attack a = new Attack();
                try {
                    a.setMaxUsages(Integer.parseInt((String) attack.get("usage")));
                    a.setAttackName((String) attack.get("name"));
                    a.setBaseDamage(Integer.parseInt((String) attack.get("dmg")));
                    a.setMonsterTypes(getMonsterTypesFromJson(attack));
                    a.setLvl(Integer.parseInt((String) attack.get("lvl")));
                } catch (Exception e) {
                    fail = true;
                }
                try {
                    a.setStatusEffect(StatusEffect.stringToStatEffect((String) attack.get("state")));
                } catch (Exception e) {
                }
                if (!fail)
                    m.getAttacks().add(a);
            }
            m.finish();
            if (!fail)
                monsters.add(m);
        }
        return monsters;
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

    public Monster getRandomMonster(Item.Rarity minRarity) {
        ArrayList<Monster> monsters = getShownListOfMonsters();
        int r = ThreadLocalRandom.current().nextInt(0, monsters.size() - 1);
        Monster m = null;
        try {
            m = monsters.get(r).clone();
        } catch (Exception e) {
            return null;
        }
        if (Item.rarityToInt(minRarity) <= Item.rarityToInt(m.getItemRarity())) {
            int i = Item.rarityToInt(m.getItemRarity());
            int r2 = ThreadLocalRandom.current().nextInt(0, 100);
            if (i == 0) {
                return m;
            } else if (i == 1) {
                if (r2 > 30) {
                    return m;
                }
            } else if (i == 2) {
                if (r2 > 50) {
                    return m;
                }
            } else if (i == 3) {
                if (r2 > 80) {
                    return m;
                }
            }
        }
        return getRandomMonster(minRarity);
    }

    public ArrayList<Monster> getShownListOfMonsters(){
        ArrayList<Monster> mnstr = new ArrayList<>();
        engine.getDiscEngine().getFilesHandler().getMonsters().forEach(e -> {if(e.isShown()) mnstr.add(e);});

        return mnstr;
    }
}
