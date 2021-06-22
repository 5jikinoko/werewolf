/**
* クラス概要:ゲーム時のユーザの生死と役職の情報を持つ
*
* @version 1.0
* @author al19067
*/

/*
* 進捗：
* getWerewolfPlayerUUIDsメソッドにおいてListに追加する順番は大切なのか？
*/
import java.util.*;



class PlayersStatus　{
  private Map<UUID,　PlayerStatus>;

  void setPlayers　(List<UUID> userUUIDs)　{
    /*
    * メソッドの機能概要：Mapに引数で受け取ったUUIDをkeyにしてエントリーを追加していく。
    *                  valueはPlayerStatusのインスタンスを生成して入れる。
    */
    for (int i　=　0;　i　<　userUUIDs.size();　i++)　{
      PlayerStatus PS = new PlayerStatus();
      Map.put(userUUIDs.get(i),　PS);
    }
  }

  void setRoles　(List<String> roles)　{
    /*
    * メソッドの機能概要：役職を設定。
    *                 受け取ったRoleのリストの要素をそれぞれMapのvalueであるPlayerStatusのインスタンスのroleフィールドに順に代入していく.
    *                 aliveフィールドにはtrueを代入していく。
    */
    for (int i　=　0;　i　<　roles.size();　i++)　{
      PlayerStatus PS = new PlayerStatus();
      PS.role = roles.get(i);
      PS.alive = true;
    }
  }

  String getRole　(UUID userUUID)　{
    //メソッドの機能概要：ユーザUUIDが指すプレイヤーの役職を返す。
    //エラー処理：UUIDが存在しないなら戻り値roleは"error"
    int keyCounter = 0;
    int keyCounter2 = 0;

    for(UUID key : Map.keySet()) {
      keyCounter++;
    }

    for(UUID key : Map.keySet()) {
      if(key.equals(userUUID)) {
        return Map.get(userUUID).role;
        break;
      }
      keyCounter2++;
    }
    if(keyCounter == keyCounter2) {
      return "error";
    }

    return role;
  }

  void changeRole (UUID userUUID,String role) {
    //メソッドの機能概要：ユーザUUIDが指すプレイヤーの役職を変更。
    Map.get(userUUID).role = role;
  }

  boolean isAlive (UUID userUUID) {
    //メソッドの機能概要：ユーザUUIDが指すプレイヤーが生存しているか返す(aliveの値を返す)。
    //エラー処理：UUIDが存在しないなら戻り値はfalse
    //生存：true,死亡：false
    int keyCounter = 0;
    int keyCounter2 = 0;
    for(UUID key : Map.keySet()) {
      keyCounter++;
    }

    for(UUID key : Map.keySet()) {
      if(key.equals(userUUID)) {
        return Map.get(userUUID).alive;
        break;
      }
      keyCounter2++;
    }
    if(keyCounter == keyCounter2) {
      return false;
    }
  }

  /*
  * @param numberofPlayer プレイヤーの人数
  */
  int playerCount () {
    //メソッドの機能概要：プレイヤーの人数を返す。
    int numberofPlayer = 0;
    for (UUID key : Map.keySet()) {
      numberofPlayer++;
    }
    return numberofPlayer;
  }

  /*
  * @param numberofSurvivingVillager 生きている村人陣営の人数
  */
  int survivingVillagersTeamSize () {
    //メソッドの機能概要：生きている村人陣営の人数を返す
    int numberofSurvivingVillager = 0;
    for (UUID key : Map.keySet()) {
      if (Map.get(key).role.equals("villager") || Map.get(key).role.equals("seer") ||
            Map.get(key).role.equals("necromancer") || Map.get(key).role.equals("knight") ||
              Map.get(key).role.equals("hunter") || Map.get(key).role.equals("brackKnight") ||
                Map.get(key).role.equals("freemasonary") || Map.get(key).role.equals("baker")) {

        if(Map.get(key).alive == true) {
          numberofSurvivingVillager++;
        }
      }
    }
    return numberofSurvivingVillager;
  }

