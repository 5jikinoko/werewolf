/**
 * プロフィール情報を取得する
 *
 * @version 1.0
 * @author
 */

package werewolf.process.profile;

import werewolf.store.user.NameAndIcon;

import java.util.*;
import werewolf.store.room.*;
import werewolf.store.user.*;

public class ProfileGetter {

    /**
     * プレイヤーネームとアイコン画像の番号を取得
     * @param userUUID
     * @return 存在しな場合null
     */
    public static NameAndIcon getProfile(UUID userUUID) {
        return UserProfile.getNameAndIcon(userUUID);
    }

    /**
     * 引数の部屋IDが指す部屋の参加者の名前とアイコン画像の番号をListで取得
     * @param roomID
     * @return 部屋が存在しない場合null
     */
    public static List<NameAndIcon> getRoomMemberProfile(int roomID) {
        Set<UUID> users = RoomInfo.getParticipantsSet(roomID);
        if (users == null) {
            System.out.println("ProfileGetter:部屋見つかりません");
            return null;
        }
        return getRoomMemberProfileSub(users);
    }


    /**
     * getRoomMemberProfileの処理に使う内部メソッド
     * 引数のユーザのセットからそのユーザーたちのユーザネームとアイコン画像の番号のリストを取得
     * @param users
     * @return
     */
    private static List<NameAndIcon> getRoomMemberProfileSub(Set<UUID> users) {
        List<NameAndIcon> result = new ArrayList<NameAndIcon>();
        for (UUID userUUID : users) {
            NameAndIcon nameAndIcon = UserProfile.getNameAndIcon(userUUID);
            if (nameAndIcon == null) {
                System.out.println("ProfileGetter:エラー");
            }
            result.add(nameAndIcon);
        }
        return result;
    }

    /**
     * 引数の部屋で使われていないアイコン画像の番号を取得
     * @param roomID
     * @return その部屋で使われていないアイコン画像の番号の一覧
     */
    public static List<Integer> notUsedIcon(int roomID) {
        Set<UUID> users = RoomInfo.getParticipantsSet(roomID);
        List<Integer> result = new ArrayList<Integer>();

        //1から20の数値が入ったリストを作る
        for(int i = 1; i <= 20; ++i) {
            result.add(Integer.valueOf(i));
        }
        //リストから参加者のアイコン画像の番号を取り除く
        for (UUID userUUID : users) {
            result.remove(Integer.valueOf( UserProfile.getNameAndIcon(userUUID).icon ));
        }
        return result;
    }

    /**
     * 引数の部屋で使われている参加者のユーザネームの一覧を取得
     * @param roomID
     * @return その部屋で使われているユーザネームの一覧
     */
    public static List<String> UsedName(int roomID) {
        Set<UUID> users = RoomInfo.getParticipantsSet(roomID);
        List<String> result = new ArrayList<String>();

        for (UUID userUUID : users) {
            result.add(UserProfile.getNameAndIcon(userUUID).name);
        }
        return result;
    }

}
