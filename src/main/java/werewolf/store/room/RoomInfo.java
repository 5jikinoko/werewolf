/**
* 部屋の情報を管理する
*
* @version 2.0
* @author
*/

package werewolf.store.room;

import java.util.*;

import werewolf.process.profile.ProfileGetter;
import werewolf.store.user.*;

public class RoomInfo {
    static private  Map<Integer, RoomSettings> roomSettingsMap = new HashMap<Integer, RoomSettings>();
    static private Map<Integer, Set<UUID>> roomIDtoUserUUIDSet = new HashMap<Integer, Set<UUID>>();
    static private Map<UUID, Integer> userUUIDtoRoomID = new HashMap<UUID, Integer>();
    static int nextRoomID = 1;

    /**
     * パスワードを認証する
     * @param roomID
     * @param pass
     * @return 認証結果
     * -2：部屋が存在しない
     * -1：パスワードが間違っていた
     * 1：パスワードがあっていたか、部屋にパスワードがなかった
     */
    public static int verifyPass(int roomID, String pass) {

        //部屋が存在しないなら認証失敗
        RoomSettings roomSettings = roomSettingsMap.get(roomID);
        if (roomSettings == null) {
            return -2;
        }

        //パスワード認証
        if (roomSettings.pass == null || roomSettings.pass.equals("") || roomSettings.pass.equals(pass)) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * 部屋に入る。
     * パスワード認証をしてから実行すること
     * @param userUUID
     * @param roomID
     * @return false：部屋が存在しないか定員オーバー true：部屋に入れた
     */
    public static boolean enterRoom(UUID userUUID, int roomID) {

        //前に参加していた部屋の情報が残っているなら消す
        exitRoom(userUUID);

        Set<UUID> userSet = roomIDtoUserUUIDSet.get(roomID);

        //部屋が存在しないなら入れない
        if ( userSet == null ) {
            return false;
        }

        RoomSettings roomSettings = roomSettingsMap.get(roomID);

        //部屋が定員オーバーなら入れない
        if ( userSet.size() >= roomSettings.maxMember ) {
            return false;
        }

        //ユーザ情報を登録
        userSet.add(userUUID);
        userUUIDtoRoomID.put(userUUID, roomID);

        return true;
    }

    /**
     * UUIDが指すユーザを参加している部屋から退出させる
     * @param userUUID 退出させるユーザ
     */
    public static void exitRoom(UUID userUUID) {

        //参加している部屋を取得
        Integer oldRoomID = userUUIDtoRoomID.get(userUUID);
        if ( oldRoomID != null ) {
            //参加している部屋があるなら退出
            Set<UUID> userSet = roomIDtoUserUUIDSet.get(oldRoomID);
            userSet.remove(userUUID);
        }
        //ユーザが参加している部屋の情報をなくす
        userUUIDtoRoomID.remove(userUUID);
    }

    /**
     * 部屋を閉じる
     * @param roomID
     */
    public static void closeRoom(int roomID) {
        //部屋の設定情報を消す
        roomSettingsMap.remove(roomID);
        //部屋の参加者一覧を取得
        Set<UUID> users = roomIDtoUserUUIDSet.get(roomID);
        //参加している部屋の情報を消す
        for (UUID userUUID : users) {
            userUUIDtoRoomID.remove(userUUID);
        }
        //部屋の参加者一覧の情報を消す
        roomIDtoUserUUIDSet.remove(roomID);
    }

    /**
     * 新しく部屋を作る
     * roomSettingsの値が正常化は呼び出す前に確認すること
     * @param roomSettings
     * @return 新しくできた部屋のroomID
     */
    public static int createRoom(RoomSettings roomSettings) {

        //ToDo　部屋の数を1000までにする処理

        int roomID = nextRoomID;
        ++nextRoomID;
        UUID hostUUID = roomSettings.hostUUID;

        System.out.println(ProfileGetter.getProfile(hostUUID).name + "がroomID=" + roomID + " あいことば[" + roomSettings.pass + "]で部屋を作りました");
        Set<UUID> userSet = new HashSet<UUID>();
        userSet.add(hostUUID);
        roomIDtoUserUUIDSet.put(roomID, userSet);

        userUUIDtoRoomID.put(hostUUID, roomID);

        roomSettingsMap.put(roomID, roomSettings);

        return roomID;
    }

    /**
     * 引数のユーザが参加している部屋のroomIDを返す
     * @param userUUID
     * @return 参加している部屋のroomID どこにも参加してないなら0
     */
    public static int whereRoom(UUID userUUID) {
        Integer roomID = userUUIDtoRoomID.get(userUUID);
        System.out.println("whereRoom:" + userUUID.toString() + "はroomID=" + roomID + "にいます");
        if(roomID == null) {
            return 0;
        }
        return roomID;
    }

    /**
     * 部屋の参加者の一覧を返す
     * @param roomID
     * @return 参加者のUUIDのSet どこにも参加してなかったらnull
     */
    public static Set<UUID> getParticipantsSet(int roomID) {
        return roomIDtoUserUUIDSet.get(roomID);
    }

    /**
     * 部屋の参加者の一覧を返す
     * @param userUUID
     * @return 参加者のUUIDのSet どこにも参加してなかったらnull
     */
    public static Set<UUID> getParticipantsSetByUUID(UUID userUUID) {
        Integer roomID = userUUIDtoRoomID.get(userUUID);
        if (roomID == null) {
            return null;
        }
        return roomIDtoUserUUIDSet.get(roomID);
    }

    /**
     * 部屋の設定を返す
     * @param roomID
     * @return 部屋の設定（RoomSettings型）。部屋が存在しないならnull
     */
    public static RoomSettings getRoomSettings(int roomID) {
        return roomSettingsMap.get(roomID);
    }

    /**
     * 部屋のIDとRoomSettingsからRoomInfoForSendingを作るメソッド
     * getRoomInfoForSendingListとgetRoomInfoForSending内で使う
     * @param roomID
     * @param rs
     * @return フロントで部屋の情報を表示するときに使う構造体
     */
    private static RoomInfoForSending makeRoomInfoForSending(int roomID, RoomSettings rs) {
        //部屋の情報を持つ構造体を生成
        RoomInfoForSending roomInfo = new RoomInfoForSending();
        //部屋のIDを格納
        roomInfo.roomID = roomID;
        //部屋の名前を格納
        roomInfo.roomName = rs.roomName;
        //部屋主の名前を格納
        NameAndIcon hostNameAndIcon = UserProfile.getNameAndIcon(rs.hostUUID);
        roomInfo.hostName = hostNameAndIcon.name;
        //人数制限を格納
        roomInfo.maxMember = rs.maxMember;
        //現在の人数を格納
        Set memberSet = getParticipantsSet(roomInfo.roomID);
        roomInfo.nowMember = memberSet.size();
        //パススワードの有無を格納
        if (rs.pass == null || rs.pass.equals("")) {
            //パスワードが無い
            roomInfo.existPass = false;
        } else {
            //パスワードがある
            roomInfo.existPass = true;
        }
        //紹介文を格納
        roomInfo.introduction = rs.introduction;

        return  roomInfo;
    }

    /**
     * フロントで使う部屋の情報を持つ構造体のリストを生成して返す
     * @return 部屋の情報のリスト
     */
    public static List<RoomInfoForSending> getRoomInfoForSendingList() {
        //部屋の情報を持つリスト
        List<RoomInfoForSending> result = new ArrayList<RoomInfoForSending>();

        //部屋をの情報をクライアント向けに変換して格納
        for (Map.Entry<Integer, RoomSettings> e : roomSettingsMap.entrySet()) {
            //eに対応したRoomInfoForSendingを取得
            RoomInfoForSending roomInfo = makeRoomInfoForSending(e.getKey(), e.getValue());
            //リストに格納
            result.add(roomInfo);
        }
        System.out.println(result);
        return result;
    }

    /**
     * フロントで使う部屋の情報を持つ構造体を生成して返す
     * @param roomID
     * @return 部屋の情報
     */
    public static RoomInfoForSending getRoomInfoForSending(int roomID) {
        //部屋の設定を取得
        RoomSettings rs = roomSettingsMap.get(roomID);
        //makeRoomInfoForSendingでrsに対応するRoomInfoForSendingを取得
        return makeRoomInfoForSending(roomID, rs);
    }

    /**
     * 部屋主のUUIDを取得
     * @param roomID 探す対象の部屋ID
     * @return 部屋主のUUID
     */
    public static UUID getHostUUID(int roomID) {
        RoomSettings rs = roomSettingsMap.get(roomID);
        if (rs == null) {
            System.out.println("RoomSettingsが取得できませんでした");
            return null;
        }
        return rs.hostUUID;
    }

    /**
     * 部屋主を変更する
     * @param roomID 変更する部屋の部屋ID
     * @param newHost 新しい部屋主のUUID
     */
    public static void setHostUUID(int roomID, UUID newHost) {
        RoomSettings rs = roomSettingsMap.get(roomID);
        if (rs == null) {
            System.out.println("RoomSettingsが取得できませんでした");
            return;
        }
        rs.hostUUID = newHost;
    }
}
