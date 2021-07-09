/**
 * 部屋参加処理を行う
 *
 * @version 2.0
 * @author
 */

package werewolf.process.enterroom;


import io.javalin.http.Context;
import werewolf.process.game.GameRoom;
import werewolf.store.room.*;
import werewolf.store.user.UserProfile;

import java.util.UUID;

public class EnterRoom {

    /**
     * 部屋に参加する
     * @param ctx リクエストの内容 (自分のUUID, 参加したい部屋のroomID,パスワード)
     * @return ステータスコード
     * 200:成功
     * 201:既に参加している
     * 401:パスワード認証失敗
     * 554:ゲームが始まっている
     * 555:部屋が定員オーバーまたは部屋が無くなっています
     * 466:プロフィール登録をしていない
     * 497:UUIDが存在しない
     * 498:部屋が存在しない
     * 499:入力が不正
     */
    public static int enterRoom(Context ctx) {

        //UUIDを取得
        String stringUUID = ctx.cookie("UUID");

        if (stringUUID == null) {
            //UUIDがcookieにない
            return 497;
        }
        UUID userUUID = UUID.fromString(stringUUID);

        if (UserProfile.getNameAndIcon(userUUID) == null) {
            return 466;
        }

        //参加したい部屋の部屋IDを取得
        int roomID = Integer.parseInt(ctx.formParam("roomID", "0"));
        if (roomID == 0) {
            return 499;
        }

        //既に参加している
        if (RoomInfo.whereRoom(userUUID) == roomID) {
            return 201;
        }

        //ゲームが始まっている
        if (GameRoom.isStarted(roomID)) {
            return 554;
        }

        //パスワード認証
        int verifyResult = RoomInfo.verifyPass(roomID, ctx.formParam("pass"));

        if (verifyResult == -2) {
            //部屋が無い
            return 498;
        } else if (verifyResult == -1) {
            //パスワードが違う
            return 401;
        }

        //部屋に参加
        boolean result = RoomInfo.enterRoom(userUUID, roomID);
        if (result) {
            //参加成功
            //cookieに登録
            ctx.cookie("roomID", String.valueOf(roomID));
            return 200;
        } else {
            ///部屋が定員オーバーまたは部屋が無くなっています
            return 555;
        }
    }


}
