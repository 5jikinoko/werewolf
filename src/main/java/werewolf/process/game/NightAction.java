
class NightAction{
    playerStatus playersStatus;
    List<UUID> tonightVictim;
    Map<UUID,Integer> killNominee;
    Map<UUID,UUID> guardTarget;
    Map<UUID,UUID> lastNightGuardTarget;
    bool continuousGuard;

    public NightAction(PlayerStatus playersStatus,bool continuousGuard){
        this.playersStatus = playersStatus;
        this.continuousGuard = continuousGuard;
        tonightVictim = new ArrayList<UUID>();
        killNominee = new HashMap<UUID,Integer>();
        guardTarget = new HashMap<UUID,UUID>();
        lastNightGuardTarget  = new HashMap<UUID,UUID>(); 
    }

    public Message seerAction(UUID userUUID,UUID targetUUID){
        if(targetUUID == null){
            
        }
        Message message = new Message();
        userUUID = targetUUID;

        return message;
    }

    public Message necromancerAction(UUID userUUID,UUID targetUUID){
        Message message = new Message();
        userUUID = targetUUID;

        return message;
    }

    public Message guardAction(UUID userUUID,UUID targetUserUUID){
        Messaage message = new Message();
        userUUID = targetUserUUID;
        if(!continuousGuard  && lastNightGuardTarget.get(userUUID) == targetUserUUID){
            return message.text ="連続ガードはできません";
        }

        if(targetUserUUID == null && !continuousGuard){
            getDeadPlayerUUIDs
            getRandomUser
        }

        if(targetUserUUID == null && continuousGuard){
            getDeadPlayerUUIDs
            getRandomUser
        }
        guardTarget.put(targetUserUUID,userUUID); 

        return message;
    }

    public Message werewolfAction(UUID userUUID,UUID targetUserUUID,int priority){
        Message message = new Message();
        
        if(priority <= 1){
            priority = 1;
        }else if(priority >= 5){
            priority = 5;
        }
        killNominee.put(targetUserUUID,priority);
        return message
    }

    public Message phantomThiefAction(UUID userUUID,UUID targetUserUUID){

        playersStaus.role = //書き換え
        if(){   //  人狼か背信者の場合
            tonightVictiom.add(targetUserUUID);
        }
        if(targetUserUUID == null){ //nullの時

        }
        Messsage message = new Message();
        userUUID = targetUserUUID;
        message.text = + "奪った";
        return message;
    }

    public List<UUID> finishNightAction(){ //未完
    }
}