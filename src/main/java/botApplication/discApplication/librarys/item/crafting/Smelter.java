package botApplication.discApplication.librarys.item.crafting;

import botApplication.discApplication.librarys.item.collectables.metal.Metal;
import net.dv8tion.jda.api.entities.Member;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class Smelter {

    private Metal smelting;
    private Date startTime;
    private Member member;

    public Smelter(Metal smelting, Member member) {
        this.smelting = smelting;
        this.member = member;
    }

    public void startSmelting(){
        startTime = new Date();
        smelting.setForm(Metal.Form.Ingot);
    }

    public Metal open() throws Exception{
        if(startTime==null)
            throw new Exception("Doesn't even start!");
        Instant time = Instant.now().minus(Duration.ofMinutes(smelting.getCookTime()));
        Date dTime = Date.from(time);

        if(startTime.after(dTime))
            return smelting;

        throw new Exception("Still cooking!");
    }

    public Metal getSmelting() {
        return smelting;
    }

    public void setSmelting(Metal smelting) {
        this.smelting = smelting;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
