package werewolf.process.chat;

import io.javalin.websocket.WsConnectContext;
import werewolf.store.chat.*;
import werewolf.store.room.RoomInfo;
import werewolf.store.user.UserProfile;

import java.util.*;

public class Chat {
    /**
     * 新しいウェブソケットのコネクションを登録
     * @param roomID チャットをする部屋のID
     * @param userUUID 新しくコネクションを確立したユーザのUUID
     * @param wsCtx 登録するコネクション
     */
    static public void addWsCtx(int roomID, UUID userUUID, WsConnectContext wsCtx) {
        ChatStore.addWsCtx(roomID, userUUID, wsCtx);
    }

    /**
     * 引数のユーザのコネクションを閉じて削除
     * @param userUUID コネクションを閉じる対象のUUID
     */
    static public void removeWsCtx(UUID userUUID) {
        int roomID = RoomInfo.whereRoom(userUUID);
        if (roomID == 0) {
            System.out.println(userUUID + "は部屋に参加していません");
            return;
        }
        ChatStore.removeWsCtx(roomID, userUUID);
    }

    /**
     * 部屋の参加者全てのコネクションを閉じて部屋を削除
     * @param roomID
     */
    static public void closeRoom(int roomID) {
        ChatStore.closeChatRoom(roomID);
    }

    /**
     * 引数のデータからそのチャットの読み取り許可がある人にチャットを送る
     * @param roomID
     * @param channel
     * @param userUUID
     * @param text
     * @return ステータスコード
     *      200:成功
     *      451チャットへ書き込めない
     *      453:チャットのコネクション情報が無い
     *
     */
    static public int broadcast(int roomID, int channel, UUID userUUID, String text) {
        //チャットへの書き込み許可が無かったらエラー
        //channelが不正な値でも書き込めずエラー
        if (!ChatStore.hasWritingPermission(roomID, userUUID, channel)) {
            return 451;
        }

        //userUUIDが指す名前を取得
        String userName = UserProfile.getNameAndIcon(userUUID).name;
        //送信用の構造体
        MessageForSending message = new MessageForSending(channel, userName, text);
        //全員のウェブソケットのコネクションを取得
        Set<ChatPermissionAndWsCtx> CPandWCSet = ChatStore.getCPandWCSet(roomID);
        //チャットのコネクション情報が無い
        if (CPandWCSet == null) {
            return 453;
        }

        //一般チャットへの書き込み
        if (channel == 0) {
            for (ChatPermissionAndWsCtx CPandWS : CPandWCSet) {
                CPandWS.wsCtx.send(message);
            }
        } else if (channel == 1) {
            //人狼チャットへの書き込み
            for (ChatPermissionAndWsCtx CPandWS : CPandWCSet) {
                if (CPandWS.werewolfChatReadingPermission) {
                    CPandWS.wsCtx.send(message);
                }
            }
        } else if (channel == 2) {
            //墓場チャットへの書き込み
            for (ChatPermissionAndWsCtx CPandWS : CPandWCSet) {
                if (CPandWS.graveChatReadingPermission) {
                    CPandWS.wsCtx.send(message);
                }
            }
        }
        return 200;
    }

    /**
     * Messageインスタンスを引数にして、チャットの読み取り許可がある人にチャットを送る
     * @param message 全てのフィールドがnullでないように
     * @return ステータスコード
     *      200:成功
     *      451チャットへ書き込めない
     *      453:チャットのコネクション情報が無い
     */
    static public int broadcast(Message message) {
        return broadcast(message.roomID, message.channel, message.userUUID, message.text);
    }

    /**
     * 引数のデータからそのチャットの読み取り許可がある人にチャットを送る
     * @param channel
     * @param userUUID
     * @param text
     * @return テータスコード
     *      200:成功
     *      451チャットへ書き込めない
     *      452:部屋に参加してない
     *      453:チャットのコネクション情報が無い
     */
    static public int broadcast(int channel, UUID userUUID, String text) {
        int roomID = RoomInfo.whereRoom(userUUID);
        if (roomID == 0) {
            return 452;
        }
        return broadcast(roomID, channel, userUUID, text);
    }

