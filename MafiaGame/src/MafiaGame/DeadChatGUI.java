package MafiaGame;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import MafiaGame.Mafia_Integrated.ClientHandler;


public class DeadChatGUI extends JFrame{
    private ClientHandler clientHandler;
	private JTextField t_input;
	private JTextArea t_display;
	private JButton b_send;

	public DeadChatGUI(ClientHandler clientHandler) {
		this.clientHandler = clientHandler;
		
		setTitle("Dead Chat");
		setSize(200,200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               
    	add(createDisplayPanel(), BorderLayout.NORTH);
    	add(createInputPanel(), BorderLayout.SOUTH);
     }
	
	private JPanel createDisplayPanel() {
		 JPanel p = new JPanel(new BorderLayout());
		 
		 t_display = new JTextArea();
		 t_display.setEnabled(false);

	    p.add(t_display, BorderLayout.CENTER);
		return p;
	}
	
	private JPanel createInputPanel(){
		JPanel p = new JPanel(new BorderLayout());
		
		t_input = new JTextField();
		t_input.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        // sendMessage();
			}
		});
		p.add(t_input, BorderLayout.CENTER);
		
		b_send = new JButton("입력");
		b_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        // sendMessage();
			}
		});
		p.add(b_send, BorderLayout.EAST);
		
		return p;
	}

	public void show() {
		setVisible(true);
	}

}
