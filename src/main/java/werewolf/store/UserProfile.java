package werewolf.store;

import java.util.*;

public class UserProfile {
    public static Map<UUID, nameAndIcon> userInfoMap = new HashMap<UUID, nameAndIcon>();

    public UUID newUser(String name, int icon) {
        UUID newUUID = UUID.randomUUID();
        userInfoMap.put(newUUID, new nameAndIcon(name, icon));
        return newUUID;
    }

    

    public class nameAndIcon {
        public String name;
        public int icon;

        nameAndIcon(String name, int icon) {
            this.name = name;
            this.icon = icon;
        }
    }

}
