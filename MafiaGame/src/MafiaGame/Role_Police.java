package MafiaGame;

import MafiaGame.Mafia_Integrated.ClientHandler;

public class Role_Police extends Role {
	String role = "경찰";
	int abilityCount = 1;

	public Role_Police(ClientHandler player) {
		super(player);
	}

	@Override
	public String ability(String nickname) { // 능력사용 1번으로 제한
		if (abilityCount == 0) {
			return "더 이상 사용할 수 없습니다.";
		}
		Role role = Mafia.roles.get(nickname);
		abilityCount--;
		if (role instanceof Role_Mafia) {
			return"[" + nickname + "]" + "님은 마피아 입니다.";
		} else {
			return"[" + nickname + "]" + "님은 시민 입니다.";
		}

	}

	@Override
	public void notifyCreation() {
		String notify=
				"\n\n***************************************************\n\n"+
				"당신은 경찰입니다.\n\n"
				+ "마피아를 조사해서 진실을 밝히세요.\n\n"+
				"***************************************************\n";
		player.sendSystemMessageToClient(notify);
	}
	public String toString() {
		return role;
	}

}
