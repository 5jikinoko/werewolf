package werewolf.store.gamesettings;

import java.util.*;

public class GameSettingsStore {
    static private Map<Integer, GameSettingsANDRoleBreakdown> roomIDtoSettings = new HashMap<Integer, GameSettingsANDRoleBreakdown>();

    static public void register(int roomID, GameSettings gameSettings, RoleBreakdown roleBreakdown) {
        GameSettingsANDRoleBreakdown newVal = new GameSettingsANDRoleBreakdown(gameSettings, roleBreakdown);
        roomIDtoSettings.put(roomID, newVal);
    }

    static public GameSettings getGameSettings(int roomID) {
        return roomIDtoSettings.get(roomID).gameSettings;
    }

    static public RoleBreakdown getRoleBreakdown(int roomID) {
        return roomIDtoSettings.get(roomID).roleBreakdown;
    }

    static public GameSettingsANDRoleBreakdown getGameSettingsANDRoleBreakdown(int roomID) {
        return roomIDtoSettings.get(roomID);
    }

}
