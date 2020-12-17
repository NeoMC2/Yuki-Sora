package botApplication.discApplication.librarys;

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
    private boolean round = true;

    private final Engine engine;

    private String sAttack;

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
                if (((String) mnster.get("_id")).equals(m1)) {
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
                if (((String) mnster.get("_id")).equals(m2)) {
                    m1Json = mnster;
                    break;
                }
            }
        }

        JSONObject res = engine.getDiscEngine().getApiManager().getMonsters();
        JSONArray mnsters = (JSONArray) res.get("data");
        for (Object o : mnsters) {
            JSONObject mnster = (JSONObject) o;
            if (((String) mnster.get("_id")).equals((String) m1Json.get("rootMonster"))) {
                m1JsonRoot = mnster;
            }

            if (((String) mnster.get("_id")).equals((String) m2Json.get("rootMonster"))) {
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
            if (m1Ai) {
                JSONObject res = engine.getDiscEngine().getApiManager().fight(user2, true, false, null, m2, null);
                m1Json = (JSONObject) res.get("monster1");
                m2Json = (JSONObject) res.get("monster2");
                lastDmg = (Long) res.get("dmg");
                return fightInfo();
            }
        } else {
            if (m2Ai) {
                JSONObject res = engine.getDiscEngine().getApiManager().fight(user1, true, false, null, m1, null);
                m1Json = (JSONObject) res.get("monster1");
                m2Json = (JSONObject) res.get("monster2");
                lastDmg = (Long) res.get("dmg");
                return fightInfo();
            }
        }

        try {

            switch (w.toLowerCase()) {
                case "a1":
                    if (round) {
                        sAttack = (String) m1Json.get("a1");
                    } else {
                        sAttack = (String) m2Json.get("a1");
                    }
                    break;

                case "a2":
                    if (round) {
                        sAttack = (String) m1Json.get("a2");
                    } else {
                        sAttack = (String) m2Json.get("a2");
                    }
                    break;

                case "a3":
                    if (round) {
                        sAttack = (String) m1Json.get("a3");
                    } else {
                        sAttack = (String) m2Json.get("a3");
                    }
                    break;

                case "a4":
                    if (round) {
                        sAttack = (String) m1Json.get("a4");
                    } else {
                        sAttack = (String) m2Json.get("a4");
                    }
                    break;

                case "info":
                    String m1 = m1JsonRoot.get("name") + " with " + m1Json.get("hp") + " hp";
                    String m2 = m2JsonRoot.get("name") + " with " + m2Json.get("hp") + "hp";
                    sameUser = true;
                    return m1 + " against " + m2;

                case "help":
                    sameUser = true;
                    return "help";

                default:
                    sameUser = true;
                    return "invalid";
            }
        } catch (Exception e) {

        }
        return fightInfo();
    }

    private String fightInfo() {
        String m1 = m1JsonRoot.get("name") + " with " + m1Json.get("hp") + " hp";
        String m2 = m2JsonRoot.get("name") + " with " + m2Json.get("hp") + "hp";

        if (round) {
            return m1 + " attacked " + m2 + " and made " + lastDmg + " damage";
        } else {
            return m2 + " attacked " + m1 + " and made " + lastDmg + " damage";
        }
    }
}