package werewolf.process.game;

import werewolf.process.chat.Chat;
import werewolf.process.profile.ProfileGetter;
import werewolf.store.chat.Message;
import werewolf.store.gamesettings.GameSettings;
import werewolf.store.gamesettings.GameSettingsANDRoleBreakdown;
import werewolf.store.room.RoomInfo;

import javax.sound.midi.SysexMessage;
import java.util.*;

public class GameMaster {
    //部屋が解散したかどうか
    boolean isBreakUp = false;
    //次のフェーズに移行する時間
    //1970 年 1 月 1 日 00:00:00.000 GMT (グレゴリオ暦)からの経過ミリ秒数で表す
    long nextPhaseTime;
    /**
     * 0:ゲーム開始前
     * 1:初日夜
     * 2:朝（議論時間）
     * 3:投票
     * 4:遺言
     * 5:夜
     */
    int nowPhase = 0;
    //この部屋のUUID
    int roomID;
    //ゲームの処理を扱うクラス
    GameLogic gameLogic;
    //投票を扱うクラス
    VotingAction votingAction;
    //プレイヤーの状態を持つ
    public PlayersStatus playersStatus;
    //ゲームの設定を持つ
    GameSettings gameSettings;
    //プレイヤーのUUIDのSet
    Set<UUID> playersUUID;
    //夜のアクションをする必要のあるuserが夜のアクションを実行したかを確認する
    //trueならそのフェーズの夜のアクションは完了
    Map<UUID, Boolean> completeNightAction;

    public GameMaster(int roomID, GameSettingsANDRoleBreakdown gameSettingsANDRoleBreakdown) {
        //フィールド初期化
        System.out.println("ゲーム開始");
        this.roomID = roomID;
        this.gameSettings = gameSettingsANDRoleBreakdown.gameSettings;
        this.playersUUID = RoomInfo.getParticipantsSet(roomID);
        this.playersStatus = new PlayersStatus();
        playersStatus.setPlayers(playersUUID);
        this.votingAction = new VotingAction(playersStatus);
        this.gameLogic = new GameLogic(playersStatus, votingAction, gameSettings);
        this.completeNightAction = new HashMap<UUID, Boolean>();
        System.out.println("フィールド初期化");
        //全プレイヤーが全てのチャットに書き込めなくなり、人狼チャットと墓場チャットを見れなくなる
        for (UUID userUUID : playersUUID) {
            Chat.disableWritingPermission(roomID, 0, userUUID);
            Chat.disableWritingPermission(roomID, 1, userUUID);
            Chat.disableWritingPermission(roomID, 2, userUUID);
            Chat.disableReadingPermission(roomID, 1, userUUID);
            Chat.disableReadingPermission(roomID, 2, userUUID);
        }
        System.out.println("チャット権限変更");
        //役職分けをしてplayersStatusが設定される
        gameLogic.distributeRole(playersUUID.size(), gameSettingsANDRoleBreakdown.roleBreakdown);
        System.out.println("役職分け完了");

        //次のフェーズへ
        nowPhase = 1;
        //このフェーズが終わると処理を登録
        Timer timer = new Timer(false);
        TimerTask finishFirstNightPhaseTask = new TimerTask() {
            @Override
            public void run() {
                finishFirstNightPhase();
            }
        };
        System.out.println("初日夜が終わるまで" + gameSettings.nightTime * 1000L);

        timer.schedule(finishFirstNightPhaseTask, gameSettings.nightTime * 1000L);
        nextPhaseTime = Calendar.getInstance().getTimeInMillis() + gameSettings.nightTime * 1000L;


        System.out.println("タスク登録完了");
        //ゲーム開始のアナウンス
        Chat.announce(roomID, "ゲームを開始します\n自分の役職を確認して、必要なら夜のアクションを実行してください", -2);


        List<UUID> werewolfList = playersStatus.getWerewolfPlayerUUIDs();
        //人狼が他の人狼は誰か知るためのアナウンス文を作成
        String announceForWerewolves = "人狼は";
        for (UUID werewolfUUID : werewolfList) {
            announceForWerewolves += ProfileGetter.getProfile(werewolfUUID).name + "と";
        }
        //末尾の"と"をとって"です"を付ける
        announceForWerewolves = announceForWerewolves.substring(0, announceForWerewolves.length()-1) + "です";

        for (UUID werewolfUUID : werewolfList) {
            //人狼に人狼チャットの読み書きが許可されているなら許可する
            if (gameSettings.werewolfChatSwitch == 1 || gameSettings.werewolfChatSwitch == 2) {
                Chat.enableWritingPermission(roomID, 1, werewolfUUID);
                Chat.enableReadingPermission(roomID, 1, werewolfUUID);
            }
            //人狼に他の人狼を知らせる
            Chat.DMannounce(roomID, werewolfUUID, announceForWerewolves);
        }

        for (UUID userUUID : playersUUID){
            //夜のアクションをする役職はcompleteNightActionに登録
            //初日から夜のアクションをする必要があるプレイヤーを探す
            String role = playersStatus.getRole(userUUID);
            if (role.equals("seer") || role.equals("phantomThieve")) {
                completeNightAction.put(userUUID, false);
            }
            //共有者にもう一人の共有者を知らせる
            else if (role.equals("freemasonary")) {
                UUID anotherFreemasonary = playersStatus.getFreemasonaryPartnerUUID(userUUID);
                Chat.DMannounce(roomID, anotherFreemasonary,
                        "あなたの相方は" + ProfileGetter.getProfile(anotherFreemasonary).name + "です");
            }
            //背信者に誰が人狼か知らせる
            else if (role.equals("traitor")) {
                Chat.DMannounce(roomID, userUUID, announceForWerewolves);
            }
        }

        System.out.println("コンストラクタ完了");
    }

