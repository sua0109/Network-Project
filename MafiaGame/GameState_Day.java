package MafiaGame;

public class GameState_Day extends GameState {

	String state = "낮";
	GameState_Day(Mafia gameInstance) {
		super(gameInstance);
	}
	
	String resultAbility(String nickname, String message) {
		return "때가 아닙니다.";
	}

}
