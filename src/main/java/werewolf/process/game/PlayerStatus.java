//PlayersStatusに使う構造体
package werewolf.process.game;

public class PlayerStatus {
    public boolean alive;
    public String role;
    public PlayerStatus() {
	boolean alive = true;
    }
    public String toString() {
        if(role.equals("villager")) {
            return "村人（" + (alive ? "生存)" : "死亡)");
        } else if (role.equals("seer")) {
            return "占い師（" + (alive ? "生存)" : "死亡)");
        } else if (role.equals("necromancer")) {
            return "霊媒師（" + (alive ? "生存)" : "死亡)");
        }  else if (role.equals("knight")) {
            return "騎士（" + (alive ? "生存)" : "死亡)");
        } else if (role.equals("hunter")) {
            return "ハンター（" + (alive ? "生存)" : "死亡)");
        } else if (role.equals("blackKnight")) {
            return "黒騎士（" + (alive ? "生存)" : "死亡)");
        } else if (role.equals("freemasonary")) {
            return "共有者（" + (alive ? "生存)" : "死亡)");
        } else if (role.equals("baker")) {
            return "パン屋（" + (alive ? "生存)" : "死亡)");
        } else if (role.equals("werewolf")) {
            return "人狼（" + (alive ? "生存)" : "死亡)");
        } else if (role.equals("madman")) {
            return "狂人（" + (alive ? "生存)" : "死亡)");
        } else if (role.equals("traitor")) {
            return "背信者（" + (alive ? "生存)" : "死亡)");
        } else if (role.equals("foxSpirit")) {
            return "妖狐（" + (alive ? "生存)" : "死亡)");
        } else if (role.equals("fool")) {
            return "吊人（" + (alive ? "生存)" : "死亡)");
        }  else if (role.equals("phantomThief")) {
            return "怪盗（" + (alive ? "生存)" : "死亡)");
        }
        return "";
    }

    public String roleInJapanese() {
        if (role.equals("villager")) {
            return "村人";
        } else if (role.equals("seer")) {
            return "占い師";
        } else if (role.equals("necromancer")) {
            return "霊媒師";
        } else if (role.equals("knight")) {
            return "騎士";
        } else if (role.equals("hunter")) {
            return "ハンター";
        } else if (role.equals("blackKnight")) {
            return "黒騎士";
        } else if (role.equals("freemasonary")) {
            return "共有者";
        } else if (role.equals("baker")) {
            return "パン屋";
        } else if (role.equals("werewolf")) {
            return "人狼";
        } else if (role.equals("madman")) {
            return "狂人";
        } else if (role.equals("traitor")) {
            return "背信者";
        } else if (role.equals("foxSpirit")) {
            return "妖狐";
        } else if (role.equals("fool")) {
            return "吊人";
        } else if (role.equals("phantomThief")) {
            return "怪盗";
        }
        return "";
    }
}
