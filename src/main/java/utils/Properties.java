package utils;

import java.io.Serializable;

public class Properties implements Serializable {

    public static final long serialVersionUID = 42L;

    //Discord BotApplication stuff
    public String discBotApplicationToken = "";
    public String discBotApplicationGame = "Ich mag Züge";
    public String discBotApplicationPrefix = "-";

    //telegram BotApplication stuff
    public String telBotApplicationToken = "";
    public String telBotApplicationName;

    //BotMessage times
    public final int veryLongTime = 60000;
    public final int longTime = 10000;
    public final int middleTime = 6000;
    public final int shortTime = 4000;

    //Engine stuff
    public boolean debug = false;
    public boolean showTime = true;
    public int saveSpeed = 10;
}
