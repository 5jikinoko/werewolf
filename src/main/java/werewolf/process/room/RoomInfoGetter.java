package werewolf.process.room;

import werewolf.store.room.*;
import java.util.List;

public class RoomInfoGetter {
    public static List<RoomInfoForSending> getRoomInfoList() {
        return RoomInfo.getRoomInfoForSendingList();
    }

    public static RoomInfoForSending getRoomInfo(int roomID) {
        return RoomInfo.getRoomInfoForSending(roomID);
    }
 }
