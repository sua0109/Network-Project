package MafiaGame;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;

public class Mafia_Integrated extends JFrame {
	//GUI 관련 필드
	private JTextField t_input, t_userID, t_hostAddr, t_portNum, t_role;
	private JTextArea t_players;
	JTextPane t_display;
	private JButton b_join, b_exit, b_create, b_send, b_start ,b_close;
	private DefaultStyledDocument document;

	//서버 관련 필드
	private ServerSocket serverSocket = null;
	private Thread acceptThread = null;
	static HashMap<String, ClientHandler> players = new HashMap<>();
	
	//서버-클라이언트 공통 필드
	private String serverAddress;
	private int serverPort;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String nickname;
	
	//클라이언트 관련 필드
	private Thread receiveThread = null;
	private Socket socket;
	private boolean isHost;
	
	//마피아 게임 객체
	private static Mafia mafia= null; //서버에서만 생성
	static void endGame() {
		mafia = null;
		broadcasting(new ChatMsg(ChatMsg.MODE_END, "게임이 종료되었습니다."));
	}
	
	public Mafia_Integrated(String serverAddress, int serverPort) { // 초기 서버 주소와 포트 지정받으며 클라이언트 GUI 생성
		super("Mafia Game");
		
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		
		buildGUI();
		
		setSize(900, 600);
		setLocation(400,100);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setVisible(true);
	}
	private void buildGUI() { // GUI 생성
		add(createInfoPanel(),BorderLayout.NORTH);
		add(createDisplayPanel(),BorderLayout.CENTER);
		add(createPlayerPanel(), BorderLayout.EAST);
		add(createInputPanel(), BorderLayout.SOUTH);
	}
	private JPanel createDisplayPanel() { // 대화창
		JPanel p = new JPanel(new BorderLayout());
		
		document = new DefaultStyledDocument();
		t_display = new JTextPane(document);
		
		t_display.setEditable(false);
		
		p.add(new JLabel("대화창"),BorderLayout.NORTH);
		p.add(new JScrollPane(t_display), BorderLayout.CENTER);
		
		return p;
	}
	private JPanel createPlayerPanel() { // 플레이어 목록 & 역할 필드
		JPanel p = new JPanel(new BorderLayout());
		JPanel p2 = new JPanel(new BorderLayout());
		
		t_players = new JTextArea();
		t_players.setEditable(false);
		t_role = new JTextField(15);
		t_role.setEditable(false);
		p.add(new JLabel("플레이어 목록"),BorderLayout.NORTH);
		p.add(new JScrollPane(t_players), BorderLayout.CENTER);
		p.add(p2, BorderLayout.SOUTH);
		p2.add(t_role, BorderLayout.CENTER);
		p2.add(new JLabel("  역할  "),BorderLayout.WEST);
		
		return p;
	}
	private JPanel createInfoPanel() { // 닉네임 + ip + port 입력 패널 & 서버 생성 + 입장 + 나가기 버튼 
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		t_userID = new JTextField(7);
		t_hostAddr = new JTextField(12);
		t_portNum = new JTextField(5);
		b_join = new JButton("게임 입장");
		b_join.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					serverAddress = t_hostAddr.getText();
					serverPort = Integer.parseInt(t_portNum.getText());
					
					try {
						connectToServer();
						sendUserID();
					}catch(UnknownHostException e1) {
						printDisplay("서버 주소와 포트 번호를 확인하세요: "+e1.getMessage());
						return;
					}catch(IOException e1) {
						printDisplay("서버와의 연결 오류: "+e1.getMessage());
						return;
					}
					
					setEnableExit();
					setEnableInputPanel();
					
				}
		});
		b_create = new JButton("게임 생성");
		b_create.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				serverAddress = t_hostAddr.getText();
				serverPort = Integer.parseInt(t_portNum.getText());
			acceptThread = new Thread(new Runnable() {
				public void run() {
					startServer();
				}
			});
			acceptThread.start();
			isHost=true;
			try {
				connectToServer();
				sendUserID();
			}catch(UnknownHostException e1) {
				printDisplay("서버 주소와 포트 번호를 확인하세요: "+e1.getMessage());
				return;
			}catch(IOException e1) {
				printDisplay("서버와의 연결 오류: "+e1.getMessage());
				return;
			}
			
			setEnableExit();
//			t_userID.setEditable(false);
//			t_hostAddr.setEditable(false);
//			t_portNum.setEditable(false);
//			b_join.setEnabled(false);
//			b_create.setEnabled(false);
//			b_start.setEnabled(true);
			
			setEnableInputPanel();
