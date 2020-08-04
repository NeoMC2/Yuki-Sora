package utils;

import java.io.Serializable;
import java.util.ArrayList;

public class Properties implements Serializable {

    public static final long serialVersionUID = 42L;
    //BotMessage times
    public final int veryLongTime = 60000;
    public final int longTime = 10000;
    public final int middleTime = 6000;
    public final int shortTime = 4000;
    //Discord BotApplication stuff
    public String discBotApplicationToken = "";
    public String discBotApplicationGame = "Ich mag Züge";
    public String discBotApplicationPrefix = "-";
    //telegram BotApplication stuff
    public String telBotApplicationToken = "";
    public String telBotApplicationName;
    //Engine stuff
    public boolean debug = false;
    public boolean showTime = true;
    public int saveSpeed = 10;
    public ArrayList<String> botSlaves;
}
