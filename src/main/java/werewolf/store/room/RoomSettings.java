package werewolf.store.room;

import java.util.HashMap;
import java.util.UUID;
import java.util.Map;

public class RoomSettings {
    public UUID hostUUID;
    public String roomName;
    public String  pass;
    public int maxMember;
    public String introduction;

    public RoomSettings(UUID hostUUID,
             String roomName,
             String pass,
             int maxMember,
             String introduction) {

        this.hostUUID = hostUUID;
        this.roomName = roomName;
        this.pass = pass;
        this.maxMember = maxMember;
        this.introduction = introduction;
    }

    public RoomSettings() {

    }

}
