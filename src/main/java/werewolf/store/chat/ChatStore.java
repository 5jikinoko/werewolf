package werewolf.store.chat;

import io.javalin.websocket.WsConnectContext;
import java.util.*;

public class ChatStore {

    public static Map<Integer, Map<UUID, ChatPermissionAndWsCtx>> roomIDtoChatMember
                                = new HashMap<Integer, Map<UUID, ChatPermissionAndWsCtx>>();

    /**
     * 部屋IDに対応したチャットの部屋を作る
     * @param roomID 部屋のID
     */
    public static void createRoomChat(int roomID) {
        roomIDtoChatMember.put(roomID, new HashMap<UUID, ChatPermissionAndWsCtx>());
    }

    /**
     * 部屋のチャット情報に新しく参加者のウェブソケットのコネクションをUUIDと対応付けて保存
     * @param roomID
     * @param userUUID
     * @param wsCtx
     */
    public static void addWsCtx(int roomID, UUID userUUID, WsConnectContext wsCtx) {
        Map<UUID, ChatPermissionAndWsCtx> chatUsers = roomIDtoChatMember.get(roomID);
        ChatPermissionAndWsCtx newCPandWC = new ChatPermissionAndWsCtx();
        newCPandWC.wsCtx = wsCtx;
        chatUsers.put(userUUID, newCPandWC);
    }

    /**
     * 対象のウェブソケットのコネクションを閉じる
     * 対象のChatPermissionAndWsCtxをMapから消す
     * @param roomID 対象がいる部屋のID
     * @param userUUID セッション終了対象のUUID
     */
    public static void removeWsCtx(int roomID, UUID userUUID) {
        Map<UUID, ChatPermissionAndWsCtx> chatUsers = roomIDtoChatMember.get(roomID);
        ChatPermissionAndWsCtx newCPandWC = chatUsers.get(userUUID);
        //newCPandWC.wsCtx.session.close(200, "終了!!");
        chatUsers.remove(userUUID);
    }

    /**
     * 一つの部屋の参加者全員のウェブソケットのコネクションを閉じる
     * Mapからその部屋の情報を消す
     * @param roomID
     */
    public static void closeChatRoom(int roomID) {
        Map<UUID, ChatPermissionAndWsCtx> chatUsers = roomIDtoChatMember.get(roomID);
        if (chatUsers == null) {
            return;
        }
        //全てのコネクションを閉じる
        for (ChatPermissionAndWsCtx CPandWC : chatUsers.values()) {
            CPandWC.wsCtx.session.close(200, "終了!!");
        }
        //部屋の情報を消す
        roomIDtoChatMember.remove(roomID);
    }

    /**
     *
     * 引数の部屋に参加しているプレイヤーのウェブソケットのコネクションと読み書き権限のSetを返す
     * @param roomID
     * @return 引数の部屋に参加しているプレイヤーのウェブソケットのコネクションと読み書き権限のSet
     */
    public static Set<ChatPermissionAndWsCtx> getCPandWCSet(int roomID) {
        Map<UUID, ChatPermissionAndWsCtx> chatUser = roomIDtoChatMember.get(roomID);
        Set<ChatPermissionAndWsCtx> result = new HashSet<ChatPermissionAndWsCtx>();
        for (ChatPermissionAndWsCtx CPandWC : chatUser.values()) {
            result.add(CPandWC);
        }
        return result;
    }

    /**
     * userがその部屋でチャットの閲覧ができるかチェック
     * @param roomID
     * @param userUUID
     * @param channel
     * @return true:閲覧できる
     */
    public static boolean hasReadingPermission(int roomID, UUID userUUID, int channel) {
        //一般チャットは常に閲覧できる
        if (channel == 0) {
            return true;
        }
        Map<UUID, ChatPermissionAndWsCtx> chatUser = roomIDtoChatMember.get(roomID);
        ChatPermissionAndWsCtx CPandWC = chatUser.get(userUUID);
        if (CPandWC == null) {
            return false;
        }

        if (channel == 1) {
            return CPandWC.werewolfChatReadingPermission;
        } else  if (channel == 2) {
            return CPandWC.graveChatReadingPermission;
        }

        //ここは呼ばれないはず
        System.out.println("hasReadingPermission: 引数のchannelが異常(" + channel +")");
        return false;
    }

    public static boolean hasWritingPermission(int roomID, UUID userUUID, int channel) {
        Map<UUID, ChatPermissionAndWsCtx> chatUser = roomIDtoChatMember.get(roomID);
        ChatPermissionAndWsCtx CPandWC = chatUser.get(userUUID);
        if (CPandWC == null) {
            return false;
        }

        if (channel == 0) {
            return CPandWC.generalChatWritingPermission;
        } else if (channel == 1) {
            return CPandWC.werewolfChatWritingPermission;
        } else if (channel == 2) {
            return CPandWC.graveChatWritingPermission;
        }

        //ここは呼ばれないはず
        System.out.println("hasWritingPermission: 引数のchannelが異常(" + channel +")");
        return false;
    }

    public static ChatPermissionAndWsCtx getCPandWC(int roomID, UUID userUUID) {
        Map<UUID, ChatPermissionAndWsCtx> chatUsers = roomIDtoChatMember.get(roomID);
        return chatUsers.get(userUUID);
    }

}
