package werewolf;

import io.javalin.Javalin;
import io.javalin.websocket.WsConnectContext;
import org.eclipse.jetty.util.UrlEncoded;
import werewolf.process.chat.Chat;
import werewolf.process.enterroom.EnterRoom;
import werewolf.process.game.GameRoom;
import werewolf.process.gamesettings.GameSettingsGetter;
import werewolf.process.room.RoomInfoGetter;
import werewolf.process.gamesettings.GameSettingsRegister;
import werewolf.process.profile.ProfileGetter;
import werewolf.process.profile.ProfileRegister;
import werewolf.process.room.SetUpRoom;
import werewolf.store.chat.ChatStore;
import werewolf.store.chat.MessageForReceiving;
import werewolf.store.chat.MessageForSending;
import werewolf.store.gamesettings.GameSettings;
import werewolf.store.gamesettings.GameSettingsANDRoleBreakdown;
import werewolf.store.gamesettings.GameSettingsStore;
import werewolf.store.gamesettings.RoleBreakdown;
import werewolf.store.room.RoomInfo;
import werewolf.store.room.RoomInfoForSending;
import werewolf.store.room.RoomSettings;
import werewolf.store.user.NameAndIcon;
import werewolf.store.user.UserProfile;

import java.net.URLEncoder;
import java.util.*;

/**
 * ステータスコード
 * 465：UUIDが無い
 * 466：プロフィールが必要なのにプロフィールが登録されていない
 */


public class Main {


