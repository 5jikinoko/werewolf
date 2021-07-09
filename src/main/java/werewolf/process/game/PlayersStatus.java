/**
 * クラス概要:ゲーム時のユーザの生死と役職の情報を持つ
 *
 * @version 2.0
 * @author al19067
 */

/*
 * 進捗：
 * getWerewolfPlayerUUIDsメソッドにおいてListに追加する順番は大切なのか？
 */
package werewolf.process.game;

import java.util.*;

import werewolf.store.gamesettings.RoleBreakdown;


class PlayersStatus{
    private Map<UUID,PlayerStatus> statusMap = new HashMap<UUID,PlayerStatus>();

    PlayerStatus getPlayerStatus(UUID userUUID) {
        return statusMap.get(userUUID);
    }

    void setPlayers(Set<UUID> userUUIDs) {
        /*
         * メソッドの機能概要：Mapに引数で受け取ったUUIDをkeyにしてエントリーを追加していく。
         *                  valueはPlayerStatusのインスタンスを生成して入れる。
         */
        for (UUID userUUID : userUUIDs) {
            PlayerStatus PS = new PlayerStatus();
            statusMap.put(userUUID, PS);
        }
    }

    void setRoles(List<String> roles) {
        /*
         * メソッドの機能概要：役職を設定。
         *                 受け取ったRoleのリストの要素をそれぞれMapのvalueであるPlayerStatusのインスタンスのroleフィールドに順に代入していく.
         *                 aliveフィールドにはtrueを代入していく。
         */
        int i = 0;
        for (Map.Entry<UUID,PlayerStatus> entry : statusMap.entrySet())  {
            PlayerStatus PS = entry.getValue();
            PS.role = roles.get(i);
            PS.alive = true;
            i++;
        }
    }

    String getRole(UUID userUUID) {
        //メソッドの機能概要：ユーザUUIDが指すプレイヤーの役職を返す。
        //エラー処理：UUIDが存在しないなら戻り値roleは"error"
        PlayerStatus PS = statusMap.get(userUUID);
        if (PS == null){
            System.out.println("getRole:statusMapに" + userUUID.toString() + "がぞんざいしません");
            return "error";
        } else {
            return PS.role;
        }
    }

    void changeRole(UUID userUUID,String role) {
        //メソッドの機能概要：ユーザUUIDが指すプレイヤーの役職を変更。
        statusMap.get(userUUID).role = role;
    }

    boolean isAlive(UUID userUUID)  {
        //メソッドの機能概要：ユーザUUIDが指すプレイヤーが生存しているか返す(aliveの値を返す)。
        //エラー処理：UUIDが存在しないなら戻り値はfalse
        //生存：true,死亡：false
        PlayerStatus PS = statusMap.get(userUUID);
        if (PS == null){
            System.out.println("statusMapに" + userUUID.toString() + "がぞんざいしません");
            return false;
        } else {
            return PS.alive;
        }
    }

    /*
     * @param numberofPlayer プレイヤーの人数
     */
    int playerCount () {
        //メソッドの機能概要：プレイヤーの人数を返す。
        return statusMap.size();
    }

    /**
     * 生きてる人間の数
     * 人間は人狼・妖狐・吊人以外を指す
     * @return
     */
    int survivingHumanSize () {
        //メソッドの機能概要：生きている村人陣営の人数を返す
        int numberOfSurvivingHuman = 0;
        for (Map.Entry<UUID,PlayerStatus> entry : statusMap.entrySet())  {
            String role = entry.getValue().role;
            //生存している人間か判定
            if ( entry.getValue().alive && (
                    role.equals("villager") || role.equals("seer") || role.equals("necromancer")
                    || role.equals("knight") || role.equals("hunter") || role.equals("blackKnight")
                    || role.equals("freemasonary") || role.equals("baker") || role.equals("madman")
                    || role.equals("traitor") || role.equals("phantomThief"))) {
                numberOfSurvivingHuman++;
            }
        }
        return numberOfSurvivingHuman;
    }

    /**
     * 生きている人狼の数を返す
     * @return
     */
    int survivingWerewolvesSize () {
        //機能概要：生きている人狼の人数を返す。
        int numberOfSurvivingWerewolf = 0;
        for (Map.Entry<UUID,PlayerStatus> entry : statusMap.entrySet())  {
            if (entry.getValue().role.equals("werewolf") && entry.getValue().alive) {
                numberOfSurvivingWerewolf++;
            }
        }
        return numberOfSurvivingWerewolf;
    }

    /**
     * 生きている妖狐の人数を返す
     * @return
     */
    int survivingFoxSpiritsSize () {
        //メソッドの機能概要：生きている妖狐陣営の人数を返す。
        int numberofSurvivingFoxSpirit = 0;
        for (Map.Entry<UUID,PlayerStatus> entry : statusMap.entrySet())  {
            if (entry.getValue().role.equals("foxSpirit") && entry.getValue().alive) {
                numberofSurvivingFoxSpirit++;
            }
        }
        return numberofSurvivingFoxSpirit;
    }

