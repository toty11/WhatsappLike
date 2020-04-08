import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Fenetre extends JFrame {
	JPanel pan_user = new JPanel(null);
	JPanel pan = new JPanel(null);
	
	fonction f;
	JTextField txtMessage = new JTextField();
	
	JLabel logo_chat;
	int pos_horizontal_message = 200;
	int id_relation_discussion = 0;
	private HashMap<String,Integer> contactList;
	private HashMap<String,String> accountList = new HashMap<String,String>();
	Color vert = new Color(18, 234, 166);
	Color noir = new Color(96, 100, 110);
	JDialog ajouter_relation = new JDialog(this,"Ajouter relation",true);
	
	public Fenetre() throws IOException{
		this.setTitle("WAL");
		this.setSize(850, 700);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);             
		this.setResizable(false);
		this.setContentPane(pan_user);
		
		pan_user.setBackground(Color.white);
		FileReader input = new FileReader("identifiant.txt");
		BufferedReader bufRead = new BufferedReader(input);
		String myLine = null;
		int postion_user = 180;
		while ( (myLine = bufRead.readLine()) != null)
		{    
		    String[] array1 = myLine.split(" ");
		    accountList.put(array1[1], array1[0]);
		    JButton user = new JButton(array1[1]);
		    user.setBorder(null);
		    user.setBackground(Color.WHITE);
		    user.setFont(new Font("TimesRoman", Font.BOLD, 25));
		    user.setSize(180,180);
		    user.setLocation(postion_user,200);
		    user.addMouseListener(new userSelection());
		    postion_user += 200;
		    pan_user.add(user);
		}
		
		JButton plus_user = new JButton(new ImageIcon("img\\plus_user.png"));
		plus_user.setBorder(null);
		plus_user.setSize(64,64);
		plus_user.setLocation(postion_user,260);
		plus_user.setBackground(Color.WHITE);
		pan_user.add(plus_user);
		
	    this.setVisible(true);
	}
	
	
	public void drawR(Graphics g) {
		g.setColor(noir);
		g.drawRect(90, 145, 460, 340);
	}
	
	//Initialise le jpanel avec les composant
	public void init_acceuil(String idUserConnecter) {
		pan_user.setVisible(false);
		f = new fonction(idUserConnecter);
		contactList =  f.afficherContacts();
		this.setContentPane(pan);
		pan.setBackground(Color.white);
		int horizontal = 150;
		JLabel vosContacts = new JLabel("Vos contacts");
		vosContacts.setForeground(vert);
		vosContacts.setFont(new Font("TimesRoman", Font.BOLD, 25));
		vosContacts.setSize(200,30);
		vosContacts.setLocation(580,100);
		pan.add(vosContacts);
		
		JButton logo_plus = new JButton(new ImageIcon("img\\plus.png"));
		logo_plus.setBorder(null);
		logo_plus.setSize(24,24);
		logo_plus.setLocation(760,100);
		logo_plus.setBackground(Color.WHITE);
		logo_plus.addMouseListener(new ajouterContactListener());
		pan.add(logo_plus);
		
		JLabel wal = new JLabel("WhatsAppLike");
		wal.setForeground(noir);
		wal.setFont(new Font("TimesRoman", Font.BOLD, 30));
		wal.setSize(300,30);
		wal.setLocation(300,50);//225,50
		pan.add(wal);
		
		logo_chat = new JLabel(new ImageIcon("img\\comment_128.png"));
		logo_chat.setSize(128,128);
		logo_chat.setLocation(360,286);
		pan.add(logo_chat);
		
		Iterator it = contactList.entrySet().iterator();
	    while (it.hasNext()) {
	        HashMap.Entry pair = (HashMap.Entry)it.next();
	        JLabel contactLabel = new JLabel(pair.getKey().toString(),JLabel.RIGHT);
			contactLabel.setForeground(noir);
			contactLabel.setFont(new Font("TimesRoman", Font.PLAIN, 15));
			contactLabel.setBounds(650, horizontal, 80, 30);
			contactLabel.addMouseListener(new contactListener());
			pan.add(contactLabel);
			horizontal += 35;
	        it.remove();
	    }
	    ajouter_relation.setSize(500,250);
		ajouter_relation.setLocationRelativeTo(this);
	}
	
	class userSelection implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			JButton user_selection = (JButton)e.getSource();
			init_acceuil(accountList.get(user_selection.getText()));
		}
		@Override
		public void mouseEntered(MouseEvent arg0) {}
		@Override
		public void mouseExited(MouseEvent arg0) {}
		@Override
		public void mousePressed(MouseEvent arg0) {}
		@Override
		public void mouseReleased(MouseEvent arg0) {}
	}
		
	//Gestion du click sur un contact de la liste
		class contactListener implements MouseListener {
			private HashMap<String,Integer> contactList =  f.afficherContacts();
			
			@Override
			public void mouseClicked(MouseEvent e) {
				logo_chat.setVisible(false);
				logo_chat.setBackground(null);
				logo_chat.repaint();
				JLabel contact = (JLabel) e.getSource();	
				id_relation_discussion = contactList.get(contact.getText());
				HashMap<String,String> messages = f.getMessage(id_relation_discussion);
				if(messages.size() > 0) {
					Iterator it = messages.entrySet().iterator();
				    while (it.hasNext()) {
				    	HashMap.Entry pair = (HashMap.Entry)it.next();
				    	JLabel msg = new JLabel(pair.getKey()+": "+pair.getValue());
				    	msg.setForeground(noir);
				    	msg.setBounds(100,pos_horizontal_message,420,15);
				    	msg.setFont(new Font("TimesRoman", Font.PLAIN, 14));
				    	pos_horizontal_message += 20;
				    	pan.add(msg);
				    	msg.repaint();
				    	it.remove();
				    }
				}
				
				txtMessage.setBounds(100, 500, 350, 30);
			    txtMessage.addKeyListener(new envoieListenerEntrer());
			    pan.add(txtMessage);
			    txtMessage.repaint();
			    
			    JButton btnEnvoyerMessage = new JButton("Envoyer");
			    btnEnvoyerMessage.setBounds(465, 500, 80, 30);
			    btnEnvoyerMessage.addMouseListener(new envoieListener());
			    btnEnvoyerMessage.setBackground(vert);
			    pan.add(btnEnvoyerMessage);
			    btnEnvoyerMessage.repaint();
			    drawR(pan.getGraphics());
			}

			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
		}
		
		//Gestion du click sur bouton envoyer message
		class envoieListener implements MouseListener{

			@Override
			public void mouseClicked(MouseEvent e) {
				if(txtMessage.getText().length() > 0 && f.envoyerMessage(id_relation_discussion, txtMessage.getText())) {
					JLabel msg = new JLabel("Cuda: "+txtMessage.getText());
					msg.setForeground(noir);
			    	msg.setBounds(100,pos_horizontal_message,200,15);
			    	msg.setFont(new Font("TimesRoman", Font.PLAIN, 14));
			    	pos_horizontal_message += 20;
			    	txtMessage.setText("");
			    	pan.add(msg);
			    	msg.repaint();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}	
		}
		
		//Gestion click logo ajouter contact
		class ajouterContactListener implements MouseListener{

			@Override
			public void mouseClicked(MouseEvent arg0) {
				JPanel pan_dialog = new JPanel(null);
				pan_dialog.setBackground(Color.white);
				pan_dialog.setLayout(null);
				pan_dialog.setBackground(Color.WHITE);
				
				JTextField email = new JTextField();
				email.setLocation(75,50);
				email.setSize(350,30);
				pan_dialog.add(email);
				
				JButton btnEnvoyerMessage = new JButton("Ajouter");
				btnEnvoyerMessage.setSize(80,30);
				btnEnvoyerMessage.setLocation(210,100);
			    btnEnvoyerMessage.addActionListener ( new ActionListener()  {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						JTextField txtEmail = (JTextField) pan_dialog.getComponent(0);
						if(f.ajouterContact(txtEmail.getText())) {
							ajouter_relation.setVisible(false);
						}else {
							JLabel erreur = new JLabel("Erreurs lors de l'ajout de ce contact");
							erreur.setForeground(Color.red);
						}
					}
				});
			    
			    btnEnvoyerMessage.setBackground(vert);
			    pan_dialog.add(btnEnvoyerMessage);
			    
				ajouter_relation.add(pan_dialog);
				ajouter_relation.setVisible(true);
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {}
			@Override
			public void mouseExited(MouseEvent arg0) {}
			@Override
			public void mousePressed(MouseEvent arg0) {}
			@Override
			public void mouseReleased(MouseEvent arg0) {}	
		}
		
		//Gestion de l'utilisation du bouton entrer pour envoyer un message
		class envoieListenerEntrer implements KeyListener{

			@Override
			public void keyPressed(KeyEvent k) {
				if(k.getKeyCode() == KeyEvent.VK_ENTER && txtMessage.getText() != "" 
				&& f.envoyerMessage(id_relation_discussion, txtMessage.getText())) {
					JLabel msg = new JLabel("cuda: "+txtMessage.getText());
					msg.setForeground(noir);
			    	msg.setBounds(100,pos_horizontal_message,200,15);
			    	msg.setFont(new Font("TimesRoman", Font.PLAIN, 14));
			    	pos_horizontal_message += 20;
			    	txtMessage.setText("");
			    	pan.add(msg);
			    	msg.repaint();
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {}
			@Override
			public void keyTyped(KeyEvent arg0) {}	
		}
}