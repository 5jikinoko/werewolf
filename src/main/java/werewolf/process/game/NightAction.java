/**
 * 役職ごとの夜のアクションを実行する
 *
 * version 2.0
 * last update date 2021/07/08
 * auther al19013
 */
package werewolf.process.game;

import java.util.*;

import werewolf.process.profile.ProfileGetter;
import werewolf.store.chat.Message;

public class NightAction{
    /**
     *役職ごとの夜のアクションを遂行する
     * playersStatus プレイヤーの生死状況、役職
     * tonightVictim 今夜の犠牲者のリスト
     * killNominee 人狼襲撃候補者
     * guardTarget 騎士の守り先
     * lastNightGuardTarget 昨夜の騎士の守り先
     * continuousGuard 連続ガードの有無
     */
    PlayersStatus playersStatus;
    List<UUID> tonightVictim;
    Map<UUID,Integer> killNominee;
    //keyが護衛する騎士、valueが護衛対象
    Map<UUID,UUID> guardTarget;
    Map<UUID,UUID> lastNightGuardTarget;
    boolean continuousGuard;

    //コンストラクタ
    public NightAction(PlayersStatus playersStatus,boolean continuousGuard){
        this.playersStatus = playersStatus;
        this.continuousGuard = continuousGuard;
        tonightVictim = new ArrayList<UUID>();
        killNominee = new HashMap<UUID,Integer>();
        guardTarget = new HashMap<UUID,UUID>();
        lastNightGuardTarget  = new HashMap<UUID,UUID>();
    }

    //占い師行動処理
    public Message seerAction(UUID userUUID,UUID targetUUID){
        Message message = new Message();
        //占い対象が指定されていないなら自分と死んでるプレイヤー以外からランダムで選ぶ
        if(targetUUID == null){
            List<UUID> notSeeList = playersStatus.getDeadPlayerUUIDs();
            notSeeList.add(userUUID);
            targetUUID = playersStatus.getRandomUser(notSeeList);
        }
        message.userUUID = targetUUID;
        if(playersStatus.getRole(targetUUID).equals("werewolf") || playersStatus.getRole(targetUUID).equals("blackKnight")){
            message.text = "人狼だった";
        }else if(playersStatus.getRole(targetUUID).equals("foxSpirit")){
            //妖狐は占われたら死亡する
            tonightVictim.add(targetUUID);
            message.text = "人狼でなかった";
        }else{
            message.text = "人狼でなかった";
        }
        return message;
    }

    //霊媒師行動処理
    public Message necromancerAction(UUID targetUUID){
        Message message = new Message();
        message.userUUID = targetUUID;
        if(playersStatus.getRole(targetUUID).equals("werewolf")){
            message.text = "人狼だった";
        } else {
            message.text = "人狼でなかった";
        }
        return message;
    }

    //騎士行動処理
    public Message guardAction(UUID userUUID,UUID targetUserUUID){
        Message message = new Message();
        message.text = "護衛した";
        //護衛対象が指定されていないかつ連続無しなら自分と死亡したプレイヤーと前回護衛したプレイヤー以外からランダムで護衛
        if(targetUserUUID == null && !continuousGuard){
            List<UUID> notGuardList = playersStatus.getDeadPlayerUUIDs();
            //昨晩護衛したプレイヤーを取得
            UUID lastNightGuardTarget = this.lastNightGuardTarget.get(userUUID);
            if (lastNightGuardTarget != null) {
                notGuardList.add(lastNightGuardTarget);
            }
            notGuardList.add(userUUID);
            targetUserUUID = playersStatus.getRandomUser(notGuardList);
        }
        //護衛対象がしてされていないなら自分と死亡したプレイヤー以外からランダムで護衛
        if(targetUserUUID == null && continuousGuard){
            List<UUID> notGuardList = playersStatus.getDeadPlayerUUIDs();
            notGuardList.add(userUUID);
            targetUserUUID = playersStatus.getRandomUser(notGuardList);
        }
        message.userUUID = targetUserUUID;
        if(!continuousGuard  && lastNightGuardTarget.get(userUUID).equals(targetUserUUID)){
            message.text ="連続ガードはできません";
            return message;
        }
        guardTarget.put(userUUID,targetUserUUID);

        return message;
    }

