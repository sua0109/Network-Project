package MafiaGame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import MafiaGame.Mafia_Integrated.ClientHandler;

public class Mafia {
	HashMap<String, ClientHandler> players = new HashMap<>();
	HashMap<String, Role> roles;
	GameState state;
	//GameSetting setting;
	/*
	 * 밤이 되면 State time 필드에 밤 객체를 생성 
	 * 밤 객체가 생성되면 밤에 펼치는 능력들을 특정 객체 형태로 저장.
	 * 예를 들어 마피아 팀이 player1을 지목, 의사가 player1을 지목.
	 * 그럼 밤의 int mafiaIndicate = 'playerIndex'; 등을 통해 정보 변경
	 * 타이머가 끝나면 밤의 정보를 토대로 마피아 게임의 정보를 변경하며 브로드 캐스팅
	 * 다시 State time 필드에 낮 객체를 할당. 밤 객체는 메모리에서 삭제.
	 */
	public Mafia(HashMap<String, ClientHandler> players) {
		this.players=players;
		broadcasting("마피아 게임이 시작되었습니다.");
	}
	void readMsg(ChatMsg msg) {
		if(msg.mode==ChatMsg.MODE_MESSAGE) {
			broadcasting(msg);
		}
		else if(msg.mode==ChatMsg.MODE_COMMAND) {
			String returnMessage = state.resultAbility(msg.nickname ,msg.message);
			players.get(msg.nickname).sendMessageToClient(returnMessage);
		}
		else if(msg.mode==ChatMsg.MODE_SYSTEM) {
			broadcasting(msg.message);
		}
	}
	void broadcasting(ChatMsg msg) { // 서버 모드 -> 서버에 접속된 모든 클라이언트에게 메세지 객체 보내기
		for (Entry<String, ClientHandler> c : players.entrySet()) {
			c.getValue().sendToClient(msg);
		}
	}
	void broadcasting(String msg) { // 서버 모드 -> 서버에 접속된 모든 클라이언트에게 일반 메세지 보내기
		for (Entry<String, ClientHandler> c : players.entrySet()) {
			c.getValue().sendMessageToClient(msg);
		}
	}
}
