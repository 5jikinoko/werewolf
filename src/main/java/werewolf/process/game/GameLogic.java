/**
 * ゲームのロジックを持ち、実行する
 *
 * @version 2.0
 * @author al19100
 */

package werewolf.process.game;

import java.util.Random;

import java.util.*;

import werewolf.store.gamesettings.GameSettings;

import werewolf.store.gamesettings.RoleBreakdown;

import werewolf.store.chat.Message;

public class GameLogic {
	public static void main(String[] args){

	}

	PlayersStatus playersStatus;
	VotingAction votingAction;
	GameSettings gameSettings;
	NightAction nightAction;

	/**
	 * コンストラクタ
	 * @param playersStatus  ゲーム時のユーザの生死と役職の情報
	 * @param votingAction 投票について
	 * @param gameSettings ゲームの設定情報
	 */

	GameLogic(PlayersStatus playersStatus, VotingAction votingAction, GameSettings gameSettings) {
		this.playersStatus = playersStatus;
		this.gameSettings = gameSettings;

		this.votingAction = votingAction;

		this.nightAction = new NightAction(playersStatus, gameSettings.canContinuousGuard);
	}

	/**
	 * 2日目朝に勝敗が決まる可能性があるかチェック
	 * @param playerCount 参加人数
	 * @param roleLimit 各役職の上限人数
	 * @return 2日目朝に勝敗が決まるor共有者の数が不適切ならfalse, そうでなければtrue
	 */

	public static boolean checkRoleSetting(int playerCount, RoleBreakdown roleLimit) {
		//共有者の数が0,2以外でないかチェック
		if (roleLimit.freemasonariesNum != 0 && roleLimit.freemasonariesNum != 2) {
			return false;
		}
		//人間の数を取得
		int humanCount = playerCount - (roleLimit.werewolvesNum + roleLimit.foxSpiritsNum + roleLimit.foolsNum);
		//怪盗がいた場合、怪盗が人狼がまたは背信者を盗む対象としたとき初日夜に人間が一人死亡するので
		//条件を満たすのに必要な人間の数が増える
		if (roleLimit.phantomThievesNum != 0) {
			humanCount -= 1;
		}
		//初日朝の時点で人狼の勝利条件を満たさないならtrue
		return humanCount > roleLimit.werewolvesNum;
	}

	/**
	 * 役職わけ
	 * @param playerCount 参加人数
	 * @param roleLimit 各役職の上限人数
	 */

	public void distributeRole(int playerCount, RoleBreakdown roleLimit) {
		System.out.println("参加者数(playerCount):" + playerCount + "役職の制限人数(roleLimit): 村人:" + roleLimit.villagersNum + " 占い師:" + roleLimit.seersNum
				+ " 霊媒師:" + roleLimit.necromancersNum + " 騎士:" + roleLimit.knightsNum
				+ " ハンター:" + roleLimit.huntersNum + " 黒騎士:" + roleLimit.blackKnightsNum
				+ " 共有者:" + roleLimit.freemasonariesNum + " パン屋:" + roleLimit.bakersNum
				+ " 人狼:" + roleLimit.werewolvesNum + " 狂人:" + roleLimit.madmenNum
				+ " 背信者:" + roleLimit.traitorsMum + " 妖狐:" + roleLimit.foxSpiritsNum
				+ " 吊人:" + roleLimit.foolsNum + " 怪盗:" + roleLimit.phantomThievesNum);

		List<String> roles = new ArrayList<>();
		//リストに人狼以外全ての役職を上限まで入れる
		for (int i = 0; i< roleLimit.villagersNum; ++i) {
			roles.add("villager");
		}
		for (int i = 0; i< roleLimit.seersNum; ++i) {
			roles.add("seer");
		}
		for (int i = 0; i< roleLimit.necromancersNum; ++i) {
			roles.add("necromancer");
		}
		for (int i = 0; i< roleLimit.knightsNum; ++i) {
			roles.add("knight");
		}
		for (int i = 0; i< roleLimit.huntersNum; ++i) {
			roles.add("hunter");
		}
		for (int i = 0; i< roleLimit.blackKnightsNum; ++i) {
			roles.add("blackKnight");
		}
		for (int i = 0; i< roleLimit.freemasonariesNum; ++i) {
			roles.add("freemasonary");
			//共有者はいるなら一人だけ入れる
			++i;
		}
		for (int i = 0; i< roleLimit.bakersNum; ++i) {
			roles.add("baker");
		}
		for (int i = 0; i< roleLimit.madmenNum; ++i) {
			roles.add("madman");
		}
		for (int i = 0; i< roleLimit.traitorsMum; ++i) {
			roles.add("traitor");
		}
		for (int i = 0; i< roleLimit.foxSpiritsNum; ++i) {
			roles.add("foxSpirit");
		}
		for (int i = 0; i< roleLimit.foolsNum; ++i) {
			roles.add("fool");
		}
		for (int i = 0; i< roleLimit.phantomThievesNum; ++i) {
			roles.add("phantomThief");
		}
		//シャッフル
		Collections.shuffle(roles);
		//リストの先頭から（参加人数-人狼の数）番目までを切り出す
		List<String> result = roles.subList(0, playerCount - roleLimit.werewolvesNum);
		//選ばれた役職に共有者が含まれているなら共有者を一人追加
		if (result.contains("freemasonary")) {
			//先頭が共有者なら配列の2番目を共有者にそうでないなら先頭を共有者に
			if (result.get(0).equals("freemasonary")) {
				result.set(1, "freemasonary");
			} else {
				result.set(0, "freemasonary");
			}
		}
		//人狼の数だけ人狼の役職をリストに追加
		for (int i = 0; i < roleLimit.werewolvesNum; ++i) {
			result.add("werewolf");
		}
		//シャッフル
		Collections.shuffle(result);

		System.out.println("振り分け完了！");
		for (String role : result) {
			System.out.println(role);
		}
		playersStatus.setRoles(result);
	}


