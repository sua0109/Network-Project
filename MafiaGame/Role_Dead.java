package MafiaGame;

import MafiaGame.Mafia_Integrated.ClientHandler;

public class Role_Dead extends Role{
	String role = "사망";

	public Role_Dead(ClientHandler player) {
		super(player);
	}

	@Override
	public String ability(String nickname) {
		return null;
	}

	@Override
	public void notifyCreation() {
		String notify="당신은 죽었습니다.\n";
		player.sendSystemMessageToClient(notify);
	}

}
