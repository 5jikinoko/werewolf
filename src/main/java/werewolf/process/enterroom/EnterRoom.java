/**
 * 部屋参加処理を行う
 *
 * @version 1.0
 * @author
 */

package werewolf.process.enterroom;


import io.javalin.http.Context;
import werewolf.store.room.*;

import java.util.UUID;

public class EnterRoom {

    /**
     * 部屋に参加する
     * @param ctx リクエストの内容 (自分のUUID, 参加したい部屋のroomID,パスワード)
     * @return ステータスコード
     * 200:成功
     * 401:パスワード認証失敗
     * 555:部屋が定員オーバーまたは部屋が無くなっています
     * 497:UUIDが存在しない
     * 498:部屋が存在しない
     * 499:入力が不正
     */
    public static int enterRoom(Context ctx) {
        //UUIDを取得
        UUID userUUID = UUID.fromString(ctx.cookie("UUID"));
        if (userUUID == null) {
            //UUIDがcookieにない
            return 497;
        }
        //参加したい部屋の部屋IDを取得
        Integer roomID = Integer.valueOf(ctx.formParam("roomID"));
        if (roomID == null) {
            return 499;
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
            return 200;
        } else {
            ///部屋が定員オーバーまたは部屋が無くなっています
            return 555;
        }
    }


}
