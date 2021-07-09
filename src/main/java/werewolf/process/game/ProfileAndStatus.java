package werewolf.process.game;

import werewolf.process.profile.ProfileGetter;
import werewolf.store.user.NameAndIcon;

public class ProfileAndStatus {
    public String name;
    public int icon;
    public boolean alive;
    public ProfileAndStatus(NameAndIcon nameAndIcon, boolean alive) {
        this.name = nameAndIcon.name;
        this.icon = nameAndIcon.icon;
        this.alive = alive;
    }
}
