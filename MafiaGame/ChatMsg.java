package MafiaGame;
//1891090 장은

import java.io.Serializable;

import javax.swing.ImageIcon;

public class ChatMsg implements Serializable {
	public final static int MODE_ENTRANCE = 0x1;
	public final static int MODE_EXIT = 0x2;
	public final static int MODE_MESSAGE = 0x10;
	public final static int MODE_COMMAND = 0x20;
	public final static int MODE_SYSTEM = 0x40;
	String nickname;
	int mode;
	String message;
	
	public ChatMsg(String nickname, int code, String message){
		this.nickname = nickname;
		this.mode=code;
		this.message=message;
	}
	
	public ChatMsg(String nickname, int code) {
		this(nickname, code, "");
	}
}
