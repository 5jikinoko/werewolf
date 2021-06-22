package werewolf.store;

public class MessageForSending {
    public int type;
    public String userName;
    public String text;

    public MessageForSending(int type, String userName, String text) {
        this.type = type;
        this.userName = userName;
        this.text = text;
    }

}
