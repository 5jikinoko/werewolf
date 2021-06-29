/**
* クラス概要:ゲームの退出処理を記述する
*
* @version 1.0
* @author al19067
*/

/*
* 進捗：部屋に参加者がいない時の記述
*/

import java.util.*;
import werewolf.store.room.*;
class ExitRoom {

  /*
  * @param roomID 引数が示すユーザが参加している部屋のIDを格納する変数
  * @param playersCounter 参加している部屋の人数を格納する変数
  * @param playersCounter2 現在見ているSet内のプレイヤーの番号を格納する変数
  * @param playerSet 参加している部屋のユーザ一覧を格納するSet
  * メソッドの機能概要：ユーザ情報管理に「参加している部屋の退出」を依頼する
  */
  void canExit (String UUID) {
    int playersCounter = 0;
    int playersCounter2 = 0;
    UUID newHost;
    int roomID = whereRoom(UUID);
    //部屋に参加していない場合、break
    if(RoomID = 0) {
      break;
    }
    //部屋主の場合、部屋主を変える。
    if(UUID.equals(getHostUUID(roomID))) {
      //部屋の参加者一覧を取得->元々の部屋主の次の人を部屋主にする
      Set<UUID> playersSet = new HashSet<UUID>();
      for(int UUID : set) {
        playersSet.add(getParticipantsSet(UUID).get(playersCounter));
        playersCounter++;
      }
      for(int UUID : set) {
        //SetのplayersCounter2番目に格納されているUUIDが引数のプレイヤーと一致した場合、
        //その次のプレイヤーを部屋主にする
        if(playersSet.get(playersCounter).equals(UUID)) {
          //部屋主が抜けたことで部屋にプレイヤーがいなくなった場合、部屋を閉じる
          if(playerCounter == 1) {
            closeRoom(roomID);
          }
          //部屋主がSetに格納されている一番後ろのプレイヤーだった場合、Setの最初のプレイヤーを部屋主へ
          else if(playersSet.get(playersCounter2 + 1) == null) {
            newHost = playersSet.get(0);
          }
          else {
            newHost = playersSet.get(playersCounter2 + 1);
          }
          break;
        }
        playersCounter2++;
      }
      setHostUUID(roomID,newHost);
    }
    //部屋の退出処理
    exitRoom(userUUID);
    }
  }
}
