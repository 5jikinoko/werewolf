import java.util.Random;

import org.graalvm.compiler.nodes.PiArrayNode.Placeholder;

import jdk.javadoc.internal.doclets.formats.html.markup.Navigation;

import java.util.*;

public class GameLogic {
	public static void main(String[] args){    // test
		PlayersStatus playersStatus = new PlayersStatus();
		GameSettings gameSettings = new GameSettings(playersStatus);
		GameLogic gameLogic = new GameLogic();
		GameLogic.checkRoleSetting(23, gameSettings);
		gameLogic.distributeRole();
	}

	public void GameLogic(PlayersStatus playersStatus) {
		GameSettings gameSettings = new GameSettings(playersStatus);
	}

	public static boolean checkRoleSetting(int playerCount, GameSettings gameSettings) {
		RoleBreakdown roleLimit = new RoleBreakdown();
		int temp;

		//初日占いあり/なしでの分岐
		if(gameSettings.firstNightSee == 1){
			//全員-人狼-1
			temp = playerCount - roleLimit.werewolvesNum - 1;
			if(temp > roleLimit.werewolvesNum) {
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
			if(temp > roleLimit.werewolvesNum) {
				return true;
			} else {
				return false;
			}
		}
	}

	public void distributeRole() {
		int playerCount;
		RoleBreakdown roleLimit = new RoleBreakdown();
		PlayersStatus playersStatus = new PlayersStatus();
		GameSettings gameSettings = new GameSettings(playersStatus);
		List<String> roles = new ArrayList<>();
		Random rand = new Random();

		playerCount = playersStatus.playerCount();

		while(true){
			for (int i = 0; i < playerCount - roleLimit.werewolvesNum; i++){
				int num = rand.nextInt(13);
				if(num == 0) {
					roles.add("村人");
				} else if (num == 1) {
					roles.add("占い師");
				} else if (num == 2) {
					roles.add("霊媒師");
				} else if (num == 3) {
					roles.add("騎士");
				} else if (num == 4) {
					roles.add("ハンター");
				} else if (num == 5) {
					roles.add("黒騎士");
				} else if (num == 6) {
					roles.add("共有者");
				} else if (num == 7) {
					roles.add("パン屋");
				} else if (num == 8) {
					roles.add("狂人");
				} else if (num == 9) {
					roles.add("背信者");
				} else if (num == 10) {
					roles.add("妖狐");
				} else if (num == 11) {
					roles.add("吊人");
				} else if (num == 12) {
					roles.add("怪盗");
				}
			}

			for (int i = 0; i < roleLimit.werewolvesNum; i++) {
				int num = rand.nextInt(playerCount - roleLimit.werewolvesNum + i);
				roles.add(num, "人狼");
			}

			playersStatus.setRoles(roles);

			if (GameLogic.checkRoleSetting(playerCount, gameSettings) == true) {
				break;
			}
		}
	}

	int priority;

	public Message doNightAction(UUID userUUID) {
		UUID targetUUID = new UUID();
		PlayersStatus playersStatus = new PlayersStatus();
		NightAction nightAction = new NightAction(playersStatus);
		VotingAction votingAction = new VotingAction(playersStatus);
		Message message = new Message();
		String role;
		role = playerStatus.getRole(userUUID, targetUUID);
		if (role.equals("占い師")) {
			message = nightAction.seerAction(userUUID);
		} else if(role.equals("霊媒師")) {
			message = nightAction.necromancersAction(userUUID, votingAction.getVotingResult);
		} else if(role.equals("騎士") || role.equals("黒騎士")) {
			message = nightAction.guardAction(userUUID, targetUUID);
		}  else if(role.equals("人狼")) {
			message = nightAction.werewolvAction(userUUID, targetUUID, priority);
			priority += 1;
		}  else if(role.equals("怪盗")) {
			message = nightAction.phantomThiefAction(userUUID, targetUUID);
		}
		return message;
	}

	public List<UUID> finishNightPhase() {
		PlayersStatus playersStatus = new PlayersStatus();
		NightAction nightAction = new NightAction(playersStatus);
		return nightAction.finishNightPhase;
	}

	public int existWinner() {
		PlayersStatus playersStatus = new PlayersStatus();
		int villagers, werewolves, foxSpirits;

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
