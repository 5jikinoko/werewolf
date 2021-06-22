/**
 * ゲームのロジックを持ち、実行する
 *
 * @version 1.1
 * @author al19100
 */

import java.util.Random;

import org.graalvm.compiler.nodes.PiArrayNode.Placeholder;

import jdk.javadoc.internal.doclets.formats.html.markup.Navigation;

import java.util.*;

public class GameLogic extends VotingAction {

	PlayerStatus playersStatus;
	VotingAction votingAction;
	GameSettings gameSettings;
	NightAction nightAction;

	/**
	 * コンストラクタ
	 * @param playersStatus  ゲーム時のユーザの生死と役職の情報
	 * @param votingAction 投票について
	 * @param gaemSettings ゲームの設定情報
	 * @param nightAction 夜のアクション
	 */

	public GameLogic(PlayersStatus playersStatus, VotingAction votingAction, GameSettings gameSettings, NightAction nightAction) {
		this.playersStatus = new PlayersStatus();
		this.playersStatus = playersStatus;

		this.gameSettings = new GameSettings();
		this.gameSettings = gameSettings;

		this.votingAction = new VotingAction(playersStatus);
		this.votingAction = votingAction;
		
		this.nightAction = new NightAction(playersStatus, gameSettings.canContinuousGuard);
		this.nightAction = nightAction;
	}

	/**
	 * 2日目朝に勝敗が決まる可能性があるかチェック
	 * @param playerCount 参加人数
	 * @param firstNightSee 初日占いの設定
	 * @param roleLimit 各役職の上限人数
	 * @return 2日目朝に勝敗が決まるor共有者の数が不適切ならfalse, そうでなければtrue
	 */

