package botApplication.discApplication.librarys.item.crafting;

import botApplication.discApplication.librarys.item.Item;
import net.dv8tion.jda.api.entities.User;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class Cooking {

    private Cookable smelting;
    private Date startTime;
    private User member;

    public Cooking(Cookable smelting, User member) {
        this.smelting = smelting;
        this.member = member;
    }

    public void startCooking(){
        startTime = new Date();
    }

    public Item open() throws Exception{
        if(startTime==null)
            throw new Exception("Doesn't even start!");
        Instant time = Instant.now().minus(Duration.ofMinutes(smelting.cookTime()));
        Date dTime = Date.from(time);

        if(startTime.before(dTime))
            return smelting.result();

        throw new Exception("Still cooking!");
    }

    public int minutesLeft(){
        Instant time = Instant.now().minus(Duration.ofMinutes(smelting.cookTime()));
        Date dTime = Date.from(time);
        return (int) (startTime.getTime() - dTime.getTime());
    }

    public Cookable getSmelting() {
        return smelting;
    }

    public void setSmelting(Cookable smelting) {
        this.smelting = smelting;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public User getMember() {
        return member;
    }

    public void setMember(User member) {
        this.member = member;
    }
}
