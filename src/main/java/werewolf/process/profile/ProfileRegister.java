/**
*プロフィール登録をする
*
* @version 2.0
* @author
*/

package werewolf.process.profile;

import java.util.*;
import werewolf.store.room.*;
import werewolf.store.user.*;

public class ProfileRegister {

    /**
     * プロフィールを登録
     * ステータスコードを返す
     * @param userUUID
     * @param roomID　roomIDが指す部屋の参加者とプロフィールの被りが無いか確認
     *              部屋に参加画面から呼ばれたら参加しようとしてる部屋のroomID
     *              ゲームチャット画面で呼ばれたら参加してる部屋のroomID
     *              それ以外からで呼ばれたらroomIDは0
     * @param userName
     * @param icon
     * @return ステータスコード
     * 200番台　登録成功
     * 400番台　エラー
     * 10の位が 9：ユーザネームが不正な値 8：ユーザネームが不正ではないが部屋いにる他のユーザと被っている
     * 1の位が 9：アイコンが不正な値 8：アイコンが不正ではないが部屋にいる他のユーザと被っている
     * 477：その他エラー
     */
    public static int register(UUID userUUID, int roomID, String userName, int icon) {
        int statusCode = 200;
        int nameCode = 0;
        int iconCode = 0;

        //入力が不正でないか確認
        if (userUUID == null) {
            System.out.println("ProfileRegister：UUIDが無い");
            return 477;
        }
        //ユーザネームは10文字まで
        if (userName == null || userName.equals("") || userName.length() > 10) {
            statusCode = 400;
            nameCode = 9;
        }
        //アイコン画像は1から20まで
        if ( icon <= 0 || 20 < icon) {
            statusCode = 400;
            iconCode = 9;
        }
        //入力が不正ならエラー
        if (statusCode != 200) {
            return statusCode + 10*nameCode + iconCode;
        }

        //roomIDが0(被りチェック無し)でエラーが無いならプロフィールを登録
        if (roomID == 0) {
            UserProfile.register(userUUID, userName, icon);
            return statusCode;
        }

        //roomIDが指す部屋のユーザとプロフィール被りが無いか確認
        int checkResult;
        checkResult = checkDuplicates(userUUID, roomID, userName, icon);
        System.out.println("checkResult=" + checkResult);
        //被りかエラーが有り
        if (checkResult != 0) {
            statusCode = 400;

            if (checkResult <= -4) {
                return 477;
            } else if (checkResult == -3) {
                if (nameCode != 9) {
                    nameCode = 8;
                }
                if (iconCode != 9) {
                    statusCode = 400;
                    iconCode = 8;
                }
            } else if (checkResult == -2) {
                if (iconCode != 9) {
                    iconCode = 8;
                }
            } else if (checkResult == -1) {
                if (nameCode != 9) {
                    nameCode = 8;
                }
            }
        } else {
            //被り無しならプロフィールを登録
            UserProfile.register(userUUID, userName, icon);
            return statusCode;
        }
        System.out.println(statusCode + 10*nameCode + iconCode);
        return statusCode + 10*nameCode + iconCode;
    }

    /**
     * 引数のroomIDの部屋に同じユーザネームかアイコンをもったユーザがいないか確認
     * ただしuserUUIdが指すユーザとは被ってもよい
     * @param userUUID
     * @param roomID
     * @param userName
     * @param icon
     * @return
     * 0：被り無し
     * -1：ユーザネームの被りあり
     * -2：アイコンの被りあり
     * -3：ユーザネームとアイコンに被りあり
     * -4：部屋が存在しない
     * -5：データの不整合
     */
    public static int checkDuplicates(UUID userUUID, int roomID, String userName, int icon) {
        //部屋のユーザを取得
        Set<UUID> sameRoomMembers = RoomInfo.getParticipantsSet(roomID);
        //部屋が存在しない
        if (sameRoomMembers == null) {
            return -4;
        }
        //名前の被りがあるなら-1
        int nameDuplicates = 0;
        //アイコンの被りがあるなら-2
        int iconDuplicates = 0;

        //プレイヤーネームとアイコンの被りをチェック
        for (UUID m : sameRoomMembers) {
            //プロフィール登録対象のUUIDならスルー
            if (m == userUUID) {
                continue;
            }

            //部屋の参加者のユーザネームとアイコン画像の番号を取得
            NameAndIcon nameAndIcon = UserProfile.getNameAndIcon(m);

            //ここのif文は成り立たないはず
            if(nameAndIcon == null) {
                System.out.println("ProfileRegister：UserProfileとRoomInfoのデータがかみ合ってない");
                return -5;
            }

            if (nameDuplicates != -1 && nameAndIcon.name.equals(userName)) {
                nameDuplicates = -1;
            }
            if (iconDuplicates != -2 && nameAndIcon.icon == icon) {
                iconDuplicates = -2;
            }
        }

        return nameDuplicates + iconDuplicates;
    }
}
