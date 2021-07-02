/**
 * 役職ごとの夜のアクションを実行する
 *
 * version 1.4
 * last update date 2021/06/19
 * auther al19013
 */
package werewolf.process.game;

import java.util.*;

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
        if(targetUUID == null){
            List<UUID> seerList = new ArrayList<UUID>();
            seerList = playersStatus.getDeadPlayerUUIDs();
            seerList.add(userUUID);
            targetUUID = playersStatus.getRandomUser(seerList);
        }
        message.userUUID = targetUUID;
        if(playersStatus.getRole(targetUUID).equals("werewolf") || playersStatus.getRole(targetUUID).equals("blackKnight")){
            message.text = "人狼だった";
        }else if(playersStatus.getRole(targetUUID).equals("foxSpirit")){
            tonightVictim.add(targetUUID);
            message.text = "人狼でなかった";
        }else{
            message.text = "人狼でなかった";
        }
        return message;
    }

    //霊媒師行動処理
    public Message necromancerAction(UUID userUUID,UUID targetUUID){
        Message message = new Message();
        message.userUUID = targetUUID;
        if(playersStatus.getRole(targetUUID).equals("werewolf")){
            message.text = "人狼だった";
        }else{
            message.text = "人狼でなかった";
        }
        return message;
    }

    //騎士行動処理
    public Message guardAction(UUID userUUID,UUID targetUserUUID){
        Message message = new Message();
        message.text = "護衛した";
        if(targetUserUUID == null && !continuousGuard){
            List<UUID> guardList = new ArrayList<UUID>();
            guardList = playersStatus.getDeadPlayerUUIDs();
            for (Map.Entry<UUID,UUID>   entry : lastNightGuardTarget.entrySet()){
                guardList.add(entry.getKey());
            }
            targetUserUUID = playersStatus.getRandomUser(guardList);
        }

        if(targetUserUUID == null && continuousGuard){
            targetUserUUID = playersStatus.getRandomUser(playersStatus.getDeadPlayerUUIDs());
        }
        message.userUUID = targetUserUUID;
        if(!continuousGuard  && lastNightGuardTarget.get(userUUID) == targetUserUUID){
            message.text ="連続ガードはできません";
            return message;
        }
        guardTarget.put(targetUserUUID,userUUID);

        return message;
    }

    //人狼行動処理
    public Message werewolfAction(UUID userUUID,UUID targetUserUUID,int priority){
        List<UUID> werewolfList = new ArrayList<UUID>();
        Message message = new Message();
        message.text = "襲撃を試みた";

        if(playersStatus.getWerewolfPlayerUUIDs().contains(targetUserUUID)){
            message.text = "エラー:人狼を襲撃できない";
            return message;
        }
        if(targetUserUUID == null){
            targetUserUUID = playersStatus.getRandomUser(playersStatus.getDeadPlayerUUIDs());
            priority = 1;
        }
        message.userUUID = targetUserUUID;
        if(priority <= 1){
            priority = 1;
        }else if(priority >= 5){
            priority = 5;
        }
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

        if(targetUserUUID == null){
            List<UUID> userUUID_ = new ArrayList<UUID>();
            userUUID_.add(userUUID);
            targetUserUUID = playersStatus.getRandomUser(userUUID_);
        }
        message.userUUID = targetUserUUID;
        message.text = playersStatus.getRole(targetUserUUID) + "を奪った";
        playersStatus.changeRole(userUUID,playersStatus.getRole(targetUserUUID));
        playersStatus.changeRole(targetUserUUID,"phantomThief");
        if(playersStatus.getRole(targetUserUUID).equals("werewolf") || playersStatus.getRole(targetUserUUID).equals("traitor") ){
            tonightVictim.add(targetUserUUID);
        }
        return message;
    }

    //犠牲者処理
    public List<UUID> finishNightAction(){
        UUID maxValueUUID = null;
        Integer maxValue = 0;
        Integer i = 0;
        List<UUID> result;

        for (Map.Entry<UUID, Integer> temp : killNominee.entrySet()) {
            if(temp.getValue() > maxValue){
                maxValueUUID = temp.getKey();
                maxValue = temp.getValue();
            }
        }
        if(!guardTarget.containsValue(maxValueUUID)){
            tonightVictim.add(maxValueUUID);
        }
        for(i = 0;i < tonightVictim.size();i++){
            playersStatus.kill(tonightVictim.get(i));
        }
        lastNightGuardTarget = guardTarget;
        guardTarget = new HashMap<UUID,UUID>();
        killNominee = new HashMap<UUID,Integer>();
        result = tonightVictim;
        tonightVictim = new ArrayList<UUID>();

        return result;
    }
}
