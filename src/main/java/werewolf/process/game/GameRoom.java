package werewolf.process.game;

import io.javalin.http.Context;
import werewolf.process.gamesettings.GameSettingsGetter;
import werewolf.process.profile.ProfileGetter;
import werewolf.store.chat.ChatPermissionAndWsCtx;
import werewolf.store.gamesettings.GameSettingsANDRoleBreakdown;
import werewolf.store.room.RoomInfo;
import werewolf.store.user.NameAndIcon;

import java.util.*;

public class GameRoom {
    static Map<Integer, GameMaster> gameRoomMap = new HashMap<Integer, GameMaster>();
    static Map<Integer, Map<String, UUID>> userNameToUUID = new HashMap<Integer, Map<String, UUID>>();

    /**
     * 引数の示す部屋がゲーム開始しているか返す
     *
     * @param roomID
     * @return
     */
    public static boolean isStarted(int roomID) {
        return gameRoomMap.containsKey(roomID);
    }

    /**
     * @param userUUID
     * @return ステータスコード
     * 200:成功
     * 476:ゲーム設定が不適切
     * 497:部屋主でない
     * 498:部屋が存在しない
     */
    public static int gameStart(UUID userUUID) {
        int roomID = RoomInfo.whereRoom(userUUID);
        if (roomID == 0) {
            return 498;
        }
        //部屋主じゃなかった
        if (!userUUID.equals(RoomInfo.getHostUUID(roomID))) {
            return 497;
        }
        GameSettingsANDRoleBreakdown GSandRB = GameSettingsGetter.getGameSettingsANDRoleBreakdown(roomID);
        if (GSandRB == null) {
            return 476;
        }
        int playerCount = RoomInfo.getParticipantsSet(roomID).size();

        //2日目朝に勝敗が決まるor共有者の数が不適切ならfalse, そうでなければtrue
        if (!GameLogic.checkRoleSetting(playerCount, GSandRB.roleBreakdown)) {
            return 476;
        }

        //名前からUUIDに変換できるようにmapオブジェクトを生成
        Map<String, UUID> map = new HashMap<String, UUID>();
        Set<UUID> participantsSet = RoomInfo.getParticipantsSet(roomID);
        for (UUID participant : participantsSet) {
            map.put(ProfileGetter.getProfile(participant).name, participant);
        }
        userNameToUUID.put(roomID, map);

        //ゲーム開始
        gameRoomMap.put(roomID, new GameMaster(roomID, GSandRB));
        System.out.println("GameRoom:GM初期化完了");
        return 200;
    }

    /**
     * 自身の役職と生死を取得
     * 200:成功
     * 464:ゲームが始まっていない
     * 465:UUIDが無い
     * 498:部屋が存在しない
     */
    public static void getMyStatus(Context ctx) {
        //roomIDとUUIDを取得
        String stringUUID = ctx.cookie("UUID");
        //ToDo debug用 消す
        //stringUUID = "46453234-dd1f-4ecc-abb7-ecfca363689f";
        //System.out.println("getMyStatus開始");
        //ここまで
        if (stringUUID == null) {
            ctx.status(465);
        } else {
            UUID userUUID = UUID.fromString(stringUUID);
            int roomID = RoomInfo.whereRoom(userUUID);
            if (roomID == 0) {
                ctx.status(498);
            } else {
                GameMaster GM = gameRoomMap.get(roomID);
                PlayerStatus playerStatus;
                if (GM != null) {
                    playerStatus = GM.playersStatus.getPlayerStatus(userUUID);
                    if (playerStatus == null) {
                        System.out.println("GM.playersStatus.getPlayerStatusが失敗");
                    } else {
                        System.out.println("playerStatus{" + playerStatus.alive + ", " + playerStatus.role + "}");
                        ctx.json(playerStatus);
                    }
                } else {
                    playerStatus = new PlayerStatus();
                    playerStatus.role = "";
                    playerStatus.alive = true;
                    ctx.json(playerStatus);
                }
            }
        }
        System.out.println("getMyStatus終了！");
    }