    /**
     * 夜のアクションを実行してウェブソケットから結果を知らせる
     * @param userUUID
     * @param targetUUID
     * @param votingPriority
     */
    public void doNightAction(UUID userUUID, UUID targetUUID, int votingPriority) {
        //夜のフェーズが終わっているなら何もしない
        if (nowPhase != 1 && nowPhase != 5) {
            return;
        }
        //占い師は初日占い有りでないなら夜のアクションができない
        if (nowPhase == 1 && playersStatus.getRole(userUUID).equals("seer") && gameSettings.firstNightSee != 0) {
            return;
        }
        //夜のアクションを完了しているなら何もしない
        if (completeNightAction.containsKey(userUUID)) {
            if (completeNightAction.get(userUUID)) {
                System.out.println(userUUID + "は既に夜のアクションを終えています");
                return;
            }
        } else {
            System.out.println("不正なリクエスト 夜のアクションをできません");
            return;
        }
        //夜のアクションを実行
        completeNightAction.replace(userUUID, true);
        Message message = gameLogic.doNightAction(userUUID, targetUUID, votingPriority);
        String targetName = ProfileGetter.getProfile(message.userUUID).name;

        //プレイヤーに結果を伝えるテキストを作成
        String actionInfo = message.text;
        String announce = "";
        if (actionInfo.contains("人狼でなかった")) {
            announce = targetName + "は人狼ではなかった";
        } else if (actionInfo.contains("人狼だった")) {
            announce = targetName + "は人狼だった";
        } else if (actionInfo.contains("霊視の対象がいませんでした")) {
            announce = actionInfo;
        } else if (actionInfo.contains("護衛した")) {
            announce = targetName + "を今夜護衛します";
        } else if (actionInfo.contains("襲撃を試みた")) {
            announce = ProfileGetter.getProfile(userUUID).name
                        + "は" + targetName + "に" + votingPriority + "票入れました";
            //人狼なら夜のアクションを人狼の人全員に知らせる
            List<UUID> werewolves = playersStatus.getWerewolfPlayerUUIDs();
            for (UUID werewolf : werewolves) {
                Chat.DMannounce(roomID, werewolf, announce);
            }
            return;
        } else if (actionInfo.contains("を奪った")) {
            announce = targetName + "から" + actionInfo;
        } else if (actionInfo.contains("連続ガードはできません")) {
            announce = targetName + "を連続ガードはできません";
            //エラーなら夜のアクションを完了させない
            completeNightAction.replace(userUUID, false);
        } else if (actionInfo.contains("エラー:人狼を襲撃できない")) {
            announce = targetName + "は人狼です 人狼を襲撃できません";
            //エラーなら夜のアクションを完了させない
            completeNightAction.replace(userUUID, false);
        } else {
            System.out.println("GameMaster:不正な夜のアクション " + userUUID + " " + targetUUID);
            //エラーなら夜のアクションを完了させない
            completeNightAction.replace(userUUID, false);
            return;
        }

        //結果を知らせる
        Chat.DMannounce(roomID, userUUID, announce);
    }

