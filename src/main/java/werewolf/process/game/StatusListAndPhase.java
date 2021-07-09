package werewolf.process.game;

import java.util.List;

public class StatusListAndPhase {
    public List<ProfileAndStatus> statusList;
    public long nextPhaseTime;
    public int nowPhase;
    public StatusListAndPhase(List<ProfileAndStatus> statusList, long nextPhaseTime, int nowPhase) {
        this.statusList = statusList;
        this.nextPhaseTime = nextPhaseTime;
        this.nowPhase = nowPhase;
    }
}
