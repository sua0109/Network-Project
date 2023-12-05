package MafiaGame;

import static MafiaGame.Mafia_Integrated.healUser;

import MafiaGame.Mafia_Integrated.ClientHandler;

public class Role_Doctor extends Role {
	String role = "의사";
	boolean alive;
	String Healed;

	public Role_Doctor(ClientHandler player) {
		super(player);
	}

	@Override
	public String ability(String nickname) {
		healUser.add(nickname);
		return "[" + nickname + "]" + "님을 치료합니다.";
	}

	@Override
	public void notifyCreation() {
		String notify=
				"\n\n***************************************************\n\n"+
				"당신은 의사입니다.\n\n"
				+ "마피아의 공격으로부터 시민을 보호하세요.\n\n"+
				"***************************************************\n";
		player.sendSystemMessageToClient(notify);

	}
	public String toString() {
		return role;
	}

}
