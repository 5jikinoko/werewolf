package werewolf.store.user;

import java.util.*;

public class UserProfile {
    public static Map<UUID, NameAndIcon> userProfileMap = new HashMap<UUID, NameAndIcon>();

    public static UUID newUser(String name, int icon) {
        UUID newUUID = UUID.randomUUID();
        userProfileMap.put(newUUID, new NameAndIcon(name, icon));
        return newUUID;
    }

    public static void register (UUID userUUID, String name, int icon) {
         userProfileMap.put(userUUID, new NameAndIcon(name, icon));
    }

    public static NameAndIcon getNameAndIcon(UUID userUUID) {
        return userProfileMap.get(userUUID);
    }
}
