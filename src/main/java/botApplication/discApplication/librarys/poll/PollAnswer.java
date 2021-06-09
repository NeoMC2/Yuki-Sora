package botApplication.discApplication.librarys.poll;

import java.io.Serializable;

public class PollAnswer implements Serializable {

    private static final long serialVersionUID = 42L;

    private String answer;
    private String answerEmoji;
    private boolean isEmojiServerEmote = false;
    private int place = 0;
    private int count = 0;
    private String role = "";
    private String lang = "";

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answers) {
        this.answer = answers;
    }

    public String getAnswerEmoji() {
        return answerEmoji;
    }

    public void setAnswerEmoji(String answerEmoji) {
        this.answerEmoji = answerEmoji;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public boolean isEmojiServerEmote() {
        return isEmojiServerEmote;
    }

    public void setEmojiServerEmote(boolean emojiServerEmote) {
        isEmojiServerEmote = emojiServerEmote;
    }
}