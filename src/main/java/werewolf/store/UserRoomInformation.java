/**
 * 部屋情報についての管理部
 *
 * @version 1.0
 * @author al19100
 */

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserRoomInformation {
	static Map<UUID, Integer> roomIDMap = new HashMap<>();

	public void RegisterRoomID(UUID playerUUID, int roomID) {
		UserRoomInformation.roomIDMap.put(playerUUID, roomID);
	}
}