  /*
  * @param numberofSurvivingWerewolf 生きている人狼陣営の人数
  */
  int survivingWerewolvesTeamSize () {
    //機能概要：生きている人狼陣営の人数を返す。
    int numberofSurvivingWerewolf = 0;
    for (UUID key : Map.keySet()) {
      if (Map.get(key).role.equals("werewolf") || Map.get(key).role.equals("madman") ||
          Map.get(key).role.equals("traitor")) {
            if(Map.get(key).alive == true) {
              numberofSurvivingWerewolf++;
            }
      }
    }
    return numberofSurvivingWerewolf;
  }

  /*
  * @param numberofSurvivingFoxSpirit 生きている妖狐陣営の人数
  */
  int survivingFoxSpiritsTeamSize () {
    //メソッドの機能概要：生きている妖狐陣営の人数を返す。
    int numberofSurvivingFoxSpirit = 0;
    for (UUID key : Map.keySet()) {
      if (Map.get(key).role.equals("foxSpirit")) {
        if(Map.get(key).alive == true) {
          numberofSurvivingFoxSpirit++;
        }
      }
    }
    return numberofSurvivingFoxSpirit;
  }

  /*
  * @param numberofSurvivingFool 生きている吊人陣営の人数
  */
  int survivingFoolsTeamSize () {
    //メソッドの機能概要：生きている吊人陣営の人数を返す。
    int numberofSurvivingFool = 0;
    for (UUID key : Map.keySet()) {
      if (Map.get(key).role.equals("fool") || Map.get(key).role.equals("phantomThief")) {
        if(Map.get(key).alive == true) {
          numberofSurvivingFool++;
        }
      }
    }
    return numberofSurvivingFool;
  }