    public void vote(UUID userUUID, UUID targetUUID) {
        //投票フェーズでないなら何もしない
        if (nowPhase != 3) {
            return;
        }
        votingAction.vote(userUUID, targetUUID);
        Chat.DMannounce(roomID, userUUID, "投票しました");
    }

    void finishFirstNightPhase() {
        if (isBreakUp) {
            return;
        }
        //朝（議論）フェーズへ
        nowPhase = 2;
        System.out.println("初日夜終了");

        //夜のアクションを実行しなければならないのに実行していなかったら実行
        for (Map.Entry<UUID, Boolean> e : completeNightAction.entrySet()) {
            if ( !e.getValue()) {
                //占い師は初日占い無しなら何もしない
                if (gameSettings.firstNightSee == 1 && playersStatus.getRole(e.getKey()).equals("seer")) {
                    continue;
                }
                doNightAction(e.getKey(), null, 1);
            }
        }

        //昨晩の犠牲者をチャットで知らせる
        List<UUID> lastNightVictim = gameLogic.finishFirstNightAction();
        String deadAnnounce = "恐ろしい夜が明けました。\n昨晩の犠牲者は......";
        if (lastNightVictim.size() == 0) {
            deadAnnounce += "\nいませんでした！";
        } else {
            for (UUID victim : lastNightVictim) {
                deadAnnounce += "\n" + ProfileGetter.getProfile(victim).name;
            }
            deadAnnounce += "\nでした";
        }

        //フェーズの終了処理をスケジュール
        Timer timer = new Timer(false);
        TimerTask finishDiscussionPhaseTask = new TimerTask() {
            @Override
            public void run() {
                finishDiscussionPhase();
            }
        };
        timer.schedule(finishDiscussionPhaseTask, gameSettings.discussionTime * 1000L);
        nextPhaseTime = Calendar.getInstance().getTimeInMillis()
                + gameSettings.discussionTime * 1000L;
        System.out.println("議論時間が終わるまで" + gameSettings.discussionTime * 1000L);

        Chat.announce(roomID, deadAnnounce, -2);

        //パン屋の生存をチャットで知らせる
        if (playersStatus.isSurvivingBaker()) {
            Chat.announce(roomID, "今日もおいしいパンが運ばれてきました！", -1);
        }

        Chat.announce(roomID, "議論を開始してください", -1);

        //チャットの書き込みを読み書きの権限を設定
        //人狼チャットが夜にしか使えないならチャット書き込みを止める
        if (gameSettings.werewolfChatSwitch == 1) {
            for (UUID werewolfUUID : playersStatus.getWerewolfPlayerUUIDs()) {
                Chat.disableWritingPermission(roomID, 1, werewolfUUID);
            }
        }
        //死亡したプレイヤーに墓場チャットの読み書きを許可
        //人狼なら人狼チャットの書き込みを禁止
        for (UUID userUUID : lastNightVictim) {
            Chat.enableReadingPermission(roomID, 2, userUUID);
            Chat.enableReadingPermission(roomID, 2, userUUID);
            Chat.disableWritingPermission(roomID, 1, userUUID);
        }
        //生存しているなら一般チャットの書き込みを許可
        for (UUID userUUID : playersUUID) {
            if (playersStatus.isAlive(userUUID)) {
                Chat.enableWritingPermission(roomID, 0, userUUID);
            }
        }
        System.out.println("チャット権限変更完了");
    }

