package MafiaGame;

import java.util.*;
import java.util.Map.Entry;

public class GameState_Voting extends GameState {
	String state="투표";
	
	HashMap<String, String> votingList = new HashMap<>();
	HashMap<String, Integer> vote=new HashMap<>();
	GameState_Voting(Mafia gameInstance) {
		super(gameInstance);
		for(String nickname : game.players.keySet()) {
			vote.put(nickname, 0);	
		}
	}
	void vote(String voter, String nominee){
		votingList.put(voter, nominee);
	}
	public void result() {
		for(String nickname : votingList.keySet()) {
			vote.replace(nickname, vote.get(nickname)+1);	
		}
	}
	String resultAbility(String user, String nominee) {
		vote(user, nominee);
		return "["+nominee+"] 투표";
	}

}
