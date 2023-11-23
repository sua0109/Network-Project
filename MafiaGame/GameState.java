package MafiaGame;

abstract class GameState {
	Mafia game;
	String state; //"낮" & "밤" & "투표"
	GameState(Mafia gameInstance) {
		this.game = gameInstance;
		//게임 상태에 따른 시스템 메세지 전송
		//예) 투표 시간이 되었습니다. 밤이 되었습니다.
	}
	public String toString() {
		return state;
	}
	abstract String resultAbility(String nickname, String message);
}