    void finishDiscussionPhase() {
        if (isBreakUp) {
            return;
        }
        //投票フェーズへ移行
        nowPhase = 3;
        //フェーズの終了処理をスケジュール
        Timer timer = new Timer(false);
        TimerTask finishVotePhaseTask = new TimerTask() {
            @Override
            public void run() {
                finishVotePhase();
            }
        };
        timer.schedule(finishVotePhaseTask, gameSettings.votingTime * 1000L);
        nextPhaseTime = Calendar.getInstance().getTimeInMillis()
                + gameSettings.votingTime * 1000L;
        System.out.println("投票が終わるまで" + gameSettings.nightTime * 1000L);
        //一般チャットの書き込みを禁止
        for (UUID userUUID : playersUUID) {
            Chat.disableWritingPermission(roomID, 0, userUUID);
        }

        //一般チャットから議論終了を知らせて投票を促す
        Chat.announce(roomID, "議論時間が終了しました\n処刑したいと思う人に投票してください", -2);
        votingAction.startNewVoting();
    }

    void finishVotePhase() {
        if (isBreakUp) {
            return;
        }
        //遺言フェーズへ
        nowPhase = 4;
        //投票終了のアナウンス
        Chat.announce(roomID, "投票を締め切りました", -1);
        //匿名投票でないなら全員の投票を開示
        if (!gameSettings.isSecretBallot) {
            System.out.println("投票結果開示");
            String voteDisclosure = "投票の開示をします";
            Map<UUID, UUID> voteData = votingAction.getVotesData();
            for (Map.Entry<UUID, UUID> vote : voteData.entrySet()) {
                voteDisclosure += "\n" + ProfileGetter.getProfile(vote.getKey()).name
                        + "→" + ProfileGetter.getProfile(vote.getValue()).name;
            }
            Chat.announce(roomID, voteDisclosure, -1);
        }

        //投票の結果から処刑者を決める
        boolean isFinish = votingAction.finishVote();
        UUID executedPlayer = null;
        //得票の結果最多得票数が同数
        if (!isFinish) {
            //最多得票数のプレイヤーからランダムで選ぶ
            if (gameSettings.tieVoteOption == 0) {
                executedPlayer = votingAction.determineRandomly();
            }
            //誰も処刑しない
            else if(gameSettings.tieVoteOption == 1) {
                executedPlayer = null;
            }
        } else {
            executedPlayer = votingAction.getVotingResult();
        }
        //処刑者がいるなら処刑　結果をチャットで伝える
        if (executedPlayer != null) {
            playersStatus.kill(executedPlayer);
            //チャット権限変更
            Chat.enableReadingPermission(roomID, 2, executedPlayer);
            Chat.enableReadingPermission(roomID, 2, executedPlayer);
            Chat.disableWritingPermission(roomID, 1, executedPlayer);
            String executedPlayerName = ProfileGetter.getProfile(executedPlayer).name;
            //勝者条件判定
            if (gameLogic.existWinner() != 0) {
                Chat.announce(roomID, "投票の結果" + executedPlayerName + "が処刑されました", -1);
                endGame();
                return;
            }

            //遺言フェーズへ
            if (gameSettings.willTime != 0) {
                Chat.announce(roomID, "投票の結果" + executedPlayerName + "の処刑が決まりました... 最後に遺言を残してください", -2);
                //処刑者を一般チャットに書き込めるようにする
                Chat.enableWritingPermission(roomID, 0, executedPlayer);
                //フェーズの終了処理をスケジュール
                Timer timer = new Timer(false);
                TimerTask finishWillPhaseTask = new TimerTask() {
                    @Override
                    public void run() {
                        finishWillPhase();
                    }
                };
                timer.schedule(finishWillPhaseTask, gameSettings.willTime * 1000L);
                nextPhaseTime = Calendar.getInstance().getTimeInMillis()
                        + gameSettings.willTime * 1000L;
                System.out.println("遺言が終わるまで" + gameSettings.willTime * 1000L);
                return;
            } else {
                Chat.announce(roomID, "投票の結果" + executedPlayerName + "が処刑されました", -1);
            }
        } else {
            Chat.announce(roomID, "誰も処刑されませんでした", -1);
        }

        //遺言フェーズが無いなら夜のアクションへ移動
        nowPhase = 5;
        //フェーズの終了処理をスケジュール
        Timer timer = new Timer(false);
        TimerTask finishNightPhaseTask = new TimerTask() {
            @Override
            public void run() {
                finishNightPhase();
            }
        };
        timer.schedule(finishNightPhaseTask, gameSettings.nightTime * 1000L);
        nextPhaseTime = Calendar.getInstance().getTimeInMillis()
                + gameSettings.nightTime * 1000L;
        System.out.println("夜が終わるまで" + gameSettings.nightTime * 1000L );
        //夜のアクションが必要なプレイヤーを探し、人狼チャットの権限を変更
        completeNightAction = new HashMap<UUID, Boolean>();
        for (UUID userUUID : playersUUID) {
            String role = playersStatus.getRole(userUUID);
            boolean alive = playersStatus.isAlive(userUUID);
            //人狼に人狼チャットの読み書きが許可されているなら生きている人狼に許可する
            if (alive && role.equals("werewolf")) {
                if (gameSettings.werewolfChatSwitch == 1) {
                    Chat.disableWritingPermission(roomID, 1, userUUID);
                    Chat.disableReadingPermission(roomID, 1, userUUID);
                }
            }

            //夜のアクションをする役職はcompleteNightActionに登録
            //死亡したプレイヤーは夜のアクションができない
            if (alive &&
                    (role.equals("seer") || role.equals("necromancer")
                            || role.equals("knight") || role.equals("hunter")
                            || role.equals("blackKnight") || role.equals("werewolf"))) {
                completeNightAction.put(userUUID, false);
            }
        }
        Chat.announce(roomID, "恐ろしい夜がやってきました\n夜のアクションを実行してください", -2);
    }

