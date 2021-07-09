package werewolf.process.gamesettings;

import io.javalin.http.Context;
import werewolf.store.gamesettings.GameSettings;
import werewolf.store.gamesettings.GameSettingsStore;
import werewolf.store.gamesettings.RoleBreakdown;
import werewolf.store.room.*;
import java.util.UUID;

public class GameSettingsRegister {
    /**
     *
     * @param ctx　//httpリクエストの情報が入っている
     * @return ステータスコード
     * 200:成功
     * 496:範囲外の値
     * 495:部屋主でない
     * 497:UUIDが存在しない
     * 498:部屋が存在しない
     * 499:入力が不正
     */
    public static int register(Context ctx) {
        //リクエストを送ったユーザのUUIDを取得
        String stringUUID = ctx.cookie("UUID");
        //Todo debug用　消す
        //stringUUID = "46453234-dd1f-4ecc-abb7-ecfca363689f";
        //ここまで
        if (stringUUID == null) {
            return 497;
        }
        UUID userUUID = UUID.fromString(stringUUID);

        //参加してる部屋を取得
        int roomID = RoomInfo.whereRoom(userUUID);
        //その部屋の部屋主のUUIDを取得
        RoomSettings roomSettings = RoomInfo.getRoomSettings(roomID);
        //部屋が存在するかチェック
        if (roomSettings == null){
            return 498;
        }
        //リクエストを送ったユーザが部屋主かチェック
        if (!userUUID.equals(roomSettings.hostUUID)) {
            System.out.println("部屋主以外がゲーム設定");
            System.out.println(userUUID.toString());
            System.out.println(roomSettings.hostUUID.toString());
            return 495;
        }

        //入力された値を取得してnullでないかチェック
        Integer discussionTime = Integer.valueOf(ctx.formParam("discussionTime"));
        if (discussionTime == null) {
            System.out.println("discussionTime is null");
            System.out.println(ctx.formParam("discussionTime"));
            return 499;
        }

        Integer votingTime = Integer.valueOf(ctx.formParam("votingTime"));
        if (votingTime == null) {
            System.out.println("votingTime is null");
            return 499;
        }

        Integer nightTime = Integer.valueOf(ctx.formParam("nightTime"));
        if (nightTime == null) {
            System.out.println("nightTime is null");
            return 499;
        }

        Integer willTime = Integer.valueOf(ctx.formParam("willTime"));
        if (willTime == null) {
            System.out.println("willTime is null");
            return 499;
        }

        Integer tieVoteOption = Integer.valueOf(ctx.formParam("tieVoteOption"));
        if (tieVoteOption == null) {
            System.out.println("tieVoteOption is null");
            return 499;
        }

        Integer werewolfChatSwitch = Integer.valueOf(ctx.formParam("werewolfChatSwitch"));
        if (werewolfChatSwitch == null) {
            System.out.println("werewolfChatSwitch is null");
            return 499;
        }

        Integer firstNightSee = Integer.valueOf(ctx.formParam("firstNightSee"));
        if (firstNightSee == null) {
            System.out.println("firstNightSee is null");
            return 499;
        }

        boolean canSeeMissingPosition = Boolean.parseBoolean(ctx.formParam("canSeeMissingPosition"));
        boolean isSecretBallot = Boolean.parseBoolean(ctx.formParam("isSecretBallot", "true"));
        boolean canContinuousGuard = Boolean.parseBoolean(ctx.formParam("canContinuousGuard"));
        boolean isRandomStealing = Boolean.parseBoolean(ctx.formParam("isRandomStealing"));
        boolean isOneNight = Boolean.parseBoolean(ctx.formParam("isOneNight"));


        Integer villagersNum = Integer.valueOf(ctx.formParam("villagersNum"));
        if (villagersNum == null) {
            System.out.println("villagersNum is null");
            return 499;
        }

        Integer seersNum = Integer.valueOf(ctx.formParam("seersNum"));
        if (seersNum == null) {
            System.out.println("seersNum is null");
            return 499;
        }

        Integer necromancersNum = Integer.valueOf(ctx.formParam("necromancersNum"));
        if (necromancersNum == null) {
            System.out.println("necromancersNum is null");
            return 499;
        }

        Integer knightsNum = Integer.valueOf(ctx.formParam("knightsNum"));
        if (knightsNum == null) {
            System.out.println("knightsNum is null");
            return 499;
        }

        Integer huntersNum = Integer.valueOf(ctx.formParam("huntersNum"));
        if (huntersNum == null) {
            System.out.println("huntersNum is null");
            return 499;
        }

        Integer blackKnightsNum = Integer.valueOf(ctx.formParam("blackKnightsNum"));
        if (blackKnightsNum == null) {
            System.out.println("blackKnightsNum is null");
            return 499;
        }

        Integer freemasonariesNum = Integer.valueOf(ctx.formParam("freemasonariesNum"));
        if (freemasonariesNum == null) {
            System.out.println("freemasonariesNum is null");
            return 499;
        }

        Integer bakersNum = Integer.valueOf(ctx.formParam("bakersNum"));
        if (bakersNum == null) {
            System.out.println("bakersNum is null");
            return 499;
        }

        Integer werewolvesNum = Integer.valueOf(ctx.formParam("werewolvesNum"));
        if (werewolvesNum == null) {
            System.out.println("werewolvesNum is null");
            return 499;
        }

        Integer madmenNum = Integer.valueOf(ctx.formParam("madmenNum"));
        if (madmenNum == null) {
            System.out.println("madmenNum is null");
            return 499;
        }

        Integer traitorsMum = Integer.valueOf(ctx.formParam("traitorsNum"));
        if (traitorsMum == null) {
            System.out.println("traitorsMum is null");
            return 499;
        }

        Integer foxSpiritsNum = Integer.valueOf(ctx.formParam("foxSpiritsNum"));
        if (foxSpiritsNum == null) {
            System.out.println("foxSpiritsNum is null");
            return 499;
        }

        Integer foolsNum = Integer.valueOf(ctx.formParam("foolsNum"));
        if (foolsNum == null) {
            System.out.println("foolsNum is null");
            return 499;
        }

        Integer phantomThievesNum = Integer.valueOf(ctx.formParam("phantomThievesNum"));
        if (phantomThievesNum == null) {
            System.out.println("phantomThievesNum is null");
            return 499;
        }
        //読み取り終わり

        //値の範囲チェック
        if ( !(60 <= discussionTime && discussionTime <= 600)) {
            return 496;
        }

        if ( !(30 <= votingTime && votingTime <= 120)) {
            return 496;
        }

        if ( !(30 <= nightTime && nightTime <= 120)) {
            return 496;
        }

        if ( !(0 <= willTime && willTime <= 120)) {
            return 496;
        }

        if ( !(tieVoteOption == 0 || tieVoteOption == 1)) {
            return 496;
        }

        if ( !(werewolfChatSwitch == 0 || werewolfChatSwitch == 1 || werewolfChatSwitch == 2)) {
            return 496;
        }

        if ( !(firstNightSee == 0 || firstNightSee == 1 || firstNightSee == 2)) {
            return 496;
        }

        //値の範囲チェック終わり

        //登録
        GameSettings newGameSettings = new GameSettings(
                discussionTime,
                votingTime,
                nightTime,
                willTime,
                tieVoteOption,
                werewolfChatSwitch,
                firstNightSee,
                canSeeMissingPosition,
                isSecretBallot,
                canContinuousGuard,
                isRandomStealing,
                isOneNight
        );
        RoleBreakdown newRoleBreakdown = new RoleBreakdown(
                villagersNum,
                seersNum,
                necromancersNum,
                knightsNum,
                huntersNum,
                blackKnightsNum,
                freemasonariesNum,
                bakersNum,
                werewolvesNum,
                madmenNum,
                traitorsMum,
                foxSpiritsNum,
                foolsNum,
                phantomThievesNum
        );
        GameSettingsStore.register(roomID, newGameSettings, newRoleBreakdown);
        return 200;
    }
}
