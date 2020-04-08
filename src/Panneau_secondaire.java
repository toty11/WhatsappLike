import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Panneau_secondaire extends JPanel {
	
	public void paintComponent(Graphics g){
		Color vert = new Color(18, 234, 166);
		g.setColor(Color.white);
		g.fillRect(0,0,600,300);
		
		JTextField email = new JTextField();
		email.setBounds(150,100,300,30);
		add(email);
		
		JButton envoyer = new JButton("Ajouter");
		envoyer.setBounds(260,200,80,30);
		envoyer.setBackground(vert);
		add(envoyer);
	}
}