    /**
     * アクションのログを送信
     * 個人にしか見えない
     * @param roomID
     * @param userUUID
     * @param text
     */
    static public void DMannounce(int roomID, UUID userUUID, String text) {
        //ToDo debug用　消す
        //UUID testUser = UUID.fromString("46453234-dd1f-4ecc-abb7-ecfca363689f");
        //if (!userUUID.equals(testUser)) return;
        //ここまで
        ChatPermissionAndWsCtx CPandWC = ChatStore.getCPandWC(roomID, userUUID);
        MessageForSending message = new MessageForSending(-3, "GM", text);
        CPandWC.wsCtx.send(message);
    }

    /**
     * 引数に対応するチャットへ書き込めるようにする
     * @param roomID 対象の部屋ID
     * @param channel 書き込みを有効にするチャットの種類
     *                  0:一般チャット
     *                  1:人狼チャット
     *                  2:墓場チャット
     * @param userUUID 書き込めるようになるユーザのUUID
     */
    static public void enableWritingPermission(int roomID, int channel, UUID userUUID) {
        ChatPermissionAndWsCtx CPandWC = ChatStore.getCPandWC(roomID, userUUID);
        if (CPandWC != null) {
            if (channel == 0) {
                CPandWC.generalChatWritingPermission = true;
            } else if (channel == 1) {
                CPandWC.werewolfChatWritingPermission = true;
            } else if (channel == 2) {
                CPandWC.graveChatWritingPermission = true;
            }
        }
    }
    /**
     * 引数に対応するチャットへ書き込めなくする
     * @param roomID 対象の部屋ID
     * @param channel 書き込みを無効にするチャットの種類
     *                  0:一般チャット
     *                  1:人狼チャット
     *                  2:墓場チャット
     * @param userUUID 書き込めなくなるユーザのUUID
     */
    static public void disableWritingPermission(int roomID, int channel, UUID userUUID) {
        ChatPermissionAndWsCtx CPandWC = ChatStore.getCPandWC(roomID, userUUID);
        if (CPandWC != null) {
            if (channel == 0) {
                CPandWC.generalChatWritingPermission = false;
            } else if (channel == 1) {
                CPandWC.werewolfChatWritingPermission = false;
            } else if (channel == 2) {
                CPandWC.graveChatWritingPermission = false;
            }
        }
    }

    /**
     * 引数に対応するチャットを閲覧できるようにする
     * @param roomID 対象の部屋ID
     * @param channel 閲覧を有効にするチャットの種類
     *                  1:人狼チャット
     *                  2:墓場チャット
     * @param userUUID 閲覧できるようになるユーザのUUID
     */
    static public void enableReadingPermission(int roomID, int channel, UUID userUUID) {
        ChatPermissionAndWsCtx CPandWC = ChatStore.getCPandWC(roomID, userUUID);
        if (CPandWC != null) {
            if (channel == 1) {
                CPandWC.werewolfChatReadingPermission = true;
            } else if (channel == 2) {
                CPandWC.graveChatReadingPermission = true;
            }
        }
    }

    /**
     * 引数に対応するチャットを閲覧できなくする
     * @param roomID 対象の部屋ID
     * @param channel 閲覧を無効にするチャットの種類
     *                  1:人狼チャット
     *                  2:墓場チャット
     * @param userUUID 閲覧できなくなるユーザのUUID
     */
    static public void disableReadingPermission(int roomID, int channel, UUID userUUID) {
        ChatPermissionAndWsCtx CPandWC = ChatStore.getCPandWC(roomID, userUUID);
        if (CPandWC != null) {
            if (channel == 1) {
                CPandWC.werewolfChatReadingPermission = false;
            } else if (channel == 2) {
                CPandWC.graveChatReadingPermission = false;
            }
        }
    }

    /**
     * システムからのアナウンスをチャットに表示する
     * @param roomID アナウンスする部屋のID
     * @param text 内容
     * @param channel -1:GMのアナウンス
     *                -2:GMのアナウンス ゲーム情報の更新を知らせる
     */
    static public void announce(int roomID, String text, int channel) {
        //送信用の構造体
        //一般チャットでアナウンスする
        MessageForSending message = new MessageForSending(channel, "ゲームマスター", text);
        //全員のウェブソケットのコネクションを取得
        Set<ChatPermissionAndWsCtx> CPandWCSet = ChatStore.getCPandWCSet(roomID);

        if (CPandWCSet == null) {
            return;
        }
        for (ChatPermissionAndWsCtx CPandWS : CPandWCSet) {
            CPandWS.wsCtx.send(message);
        }
    }



}