    void finishWillPhase() {
        //夜のアクションへ移動
        nowPhase = 5;
        //フェーズの終了処理をスケジュール
        Timer timer = new Timer(false);
        TimerTask finishNightPhaseTask = new TimerTask() {
            @Override
            public void run() {
                finishNightPhase();
            }
        };
        timer.schedule(finishNightPhaseTask, gameSettings.nightTime * 1000L);
        nextPhaseTime = Calendar.getInstance().getTimeInMillis()
                + gameSettings.nightTime * 1000L;
        completeNightAction = new HashMap<UUID, Boolean>();

        //処刑されたプレイヤーのチャットの権限を変更
        UUID executedPlayer = votingAction.getVotingResult();

        Chat.disableWritingPermission(roomID, 0, executedPlayer);
        Chat.disableWritingPermission(roomID, 1, executedPlayer);
        Chat.enableReadingPermission(roomID, 2, executedPlayer);
        Chat.enableWritingPermission(roomID, 2, executedPlayer);
        Chat.announce(roomID, ProfileGetter.getProfile(executedPlayer).name + "の処刑が執行されました", -2);
        //夜のアクションが必要なプレイヤーを探し、人狼チャットの権限を変更
        completeNightAction = new HashMap<UUID, Boolean>();
        for (UUID userUUID : playersUUID) {
            String role = playersStatus.getRole(userUUID);
            boolean alive = playersStatus.isAlive(userUUID);
            if (alive && role.equals("werewolf")) {
                //人狼に人狼チャットの読み書きが許可されているなら許可する
                if (gameSettings.werewolfChatSwitch == 1) {
                    Chat.disableWritingPermission(roomID, 1, userUUID);
                    Chat.disableReadingPermission(roomID, 1, userUUID);
                }
            }

            //夜のアクションをする役職はcompleteNightActionに登録
            //死亡したプレイヤーは夜のアクションができない
            if (alive &&
                    (role.equals("seer") || role.equals("necromancer")
                            || role.equals("knight") || role.equals("hunter")
                            || role.equals("blackKnight") || role.equals("werewolf"))) {
                completeNightAction.put(userUUID, false);
            }
        }
        Chat.announce(roomID, "恐ろしい夜がやってきました\n夜のアクションを実行してください", -1);
    }

