/**
 * プロフィール編集の管理部
 *
 * @version 1.0
 * @author al19100
 */

import java.util.UUID;

public class ProfileEditManagement {

	/**
	 * UUIDに対応するプレイヤーネーム、アイコン画像を登録
	 * 引数nullなら新規生成して返す。そうでなければ引数を返す
	 * @param uuid 新規生成したUUID
	 */

	public UUID profileEntry(String playerName, int imageNum, UUID playerUUID) {
		UserProfileInformation userProfileInformation = new UserProfileInformation();
		if (playerUUID == null) {
			// UUID生成
			UUID uuid = UUID.randomUUID();
			// ユーザ情報登録
			userProfileInformation.RegisterPlayerProfile(uuid, playerName, imageNum);
			return uuid;
		} else {
			// ユーザ情報登録
			userProfileInformation.RegisterPlayerProfile(playerUUID, playerName, imageNum);
			return playerUUID;
		}
	}

	/**
	 * UUIDに対応するプレイヤーネームを返す
	 */

	public String playerNameGet(UUID playerUUID) {
		return UserProfileInformation.playerNameMap.get(playerUUID);
	}

	/**
	 * UUIDに対応するアイコン画像を返す
	 */

	public int imageNumGet(UUID playerUUID) {
		return UserProfileInformation.imageNumMap.get(playerUUID);
	}

	/**
	 * UUIDに対応する部屋IDを登録する
	 */

	public void roomEntry(UUID playerUUID, int roomID) {
		UserRoomInformation userRoomInformation = new UserRoomInformation();
		userRoomInformation.RegisterRoomID(playerUUID, roomID);
	}

	/**
	 * UUIDに対応する部屋IDをを返す
	 */

	public int roomGet(UUID playerUUID) {
		return UserRoomInformation.roomIDMap.get(playerUUID);
	}

	/**
	 * 部屋退出処理を依頼する。退出した旨をチャットに送信する
	 */

	public void roomExit(UUID playerUUID) { // 仮void
		// TBD C10内部設計書できてから書く
	}


}
