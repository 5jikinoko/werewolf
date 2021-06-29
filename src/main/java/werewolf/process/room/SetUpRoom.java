/**
 * 部屋の作成と部屋の設定する
 *
 * @version 1.0
 * @author
 */

package werewolf.process.room;

import io.javalin.http.Context;
import werewolf.store.room.*;

import java.util.UUID;

public class SetUpRoom {
    /**
     * 新しく部屋を作る
     * @param ctx リクエストの内容
     * @return ステータスコード
     * 200:成功
     * 496:範囲外の値
     * 497:UUIDが存在しない
     * 499:入力が不正
     */
    public static int CreateRoom(Context ctx) {
        //リクエストを送ったユーザのUUIDを取得
        String stringUUID = ctx.cookie("UUID");
        if (stringUUID == null) {
            System.out.println("SetUpRoom:UUIDがcookieにない");
            return 497;
        }
        UUID hostUUID = UUID.fromString(stringUUID);

        //部屋の名前を取得
        String roomName = ctx.formParam("roomName");
        if (roomName == null) {
            return 499;
        } else if (roomName.length() > 10) {
            //文字数制限を超えた
            //ToDo 字数制限決定
            return 496;
        }

        //パスワードを取得
        String pass = ctx.formParam("pass");
        if (pass == null) {
            return 499;
        } else if (pass.length() > 15) {
            //字数制限を超えた
            return 496;
        } else if (pass == "") {
            System.out.println("パスワード無しの部屋");
        }

        //人数制限を取得
        Integer maxMember = Integer.valueOf(ctx.formParam("maxMember"));
        if (maxMember == null) {
            return 499;
        } else if ( maxMember <= 3 || 20 < maxMember) {
            return 496;
        }

        //紹介文を取得
        String introduction = ctx.formParam("introduction");
        if (introduction == null) {
            return 499;
        } else if (introduction.length() > 500) {
            //ToDo 文字数制限を決める
            return 496;
        }

        //部屋の設定をもつインスタンスを生成
        RoomSettings roomSettings = new RoomSettings(
                hostUUID, roomName, pass, maxMember, introduction);

        //部屋の設定とともに新しい部屋を作成
        int roomID = RoomInfo.createRoom(roomSettings);
        System.out.println(hostUUID + "が部屋ID" + roomID + "で部屋を作成");
        return 200;
    }
}
