/**
 * プロフィールについての管理部
 *
 * @version 1.0
 * @author al19100
 */

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserProfileInformation {
	static Map<UUID, String> playerNameMap = new HashMap<>();
	static Map<UUID, Integer> imageNumMap = new HashMap<>();

	public void RegisterPlayerProfile(UUID playerUUID, String playerName, int imageNum) {
		UserProfileInformation.playerNameMap.put(playerUUID, playerName);
		UserProfileInformation.imageNumMap.put(playerUUID, imageNum);
	}
}
