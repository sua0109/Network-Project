package MafiaGame;

public abstract class Role {
	Mafia game;
	public Role(Mafia gameInstance) {
		game=gameInstance;
	}
	public abstract String ability(String nickname);

}
