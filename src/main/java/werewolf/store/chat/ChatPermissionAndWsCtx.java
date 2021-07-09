package werewolf.store.chat;

import java.util.UUID;
import io.javalin.websocket.WsConnectContext;

public class ChatPermissionAndWsCtx {
    public boolean generalChatWritingPermission = true;
    public boolean werewolfChatWritingPermission = false;
    public boolean werewolfChatReadingPermission = false;
    public boolean graveChatWritingPermission = false;
    public boolean graveChatReadingPermission = false;
    public WsConnectContext wsCtx;
}
