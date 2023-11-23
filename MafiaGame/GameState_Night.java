package MafiaGame;

import java.util.HashMap;

public class GameState_Night extends GameState {
	String state = "밤";
	
	HashMap<String, String> atackList = new HashMap<>();
	
	GameState_Night(Mafia gameInstance) {
		super(gameInstance);
		
	}

	String resultAbility(String nickname, String message) {
		
		return null;
	}

}
