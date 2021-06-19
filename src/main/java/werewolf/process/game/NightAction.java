/**
 * ゲームのロジックを持ち、実行する
 *
 * version 1.4
 * last update date 2021/06/19
 * auther al19013
 */



import java.util.*;

public class NightAction{
    PlayersStatus playersStatus;            //プレイヤーの生死状況、役職
    List<UUID> tonightVictim;               //今夜の犠牲者一覧
    Map<UUID,Integer> killNominee;          //人狼の襲撃候補者
    Map<UUID,UUID> guardTarget;             //騎士の守り先
    Map<UUID,UUID> lastNightGuardTarget;    //昨夜の騎士の守り先
    boolean continuousGuard;                //連続ガードの有無

    public NightAction(PlayersStatus playersStatus,boolean continuousGuard){ //コンストラクタ
        this.playersStatus = playersStatus;
        this.continuousGuard = continuousGuard;
        tonightVictim = new ArrayList<UUID>();
        killNominee = new HashMap<UUID,Integer>();
        guardTarget = new HashMap<UUID,UUID>();
        lastNightGuardTarget  = new HashMap<UUID,UUID>(); 
    }

    public Message seerAction(UUID userUUID,UUID targetUUID){ //占い師行動処理
        Message message = new Message();
        if(targetUUID == null){
            List<UUID> seerList = new ArrayList<UUID>();
            seerList = playersStatus.getDeadPlayerUUIDs();
            seerList.add(userUUID);
            targetUUID = playersStatus.getRandomUser(seerList);
        }
        message.userUUID = targetUUID;
        if(playersStatus.role.equals("werewolf") || playersStatus.role.equals("blackKnight")){
            message.text = "人狼だった";
        }else if(playersStatus.role.equals("foxSpirit")){
            tonightVictim.add(targetUUID);
            message.text = "人狼でなかった";
        }else{
            message.text = "人狼でなかった";
        }
        return message;
    }

    public Message necromancerAction(UUID userUUID,UUID targetUUID){ //霊媒師行動処理
        Message message = new Message();
        message.userUUID = targetUUID;
        if(playersStatus.role.equals("werewolf")){
            message.text = "人狼だった";
        }else{ //その他
            message.text = "人狼でなかった";
        }
        return message;
    }

    public Message guardAction(UUID userUUID,UUID targetUserUUID){ //騎士行動処理
        Messaage message = new Message();
        message.text = "護衛した";
        if(targetUserUUID == null && !continuousGuard){
            List<UUID> guardList = new ArrayList<UUID>();
            guardList = playersStatus.getDeadPlayerUUIDs(); 
            guardList.add(lastNightGuardTarget.getKey());
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

    public Message werewolfAction(UUID userUUID,UUID targetUserUUID,int priority){ //人狼行動処理
        List<UUID> werewolfList = new ArrayList<UUID>();
        Message message = new Message();
        message.text = "襲撃を試みた";
        
        if(playersStatus.getWerewolfPlayerUUIDs.contains(targetUserUUID)){
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

    public Message phantomThiefAction(UUID userUUID,UUID targetUserUUID){ //怪盗行動処理
        Messsage message = new Message();

        if(targetUserUUID == null){
            targetUserUUID = playersStatus.getRandomUser(userUUID);
        }
        message.userUUID = targetUserUUID;
        message.text = playersStatus.getRole(targetUserUUID) + "を奪った";
        playersStatus.changeRole(userUUID,playersStatus.getRole(targetUserUUID));
        playersStatus.changeRole(targetUserUUID,"phantomThief");
        if(playersStatus.role.equals("werewolf") || playersStatus.role.equals("traitor") ){
            tonightVictiom.add(targetUserUUID);
        }
        return message;
    }

    public List<UUID> finishNightAction(){  //犠牲者処理
        UUID maxValueUUID = null;
        Integer maxValue = 0;
        Integer i = 0;
        List<UUID> result = new ArrayList<UUID>();

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