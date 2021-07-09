/**
 * チャットを表示したり保存したりするための情報を持つ構造体
 */

package werewolf.store.chat;

import java.util.UUID;



public class Message {
    //ゲームマスター（このプログラムの）UUID
    public static UUID GMUUID = UUID.fromString("1520cd65-64a9-43d2-8de2-355d08cc8b86");

    //発言した部屋のID
    public int roomID;
    /**
     * チャットの種類
     * 0:一般チャット
     * 1:人狼チャット
     * 2:墓場チャット
     */
    public int channel;
    //そのチャットで何番目の投稿か
    public int number;
    //書き込んだプレイヤーのUUID
    public UUID userUUID;
    //メッセージの本文
    public String text;

    public Message() {

    }
}
