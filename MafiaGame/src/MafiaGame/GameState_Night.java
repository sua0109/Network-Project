package MafiaGame;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class GameState_Night extends GameState {
	String state = "밤";
	private Timer timer=new Timer();
	int time = 60;
	
	HashMap<String, String> atackList = new HashMap<>();
	
	GameState_Night() {
		super();
		Mafia_Integrated.broadcastingSystem(time+"초 남았습니다.");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
            	if(time==30)
            		Mafia_Integrated.broadcastingSystem(time+"초 남았습니다.");
            	else if(time==10)
            		Mafia_Integrated.broadcastingSystem(time+"초 남았습니다.");
            	else if(time==0)
            		nextState();
            	time--;
            }
        }, 0, 1000); // 0초부터 시작하여 1초 간격으로 실행
		
	}

	String resultAbility(String nickname, String message) {
		String returnMessage = Mafia.roles.get(nickname).ability(message);
		return returnMessage;
	}

	void readMessage(ChatMsg msg) {
		Mafia_Integrated.players.get(msg.nickname).sendSystemMessageToClient("주무실 시간입니다.");
	}

	
	@Override
	void notifyCreation() {
		Mafia.broadcastingToAlive(new ChatMsg(ChatMsg.CODE_NIGHT));
		Mafia_Integrated.broadcastingSystem("밤이 되었습니다.");
		Mafia_Integrated.broadcastingMafia("\n****************** 마피아 전용채팅 ******************\n");
	}

	@Override
	void nextState() {
		Mafia.state = new GameState_Day();
	}
}
