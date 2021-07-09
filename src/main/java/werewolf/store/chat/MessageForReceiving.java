package werewolf.store.chat;

public class MessageForReceiving {
    /**
     * 0:一般チャット
     * 1:人狼チャット
     * 2:墓場チャット
     */
    public int channel;
    public String userUUID;
    public String text;

}
