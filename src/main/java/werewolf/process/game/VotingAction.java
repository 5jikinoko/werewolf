import java.util.*;
//ゲームのロジックを持ち実行する。
public class VotingAction {

    //keyが投票者のUUID、valueが投票先のUUID
    Map <UUID,UUID> votesData = new HashMap <UUID,UUID>();
    PlayersStatus playersStatus;

    //投票の結果処刑されるプレイヤーのUUID
    UUID votingResult = null;

    //keyが得票者、valueが得票数
    Map <UUID,Integer> votedCount = null;

    //投票で同率一位になったプレイヤーの数
    int firstPlaceCount = 0;

    //最多得票数
    int maxVote = 0;

    //ゲームに参加するプレイヤー毎のUUIDと対応する役職と生死の状態を持たせる。
    VotingAction (PlayersStatus playersStatus) {
	this.playersStatus = playersStatus;
    }

    //投票者と投票者の投票先の情報を追加する。
    void vote ( UUID userUUID, UUID targetUserUUID) {
	votesData.put( userUUID,targetUserUUID);
    }

    //投票結果を確定させる。同数なものがあったら変数を代入して返す
    boolean finishVote () {

        //前回の投票結果を消して初期化
        maxVote = 0;
        UUID result = null;
        votedCount = new HashMap <UUID,Integer>();

        //votedCountにkeyが得票者、valueが得票数とすることで投票の結果を集計する
        for (Map.Entry<UUID, UUID> entry : votesData.entrySet()) {
            if ( votedCount.get( entry.getValue()) == null){
		votedCount.put( entry.getValue(), 1);
            } else {
		votedCount.replace( entry.getValue(), 
				    votedCount.get(entry.getValue()) + 1);
            }
        }

        //得票数が最多のプレイヤーを求める
        for ( Map.Entry <UUID,Integer> entry2 : votedCount.entrySet()) {
            if ( entry2.getValue() > maxVote) {

                //得票数がこれまでの最多(max)より多い人がいた
                firstPlaceCount = 1;
                maxVote = entry2.getValue();
                result = entry2.getKey();
            } else if ( entry2.getValue() == maxVote) {

                //得票数がmaxのプレイヤーがもう一人いた
                firstPlaceCount += 1;
            }
        } 
        if ( firstPlaceCount == 1) {

            //投票の結果単独1位ならVotingResult（処刑対象）が確定
            votingResult = result;
            return true;
        } else {

            //投票の結果1位のプレイヤーが複数いたら結果は保留
            votingResult = null;
            return false;
        }
    }

    //投票されたプレイヤーを返す。複数いたらnullを返す。
    UUID getVotingResult() {
	return votingResult;
    }

    //投票されたプレイヤーが複数の時ランダムに一人選ぶ。
    UUID determineRandomly() {

        //同率1位が1人だけなら既に結果が決まっているのでその結果を返す
        if ( firstPlaceCount == 1) {
            return votingResult;
        }

        //0以上同率1位の人の数未満のランダムな整数
        int index = (new Random()).nextInt(firstPlaceCount);

        //得票数が同率1位の人を探し見つけたときにindexが0ならその人を投票の結果にする。
        //得票数が同率1位の人を探し見つけたときにindexが0でないならデクリメント。
        for ( Map.Entry<UUID, Integer> candidates : votedCount.entrySet() ) {
            if ( candidates.getValue() == maxVote) {

                //得票数が最多の内の1人だったら
                if ( index == 0 ) {
                    votingResult = candidates.getKey();
                    return votingResult;
                } else {
                    --index;
                }
            }
        }
        //エラー。ここは呼ばれないはず
        return null;
    }
    void startNewVoting() {
	this.votesData = new HashMap <UUID,UUID>();
    }
}