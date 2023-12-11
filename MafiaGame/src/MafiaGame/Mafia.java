package MafiaGame;
import java.util.HashMap;
import java.util.Map.Entry;

import MafiaGame.Mafia_Integrated.ClientHandler;
import static MafiaGame.Mafia_Integrated.*;

public class Mafia {
   static HashMap<String, Role> roles = new HashMap<>();
   static GameState state;
   String nickname;
   
public Mafia() {
      Mafia_Integrated.broadcastingSystem("마피아 게임이 시작되었습니다.");
      Mafia_Integrated.broadcasting(new ChatMsg(ChatMsg.CODE_START));
      generateRandomRole();
      state=new GameState_Day();
      Mafia_Integrated.controlUpdate();
   }
   void readMsg(ChatMsg msg) {
      if(msg.mode==ChatMsg.MODE_MESSAGE) {
         if(roles.get(msg.nickname).toString().equals("관전자"))
            Mafia_Integrated.players.get(msg.nickname).sendSystemMessageToClient("생존자만 대화할 수 있습니다.");
         else if (roles.get(msg.nickname).toString().equals("마피아") && state instanceof GameState_Night) {
            broadcastingMafia(msg);
         }else if (roles.get(msg.nickname).toString().equals("사망")) {
            broadcastingDeath(msg);
         }else
            state.readMessage(msg);
      }
      else if(msg.mode==ChatMsg.MODE_COMMAND) {
         String returnMessage = state.resultAbility(msg.nickname ,msg.message);
         Mafia_Integrated.players.get(msg.nickname).sendSystemMessageToClient(returnMessage);
      }
   }
   void generateRandomRole() {
      int count=0;
      for(Entry<String, ClientHandler> c : Mafia_Integrated.players.entrySet()) {
         String nickname = c.getKey();
         ClientHandler clientHandler = c.getValue();
             if (count == 0) {
                roles.put(nickname, new Role_Doctor(clientHandler));
                count++;
                continue;
             }
             if (count == 1) {
                roles.put(nickname, new Role_Mafia(clientHandler));
                count++;
                continue;
             }
             if (count == 2) {
                roles.put(nickname, new Role_Civilian(clientHandler));
                count++;
                continue;
             }
             if (count == 3) {
                roles.put(nickname, new Role_Police(clientHandler));
                count++;
                continue;
             }
             if (count == 4) {
                roles.put(nickname, new Role_Civilian(clientHandler));
                count++;
                continue;
             }
             if (count == 5) {
                roles.put(nickname, new Role_Mafia(clientHandler));
                count++;
                continue;
             }
             if (count == 6) {
                roles.put(nickname, new Role_Civilian(clientHandler));
                count++;
                continue;
             }
             if (count == 7) {
                roles.put(nickname, new Role_Civilian(clientHandler));
                count++;
             }

           
      }
         
   }
   static void broadcastingToAlive(ChatMsg msg) { // 마피아 게임 내의 생존자에게만 메세지 전송 -> GUI 조작 객체 전송시 활용
      for (Entry<String, ClientHandler> c : Mafia_Integrated.players.entrySet()) {
         if(roles.get(c.getKey()).toString()!="사망")
            c.getValue().sendToClient(msg);
      }
   }
}
