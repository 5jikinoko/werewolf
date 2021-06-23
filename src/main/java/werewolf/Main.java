package werewolf;

import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;
import io.javalin.http.staticfiles.Location;
import java.util.*;

import io.javalin.websocket.WsConnectContext;
import werewolf.store.*;

public class Main {

    static int i=0;

    public static void main(String[] args) {

        int roomID = RoomInfo.createRoom();

        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/public");
        }).start(51000);

        app.get("/hello", ctx -> {
            Map<String, String > cookieMap = ctx.cookieMap();
            for (Map.Entry<String, String> cookie : cookieMap.entrySet()) {
                System.out.println(cookie);
            }
            //ctx.result(ctx.method());
            if (ctx.cookie("UUID") == null) {
                UUID userUUID = UUID.randomUUID();
                ctx.cookie("UUID", userUUID.toString());
                UserProfile.newUser(i + "番目のユーザ", i);
                RoomInfo.enterRoom(userUUID, roomID);
            }
            ctx.html("test.html");
        });

        app.ws("/websocket", ws -> {

            ws.onConnect(ctx -> {

                UUID userUUID = UUID.fromString(ctx.cookie("UUID"));
                RoomInfo.enterRoom(userUUID, roomID);

                if (userUUID == null) {
                    System.out.println("no cookie");
                } else {
                    System.out.println("connect to " + userUUID + " in room" + RoomInfo.whereRoom(userUUID));
                    ChatStore.addWsCtx(roomID, ctx);
                    MessageForSending message = new MessageForSending(1, "GM", ++i + "番目のユーザが参加しました");
                    Set<WsConnectContext> ctxSet = ChatStore.getWsCtxSet(roomID);
                    for (WsConnectContext wsCtx : ctxSet) {
                        wsCtx.send(message);
                    }
                }
                System.out.println("connected");
            });
            ws.onMessage(ctx -> {
                System.out.println(ctx.message());
                String t = ctx.message().replace("{", "");
                t = t.replace("}" , "");
                t = t.replace("\"" , "");
                t = t.replace(",", ":");
                String []key = t.split(":");
                int type = Integer.valueOf(key[1]);
                String userName = key[3];
                String text = key[5];

                System.out.println("\n" + type + userName + text);
                MessageForSending message = new MessageForSending(type, userName, text);

                Set<WsConnectContext> ctxSet = ChatStore.getWsCtxSet(roomID);
                for (WsConnectContext wsCtx : ctxSet) {
                    wsCtx.send(message);
                }

            });
        });
    }
}