    void finishNightPhase(){
        if (isBreakUp) {
            return;
        }
        System.out.println("夜のアクションを終了します");
        //夜のアクションを実行しなければならないのに実行していなかったら実行
        for (Map.Entry<UUID, Boolean> e : completeNightAction.entrySet()) {
            if ( !e.getValue()) {
                doNightAction(e.getKey(), null, 1);
            }
        }


        //朝（議論）フェーズへ
        nowPhase = 2;

        //昨晩の犠牲者をチャットで知らせる
        List<UUID> lastNightVictim = gameLogic.finishNightPhase();
        String deadAnnounce = "恐ろしい夜が明けました。\n昨晩の犠牲者は......";
        if (lastNightVictim.size() == 0) {
            deadAnnounce += "\nいませんでした！";
        } else {
            for (UUID victim : lastNightVictim) {
                deadAnnounce += "\n" + ProfileGetter.getProfile(victim).name;
            }
            deadAnnounce += "\nでした";
        }


        //勝利判定
        if (gameLogic.existWinner() != 0) {
            Chat.announce(roomID, deadAnnounce, -1);
            endGame();
            return;
        } else {
            //死亡したプレイヤーに墓場チャットの読み書きを許可
            //人狼なら人狼チャットの書き込みを禁止
            for (UUID userUUID : lastNightVictim) {
                Chat.enableReadingPermission(roomID, 2, userUUID);
                Chat.enableReadingPermission(roomID, 2, userUUID);
                Chat.disableWritingPermission(roomID, 1, userUUID);
            }
            Chat.announce(roomID, deadAnnounce, -2);
        }

        //フェーズの終了処理をスケジュール
        Timer timer = new Timer(false);
        TimerTask finishDiscussionPhaseTask = new TimerTask() {
            @Override
            public void run() {
                finishDiscussionPhase();
            }
        };
        timer.schedule(finishDiscussionPhaseTask, gameSettings.discussionTime * 1000L);
        nextPhaseTime = Calendar.getInstance().getTimeInMillis()
                + gameSettings.discussionTime * 1000L;
        System.out.println("議論が終わるまで" + gameSettings.discussionTime * 1000L);

        //パン屋の生存をチャットで知らせる
        if (playersStatus.isSurvivingBaker()) {
            Chat.announce(roomID, "今日もおいしいパンが運ばれてきました！", -1);
        }

        //チャットの書き込みを読み書きの権限を設定
        //人狼チャットが夜にしか使えないならチャット書き込みを止める
        if (gameSettings.werewolfChatSwitch == 1) {
            for (UUID werewolfUUID : playersStatus.getWerewolfPlayerUUIDs()) {
                Chat.disableWritingPermission(roomID, 1, werewolfUUID);
            }
        }

        //生存しているなら一般チャットの書き込みを許可
        for (UUID userUUID : playersUUID) {
            if (playersStatus.isAlive(userUUID)) {
                Chat.enableWritingPermission(roomID, 0, userUUID);
            }
        }
    }



    void endGame() {
        nowPhase = 0;
        //勝利陣営の発表
        int winner = gameLogic.existWinner();
        System.out.println("ゲーム終了！");
        Chat.announce(roomID, "ゲーム終了！", -2);
        if (winner == 1) {
            Chat.announce(roomID, "村人陣営の勝利です！",-1);
        } else if (winner == 2) {
            Chat.announce(roomID, "人狼陣営の勝利です！", -1);
        } else if (winner == 3) {
            Chat.announce(roomID, "妖狐陣営の勝利です！", -1);
        } else if (winner == 4) {
            Chat.announce(roomID, "吊人陣営の勝利です！", -1);
        }

        //役職の内訳と生死情報の開示
        Map<UUID,PlayerStatus> statusMap = playersStatus.getStatusMap();
        String result = "詳細";
        for (Map.Entry<UUID, PlayerStatus> status : statusMap.entrySet()) {
            result += "\n" + ProfileGetter.getProfile(status.getKey()).name
                    + ":" + status.getValue().toString();
        }
        Chat.announce(roomID, result, -1);

        //チャット権限を変更
        for (UUID userUUID : playersUUID) {
            Chat.disableWritingPermission(roomID, 1, userUUID);
            Chat.disableWritingPermission(roomID, 2, userUUID);
            Chat.enableReadingPermission(roomID, 0, userUUID);
            Chat.enableReadingPermission(roomID, 1, userUUID);
            Chat.enableReadingPermission(roomID, 2, userUUID);
        }
        GameRoom.gameEnd(roomID);
    };

    public Map<UUID, PlayerStatus> getStatusMap(){
        return playersStatus.getStatusMap();
    }

    public long getNextPhaseTime() {
        return nextPhaseTime;
    }

    public int getNowPhase() {
        return nowPhase;
    }

}