    public static void main(String[] args) {
        UUID testHostUUID = UUID.randomUUID();;
        Javalin app = Javalin.create(config -> {
            config.enableCorsForAllOrigins();
            config.addStaticFiles("/public/");

            //Location l = Location.valueOf("public/img");
            //config.addStaticFiles("/img", l);
        }).start(51000);

        app.get("/hello", ctx -> {
            String t = ctx.queryParam("roomID", "0");
            System.out.println(t);
            //System.out.println(ctx.formParam("form", "無いよ"));
            //UUID userUUID = UUID.randomUUID();
            //ctx.cookie("UUID", "46453234-dd1f-4ecc-abb7-ecfca363689f");
            ctx.html("test.html");
        });

        app.get("/debug", ctx -> {
            List<UUID> users = new ArrayList<UUID>();
            users.add(testHostUUID);
            users.add(UUID.randomUUID());
            users.add(UUID.randomUUID());
            users.add(UUID.randomUUID());
            users.add(UUID.randomUUID());
            UserProfile.register(users.get(0), "CPU1", 1);
            UserProfile.register(users.get(1), "CPU2", 2);
            UserProfile.register(users.get(2), "CPU3", 3);
            UserProfile.register(users.get(3), "CPU4", 4);
            UserProfile.register(users.get(4), "CPU5", 5);
            UserProfile.register(UUID.fromString("46453234-dd1f-4ecc-abb7-ecfca363689f"), "al19009", 6);
            GameSettings gameSettings = new GameSettings(60, 30, 30, 30, 0, 1, 1, false, false, false, false, false);
            RoleBreakdown roleBreakdown = new RoleBreakdown(3, 7, 1, 1, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0);
            RoomInfo.createRoom(new RoomSettings(UUID.fromString("46453234-dd1f-4ecc-abb7-ecfca363689f"), "room1", "", 10, "debug用の部屋1"));
            //RoomInfo.createRoom(new RoomSettings(users.get(1), "room2", "パス", 11, "debug用の部屋2"));
            GameSettingsStore.register(1, gameSettings, roleBreakdown);
            ChatStore.createRoomChat(1);
            //ChatStore.createRoomChat(2);
            RoomInfo.enterRoom(users.get(1), 1);
            RoomInfo.enterRoom(users.get(2), 1);
            RoomInfo.enterRoom(users.get(3), 1);
            RoomInfo.enterRoom(users.get(4), 1);
            RoomInfo.enterRoom(UUID.fromString("46453234-dd1f-4ecc-abb7-ecfca363689f"), 1);
            ctx.html("test.html");
        });

        /**
         * 使えるアイコン画像の番号のリストを返す
         * 入力：roomIDかUUID（cookie）
         * 出力:使えるアイコン画像の番号のリスト
         *      ステータスコード
         *      200:成功
         *      250:roomIDとUUIDが無い
         *      498:部屋が存在しない
         */
        app.get("/icon-list", ctx -> {
            String StringRoomID = ctx.queryParam("roomID", "0");
            System.out.println(StringRoomID);
            String stringUUID = ctx.cookie("UUID");
            //Todo debug用　消す
            //stringUUID = "46453234-dd1f-4ecc-abb7-ecfca363689f";
            //ここまで
            if (StringRoomID == null && stringUUID == null) {
                UUID newUUID = UUID.randomUUID();
                System.out.println("cookieに登録:" + newUUID.toString());
                ctx.cookie("UUID", newUUID.toString());
                ctx.status(250);
            } else {
                //対象の部屋のIDを取得
                int roomID;
                if (StringRoomID != null) {
                    roomID = Integer.parseInt(StringRoomID);
                } else {
                    roomID = RoomInfo.whereRoom(UUID.fromString(stringUUID));
                }
                List<Integer> result = ProfileGetter.notUsedIcon(roomID);
                if (result.size() == 0) {
                    ctx.status(498);
                } else {
                    class t{
                        public List<Integer> iconList;
                        t(List<Integer> iconList){this.iconList = iconList;}
                    }
                    ctx.json(new t(result));
                }
            }
        });



        /**
         * 部屋にいる他のプレイヤーの名前のリストを返す
         * 入力：roomIDかUUID（cookie）
         * 出力:使えるアイコン画像の番号のリスト
         *      ステータスコード
         *      200:成功
         *      250:roomIDとUUIDが無い
         *      498:部屋が存在しない
         */
        app.get("/used-name", ctx -> {
            int roomID = Integer.valueOf(ctx.formParam("roomID", "0"));
            String stringUUID = ctx.cookie("UUID");
            //Todo debug用　消す
            //stringUUID = "46453234-dd1f-4ecc-abb7-ecfca363689f";
            //ここまで
            if (stringUUID == null) {
                UUID newUUID = UUID.randomUUID();
                System.out.println("cookieに登録:" + newUUID.toString());
                ctx.cookie("UUID", newUUID.toString());
            }
            if (roomID == 0 && stringUUID == null) {
                ctx.status(250);
            } else {
                //対象の部屋のIDを取得
                if (roomID == 0) {
                    roomID = RoomInfo.whereRoom(UUID.fromString(stringUUID));
                }
                List<String> result = ProfileGetter.UsedName(roomID);
                if (result == null) {
                    ctx.status(498);
                } else {
                    class t{
                        public List<String> usedNameList;
                        t(List<String> usedNameList){this.usedNameList = usedNameList;}
                    }
                    ctx.json(new t(result));
                }
            }
        });

        /**
         * プロフィール編集をする
         * 入力：UUID（cookie）
         *      userName:ユーザネーム
         *      icno:アイコン画像の番号
         *
         *      URLクエリパラメータ
         *      checkDuplicates(被りチェックをする部屋のroomID　0なら被りチェック無し）
         *      announce(プロフィールの変更を知らせるかどうかゲームチャット画面のみtrue)
         * 出力：プレイヤーネームとアイコン画像の番号（cookie）
         *      ステータスコード
         *      200番台　登録成功
         *      400番台　エラー
         *      10の位が 9：ユーザネームが不正な値 8：ユーザネームが不正ではないが部屋いにる他のユーザと被っている
         *      1の位が 9：アイコンが不正な値 8：アイコンが不正ではないが部屋にいる他のユーザと被っている
         *      477：その他エラー
         */
        app.put("/profile-register", ctx -> {
            //UUID取得。ないなら新しく生成してcookieに追加
            UUID userUUID;
            //Todo 消す
            //userUUID = UUID.fromString("46453234-dd1f-4ecc-abb7-ecfca363689f");
            //ここまで
            // Todo コメントアウト解除

            if (ctx.cookie("UUID") == null) {
                userUUID = UUID.randomUUID();
                System.out.println("cookieにUUID" + userUUID.toString() + "登録");
                ctx.cookie("UUID", userUUID.toString());
            } else {
                userUUID = UUID.fromString(ctx.cookie("UUID"));
            }
            //ユーザネーム
            String userName = ctx.formParam("userName", "");
            //アイコン画像の番号
            int icon = Integer.parseInt(ctx.formParam("icon", "0"));

            System.out.println("userUUID:" + userUUID);
            System.out.println("userName:" + userName);
            System.out.println("icon:" + icon);
            System.out.println("announce=" + ctx.queryParam("announce"));
            System.out.println("checkDuplicates=" + ctx.queryParam("checkDuplicates"));
            //登録結果のステータスコード
            int status;

            //USLパラメータannounce=trueならばプロフィールの変更をアナウンスするために変更前のプロフィールを取得
            NameAndIcon oldProfile = null;
            if (ctx.queryParam("announce", "false").equals("true")) {
                System.out.println("プロフィール変更アナウンスあり");
                oldProfile = ProfileGetter.getProfile(userUUID);
                if (oldProfile == null) {
                    System.out.println("announce=true　だがプロフィールが登録されていない");
                    ctx.status(477);
                } else {
                    System.out.println("変更前のプロフィール 名前:" + oldProfile.name + "　アイコン:" + oldProfile.icon);
                }
            }

            /*
                被りチェックありの場合パラメータが
                /?checkDuplicates=[roomID]
                になっている
                被りチェック無しならroomID=0
             */
            int roomID = Integer.valueOf(ctx.queryParam("checkDuplicates", "0"));
            System.out.println("roomID:" + roomID);
            status = ProfileRegister.register(userUUID, roomID, userName, icon);


            //プロフィール登録が成功したならプロフィールをcookieにも保存する
            //announce=trueならばプロフィールの変更があることをチャットで参加者全員に知らせる
            if(status == 200) {
                System.out.println("cookieに登録 icon:" + icon + " userName:" + userName);
                assert userName != null;
                ctx.cookie("userName", URLEncoder.encode(userName, "UTF-8"));
                ctx.cookie("icon", String.valueOf(icon));
                System.out.println("cookieに登録完了");


                if (ctx.queryParam("announce", "false").equals("true") && (!oldProfile.name.equals(userName) || oldProfile.icon != icon)) {
                    String announceText="";
                    //プレイヤーネームの変更有
                    if (oldProfile.name != userName) {
                        announceText += oldProfile.name + "の名前が" + userName + "に変更されました。";
                    } else {
                        announceText += oldProfile.name + "の";
                    }
                    if (oldProfile.icon != icon) {
                        announceText += "アイコンが変更されました。";
                    }
                    /**
                     * プレイヤーネームのみ変更："○○の名前が××に変更されました。"
                     * アイコンのみ変更："○○のアイコンが変更されました。"
                     * 両方変更："○○の名前が××に変更されました。アイコンが変更されました。"
                     */
                    Chat.announce(roomID, announceText, -2);
                }
            }
            ctx.status(status);
        });

        /**
         * 部屋を作成
         * 入力：UUID（cookie）
         *      roomName:部屋の名前
         *      pass:あいことば（ない場合は""空文字）
         *      maxMember:人数制限
         *      introduction:紹介文
         * 出力:ステータスコード
         *      200:成功
         *      466:プロフィール登録をしていない
         *      496:範囲外の値
         *      497:UUIDが存在しない
         *      499:入力が不正
         */
        app.post("/room-set-up", ctx -> {
            //プロフィールを登録していないならエラー
            String stringUUID = ctx.cookie("UUID");
            if (stringUUID == null) {
                ctx.status(497);
            } else if (ProfileGetter.getProfile(UUID.fromString(stringUUID)) == null) {
                ctx.status(466);
            } else {
                //部屋を作成
                int status = SetUpRoom.CreateRoom(ctx);
                ctx.status(status);
            }
        });

        /**
         * クエリパラメータの部屋の情報を得る
         * 入力：UUID（クエリパラメータ）
         * 出力:部屋の情報
         *      ステータスコード
         *      200:成功
         *      497:UUIDが存在しない
         *      498:部屋が存在しない
         *      499:クエリパラメータが不正
         */
        app.get("/room-info", ctx -> {
            String stringRoomID = ctx.queryParam("roomID");
            if (stringRoomID == null) {
                ctx.status(499);
            } else {
                Integer roomID = Integer.valueOf(stringRoomID);
                System.out.println("room-info:roomID=" + roomID);
                if (roomID == 0) {
                    String stringUUID = ctx.cookie("UUID");
                    //Todo debug用　消す
                    //stringUUID = "46453234-dd1f-4ecc-abb7-ecfca363689f";
                    //ここまで

                    if (stringUUID == null) {
                        ctx.status(497);
                    } else {
                        roomID = RoomInfo.whereRoom(UUID.fromString(stringUUID));
                        RoomInfoForSending result = RoomInfoGetter.getRoomInfo(roomID);
                        if (result == null) {
                            ctx.status(498);
                        } else {
                            ctx.json(result);
                        }
                    }
                } else {
                    RoomInfoForSending result = RoomInfoGetter.getRoomInfo(roomID);
                    if (result == null) {
                        ctx.status(498);
                    } else {
                        ctx.json(result);
                    }
                }
            }
        });

        /**
         * 部屋一覧画面を表示するために必要な情報を取得
         * 入力無し
         * 出力:List(
         *          roomID:部屋ID
         *          hostName:部屋主の名前
         *          roomName:部屋の名前
         *          maxMember:最大参加人数
         *          nowMember:現在の参加人数
         *          existPass:パスワードの有無
         *          introduction:紹介文
         *      )
         */
        app.get("/room-list", ctx -> {
            ctx.json(RoomInfoGetter.getRoomInfoList());
        });

        /**
         * 部屋に参加
         * ステータスコード
         *      200:成功
         *      201既に参加している
         *      401:パスワード認証失敗
         *      466：プロフィールが必要なのにプロフィールが登録されていない
         *      497:UUIDが存在しない
         *      498:部屋が存在しない
         *      499:入力が不正
         *      554:ゲームが始まっている
         *      555:部屋が定員オーバーまたは部屋が無くなっています
         */
        app.put("/enter-room", ctx -> {
            //ToDo 被りチェック
            int status = EnterRoom.enterRoom(ctx);
            ctx.status(status);
        });

        /**
         * ゲームの設定取得
         * 入力 UUID
         * 出力：ゲームの設定と役職の内訳
         * ステータスコード
         *      200:成功
         *      250:設定が無い
         *      465:UUIDが無い
         *      498:部屋が存在しない
         *
         */
        app.get("/gameSettings", ctx -> {
            String stringUUID = ctx.cookie("UUID");
            //Todo debug用 消す
            //stringUUID = "46453234-dd1f-4ecc-abb7-ecfca363689f";
            //ここまで
            if (stringUUID == null) {
                ctx.status(465);
            } else {
                UUID userUUID = UUID.fromString(stringUUID);
                int roomID = RoomInfo.whereRoom(userUUID);
                if (roomID == 0) {
                    ctx.status(498);
                } else {
                    GameSettingsANDRoleBreakdown result = GameSettingsGetter.getGameSettingsANDRoleBreakdown(roomID);
                    if (result == null) {
                        ctx.status(250);
                    } else {
                        ctx.json(result);
                    }
                }
            }
        });

        /**
         * ゲーム設定登録
         * ステータスコード
         *      200:成功
         *      496:範囲外の値
         *      497:部屋主でない
         *      498:部屋が存在しない
         *      499:入力が不正
         */
        app.post("/gameSettings-registry", ctx -> {

            int status = GameSettingsRegister.register(ctx);
            ctx.status(status);
            //ctx.json(GameSettingsGetter.getGameSettingsANDRoleBreakdown(1));
        });

        /**
         * 自身の役職と生死を取得
         * 200:成功
         * 464:ゲームが始まっていない
         * 465:UUIDが無い
         * 498:部屋が存在しない
         */
        app.get("/my-status", GameRoom::getMyStatus);

        /**
         * 参加者全員のプロフィールと生死を取得
         *      200:成功
         *      465:UUIDが無い
         *      498:部屋が存在しない
         */
        app.get("/game-info", GameRoom::getStatusListAndPhase);

        /**
         * ゲームスタート
         * ステータスコード
         *      200:成功
         *      497:UUIDが無い
         *      494:ゲーム設定が不適切
         *      495:部屋主でない
         *      498:部屋が存在しない
         */
        app.put("/game-start", ctx -> {
            String stringUUID = ctx.cookie("UUID");
            //Todo　debug用　消す
            //stringUUID = "46453234-dd1f-4ecc-abb7-ecfca363689f";
            //
            if (stringUUID == null) {
                ctx.status(465);
            } else {
                int statusCode = GameRoom.gameStart(UUID.fromString(stringUUID));
                ctx.status(statusCode);
            }
        });

        /**
         * 夜のアクションを実行
         * 入力
         * targetName:夜のアクションを行う対象プレイヤーネーム
         * priority:人狼の場合
         * 出力
         * ステータスコード
         *            200:成功
         *            465:UUIDが無い
         *            498:部屋が存在しない
         *            499:入力が不正
         */
        app.put("/night-action", GameRoom::doNightAction);
        /**
         * 投票する
         * 入力
         * targetName:投票したプレイヤーネーム
         * 出力
         * ステータスコード
         * 200:成功
         * 465:UUIDが無い
         * 498:部屋が存在しない
         * 499:入力が不正
         */
        app.post("vote", GameRoom::vote);


        app.ws("/websocket", ws -> {
            ws.onConnect(ctx -> {
                String stringUUID = ctx.cookie("UUID");
                if (stringUUID != null) {
                    UUID userUUID = UUID.fromString(stringUUID);
                    int roomID = RoomInfo.whereRoom(userUUID);
                    if (roomID != 0) {
                        //コネクションを登録成功
                        Chat.addWsCtx(roomID, userUUID, ctx);
                        System.out.println("connect to " + userUUID + " in room" + RoomInfo.whereRoom(userUUID));
                        Chat.announce(roomID, ProfileGetter.getProfile(userUUID).name + "が参加しました", -2);
                    }
                } else {
                    System.out.println("no cookie");
                }
            });
            ws.onMessage(ctx -> {
                //メッセージ受信
                MessageForReceiving rMessage = ctx.message(MessageForReceiving.class);
                String stringUUID = rMessage.userUUID;
                if (stringUUID != null && rMessage != null) {
                    //部屋とUUIDを取得
                    UUID userUUID = UUID.fromString(stringUUID);
                    int roomID = RoomInfo.whereRoom(userUUID);
                    if (roomID != 0  && 0 <= rMessage.channel && rMessage.channel <= 2) {
                        System.out.println("channel:" + rMessage.channel + " message:" + rMessage.text);
                        //全員にチャットを送信
                        Chat.broadcast(roomID, rMessage.channel, userUUID, rMessage.text);
                    }
                } else {
                    if (stringUUID == null)
                        System.out.println("no cookie");
                    else
                        System.out.println("onMessage:メッセージを読み込めませんでした" + ctx.message());
                }
            });
            ws.onClose(ctx -> {
                System.out.println("接続が切れました");
            });
        });
        //debug用
        /*
        app.ws("/websocket", ws -> {
            ws.onConnect(ctx -> {
                    System.out.println(ctx.cookie("UUID"));
                    UUID userUUID = UUID.fromString("46453234-dd1f-4ecc-abb7-ecfca363689f");
                    int roomID = RoomInfo.whereRoom(userUUID);
                    if (roomID != 0) {
                        //コネクションを登録成功
                        Chat.addWsCtx(roomID, userUUID, ctx);
                        System.out.println("connect to " + userUUID + " in room" + RoomInfo.whereRoom(userUUID));
                        Chat.announce(roomID, ProfileGetter.getProfile(userUUID).name + "が参加しました", -2);
                    }
            });
            ws.onMessage(ctx -> {
                //メッセージ受信
                System.out.println("受信！");
                MessageForReceiving rMessage = ctx.message(MessageForReceiving.class);
                if (rMessage != null) {
                    //部屋とUUIDを取得
                    UUID userUUID = UUID.fromString("46453234-dd1f-4ecc-abb7-ecfca363689f");
                    int roomID = RoomInfo.whereRoom(userUUID);
                    System.out.println(roomID);
                    if (roomID != 0 && 0 <= rMessage.channel && rMessage.channel <= 2) {
                        System.out.println("channel:" + rMessage.channel + " message:" + rMessage.text);
                        //全員にチャットを送信
                        Chat.broadcast(roomID, rMessage.channel, userUUID, rMessage.text);
                    } else if (rMessage.channel == -1) {
                        String userName = ProfileGetter.getProfile(UUID.fromString(rMessage.userUUID)).name;
                        Chat.announce(roomID, userName + "と接続が切れました", -1);
                    }
                } else {
                    System.out.println("onMessage:メッセージを読み込めませんでした" + ctx.message());
                }
            });
            ws.onClose(ctx -> {
                String stringUUID = ctx.cookie("UUID");
                stringUUID = "46453234-dd1f-4ecc-abb7-ecfca363689f";
                if (stringUUID != null) {
                    UUID userUUID = UUID.fromString(stringUUID);
                    int roomID = RoomInfo.whereRoom(userUUID);
                    if (roomID != 0) {
                        ChatStore.removeWsCtx(roomID, userUUID);
                        System.out.println("close connection" + userUUID + " in room" + RoomInfo.whereRoom(userUUID));
                        Chat.announce(roomID, ProfileGetter.getProfile(userUUID).name + "と接続が切れました", -1);
                    }
                } else {
                    System.out.println("no cookie");
                }
            });
        });*/
    }

}