    /**
     * 参加者全員のプロフィールと生死と現在のフェーズと次のフェーズまでの時間を得る取得
     * ToDo フェーズの変わり目にしか変更が無いのでキャッシュしておいた方がよい
     * 200:成功
     * 465:UUIDが無い
     * 498:部屋が存在しない
     */
    public static void getStatusListAndPhase(Context ctx) {
        //roomIDとUUIDを取得
        String stringUUID = ctx.cookie("UUID");
        //ToDo debug用　消す
        //stringUUID = "46453234-dd1f-4ecc-abb7-ecfca363689f";
        //System.out.println("getStatusListAndPhase開始");
        //ここまで消す
        if (stringUUID == null) {
            System.out.println("getStatusListAndPhase:UUIDが無い");
            ctx.status(200);
        } else {
            UUID userUUID = UUID.fromString(stringUUID);
            int roomID = RoomInfo.whereRoom(userUUID);
            if (roomID == 0) {
                ctx.status(498);
            } else {
                //名前、アイコン、生死のリストを作成して送信
                GameMaster GM = gameRoomMap.get(roomID);

                List<ProfileAndStatus> statusList = new ArrayList<ProfileAndStatus>();
                StatusListAndPhase result = null;
                if (GM != null) {
                    Map<UUID, PlayerStatus> map = GM.getStatusMap();
                    for (Map.Entry<UUID, PlayerStatus> e : map.entrySet()) {
                        statusList.add(new ProfileAndStatus(ProfileGetter.getProfile(e.getKey()), e.getValue().alive));
                    }
                    result = new StatusListAndPhase(statusList, GM.getNextPhaseTime(), GM.getNowPhase());
                }
                //ゲームが始まってないなら生死の情報はないから全員生存としてデータを送信
                else {
                    List<NameAndIcon> nameAndIconList = ProfileGetter.getRoomMemberProfile(roomID);
                    for (NameAndIcon nameAndIcon : nameAndIconList) {
                        statusList.add(new ProfileAndStatus(nameAndIcon, true));
                    }
                    //phaseはゲーム開始前
                    result = new StatusListAndPhase(statusList, 0, 0);
                }
                ctx.json(result);
            }
        }
        System.out.println("getStatusListAndPhase終了！");
    }

    /**
     * @param roomID
     */
    static void gameEnd(int roomID) {
        gameRoomMap.remove(roomID);
        userNameToUUID.remove(roomID);
    }

    /**
     * 夜のアクションを実行
     *
     * @param ctx ステータスコード
     *            200:成功
     *            465:UUIDが無い
     *            498:部屋が存在しない
     *            499:入力が不正
     */
    public static void doNightAction(Context ctx) {
        String stringUUID = ctx.cookie("UUID");
        //ToDo debug用　消す
        //stringUUID = "46453234-dd1f-4ecc-abb7-ecfca363689f";
        //System.out.println("doNightAction開始");
        //ここまで
        if (stringUUID == null) {
            ctx.status(465);
        } else {
            UUID userUUID = UUID.fromString(stringUUID);
            int roomID = RoomInfo.whereRoom(userUUID);
            if (roomID == 0) {
                ctx.status(498);
            } else {
                String targetName = ctx.formParam("targetName");
                if (targetName != null) {
                    int votingPriority = Integer.parseInt(ctx.formParam("priority", "1"));
                    UUID targetUUID = userNameToUUID.get(roomID).get(targetName);
                    GameMaster GM = gameRoomMap.get(roomID);
                    GM.doNightAction(userUUID, targetUUID, votingPriority);
                } else {
                    ctx.status(499);
                }
            }
        }
        System.out.println("doNightAction終了");
    }

    /**
     * ステータスコード
     * 200:成功
     * 465:UUIDが無い
     * 498:部屋が存在しない
     * 499:入力が不正
     *
     * @param ctx
     */
    public static void vote(Context ctx) {
        String stringUUID = ctx.cookie("UUID");
        //ToDo debug用 消す
        //stringUUID = "46453234-dd1f-4ecc-abb7-ecfca363689f";
        //System.out.println("vote開始");
        //ここまで
        if (stringUUID == null) {
            ctx.status(465);
        } else {
            UUID userUUID = UUID.fromString(stringUUID);
            int roomID = RoomInfo.whereRoom(userUUID);
            if (roomID == 0) {
                ctx.status(498);
            } else {
                String targetName = ctx.formParam("targetName");
                if (targetName == null) {
                    ctx.status(499);
                } else {
                    UUID targetUUID = userNameToUUID.get(roomID).get(targetName);
                    GameMaster GM = gameRoomMap.get(roomID);
                    GM.vote(userUUID, targetUUID);
                }
            }
        }
        System.out.println("vote終了");
    }
}


