import java.util.*;

//ゲームのロジックを持ち実行する。

public class VotingAction {
    Map <UUID,UUID> votesData = new HashMap <UUID,UUID>();
    PlayersStatus playersStatus;
    UUID votingResult = null;

    //ゲームに参加するプレイヤー毎のUUIDと対応する役職と生死の状態を持たせる。

    static void VotingAction ( PlayerStatus playerStatus) {
	playerStatus = votesData;
	    }

    //投票者と投票者の投票先の情報を追加する。

    static void vote ( UUID userUUID, UUID targetUserUUID) {
	votesData.put( userUUID,targetUserUUID);
    }
    
    //投票結果を確定させる。同数なものがあったら変数を代入して返す

    boolean finishVote () {
	boolean isTied = false;
	int max = 0;
	UUID result = null;
	for ( String s ; targetUUID) {
	    int v;
	    if ( m.containsKey( s)) {
		v = votesData.get( s) + 1;
	    } else {
		v = 1;
	    }
	    votesData.put( s, v);
	}
	Map <UUID,Integer> votedCount = new HashMap <UUID,Integer>();
	for (Entry <UUID, UUID> entry : votesData.entrySet()) {
	    if ( votedCount.get( entry.getValue()) == null){
		votedCount.put( entry.getValue(), 1);
	    } else {
		votedCount.replace( entry.getValue(), 
				    votedCount.get(entry.getValue()) + 1);
	    }
	}
	for ( Entry <UUID,Integer> entry2 : votedCount) {
	    if ( entry2.getValue() > max) {
		isTied = false;
		max = entry2.getValue();
		result = entry2.getKey();
	    } else if ( entry2.getValue() == max) {
		isTied = true;
	    }
	} 
	if ( isTied) {
	    votingResult = null;
	    return false;
	} else {
	    votingResult = result;
	    return true;
	}
    }
    
    //投票されたプレイヤーを返す。複数いたらnullを返す。

    UUID getVotingResult() {
	return votingResult;
    }

    //投票されたプレイヤーが複数の時ランダムに一人選ぶ。

    UUID determineRandomly() {
	if ( votingResult == null) {
	    int index = new Ramdom().nextInt(votingResult.size());
	    UUID votingResult = votingResult.get(index);
	    return votingResult;
	}
    }
    
    vote startNewVoting() {
	Map <UUID,UUID> voteData = new HashMap <UUID,UUID>();
    }
}
	    
	