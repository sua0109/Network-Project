package MafiaGame;

import java.util.TimerTask;
import java.util.Timer;

public class GameState_Day extends GameState {
	private Timer timer=new Timer();
	int time = 60;
	String state = "낮";
	GameState_Day() {
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
		return "지금은 때가 아닙니다.";
	}

	void readMessage(ChatMsg msg) {
		Mafia_Integrated.broadcasting(msg);
	}

	@Override
	void notifyCreation() {
		Mafia_Integrated.broadcastingSystem("낮이 되었습니다.");
	}

	@Override
	void nextState() {
		Mafia.state = new GameState_Voting();
	}
}
