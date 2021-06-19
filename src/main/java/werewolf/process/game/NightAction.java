import java.util.UUID;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class NightAction{
    PlayerStatus playersStatus;
    List<UUID> tonightVictim;
    Map<UUID,Integer> killNominee;
    Map<UUID,UUID> guardTarget;
    Map<UUID,UUID> lastNightGuardTarget;
    boolean continuousGuard;

    public NightAction(PlayerStatus playersStatus,boolean continuousGuard){ //コンストラクタ
        this.playersStatus = playersStatus;
        this.continuousGuard = continuousGuard;
        tonightVictim = new ArrayList<UUID>();
        killNominee = new HashMap<UUID,Integer>();
        guardTarget = new HashMap<UUID,UUID>();
        lastNightGuardTarget  = new HashMap<UUID,UUID>(); 
    }

    public Message seerAction(UUID userUUID,UUID targetUUID){ //占い処理
        List<UUID> seerList = new ArrayList<UUID>();
        Message message = new Message();
        if(targetUUID == null){ //ランダム占い
            seerList.add(userUUID);
            seerList.add(playersStatus.getDeadPlayerUUIDs());
            targetUUID = playersStatus.getRandomUser(seerList);
        }
        message.userUUID = targetUUID;
        if(playersStatus.role.equals("werewolf") || playersStatus.role.equals("blackKnights")){ //人狼発見
            message.text = "人狼だった";
        }else if(playersStatus.role.equals("foxSpirit")){ //妖狐発見
            tonightVictim.add(targetUUID);
            message.text = "人狼でなかった";
        }else{ //その他
            message.text = "人狼でなかった";
        }
        return message;
    }

    public Message necromancerAction(UUID userUUID,UUID targetUUID){ //霊媒処理
        Message message = new Message();
        message.userUUID = targetUUID;
        if(playerStatus.role.equals("werewolf")){ //人狼だった
            message.text = "人狼だった";
        }else{ //その他
            message.text = "人狼でなかった";
        }
        return message;
    }

    public Message guardAction(UUID userUUID,UUID targetUserUUID){ //騎士処理
        List<UUID> guardList = new ArrayList<UUID>();
        Messaage message = new Message();
        message.text = "護衛した";
        if(targetUserUUID == null && !continuousGuard){
            guardList.add(lastNightGuardTarget.getKey());
            guardList.add(playersStatus.getDeadPlayerUUIDs());
            targetUserUUID = playersStatus.getRandomUser(guardList);
        }

        if(targetUserUUID == null && continuousGuard){
            targetUserUUID = playersStatus.getRandomUser(playersStatus.getDeadPlayerUUIDs());
        }
        message.userUUID = targetUserUUID;
        if(!continuousGuard  && lastNightGuardTarget.get(userUUID) == targetUserUUID){ //連ガできない
            message.text ="連続ガードはできません";
            return message;
        }
        guardTarget.put(targetUserUUID,userUUID); 

        return message;
    }

    public Message werewolfAction(UUID userUUID,UUID targetUserUUID,int priority){ //人狼処理
        List<UUID> werewolfList = new ArrayList<UUID>();
        Message message = new Message();
        message.text = "襲撃を試みた";
        
        if(playersStatus.getWerewolfPlayerUUIDs.contains(targetUserUUID)){ //人狼を襲えない
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
        if(killNominee.containsKey(targetUserUUID)){ //噛み投票
            killNominee.put(targetUserUUID,killNominee.get(targetUserUUID) + priority);
        }   else    {    
            killNominee.put(targetUserUUID,priority);
        }
        return message;
    }

    public Message phantomThiefAction(UUID userUUID,UUID targetUserUUID){ //怪盗処理
        Messsage message = new Message();

        if(targetUserUUID == null){
            targetUserUUID = playersStatus.getRandomUser(userUUID);
        }
        message.userUUID = targetUserUUID;
        message.text = playersStatus.getRole(targetUserUUID) + "奪った";
        changeRole(userUUID,playersStatus.getRole(targetUserUUID));
        changeRole(targetUserUUID,"phantomThief");//書き換え
        if(playersStatus.role.equals("werewolf") || playersStatus.role.equals("traitor") ){   //  人狼か背信者の場合
            tonightVictiom.add(targetUserUUID);
        }
        return message;
    }

    public List<UUID> finishNightAction(){ 
        UUID maxValueUUID;
        UUID maxKey = null;
        Integer maxValue = 0;
        Integer i = 0;
        List<UUID> result = new ArrayList<UUID>();

        for (Map.Entry<UUID, Integer> temp : killNominee.entrySet()) {  //valueが最多のプレイヤーのUUIDを取得
            if(temp.getValue() > maxValue){
                maxKey = temp.getKey();
                maxValue = temp.getValue();
            }
        }
        maxValueUUID = maxKey;
        if(!guardTarget.containsValue(maxValueUUID)){   //守られてなければ死ぬ
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