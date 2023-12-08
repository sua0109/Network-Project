package MafiaGame;

import java.util.TimerTask;

import static MafiaGame.Mafia_Integrated.*;

import java.util.Timer;

public class GameState_Day extends GameState {
	private Timer timer= new Timer();
	int time = 60; //기본은 60
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
            	else if(time==0) {
            		nextState();
            		timer.cancel();
            	}
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
		Mafia.broadcastingToAlive(new ChatMsg(ChatMsg.CODE_DAY)); // GUI 원격 조작 메세지는 생존자에게만 전달
		if (killUSer.isEmpty()) {
			Mafia_Integrated.broadcastingSystem("낮이 되었습니다.");
			return;
		}
		if (killUSer.contains(String.valueOf(Integer.MAX_VALUE))) {
			Mafia_Integrated.broadcastingSystem("낮이 되었습니다.");
			return;
		}
		String killUserNickname =null;
		for (String user : killUSer ) {
			killUserNickname= user;
		}
		if (healUser.contains(killUserNickname)) {
			Mafia_Integrated.broadcastingSystem("[---------- 낮이 되었습니다 ---------]");
			Mafia_Integrated.broadcastingSystem("[------- " + killUserNickname + " 님이 의사에 의해 마피아의 공격에서 살아남았습니다 -------]");
		} else {
			Mafia_Integrated.broadcastingSystem("낮이 되었습니다.");
			Mafia.roles.put(killUserNickname, new Role_Dead(Mafia_Integrated.players.get(killUserNickname)));
		}

		killUSer.clear();
		healUser.clear();
	}

	@Override
	void nextState() {
		Mafia.state = new GameState_Voting();
	}
}
