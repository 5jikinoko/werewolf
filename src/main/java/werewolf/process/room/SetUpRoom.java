/**
 * 部屋の作成と部屋の設定する
 *
 * @version 2.0
 * @author
 */

package werewolf.process.room;

import io.javalin.http.Context;
import werewolf.store.room.*;
import werewolf.store.chat.ChatStore;

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
        String roomName = ctx.formParam("roomName", "");
        if (roomName == "") {
            return 499;
        } else if (roomName.length() > 10) {
            //文字数制限を超えた
            return 496;
        }

        //パスワードを取得
        String pass = ctx.formParam("pass", "");
        if (pass.length() > 15) {
            //字数制限を超えた
            return 496;
        }

        //人数制限を取得
        int maxMember = Integer.valueOf(ctx.formParam("maxMember", "0"));
        if (maxMember == 0) {
            return 499;
        } else if ( maxMember <= 3 || 20 < maxMember) {
            return 496;
        }

        //紹介文を取得
        String introduction = ctx.formParam("introduction");
        if (introduction == null) {
            return 499;
        } else if (introduction.length() > 500) {
            return 496;
        }

        //部屋の設定をもつインスタンスを生成
        RoomSettings roomSettings = new RoomSettings(
                hostUUID, roomName, pass, maxMember, introduction);

        //部屋の設定とともに新しい部屋を作成
        int roomID = RoomInfo.createRoom(roomSettings);
        //cookieに登録
        ctx.cookie("roomID", String.valueOf(roomID));
        //チャットの設定
        ChatStore.createRoomChat(roomID);
        System.out.println(hostUUID + "が部屋ID" + roomID + "で部屋を作成");
        System.out.println("roomName:" + roomName + "   pass:" + pass + "   max:" + maxMember + "   紹介文:" + introduction);
        return 200;
    }
}
