
class NightAction{
    playerStatus playersStatus;
    List<UUID> tonightVictim;
    Map<UUID,Integer> killNominee;
    Map<UUID,UUID> guardTarget;
    Map<UUID,UUID> lastNightGuardTarget;
    bool continuousGuard;

    public NightAction(PlayerStatus playersStatus,bool continuousGuard){ //コンストラクタ
        this.playersStatus = playersStatus;
        this.continuousGuard = continuousGuard;
        tonightVictim = new ArrayList<UUID>();
        killNominee = new HashMap<UUID,Integer>();
        guardTarget = new HashMap<UUID,UUID>();
        lastNightGuardTarget  = new HashMap<UUID,UUID>(); 
    }

    public Message seerAction(UUID userUUID,UUID targetUUID){ //占い処理
        Message message = new Message();
        userUUID = targetUUID;

        if(playersStatus.role.equals("werewolves") || playersStatus.role.equals("blackKnights")){ //人狼発見
            message.text = "人狼だった";
        }else if(playersStatus.role.equals("foxSpirits")){ //妖狐発見
            tonightVictim.add(targetUUID);
            message.text = "人狼でなかった";
        }else{ //その他
            message.text = "人狼でなかった";
        }
        if(targetUUID == null){ //ランダム占い
            playersStatus.getRandomUser(playersStatus.getDeadPlayerUUIDs());
        }
        return message;
    }

    public Message necromancerAction(UUID userUUID,UUID targetUUID){ //霊媒処理
        Message message = new Message();
        userUUID = targetUUID;
        if(playerStatus.role.equals("werewolves")){ //人狼だった
            message.text = "人狼だった";
        }else{ //その他
            message.text = "人狼でなかった";
        }
        return message;
    }

    public Message guardAction(UUID userUUID,UUID targetUserUUID){ //騎士処理
        Messaage message = new Message();
        userUUID = targetUserUUID;
        message.text = "護衛した";
        if(!continuousGuard  && lastNightGuardTarget.get(userUUID) == targetUserUUID){ //連ガできない
            message.text ="連続ガードはできません";
            return message;
        }

        if(targetUserUUID == null && !continuousGuard){
            playersStatus.getRandomUser(playersStatus.getDeadPlayerUUIDs());
        }

        if(targetUserUUID == null && continuousGuard){
            playersStatus.getRandomUser(playersStatus.getDeadPlayerUUIDs());
        }
        guardTarget.put(targetUserUUID,userUUID); 

        return message;
    }

    public Message werewolfAction(UUID userUUID,UUID targetUserUUID,int priority){ //人狼処理
        Message message = new Message();
        userUUID = targetUserUUID;
        message.text = "襲撃を試みた";

        if(playersStatus.role.equals("werewolves")){ //人狼を襲えない
            message.text = "エラー:人狼を襲撃できない";
        }
        if(priority <= 1){
            priority = 1;
        }else if(priority >= 5){
            priority = 5;
        }
        killNominee.put(targetUserUUID,priority);
        if(){ //killNomineeのkeyにすでに噛み対象のプレイヤーがいるときはvalueの値にpriorityの値を足す

        }

        if(targetUserUUID == null){
            playersStatus.getRandomUser(playersStatus.getDeadPlayerUUIDs());
            priority = 1;
            killNominee.put(targetUserUUID,priority);
        }
        return message;
    }

    public Message phantomThiefAction(UUID userUUID,UUID targetUserUUID){ //怪盗処理
        Messsage message = new Message();
        userUUID = targetUserUUID;
        message.text = playersStatus.role + "奪った";

        playersStaus.role = //書き換え
        if(playersStatus.role.equals("werewolves") || playersStatus.role.equals("traitors") ){   //  人狼か背信者の場合
            tonightVictiom.add(targetUserUUID);
        }
        if(targetUserUUID == null){
            playersStatus.getRandomUser(userUUID);
        }
        return message;
    }

    public List<UUID> finishNightAction(){ //未完
    }
}