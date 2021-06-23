package werewolf.store.gamesettings;

public class GameSettings {
    public final int discussionTime;
    public final int votingTime;
    public final int nightTime;
    public final int willTime;
    public final int tieVoteOption;
    public final int werewolfChatSwitch;
    public final int firstNightSee;
    public final boolean canSeeMissingPosition;
    public final boolean isSecretBallot;
    public final boolean canContinuousGuard;
    public final boolean isRandomStealing;
    public final boolean isOneNight;
    GameSettings(int discussionTime,
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
                 boolean isOneNight) {
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
