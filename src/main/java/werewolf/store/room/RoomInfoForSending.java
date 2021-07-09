/**
 * クライアントに送信するときに使う構造体
 *
 * @version 1.0
 * @author
 */

package werewolf.store.room;

public class RoomInfoForSending {
    public int roomID;
    public String hostName;
    public String roomName;
    public int maxMember;
    public int nowMember;
    public boolean existPass;
    public String introduction;

    public RoomInfoForSending(
            int roomID,
            String hostName,
            String roomName,
            int maxMember,
            int nowMember,
            boolean existPass,
            String introduction
        ) {
        this.roomID = roomID;
        this.hostName = hostName;
        this.roomName = roomName;
        this.maxMember = maxMember;
        this.nowMember = nowMember;
        this.existPass = existPass;
        this.introduction = introduction;
    }

    public RoomInfoForSending() {

    }
}
