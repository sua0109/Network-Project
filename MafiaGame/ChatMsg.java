package MafiaGame;

import java.io.Serializable;

import javax.swing.ImageIcon;

public class ChatMsg implements Serializable {
	public final static int MODE_ENTRANCE = 0x1;
	public final static int MODE_EXIT = 0x2;
	public final static int MODE_MESSAGE = 0x4; // nickname : 메세지 전달
	public final static int MODE_COMMAND = 0x8; // '/'명령어를 통해 시스템에만 전달
	public final static int MODE_SYSTEM = 0x10; // nickname 없이 전달
	public final static int MODE_MAFIACHAT = 0x20;
	public final static int MODE_DEATH = 0x40;
	public final static int MODE_START = 0x80;
	public final static int MODE_END = 0x100;
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
	public ChatMsg(int code, String message) {
		this("", code, message);
	}
	public ChatMsg(int code) {
		this("", code, "");
	}
}