  /*
  * @param numberofSurvivingBaker 生きているパン屋の人数
  */
  boolean isSurvivingBaker () {
    //機能概要：パン屋の生き残りがいるか調べ、生き残りがいるならtrueを返す。
    int numberofSurvivingBaker = 0;
    for (UUID key : Map.keySet()) {
      if (Map.get(key).role.equals("baker")) {
        if(Map.get(key).alive == true) {
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
  * @param numberofWerewolf 人狼の陣営の人数
  * @param WerewolfPlayerUUIDs 人狼の役職を持つプレイヤーのUUIDを入れるためのリスト
  */
  List<UUID> getWerewolfPlayerUUIDs () {
    //機能概要：人狼プレイヤー全員分のUUIDを返す。
    int　numberofWerewolf = 0;
    List<UUID> WerewolfPlayerUUIDs = new ArrayList<UUID> () ;
    for (UUID key : Map.keySet()) {
      if (Map.get(key).role.equals("werewolf")) {
        WerewolfPlayerUUIDs.add(numberofWerewolf, key) ;
        numberofWerewolf++;
      }
    }
    return WerewolfPlayerUUIDs;
  }

  /*
  * @param numberofDeadPlayer 死亡しているプレイヤーの数
  * @param DeadPlayerUUIDs 人狼の役職を持つプレイヤーのUUIDを入れるためのリスト
  */
  List<UUID> getDeadPlayerUUIDs () {
    //機能概要：死亡しているプレイヤー全員分のUUIDを返す。
    int numberofDeadPlayer = 0;
    List<UUID> DeadPlayerUUIDs = new ArrayList<UUID> () ;
    for (UUID key : Map.keySet()) {
      if (Map.get(key).alive == false) {
        DeadPlayerUUIDs.add(numberofDeadPlayer, key);
        numberofDeadPlayer++;
      }
    }
    return DeadPlayerUUIDs;
  }
  /*
  * @param freemasonaryUUID 共有者のUUIDを格納するための変数
  */
  public UUID getFreemasonaryPartnerUUID (UUID userUUID) {
    /*機能概要：
    * 共有者の相方のUUIDを返す。
    * ＊このメソッドの実行前にgetRoleを呼び、
    * 引数のUUIDが指すプレイヤーの役職が共有者なら実行すること。
    */
    UUID freemasonaryUUID;
    for (UUID key : Map.keySet()) {
      if (Map.get(key).role.equals("freemasonary") && !(key.equals(userUUID))) {
        freemasonaryUUID = key;
      }
    }
    return freemasonaryUUID;
  }

  /*
  * @param choisedUUID ランダムに取り出したUUIDを格納する変数
  * @param keyCounter mapに格納されているkeyの要素数を格納する変数
  * @param mapCounter　mapの要素数を格納する変数(keyCounterと役割は同じ)
  * @param listCounter　Listに格納されている要素数を格納する変数
  * @param checkListCounter 確認の際に使う、Listに格納されている要素数を格納する変数
  */
  UUID getRandomUser (List<UUID> notSelectedPlayers) {
    /*
    * 機能概要：
    * MapのkeyからランダムなUUIDを返す。
    * ただしListの中にあるプレイヤーは覗く。
    */
    UUID choisedUUID;
    int keyCounter = 0;
    int listCounter = 0;
    int checkListCounter = 0;
    int mapCounter = 0;
    //Mapに格納されている要素数を求める
    for (UUID key : Map.keySet()) {
      keyCounter++;
    }
    //Listに格納されている要素数を求める
    for(UUID s : notSelectedPlayers){
      listCounter++;
    }

    //Mapから取り出したkeyとList内のUUIDが一致しなくなるまでwhile
    Outer:
    while(1) {
      //Randomクラスのオブジェクトを生成
      Random　random = new Random ();
      //整数の変数randomValueに0~keyCounterのランダムな数字を代入
      int randomValue = random.nextInt(keyCounter);

      //変数の初期化
      mapCounter = 0;
      checkListCounter = 0;

      //Mapをfor文で回していき、randomValueの値になった時のkeyを取り出す
      for(String key : Map.keySet()) {
        if(mapCounter == randomValue) {
          choisedUUID = key;
          break;
        }
        mapCounter++;
      }
      /*
      * 確認としてList内のUUIDと1つずつ比べて、List内に同じUUIDが存在するならwhileに戻る
      *
      * checkListCounterとlistCounterが一致する場合(List内を最後まで確認してUUIDの一致がない場合)、
      * while文を抜けてUUIDを返す
      */
      Inner:
      for (UUID s : notSelectedPlayers) {
        if(choisedUUID.equals(s) {
          break Inner;
        }
        checkListCounter++;
      }
      if(checkListCounter == listCounter) {
        break Outer;
      }
    }
    return choisedUUID;
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
    RoleBreakdown eachRoleNum = new RoleBreakdown ();
    for (UUID key : Map.keySet()) {
      if(Map.get(key).role.equals("villager")) {
        eachRoleNum.villagersNum++;
      }
      else if(Map.get(key).role.equals("seer")) {
        eachRoleNum.seersNum++;
      }
      else if(Map.get(key).role.equals("necromancer")) {
        eachRoleNum.necromancersNum++;
      }
      else if(Map.get(key).role.equals("knight")) {
        eachRoleNum.knightsNum++;
      }
      else if(Map.get(key).role.equals("hunter")) {
        eachRoleNum.huntersNum++;
      }
      else if(Map.get(key).role.equals("brackKnight")) {
        eachRoleNum.brackKnightsNum++;
      }
      else if(Map.get(key).role.equals("freemasonary")) {
        eachRoleNum.freemasonariesNum++;
      }
      else if(Map.get(key).role.equals("baker")) {
        eachRoleNum.bakersNum++;
      }
      else if(Map.get(key).role.equals("werewolf")) {
        eachRoleNum.werewlovesNum++;
      }
      else if(Map.get(key).role.equals("madman")) {
        eachRoleNum.madmenNum++;
      }
      else if(Map.get(key).role.equals("traitor")) {
        eachRoleNum.traitorsNum++;
      }
      else if(Map.get(key).role.equals("foxSpirit")) {
        eachRoleNum.foxSpiritsNum++;
      }
      else if(Map.get(key).role.equals("fool")) {
        eachRoleNum.foolsNum++;
      }
      else if(Map.get(key).role.equals("phantomThief")) {
        eachRoleNum.phantomThievesNum++;
      }
    }
    return eachRoleNum;
  }

  void kill (UUID userUUID) {
    Map.get(userUUID).alive = false;
  }
}
