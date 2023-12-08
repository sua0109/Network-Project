package MafiaGame;

import static MafiaGame.Mafia.roles;
import static MafiaGame.Mafia_Integrated.*;

import java.util.Scanner;

import MafiaGame.Mafia_Integrated.ClientHandler;

public class Role_Mafia extends Role {
	String role = "마피아";
	boolean alive;
	String target;

	public Role_Mafia(ClientHandler player) {
		super(player);
		mafiaPlayers.add(player);
	}
	
	@Override
	public String ability(String nickname) {
		if (roles.get(nickname) == null) {
			return "존재하지 않는 유저입니다.";
		}
		if (roles.get(nickname) instanceof Role_Dead) {
			return "사망자는 공격할 수 없습니다.";
		}
		if (!killUSer.isEmpty()&&!killUSer.contains(nickname)) {
			killUSer.add(String.valueOf(Integer.MAX_VALUE));
			return "의견이 일치하지 않아 공격을 하지 않았습니다.";
		}
		killUSer.add(nickname);
		return "[" + nickname + "]" + "님을 공격했습니다.";
	}
	
	@Override
	public void notifyCreation() {
		String notify=
				"\n\n***************************************************\n\n"+
				"당신은 마피아입니다.\n\n"
				+ "시민의 의심을 벗어나 조직을 승리로 이끄세요.\n\n"+
				"***************************************************\n";
		player.sendSystemMessageToClient(notify);
	}
	public String toString() {
		return role;
	}
}
