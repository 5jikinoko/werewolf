package werewolf.store;

import java.util.*;

public class RoomInfo {
    static private Map<Integer, Set<UUID>> roomIDtoUserUUIDSet = new HashMap<Integer, Set<UUID>>();
    static private Map<UUID, Integer> userUUIDtoRoomID = new HashMap<UUID, Integer>();
    static int i = 0;
    public static boolean enterRoom(UUID userUUID, int roomID) {
        Integer oldRoomID = userUUIDtoRoomID.get(userUUID);
        if (oldRoomID != null) {
            Set<UUID> userSet = roomIDtoUserUUIDSet.get(oldRoomID);
            userSet.remove(oldRoomID);
        }
        Set<UUID> userSet = roomIDtoUserUUIDSet.get(roomID);
        userSet.add(userUUID);

        userUUIDtoRoomID.put(userUUID, roomID);

        return true;
    }

    public static int createRoom() {
        roomIDtoUserUUIDSet.put(++i, new HashSet<UUID>());
        ChatStore.createRoomChat(i);
        return i;
    }

    public static int whereRoom(UUID userUUID) {
        int roomID = 0;
        try {
            roomID = userUUIDtoRoomID.get(userUUID);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return roomID;
    }

    public static Set<UUID> getParticipantsSet(int roomID) {
        return roomIDtoUserUUIDSet.get(roomID);
    }


}