	/**
	 * @param userUUID 夜のアクションをするプレイヤー
	 * @param targetUUID アクションの対象となるプレイヤー
	 * @param votingPriority 人狼の投票優先度
	 * @return 夜のアクションの対象のUUIDとアクションの結果もしくはエラー
	 */

	public Message doNightAction(UUID userUUID, UUID targetUUID, int votingPriority) {
		Message message = new Message();
		String role;
		UUID votingResult;

		role = playersStatus.getRole(userUUID);

		// 役職ごとのアクション
		if (role.equals("seer")) {
			message = nightAction.seerAction(userUUID, targetUUID);
		} else if (role.equals("necromancer")) {
			votingResult = votingAction.getVotingResult();
			if (votingResult != null) {
				message = nightAction.necromancerAction(votingResult);
			} else {
				message.userUUID = userUUID;
				message.text = "霊視の対象がいませんでした";
			}
		} else if (role.equals("knight") || role.equals("blackKnight") || role.equals("hunter")) {
			message = nightAction.guardAction(userUUID, targetUUID);
		}  else if (role.equals("werewolf")) {
			message = nightAction.werewolfAction(userUUID, targetUUID, votingPriority);
		}  else if (role.equals("phantomThief")) {
			message = nightAction.phantomThiefAction(userUUID, targetUUID);
			System.out.println("怪盗が夜のアクションを実行");
		}

		return message;
	}

	public List<UUID> finishFirstNightAction() {
		return  nightAction.finishFirstNightAction();
	}

	/**
	 * @return nightActionクラスのfinishNightActionメソッドを返す
	 */
	public List<UUID> finishNightPhase() {
		return nightAction.finishNightAction();
	}

	/**
	 * 勝利条件を満たしているプレイヤーがいるかチェック
	 * @return 以下記述
	 * 	0:勝利条件を満たした陣営無し
		1:村人陣営が勝利条件を満たした
		2:人狼陣営が勝利条件を満たした
		3:妖狐陣営が勝利条件を満たした
	 	4:吊人陣営が勝利条件を満たした
	 */
	public int existWinner() {
		//投票で処刑されたのが吊人だったら吊人陣営の勝利
		UUID executedPlayer = votingAction.getVotingResult();
		if (executedPlayer != null && playersStatus.getRole(executedPlayer).equals("fool")) {
			return 4;
		}
		int villagers, werewolves, foxSpirits;

		// 陣営ごとの生存人数
		villagers = playersStatus.survivingHumanSize();
		werewolves = playersStatus.survivingWerewolvesSize();
		foxSpirits = playersStatus.survivingFoxSpiritsSize();
		if (villagers <= werewolves) {
			if (foxSpirits != 0) {
				return 3;
			}
			return 2;
		} else if (werewolves == 0) {
			if (foxSpirits != 0) {
				return 3;
			}
			return 1;
		} else {
			return 0;
		}
	}
}
