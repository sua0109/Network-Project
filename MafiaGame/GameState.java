package MafiaGame;

abstract class GameState {
	String state; //"낮" & "밤" & "투표"
	GameState() {
		notifyCreation();
	}
	public String toString() {
		return state;
	}
	abstract void nextState();
	abstract String resultAbility(String nickname, String message);
	abstract void readMessage(ChatMsg msg);
	abstract void notifyCreation();
}