    //人狼が噛む対象を決める
    public Message werewolfAction(UUID userUUID,UUID targetUserUUID,int priority){
        List<UUID> werewolfList = new ArrayList<UUID>();
        Message message = new Message();
        message.text = "襲撃を試みた";

        List<UUID> werewolfUUIDs = playersStatus.getWerewolfPlayerUUIDs();
        if(targetUserUUID == null){
            List<UUID> notSelectedPlayers = playersStatus.getDeadPlayerUUIDs();
            //人狼を噛む対象外のリストに追加
            //死んでいるなら既にリストにあるので追加しない
            for (UUID werewolfUUID : werewolfUUIDs) {
                if (playersStatus.isAlive(werewolfUUID)) {
                    notSelectedPlayers.add(werewolfUUID);
                }
            }
            targetUserUUID = playersStatus.getRandomUser(notSelectedPlayers);
            priority = 1;
        }
        if(werewolfUUIDs.contains(targetUserUUID)){
            message.text = "エラー:人狼を襲撃できない";
            return message;
        }
        message.userUUID = targetUserUUID;
        if(priority < 1){
            priority = 1;
        }else if(priority > 5){
            priority = 5;
        }
        //噛み対象に投票
        if(killNominee.containsKey(targetUserUUID)){
            killNominee.put(targetUserUUID,killNominee.get(targetUserUUID) + priority);
        }   else    {
            killNominee.put(targetUserUUID,priority);
        }
        return message;
    }

    //怪盗行動処理
    public Message phantomThiefAction(UUID userUUID,UUID targetUserUUID){
        Message message = new Message();
        //盗む対象が指定されていないなら自分以外をランダムで選ぶ
        if(targetUserUUID == null){
            List<UUID> userUUID_ = new ArrayList<UUID>();
            userUUID_.add(userUUID);
            targetUserUUID = playersStatus.getRandomUser(userUUID_);
        }
        message.userUUID = targetUserUUID;
        message.text = playersStatus.getRoleInJapanese(targetUserUUID) + "を奪った";
        String stolenRole = playersStatus.getRole(targetUserUUID);
        playersStatus.changeRole(userUUID, stolenRole);
        playersStatus.changeRole(targetUserUUID,"phantomThief");
        if(stolenRole.equals("werewolf") || stolenRole.equals("traitor") ){
            tonightVictim.add(targetUserUUID);
        }
        return message;
    }

    public List<UUID> finishFirstNightAction() {
        List<UUID> result;
        for(int i = 0; i < tonightVictim.size();i++){
            playersStatus.kill(tonightVictim.get(i));
            System.out.println(ProfileGetter.getProfile(tonightVictim.get(i)).name + "が死亡");
        }

        lastNightGuardTarget = guardTarget;
        guardTarget = new HashMap<UUID,UUID>();
        killNominee = new HashMap<UUID,Integer>();
        result = tonightVictim;
        tonightVictim = new ArrayList<UUID>();

        return result;
    }

    //犠牲者処理
    public List<UUID> finishNightAction(){
        List<UUID> result;
        //一番人狼に狙われたユーザを探す
        Integer maxValue = 0;
        UUID maxValueUUID = null;
        for (Map.Entry<UUID, Integer> temp : killNominee.entrySet()) {
            if(temp.getValue().compareTo(maxValue) > 0){
                maxValueUUID = temp.getKey();
                maxValue = temp.getValue();
            }
        }
        if (maxValueUUID == null) {
            System.out.println("エラー：襲撃対象が決まらない");
        } else {
            System.out.println("襲撃されるのは:" + ProfileGetter.getProfile(maxValueUUID).name + maxValue + "票");
        }
        //人狼に襲撃されたプレイヤー護衛されていないなら死亡
        if(!guardTarget.containsValue(maxValueUUID)){
            tonightVictim.add(maxValueUUID);
        } else {
            System.out.println(ProfileGetter.getProfile(maxValueUUID).name + "を人狼から守った");
        }

        for(int i = 0; i < tonightVictim.size();i++){
            playersStatus.kill(tonightVictim.get(i));
            System.out.println(ProfileGetter.getProfile(tonightVictim.get(i)).name + "が死亡");
        }

        lastNightGuardTarget = guardTarget;
        guardTarget = new HashMap<UUID,UUID>();
        killNominee = new HashMap<UUID,Integer>();
        result = tonightVictim;
        tonightVictim = new ArrayList<UUID>();

        return result;
    }
}