//			t_input.setEnabled(true);
//			b_send.setEnabled(true);
			
			}
		});
		b_exit = new JButton("게임 나가기");
		b_exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			disconnect();
			printDisplay("서버와 연결이 종료되었습니다.\n");
			setEnableJoin();
//			b_join.setEnabled(true);
//			b_create.setEnabled(true);
//			b_exit.setEnabled(false);
//			b_close.setEnabled(true);
//			t_userID.setEditable(true);
//			t_hostAddr.setEditable(true);
//			t_portNum.setEditable(true);
			
			setUnableInputPanel();
//			t_input.setEnabled(false);
//			b_send.setEnabled(false);
			}
		});
		t_userID.setText("guest" + getLocalAddr().split("\\.")[3]);
		t_hostAddr.setText(this.serverAddress);
		t_portNum.setText(String.valueOf(this.serverPort));
		t_portNum.setHorizontalAlignment(JTextField.CENTER);
		
		p.add(new JLabel("닉네임 "));
		p.add(t_userID);
		
		p.add(new JLabel("서버주소 "));
		p.add(t_hostAddr);
		
		p.add(new JLabel("포트 "));
		p.add(t_portNum);
		
		p.add(b_join);
		p.add(b_create);
		p.add(b_exit);
		
		b_exit.setEnabled(false);
		
		return p;
	}
	private JPanel createInputPanel() { // 채팅 입력 & 게임 시작 버튼 & GUI 종료 버튼
		JPanel p = new JPanel(new BorderLayout());
		JPanel p2 = new JPanel(new BorderLayout());
		JPanel p3 = new JPanel(new GridLayout(0,2));
		p.add(p2, BorderLayout.CENTER);
		p.add(p3, BorderLayout.EAST);
		
		t_input = new JTextField();
		t_input.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});
		p2.add(t_input, BorderLayout.CENTER);
		b_send = new JButton("입력");
		b_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});
		p2.add(b_send, BorderLayout.EAST);
		
		b_start = new JButton("시작");
		b_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mafia = new Mafia();
				broadcastingSystem("게임이 시작되었습니다.");
				b_start.setEnabled(false);
			}
		});
		
		b_close = new JButton("종료");
		b_close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			System.exit(0);
			}
		});
		
		b_start.setEnabled(false);
		t_input.setEnabled(false);
		b_send.setEnabled(false);
		p3.add(b_start);
		p3.add(b_close);
		
		return p;
	}
	private void printDisplay(String msg) { // 대화창에 메세지 생성
		int len = t_display.getDocument().getLength();
		try {
			document.insertString(len, msg+"\n", null);
		} catch(BadLocationException e) {
			e.printStackTrace();
		}
		
		t_display.setCaretPosition(len);
	}
	private String getLocalAddr() { // ip 읽어오기
		InetAddress local = null;
		String addr = "";
		try {
			local = InetAddress.getLocalHost();
			addr = local.getHostAddress();
			System.out.println(addr);
		} catch(UnknownHostException e) {
			e.printStackTrace();
		}
		return addr;
	}
	private void connectToServer() throws UnknownHostException, IOException{ // 클라이언트 모드로 접속
		socket = new Socket() ;
		SocketAddress sa = new InetSocketAddress(serverAddress, serverPort);
		socket.connect(sa, 3000);
		
		out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		
		printDisplay("서버와 연결되었습니다.");
		
		receiveThread = new Thread(new Runnable() {
			public void receiveMessages() {
			    try {
			    	ChatMsg inMsg = (ChatMsg)in.readObject();
			        if(inMsg == null) {
			        	disconnect();
			        	printDisplay("서버 연결 끊김");
			        	setEnableJoin();
				    	setUnableInputPanel();

			        	return;
			        }
			        switch(inMsg.mode) {
			        case ChatMsg.MODE_MESSAGE:
			        	printDisplay(inMsg.nickname + ": "+inMsg.message);
			        	break;
			        	
			        case ChatMsg.MODE_COMMAND:
			        	printDisplay(inMsg.nickname + ": "+inMsg.message);
			        	break;
			        case ChatMsg.MODE_SYSTEM:
			        	printDisplay(inMsg.message);
			        	break;
			        case ChatMsg.MODE_START:
			        	setUnableExit();
			        case ChatMsg.MODE_END:
			        	setEnableExit();
			        	setEnableInputPanel();
			        case ChatMsg.MODE_DEATH:
			        	setUnableInputPanel();
			        	setEnableExit();
			        }
			        
			    } catch (IOException e) {
			    	//서버에서 나갔을 때
			    	printDisplay("연결을 종료했습니다.");
			    	setEnableJoin();
			    	setUnableInputPanel();
					
					receiveThread.interrupt();
					receiveThread=null;
			    }catch(ClassNotFoundException e) {
			    	printDisplay("잘못된 객체가 전달되었습니다.");
			    }
			}
			@Override
			public void run() {
			    try {
			    	in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
			    } catch (IOException e) {
			    	e.printStackTrace();
			        printDisplay("입력 스트림이 열리지 않음");
			        return; // 스트림 열기 실패 시 스레드 종료
			    }

				while(receiveThread == Thread.currentThread()) {
					receiveMessages();
				}
			}
		});
		receiveThread.start();
	}
	private void disconnect() { // 클라이언트 모드 -> 서버 연결 해제
		send(new ChatMsg(nickname, ChatMsg.MODE_EXIT));
		try {
			receiveThread.interrupt();
			receiveThread = null;
			socket.close();
		} catch (IOException e) {
			System.err.println("접속 끊기 오류 > "+e.getMessage());
			System.exit(-1);
		}
	}
	
	// ******************** System Control ******************************
	
	private void setUnableInputPanel() { // 입력창 & 입력 버튼 얼리기
		//게임에서 죽었을 때
		t_input.setEnabled(false);
		b_send.setEnabled(false);
	}
	private void setEnableInputPanel() { // 입력창 & 입력 버튼 녹이기
		//게임이 끝났을 때
		t_input.setEnabled(true);
		b_send.setEnabled(true);
	}
	private void setEnableExit() { // 서버 나가기 가능
		// 게임이 끝났을 때
		// 게임에서 죽었을 때
		b_join.setEnabled(false);
		b_create.setEnabled(false);
		b_exit.setEnabled(!isHost); // 호스트가 아닐 때만 서버 나가기 가능
		b_close.setEnabled(false);
		b_start.setEnabled(isHost);
		
		t_userID.setEnabled(false);
		t_hostAddr.setEnabled(false);
		t_portNum.setEnabled(false);
	}
	private void setUnableExit() { // 서버 나가기 불가
		//서버 나가기 불가 -> 게임중
		b_join.setEnabled(false);
		b_create.setEnabled(false);
		b_exit.setEnabled(false);
		b_close.setEnabled(false);
		b_start.setEnabled(false);
		
		t_userID.setEnabled(false);
		t_hostAddr.setEnabled(false);
		t_portNum.setEnabled(false);
	}
	private void setEnableJoin() { // 서버 입장/생성 가능
		//서버에서 나갔을 때
		b_join.setEnabled(true);
		b_create.setEnabled(true);
		b_exit.setEnabled(false);
		b_close.setEnabled(true);
		b_start.setEnabled(false);
		
		t_userID.setEnabled(true);
		t_hostAddr.setEnabled(true);
		t_portNum.setEnabled(true);
	}
	
	// ******************************************************************
	
	void send(ChatMsg msg) { // 클라이언트 모드 -> 서버에 메세지 객체 보내기
		try {
			out.writeObject(msg);
			out.flush();
		}catch(IOException e) {
			System.err.println("클라이언트 일반 전송 오류 > "+ e.getMessage());
		}
	}
	void sendMessage(){ // 클라이언트 모드 -> 서버에 일반 메세지 보내기
		String message = t_input.getText();
		if(message.isEmpty()) return;
	
		send(new ChatMsg(nickname, ChatMsg.MODE_MESSAGE, message));
		
		t_input.setText("");
	}
	void sendUserID() { // 클라이언트 모드 -> 서버에 입장 메세지 보내기 (닉네임 생성)
		nickname = t_userID.getText();
		send(new ChatMsg(nickname, ChatMsg.MODE_ENTRANCE));
	}
	static void broadcasting(ChatMsg msg) { // 서버 모드 -> 서버에 접속된 모든 클라이언트에게 메세지 객체 보내기
		for (Entry<String, ClientHandler> c : players.entrySet()) {
			c.getValue().sendToClient(msg);
		}
	}
	static void broadcasting(String msg) { // 서버 모드 -> 서버에 접속된 모든 클라이언트에게 일반 메세지 보내기
		for (Entry<String, ClientHandler> c : players.entrySet()) {
			c.getValue().sendMessageToClient(msg);
		}
	}
	static void broadcastingSystem(String msg) { // 서버 모드 -> 서버에 접속된 모든 클라이언트에게 일반 메세지 보내기
		for (Entry<String, ClientHandler> c : players.entrySet()) {
			c.getValue().sendSystemMessageToClient(msg);
		}
	}
	private void startServer() { // 서버 모드 -> 서버 생성 (서버 소켓 생성 -> 클라이언트 접속시 클라이언트 핸들러 생성)
		Socket clientSocket = null;
		try {
			serverSocket = new ServerSocket(serverPort);
			
			printDisplay("서버가 시작되었습니다: "+ getLocalAddr());
			while(true) {
				System.out.println("클라이언트 접속 대기..");
				clientSocket = serverSocket.accept();
				
				String cAddr = clientSocket.getInetAddress().getHostAddress();
				printDisplay("클라이언트가 연결되었습니다: "+ cAddr+"\n");
				
				ClientHandler cHandler = new ClientHandler(clientSocket);
				cHandler.start();
				System.out.println("클라이언트 접속");
			}
			} catch (SocketException e) {
				printDisplay("서버 소켓 종료");
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				try {
					if(clientSocket != null) clientSocket.close();
					if(serverSocket != null) serverSocket.close();
				}catch(IOException e) {
					System.err.println("서버 닫기 오류 > "+e.getMessage());
					System.exit(-1);
				}
		}
	}
	public class ClientHandler extends Thread { // 서버 모드 -> 클라이언트별 통신 쓰레드 -> 클라이언트별 메세지 객체 읽기/보내기
		private Socket clientSocket;
		private ObjectOutputStream out;
		private ObjectInputStream in;
		private String nickname;
		private boolean isHost;
		
		public ClientHandler(Socket clientSocket){
			this.clientSocket = clientSocket;
			if(players.size()==0) {
				this.isHost=true;
				printDisplay("당신은 호스트입니다.");
			}
			try {
                out = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
                in = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
                printDisplay("스트림 초기화 오류");
            }
		}
		private void addPlayer() {
			players.put(nickname, this);
//			if(mafia!=null)
//				mafia.players.put(nickname, this);
		}
		private void removePlayer() {
			players.remove(nickname);
//			if(mafia!=null)
//				mafia.players.remove(nickname);
		}
		private void receiveMessages(Socket cs) {
			try {
				ChatMsg msg;
				while((msg=(ChatMsg)in.readObject()) != null) {
					if(msg.mode == ChatMsg.MODE_ENTRANCE) {
						
						if(players.containsKey(msg.nickname)) {
							send(new ChatMsg(nickname, ChatMsg.MODE_SYSTEM, "닉네임이 중복되었습니다."));
							if(!players.containsKey(msg.nickname))
								cs.close();
							return;
						}
						nickname = msg.nickname;
						addPlayer();
						broadcasting(new ChatMsg(nickname, ChatMsg.MODE_SYSTEM ,"새 참가자 : "+nickname));
						broadcasting(new ChatMsg(nickname, ChatMsg.MODE_SYSTEM, "현재 참가자 수 : "+ players.size()));
						continue;
					}
					else if (msg.mode == ChatMsg.MODE_EXIT) {
						break;
					}
					if(mafia!=null) {
						mafia.readMsg(msg);
						continue;
					}
					if (msg.mode == ChatMsg.MODE_MESSAGE) {
						broadcasting(msg);
					}
					else if (msg.mode == ChatMsg.MODE_COMMAND) {
						broadcasting(msg);
					}
//					else if (msg.mode == ChatMsg.MODE_SYSTEM) {
//						broadcasting(msg.message);
//					}
				}
				removePlayer();
				//updatePlayersList();
				printDisplay(nickname + " 퇴장. 현재 참가자 수: "+players.size());
			} catch (IOException e) {
				removePlayer();
				//updatePlayersList();
				printDisplay(nickname + " 연결 끊김. 현재 참가자 수: " +players.size());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					cs.close();
				}catch (IOException e) {
					System.err.println("서버 닫기 오류 > "+e.getMessage());
					System.exit(-1);
				}
			}
		}
		
		void sendToClient(ChatMsg msg) {
			try {
				out.writeObject(msg);
				out.flush();
			}catch(IOException e) {
				System.err.println("클라이언트 일반 전송 오류 > "+e.getMessage());
			}
		}
		
		void sendMessageToClient(String msg) {
			sendToClient(new ChatMsg(nickname, ChatMsg.MODE_MESSAGE,msg));
		}
		void sendSystemMessageToClient(String msg) {
			sendToClient(new ChatMsg(ChatMsg.MODE_SYSTEM, msg));
		}
		@Override
		public void run() {
				receiveMessages(clientSocket);
		}
	}
	public static void main(String[] args) { // serverAddress = "localhost", serverPort = 54321
		
		String serverAddress = "localhost";
		int serverPort = 54321;
		
		new Mafia_Integrated(serverAddress, serverPort);
		new Mafia_Integrated(serverAddress, serverPort);
	}
}
