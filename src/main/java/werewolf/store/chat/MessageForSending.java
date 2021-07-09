package werewolf.store.chat;

public class MessageForSending {
    /**
     * 0:一般チャット
     * 1:人狼チャット
     * 2:墓場チャット
     */
    public int channel;
    public String userName;
    public String text;

    public MessageForSending(int channel, String userName, String text) {
        this.channel = channel;
        this.userName = userName;
        this.text = text;
    }
}
