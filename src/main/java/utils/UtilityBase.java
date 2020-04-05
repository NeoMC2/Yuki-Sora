package utils;

import core.Engine;

import java.awt.*;
import java.util.Date;

public class UtilityBase {

    Engine engine;

    public UtilityBase(Engine engine) {
        this.engine = engine;
    }

    public Color convertStringToColor(String scolor) throws Exception {
        switch (scolor) {
            case "black":
                return Color.BLACK;
            case "blue":
                return Color.BLUE;
            case "cyan":
                return Color.CYAN;
            case "dark_gray":
                return Color.DARK_GRAY;
            case "gray":
                return Color.GRAY;
            case "green":
                return Color.GREEN;
            case "light_gray":
                return Color.LIGHT_GRAY;
            case "magenta":
                return Color.MAGENTA;
            case "orange":
                return Color.ORANGE;
            case "pink":
                return Color.PINK;
            case "red":
                return Color.RED;
            case "white":
                return Color.WHITE;
            case "yellow":
                return Color.YELLOW;
        }
        throw new Exception("Color doesn't exist");
    }

    public void printOutput(String message, boolean debug) {
        String time = "/" + getDateString() + "/ ";
        if (engine.getProperties() == null) {
            System.out.println(time + message);
            return;
        }
        if (!engine.getProperties().showTime) {
            time = "";
        }
        if (debug){
            if (engine.getProperties().debug)
                System.out.println(time + "{Debug} - " + message);
        } else {
            System.out.println(time + message);
        }

    }

    public String convertDateToString(Date date, boolean withTime) {
        String month = String.valueOf(date.getMonth() + 1);
        String day = String.valueOf(date.getDate());
        String year = String.valueOf(date.getYear() + 1900);
        String secound = String.valueOf(date.getSeconds());
        String minutes = String.valueOf(date.getMinutes());
        String hours = String.valueOf(date.getHours());
        if (withTime)
            return (hours + "." + minutes + "." + secound + "-" + day + "." + month + "." + year);
        else
            return (day + "." + month + "." + year);
    }

    public String getDateString() {
        Date date = new Date();
        String month = String.valueOf(date.getMonth() + 1);
        String day = String.valueOf(date.getDate());
        String year = String.valueOf(date.getYear() + 1900);
        String secound = String.valueOf(date.getSeconds());
        String minutes = String.valueOf(date.getMinutes());
        String hours = String.valueOf(date.getHours());
        return (hours + "." + minutes + "." + secound + "-" + day + "." + month + "." + year);
    }
}
