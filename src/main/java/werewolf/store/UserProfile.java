package werewolf.store;

import java.util.*;


public class UserProfile {
    public static Map<UUID, NameAndIcon> userProfileMap = new HashMap<UUID, NameAndIcon>();

    public static UUID newUser(String userName, int userIcon) {
        UUID newUUID = UUID.randomUUID();
        userProfileMap.put(newUUID, new NameAndIcon(userName, userIcon));
        return newUUID;
    }

    public static UUID newUser(NameAndIcon nameAndIcon) {
        UUID newUUID = UUID.randomUUID();
        userProfileMap.put(newUUID, nameAndIcon);
        return newUUID;
    }

    public static boolean changeProfile(UUID userUUID, String userName, int userIcon) {
        NameAndIcon nameAndIcon = userProfileMap.get(userUUID);

        if (nameAndIcon != null) {
            nameAndIcon.name = userName;
            nameAndIcon.icon = userIcon;
            return true;
        } else {
            return false;
        }
    }

    public static boolean changeProfile(UUID userUUID, NameAndIcon nameAndIcon) {
        NameAndIcon oldNameAndIcon = userProfileMap.get(userUUID);

        if (oldNameAndIcon != null) {
            userProfileMap.put(userUUID, nameAndIcon);
            return true;
        } else {
            return false;
        }
    }

    public static NameAndIcon getUserNameAndIcon(UUID userUUID) {
        return userProfileMap.get(userUUID);
    }

    public static String getUserName(UUID userUUID) {
        NameAndIcon nameAndIcon = userProfileMap.get(userUUID);
        if (nameAndIcon != null) {
            return nameAndIcon.name;
        } else {
            return null;
        }
    }








}
