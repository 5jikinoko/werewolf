

package werewolf.store.gamesettings;

public class GameSettings {
    public int discussionTime;
    public int votingTime;
    public int nightTime;
    public int willTime;
    public int tieVoteOption;
    public int werewolfChatSwitch;
    public int firstNightSee;
    public boolean canSeeMissingPosition;
    public boolean isSecretBallot;
    public boolean canContinuousGuard;
    public boolean isRandomStealing;
    public boolean isOneNight;

    /**
     *
     * @param discussionTime 議論時間
     * @param votingTime 投票時間
     * @param nightTime 夜の時間
     * @param willTime 遺言時間
     * @param tieVoteOption 投票が同数のときの処理
     * 0:ランダムで一人処刑
     * 1:誰も処刑されない
     * @param werewolfChatSwitch 人狼チャットが使えるフェーズ
     * 0:常に使えない
     * 1:夜のフェーズのみ
     * 2:常に使える
     * @param firstNightSee 初日占いの設定
     * 0:初日占い有り
     * 1:初日占い無し
     * 2:初日にランダム占い
     * @param canSeeMissingPosition 欠けている役職の占いの可否
     * true:欠けてる役職を占える
     * false:欠けてる役職を占えない
     * @param isSecretBallot 投票先の表示の有無
     * true:匿名投票
     * false:公開投票
     * @param canContinuousGuard 騎士の連続ガードの有無
     * true:連続ガードあり
     * false:連続ガードなし
     * @param isRandomStealing 怪盗が盗むプレイヤーの指定
     * true:ランダムで盗む
     * false:選んで盗む
     * @param isOneNight ワンナイト人狼かどうか
     * true:ワンナイト人狼である
     * false:ワンナイト人狼でない
     */
    public GameSettings(
            int discussionTime,
            int votingTime,
            int nightTime,
            int willTime,
            int tieVoteOption,
            int werewolfChatSwitch,
            int firstNightSee,
            boolean canSeeMissingPosition,
            boolean isSecretBallot,
            boolean canContinuousGuard,
            boolean isRandomStealing,
            boolean isOneNight
            ) {
        this.discussionTime = discussionTime;
        this.votingTime = votingTime;
        this.nightTime = nightTime;
        this.willTime = willTime;
        this.tieVoteOption = tieVoteOption;
        this.werewolfChatSwitch = werewolfChatSwitch;
        this.firstNightSee = firstNightSee;
        this.canSeeMissingPosition = canSeeMissingPosition;
        this.isSecretBallot = isSecretBallot;
        this.canContinuousGuard = canContinuousGuard;
        this.isRandomStealing = isRandomStealing;
        this.isOneNight = isOneNight;
    }
}
