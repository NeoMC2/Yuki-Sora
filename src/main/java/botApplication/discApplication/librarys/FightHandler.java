package botApplication.discApplication.librarys;

import botApplication.discApplication.librarys.dungeon.Dungeon;
import core.Engine;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class FightHandler {

    private JSONObject m1Json;
    private JSONObject m2Json;

    private JSONObject m1JsonRoot;
    private JSONObject m2JsonRoot;

    private String user1;
    private String user2;

    private String m1;
    private String m2;

    private boolean m1Ai;
    private boolean m2Ai;

    //false = 2 true = 1
    private boolean round = false;

    private final Engine engine;

    private String sAttack;

    private String slot;

    private long lastDmg;

    public boolean fightDone;


    private boolean sameUser = false;


    public FightHandler(String user1, String user2, String m1, String m2, Engine engine) {
        if (user1 != null) {
            this.user1 = user1;
            this.m1 = m1;
        } else {
            this.m1Ai = true;
        }

        if (user2 != null) {
            this.user2 = user2;
            this.m2 = m2;
        } else {
            this.m2Ai = true;
        }

        this.engine = engine;

        if (m1Ai) {
            JSONObject res = engine.getDiscEngine().getApiManager().createAiFight(user2);
            m1Json = (JSONObject) res.get("data");
        }
        if (m2Ai) {
            JSONObject res = engine.getDiscEngine().getApiManager().createAiFight(user1);
            m2Json = (JSONObject) res.get("data");
        }

        if (!m1Ai) {
            JSONObject res = engine.getDiscEngine().getApiManager().getUserMonstersById(user1);
            JSONArray mnsters = (JSONArray) res.get("data");
            for (Object o : mnsters) {
                JSONObject mnster = (JSONObject) o;
                if (mnster.get("_id").equals(m1)) {
                    m1Json = mnster;
                    break;
                }
            }
        }

        if (!m2Ai) {
            JSONObject res = engine.getDiscEngine().getApiManager().getUserMonstersById(user2);
            JSONArray mnsters = (JSONArray) res.get("data");
            for (Object o : mnsters) {
                JSONObject mnster = (JSONObject) o;
                if (mnster.get("_id").equals(m2)) {
                    m2Json = mnster;
                    break;
                }
            }
        }

        JSONObject res = engine.getDiscEngine().getApiManager().getMonsters();
        JSONArray mnsters = (JSONArray) res.get("data");
        for (Object o : mnsters) {
            JSONObject mnster = (JSONObject) o;
            if (mnster.get("_id").equals(m1Json.get("rootMonster"))) {
                m1JsonRoot = mnster;
            }

            if (mnster.get("_id").equals(m2Json.get("rootMonster"))) {
                m2JsonRoot = mnster;
            }
        }
    }

    public String currentPlayer() {
        if (round)
            return user1;
        else
            return user2;
    }

    public String nextPlayer() {
        if (sameUser)
            return currentPlayer();

        if (!round)
            return user1;
        else
            return user2;
    }

    public String round(String w) {
        if (!sameUser)
            round = !round;

        if (sameUser)
            sameUser = false;

        if (round) {
            if (testAi(m1Ai, user2, m2)) return fightInfo();
        } else {
            if (testAi(m2Ai, user1, m1)) return fightInfo();
        }

        try {
            switch (w.toLowerCase()) {
                case "a1":
                case "a4":
                case "a3":
                case "a2":
                    slot = w;
                    break;

                case "info": {
                    String m1 = m1JsonRoot.get("name") + " with " + m1Json.get("hp") + " hp";
                    String m2 = m2JsonRoot.get("name") + " with " + m2Json.get("hp") + " hp";
                    sameUser = true;
                    return m1 + " against " + m2;
                }

                case "help":
                    String m1 = m1JsonRoot.get("name") + " with " + m1Json.get("hp") + " hp";
                    String m2 = m2JsonRoot.get("name") + " with " + m2Json.get("hp") + " hp";
                    sameUser = true;
                    return "You are in a fight!\n\n" + m1 + " against " + m2 + "\n\nType info to see the info again and a1, a2, a3, a4 to use one of your attackslots";

                default:
                    sameUser = true;
                    return "invalid";
            }

            JSONObject res;
            if (round) {
                res = engine.getDiscEngine().getApiManager().fight(user1, false, m2Ai, m1, m2, null, slot);
            } else {
                res = engine.getDiscEngine().getApiManager().fight(user2, false, m1Ai, m2, m1, null, slot);
            }
            if (((Long) res.get("status")) == 200) {
                m1Json = (JSONObject) res.get("monster1");
                m2Json = (JSONObject) res.get("monster2");
                lastDmg = (Long) res.get("dmg");
                return fightInfo();
            } else {
                sameUser = true;
                return (String) res.get("message");
            }

        } catch (Exception ignored) {

        }
        return fightInfo();
    }

    private boolean testAi(boolean m1Ai, String user2, String m22) {
        if (m1Ai) {
            JSONObject res = engine.getDiscEngine().getApiManager().fight(user2, true, false, null, m22, null, null);
            m1Json = (JSONObject) res.get("monster1");
            m2Json = (JSONObject) res.get("monster2");
            lastDmg = (Long) res.get("dmg");
            return true;
        }
        return false;
    }

    private String fightInfo() {
        fightDone = calcFightDone();
        String m1 = m1JsonRoot.get("name") + " with " + m1Json.get("hp") + " hp";
        String m2 = m2JsonRoot.get("name") + " with " + m2Json.get("hp") + " hp";

        if (round) {
            return m1 + " attacked " + m2 + " and made " + lastDmg + " damage";
        } else {
            return m2 + " attacked " + m1 + " and made " + lastDmg + " damage";
        }
    }

    private boolean calcFightDone(){
        return (Double) m1Json.get("hp") <= 0 || (Double) m2Json.get("hp") <= 0;
    }

    public String getWinner(){
        if(fightDone){
            if((Long) m1Json.get("hp") <= 0)
                return user2;
            if((Long) m2Json.get("hp") <= 0)
                return user1;
        }
        return null;
    }
}