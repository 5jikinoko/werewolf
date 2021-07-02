/**
 * クラス概要:ゲーム時のユーザの生死と役職の情報を持つ
 *
 * @version 1.1
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

    void setPlayers(List<UUID> userUUIDs) {
        /*
         * メソッドの機能概要：Mapに引数で受け取ったUUIDをkeyにしてエントリーを追加していく。
         *                  valueはPlayerStatusのインスタンスを生成して入れる。
         */
        for (int i=0;i<userUUIDs.size();i++)  {
            PlayerStatus PS = new PlayerStatus();
            statusMap.put(userUUIDs.get(i),PS);
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
            PlayerStatus PS = new PlayerStatus();
            PS.role = roles.get(i);
            PS.alive = true;
            entry.setValue(PS);
            i++;
        }
    }

    String getRole(UUID userUUID) {
        //メソッドの機能概要：ユーザUUIDが指すプレイヤーの役職を返す。
        //エラー処理：UUIDが存在しないなら戻り値roleは"error"
        for (Map.Entry<UUID,PlayerStatus> entry : statusMap.entrySet())  {
            if(entry.getKey() == userUUID) {
                return entry.getValue().role;
            }
        }
        return "error";
    }

    void changeRole(UUID userUUID,String role) {
        //メソッドの機能概要：ユーザUUIDが指すプレイヤーの役職を変更。
        statusMap.get(userUUID).role = role;
    }

    boolean isAlive(UUID userUUID)  {
        //メソッドの機能概要：ユーザUUIDが指すプレイヤーが生存しているか返す(aliveの値を返す)。
        //エラー処理：UUIDが存在しないなら戻り値はfalse
        //生存：true,死亡：false
        for (Map.Entry<UUID,PlayerStatus> entry : statusMap.entrySet())  {
            if(entry.getKey() == userUUID) {
                return entry.getValue().alive;
            }
        }
        return false;
    }

    /*
     * @param numberofPlayer プレイヤーの人数
     */
    int playerCount () {
        //メソッドの機能概要：プレイヤーの人数を返す。
        return statusMap.size();
    }

    /*
     * numberofSurvivingVillager 生きている村人陣営の人数
     */
    int survivingVillagersTeamSize () {
        //メソッドの機能概要：生きている村人陣営の人数を返す
        int numberofSurvivingVillager = 0;
        for (Map.Entry<UUID,PlayerStatus> entry : statusMap.entrySet())  {
            if (entry.getValue().role.equals("villager") || entry.getValue().role.equals("seer") ||
                    entry.getValue().role.equals("necromancer") || entry.getValue().role.equals("knight") ||
                    entry.getValue().role.equals("hunter") || entry.getValue().role.equals("brackKnight") ||
                    entry.getValue().role.equals("freemasonary") || entry.getValue().role.equals("baker")) {

                if(entry.getValue().alive == true) {
                    numberofSurvivingVillager++;
                }
            }
        }
        return numberofSurvivingVillager;
    }

    /*
     * numberofSurvivingWerewolf 生きている人狼陣営の人数
     */
    int survivingWerewolvesTeamSize () {
        //機能概要：生きている人狼陣営の人数を返す。
        int numberofSurvivingWerewolf = 0;
        for (Map.Entry<UUID,PlayerStatus> entry : statusMap.entrySet())  {
            if (entry.getValue().role.equals("werewolf") || entry.getValue().role.equals("madman") ||
                    entry.getValue().role.equals("traitor")) {

                if(entry.getValue().alive == true) {
                    numberofSurvivingWerewolf++;
                }
            }
        }
        return numberofSurvivingWerewolf;
    }

    /*
     * numberofSurvivingFoxSpirit 生きている妖狐陣営の人数
     */
    int survivingFoxSpiritsTeamSize () {
        //メソッドの機能概要：生きている妖狐陣営の人数を返す。
        int numberofSurvivingFoxSpirit = 0;
        for (Map.Entry<UUID,PlayerStatus> entry : statusMap.entrySet())  {
            if (entry.getValue().role.equals("foxSpirit")) {

                if(entry.getValue().alive == true) {
                    numberofSurvivingFoxSpirit++;
                }
            }
        }
        return numberofSurvivingFoxSpirit;
    }

    /*
     * numberofSurvivingFool 生きている吊人陣営の人数
     */
    int survivingFoolsTeamSize () {
        //メソッドの機能概要：生きている吊人陣営の人数を返す。
        int numberofSurvivingFool = 0;
        for (Map.Entry<UUID,PlayerStatus> entry : statusMap.entrySet())  {
            if (entry.getValue().role.equals("fool") || entry.getValue().role.equals("phantomThief")) {

                if(entry.getValue().alive == true) {
                    numberofSurvivingFool++;
                }
            }
        }
        return numberofSurvivingFool;
    }

    /*
     * numberofSurvivingBaker 生きているパン屋の人数
     */
    boolean isSurvivingBaker () {
        //機能概要：パン屋の生き残りがいるか調べ、生き残りがいるならtrueを返す。
        int numberofSurvivingBaker = 0;
        for (Map.Entry<UUID,PlayerStatus> entry : statusMap.entrySet())  {
            if (entry.getValue().role.equals("baker")) {

                if(entry.getValue().alive == true) {
                    numberofSurvivingBaker++;
                }
            }
        }
        if (numberofSurvivingBaker != 0) {
            return true;
        }
        else {
            return false;
        }
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
            if(entry.getKey() != userUUID && entry.getValue().role.equals("freemasonary")){
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

    /*
     * eachRoleNum それぞれの役職の人数を格納するためのRoleBreakdownクラスのインスタンス
     */
    RoleBreakdown getBreakdown () {
        /*機能概要：
         * 役職の内訳を返す。
         * Breakdownクラスのインスタンスを生成して対応する役職に
         * その役職のプレイヤーの合計人数(生死は問わない)を入れて返す。
         */
        RoleBreakdown eachRoleNum = new RoleBreakdown();
        for(Map.Entry<UUID, PlayerStatus> entry :  statusMap.entrySet()) {
            if(entry.getValue().role.equals("villager")) {
                eachRoleNum.villagersNum++;
            }
            else if(entry.getValue().role.equals("seer")) {
                eachRoleNum.seersNum++;
            }
            else if(entry.getValue().role.equals("necromancer")) {
                eachRoleNum.necromancersNum++;
            }
            else if(entry.getValue().role.equals("knight")) {
                eachRoleNum.knightsNum++;
            }
            else if(entry.getValue().role.equals("hunter")) {
                eachRoleNum.huntersNum++;
            }
            else if(entry.getValue().role.equals("brackKnight")) {
                eachRoleNum.blackKnightsNum++;
            }
            else if(entry.getValue().role.equals("freemasonary")) {
                eachRoleNum.freemasonariesNum++;
            }
            else if(entry.getValue().role.equals("baker")) {
                eachRoleNum.bakersNum++;
            }
            else if(entry.getValue().role.equals("werewolf")) {
                eachRoleNum.werewolvesNum++;
            }
            else if(entry.getValue().role.equals("madman")) {
                eachRoleNum.madmenNum++;
            }
            else if(entry.getValue().role.equals("traitor")) {
                eachRoleNum.traitorsMum++;
            }
            else if(entry.getValue().role.equals("foxSpirit")) {
                eachRoleNum.foxSpiritsNum++;
            }
            else if(entry.getValue().role.equals("fool")) {
                eachRoleNum.foolsNum++;
            }
            else if(entry.getValue().role.equals("phantomThief")) {
                eachRoleNum.phantomThievesNum++;
            }
        }
        return eachRoleNum;
    }

    void kill (UUID userUUID) {
        statusMap.get(userUUID).alive = false;
    }
}