	public static boolean checkRoleSetting(int playerCount, int firstNightSee, RoleBreakdown roleLimit) {
		int temp;

		//共有者の数が0,2以外でないかチェック
		if (roleLimit.freemasonariesNum != 0 && roleLimit.freemasonariesNum != 2) {
			return false;
		}

		//初日占いあり/なしでの分岐
		if (firstNightSee == 1 || firstNightSee == 2) {
			//全員-人狼-1
			temp = playerCount - roleLimit.werewolvesNum - 1;
			if (temp > roleLimit.werewolvesNum) {
				return true;
			} else {
				return false;
			}
		} else {
			//妖狐>=占いまたは妖狐<占い
			if (roleLimit.foxSpiritsNum >= roleLimit.seersNum) {
				//全員-人狼-(妖狐-占い)-1
				temp = playerCount - roleLimit.werewolvesNum - (roleLimit.foxSpiritsNum - roleLimit.seersNum) - 1;
			} else {
				//全員-人狼-妖狐-1
				temp = playerCount - roleLimit.werewolvesNum - roleLimit.foxSpiritsNum - 1;
			}
			if (temp > roleLimit.werewolvesNum) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * 役職わけ
	 * @param playerCount 参加人数
	 * @param roleLimit 各役職の上限人数
	 * @param roles 振り分ける役職のリスト
	 * @param temp 役職ごとの振り分け済み人数
	 */

	public void distributeRole(int playerCount, RoleBreakdown roleLimit) {
		List<String> roles = new ArrayList<>();
		int[] temp = new int[13];
		Random rand = new Random();

		while(true){
			for (int i = 0; i < 13; i++) {
				temp[i] = 0;
			}

			// 人狼以外を振り分け
			for (int i = 0; i < playerCount - roleLimit.werewolvesNum; i++) {
				int num = rand.nextInt(13);
				if (num == 0 && roleLimit.villagersNum != temp[0]) {
					roles.add("villager");
					temp[0]++;
				} else if (num == 1 && roleLimit.seersNum != temp[1]) {
					roles.add("seer");
					temp[1]++;
				} else if (num == 2 && roleLimit.necromancersNum != temp[2]) {
					roles.add("necromancer");
					temp[2]++;
				} else if (num == 3 && roleLimit.knightsNum != temp[3]) {
					roles.add("knight");
					temp[3]++;
				} else if (num == 4 && roleLimit.huntersNum != temp[4]) {
					roles.add("hunter");
					temp[4]++;
				} else if (num == 5 && roleLimit.blackKnightsNum != temp[5]) {
					roles.add("blackKnight");
					temp[5]++;
				} else if (num == 6 && roleLimit.freemasonariesNum != temp[6]) {
					roles.add("freemasonary");
					temp[6]++;
				} else if (num == 7 && roleLimit.bakersNum != temp[7]) {
					roles.add("baker");
					temp[7]++;
				} else if (num == 8 && roleLimit.madmenNum != temp[8]) {
					roles.add("madmen");
					temp[8]++;
				} else if (num == 9 && roleLimit.traitorsMum !=  temp[9]) {
					roles.add("traitor");
					temp[9]++;
				} else if (num == 10 && roleLimit.foxSpiritsNum != temp[10]) {
					roles.add("foxSpirit");
					temp[10]++;
				} else if (num == 11 && roleLimit.foolsNum != temp[11]) {
					roles.add("fool");
					temp[11]++;
				} else if (num == 12 && roleLimit.phantomThievesNum != temp[12]) {
					roles.add("phantomThieve");
					temp[12]++;
				} else {
					// 既に上限人数に達していた場合
					i--;
				}
			}

			// 人狼を振り分け
			for (int i = 0; i < roleLimit.werewolvesNum; i++) {
				int num = rand.nextInt(playerCount - roleLimit.werewolvesNum + i);
				roles.add(num, "werewolve");
			}

			// 振り分けに問題がないかチェック
			if (GameLogic.checkRoleSetting(playerCount, gameSettings) == true) {
				playersStatus.setRoles(roles);
				break;
			}
			// 問題があれば再振り分け
		}
	}

	/**
	 * @param userUUID 夜のアクションをするプレイヤー
	 * @param targetUUID アクションの対象となるプレイヤー
	 * @param votingPriority 人狼の投票優先度
	 * @param role アクションをするプレイヤーの役職
	 * @param votingResult necromancerの対象となるプレイヤーのUUID
	 * @return 夜のアクションの対象のUUIDとアクションの結果もしくはエラー
	 */

	public Message doNightAction(UUID userUUID, UUID targetUUID, int votingPriority) {
		Message message = new Message();
		String role;
		UUID votingResult;

		role = playersStatus.getRole(userUUID);

		if (role.equals("seer")) {
			message = nightAction.seerAction(userUUID, targetUUID);
		} else if (role.equals("necromancer")) {
			votingResult = nightAction.getVotingResult();
			if (votingResult != null) {
				message = nightAction.necromancersAction(userUUID, votingResult);
			} else {
				message.userUUID = null;
				message.text = "霊視の対象がいませんでした";
			}
		} else if (role.equals("knight") || role.equals("blackKnight")) {
			message = nightAction.guardAction(userUUID, targetUUID);
		}  else if (role.equals("werewolve")) {
			message = nightAction.werewolvAction(userUUID, targetUUID, votingPriority);
		}  else if (role.equals("phantomThief")) {
			message = nightAction.phantomThiefAction(userUUID, targetUUID);
		}

		return message;
	}

	/**
	 * @return nightActionクラスのfinishNightPhaseメソッドを返す
	 */
	public List<UUID> finishNightPhase() {
		return nightAction.finishNightPhase;
	}

	/**
	 * 勝利条件を満たしているプレイヤーがいるかチェック
	 * @return 以下記述
	 * 	0:勝利条件を満たした陣営無し
		1:村人陣営が勝利条件を満たした
		2:人狼陣営が勝利条件を満たした
		3:妖狐陣営が勝利条件を満たした
	 */
	public int existWinner() {
		int villagers, werewolves, foxSpirits;

		// 陣営ごとの生存人数
		villagers = playersStatus.survivingVillagersTeamSize;
		werewolves = playersStatus.survivingWerewolvesTeamSize;
		foxSpirits = playersStatus.survivingFoxSpiritsTeamSize;

		if (villagers == werewolves) {
			if (foxSpirits != 0) {
				return 3;
			}
			return 1;
		} else if (foxSpirits == 0 && werewolves == 0) {
			if (foxSpirits != 0) {
				return 3;
			}
			return 2;
		} else {
			return 0;
		}
	}
}
