

package werewolf.process.gamesettings;

import werewolf.store.gamesettings.GameSettingsANDRoleBreakdown;
import werewolf.store.gamesettings.GameSettingsStore;
import werewolf.store.room.RoomInfo;

public class GameSettingsGetter {
    public static GameSettingsANDRoleBreakdown getGameSettingsANDRoleBreakdown(int roomID) {
        return GameSettingsStore.getGameSettingsANDRoleBreakdown(roomID);
    }
}
