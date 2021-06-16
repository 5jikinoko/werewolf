import java.util.*;

public class VotingAction{
    Map<UUID,UUID>votesData=new HashMap<UUID,UUID>();
    PlayersStatus playersStatus;
    UUID votingResult = null;
    static void VotingAction(PlayerStatus playerStatus){
	playerStatus = votesData;
	    }
    static void vote(UUID userUUID, UUID targetUserUUID){
	votesData.put(userUUID,targetUserUUID);
    }
    boolean finishVote() {
	boolean isTied=false;
	int max = 0;
	UUID result = null;
	for(String s ; targetUUID){
	    int v;
	    if(m.containsKey(s)){
		v=votesData.get(s)+1;
	    }else{
		v=1;
	    }
	    votesData.put(s, v);
	}
	Map<UUID,Integer> votedCount = new HashMap<UUID,Integer>();
	for(Entry<UUID, UUID> entry : votesData.entrySet()){
	    if(votedCount.get(entry.getValue()) == null){
		votedCount.put(entry.getValue(), 1);
	    } else {
		votedCount.replace(entry.getValue(), votedCount.get(entry.getValue())+1);
	    }
	}
	for(Entry<UUID,Integer> entry2 : votedCount){
	    if(entry2.getValue() > max){
		isTied = false;
		max = entry2.getValue();
		result = entry2.getKey();
	    } else if(entry2.getValue() == max){
		isTied = true;
	    }
	} 
	if(isTied){
	    votingResult = null;
	    return false;
	} else {
	    votingResult = result;
	    return true;
	}
    }
    UUID getVotingResult(){
	return votingResult;
    }
    vote startNewVoting(){
	Map<UUID,UUID> voteData=new HashMap<UUID,UUID>();
    }
}
	    
	