    /**
     * 生きている吊人の人数を返す
     */
    int survivingFoolsSize () {
        //メソッドの機能概要：生きている吊人陣営の人数を返す。
        int numberofSurvivingFool = 0;
        for (Map.Entry<UUID,PlayerStatus> entry : statusMap.entrySet())  {
            if (entry.getValue().role.equals("fool") && entry.getValue().alive) {
                numberofSurvivingFool++;
            }
        }
        return numberofSurvivingFool;
    }

    /**
     * 生存しているパン屋がいるならtrueを返す
     * @return
     */
    boolean isSurvivingBaker () {
        //機能概要：パン屋の生き残りがいるか調べ、生き残りがいるならtrueを返す。
        for (Map.Entry<UUID,PlayerStatus> entry : statusMap.entrySet())  {
            if (entry.getValue().role.equals("baker") && entry.getValue().alive) {
                return true;
            }
        }
        return false;
    }

    /*
     * numberofWerewolf 人狼の陣営の人数
     * WerewolfPlayerUUIDs 人狼の役職を持つプレイヤーのUUIDを入れるためのリスト
     */
    List<UUID> getWerewolfPlayerUUIDs () {
        //機能概要：人狼プレイヤー全員分のUUIDを返す。
        List<UUID> WerewolfPlayerUUIDs = new ArrayList<UUID> () ;
        for (Map.Entry<UUID,PlayerStatus> entry : statusMap.entrySet())  {
            if (entry.getValue().role.equals("werewolf")) {
                WerewolfPlayerUUIDs.add(entry.getKey()) ;
            }
        }
        return WerewolfPlayerUUIDs;
    }

    /*
     * numberofDeadPlayer 死亡しているプレイヤーの数
     * DeadPlayerUUIDs 人狼の役職を持つプレイヤーのUUIDを入れるためのリスト
     */
    List<UUID> getDeadPlayerUUIDs () {
        //機能概要：死亡しているプレイヤー全員分のUUIDを返す。
        List<UUID> DeadPlayerUUIDs = new ArrayList<UUID> () ;
        for (Map.Entry<UUID,PlayerStatus> entry : statusMap.entrySet())  {
            if (entry.getValue().alive == false) {
                DeadPlayerUUIDs.add(entry.getKey());
            }
        }
        return DeadPlayerUUIDs;
    }
    /*
     * freemasonaryUUID 共有者のUUIDを格納するための変数
     */
    public UUID getFreemasonaryPartnerUUID (UUID userUUID) {
        /*機能概要：
         * 共有者の相方のUUIDを返す。
         * ＊このメソッドの実行前にgetRoleを呼び、
         * 引数のUUIDが指すプレイヤーの役職が共有者なら実行すること。
         */
        if(!getRole(userUUID).equals("freemasonary")){
            return null;
        }
        UUID freemasonaryUUID = null;
        for (Map.Entry<UUID, PlayerStatus> entry :  statusMap.entrySet()) {
            //自分自身でない　かつ　共有者
            if(entry.getKey() != userUUID && entry.getValue().role.equals("freemasonary")){
                freemasonaryUUID = entry.getKey();
                break;
            }
        }
        return freemasonaryUUID;
    }

    /*
     * choisedUUID ランダムに取り出したUUIDを格納する変数
     * keyCounter mapに格納されているkeyの要素数を格納する変数
     * mapCounter　mapの要素数を格納する変数(keyCounterと役割は同じ)
     * listCounter　Listに格納されている要素数を格納する変数
     * checkListCounter 確認の際に使う、Listに格納されている要素数を格納する変数
     */
    UUID getRandomUser (List<UUID> notSelectedPlayers) {
        /*
         * 機能概要：
         * MapのkeyからランダムなUUIDを返す。
         * ただしListの中にあるプレイヤーは覗く。
         */
        //選ばれたプレイヤーのUUID
        UUID chosenUUID = null;


        //(選ばれる対象の数) = (プレイヤー全員の数) - (対象外のプレイヤーの数)
        int targetNum = statusMap.size() - notSelectedPlayers.size();

        //0から選ばれる対象の数より小さいランダムな値を得る
        Random  random = new Random ();
        int randomValue = random.nextInt(targetNum);

        //randomValue番目のプレイヤーを選ぶ
        //ただしnotSelectedPlayersに含まれるプレイヤーは数えない
        for (UUID userUUID : statusMap.keySet()) {
            //notSelectedPlayersにuserUUIDが含まれていないならそのプレイヤーがrandomValue目かチェック
            //そうでないならカウントダウン
            if ( !notSelectedPlayers.contains(userUUID)) {
                if (randomValue == 0) {
                    chosenUUID = userUUID;
                    break;
                } else {
                    --randomValue;
                }
            }
        }

        if (chosenUUID == null) {
            System.out.println("エラー:PlayersStatus.getRandomUser");
            System.out.println(statusMap);
            System.out.println(notSelectedPlayers);
        }

        return chosenUUID;
    }

    void kill (UUID userUUID) {
        statusMap.get(userUUID).alive = false;
    }

    Map<UUID,PlayerStatus> getStatusMap() {
        return statusMap;
    }

    public String getRoleInJapanese(UUID userUUID) {
        return statusMap.get(userUUID).roleInJapanese();
    }
}
