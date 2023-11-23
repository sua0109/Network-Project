package MafiaGame;

import java.util.*;
import java.util.Map.Entry;

public class GameState_Voting extends GameState {
//	Mafia game;
	String state="투표";
	int time = 60; // 기본 60초, 추후 설정을 통해 변경 가능
	private Timer timer=new Timer();
	String Nominee=null;

	
	HashMap<String, String> votingList;
	HashMap<String, Integer> vote;
	HashMap<String, Integer> approvalVote=new HashMap<>();

	GameState_Voting() {
		super();
		startVote();
	}
	void notifyCreation() {
		Mafia_Integrated.broadcastingSystem("투표가 시작되었습니다.");
	}
	void startVote() {
		votingList = new HashMap<>();
		vote=new HashMap<>();
		
		Mafia_Integrated.broadcastingSystem(time+"초 남았습니다.");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
            	if(time==30)
            		Mafia_Integrated.broadcastingSystem(time+"초 남았습니다.");
            	else if(time==10)
            		Mafia_Integrated.broadcastingSystem(time+"초 남았습니다.");
            	else if(time==0) {
                    if(resultVote())
                    	startApprovalVote();
                    else
                    	nextState();
            		this.cancel();
            	}
            	time--;
            }
        }, 0, 1000); // 0초부터 시작하여 1초 간격으로 실행
	}
	public boolean resultVote() {
		int max=0;
		for(String nickname : votingList.keySet()) {
		    int currentVotes = vote.getOrDefault(nickname, 0) + 1;
		    vote.put(nickname, currentVotes);
		    max = Math.max(max, currentVotes);
		}
		
		PriorityQueue<String> pq = new PriorityQueue<>(new Comparator<String>() {

			@Override
			public int compare(String player1, String player2) {
				
				return Integer.compare(vote.get(player1), vote.get(player2));
			}
			
		});
		
		for(String nickname : vote.keySet())
			pq.offer(nickname);
		while(!pq.isEmpty()) {
			String now = pq.poll();
			if(vote.get(now)==max) {
				if(Nominee==null)
					Nominee=now;
				else
					Nominee="None";
			}
			Mafia_Integrated.broadcastingSystem("["+now+"] - "+vote.get(now)+"\n");
		}
		if(Nominee==null||Nominee.equals("None")) {
			Mafia_Integrated.broadcastingSystem("부결되었습니다.");
			return false;
		}
		else {
			Mafia_Integrated.broadcastingSystem("["+Nominee+"]"+" 지목되었습니다.");
			return true;
		}
	}
	String resultAbility(String user, String nominee) {
		if(!Mafia.roles.get(nominee).toString().equals("사망")) {
			votingList.put(user, nominee);
			return "["+nominee+"] 투표";
		}
		else {
			return "잘못된 입력입니다.";
		}
	}
	void startApprovalVote() {
		votingList = new HashMap<>();
		vote=new HashMap<>();
		vote.put("Y", 0);
		vote.put("N", 0);
		String notify = "찬반 투표가 시작되었습니다.\n"
				+ "/Y 또는 /N 을 통해 투표할 수 있습니다.\n"
				+ "사형을 원하시면 /Y , 원치 않으시면 /N 를 입력해주세요.";
		Mafia_Integrated.broadcastingSystem(notify);
		Mafia_Integrated.broadcastingSystem(time/2000+"초 남았습니다.");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
            	Mafia_Integrated.broadcastingSystem(time/6000+"초 남았습니다.");
            }
        }, time/3);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
            	resultApprovalVote();
            }
        }, time/6);
		for(String nickname : votingList.keySet()) {
		    vote.put(nickname, vote.getOrDefault(nickname, 0) + 1);
		}
		nextState();
	}
	private void resultApprovalVote() {
		int approval = vote.get("Y");
		int disapproval= vote.get("N");
		Mafia_Integrated.broadcastingSystem("찬성 : "+approval +
				"\n반대 :" + disapproval);
		if(approval>disapproval) {
			Mafia_Integrated.broadcastingSystem("["+Nominee+"]"+" 사형되었습니다.");
			Mafia.roles.put(Nominee, new Role_Dead(Mafia_Integrated.players.get(Nominee)));
		}
	}
	@Override
	void readMessage(ChatMsg msg) {
		Mafia_Integrated.players.get(msg.nickname).sendToClient(new ChatMsg(ChatMsg.MODE_SYSTEM, "투표 시간엔 대화할 수 없습니다."));
	}
	@Override
	void nextState() {
		Mafia.state=new GameState_Night();
	}

}
