package werewolf.store;

import io.javalin.websocket.WsConnectContext;
import java.util.*;

public class ChatStore {
    public static Map<Integer, Set<WsConnectContext>> roomIDtoChatMember
                                                    = new HashMap<Integer, Set<WsConnectContext>>();
    public static void createRoomChat(int roomID) {
        roomIDtoChatMember.put(roomID, new HashSet<WsConnectContext>());
    }

    public static void addWsCtx(int roomID, WsConnectContext ctx) {
        Set<WsConnectContext> ctxSet = roomIDtoChatMember.get(roomID);
        ctxSet.add(ctx);
    }

    public static Set<WsConnectContext> getWsCtxSet(int roomID) {
        return roomIDtoChatMember.get(roomID);
    }


}
