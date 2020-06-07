import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
	JLabel contact_chat = new JLabel();
	JButton plus_user = new JButton(new ImageIcon("img\\plus_user.png"));
	int pos_horizontal_message = 200;
	int id_relation_discussion = 0;
	int position_user = 120;
	int horizontal_user = 200;
	int nbContacts = 0;
	private HashMap<String,Integer> contactList;
	private HashMap<String,String> accountList = new HashMap<String,String>();
	private ArrayList contactLabel = new ArrayList<>();
	Color vert = new Color(18, 234, 166);
	Color noir = new Color(96, 100, 110);
	Color gris = new Color(222, 220, 213); 
	Color bleu = new Color(152, 188, 245);
	JDialog ajouter_contact = new JDialog(this,"Ajouter un contact",true);
	JDialog ajouter_compte = new JDialog(this,"Ajouter un compte",true);
	String dernierMessageReçu = "";
	String dernierMessageEnvoye = "";
	ScheduledExecutorService executor;
	
	public Fenetre() throws IOException{
		this.setTitle("WAL");
		this.setSize(850, 700);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);             
		this.setResizable(false);
		this.setContentPane(pan_user);
		
		init_accueil(false);
	    this.setVisible(true);
	}
	
	public void drawR(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(90, 140, 460, 440);
		g.setColor(noir);
		g.drawRect(90, 140, 460, 440);
	}
	
	public void drawFill(Graphics g) {
		g.setColor(Color.red);
		g.fillRect(0, 0, 850, 70);
	}
	
	//Initialise le jpanel avec les composant : liste des contacts, chat..
	public void init_user(String idUserConnecter, String pseudo) {
		pan_user.setVisible(false);
		pan.setVisible(true);
		f = new fonction(idUserConnecter,pseudo);
		contactList =  f.afficherContacts();
		this.setContentPane(pan);
		pan.setBackground(Color.white);
		
		JButton btnRetour = new JButton("Accueil");
		btnRetour.setBounds(30, 15, 80, 30);
		btnRetour.addMouseListener(new acceuilListener());
		btnRetour.setBackground(bleu);
		pan.add(btnRetour);
		
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
	    ajouter_contact.setSize(500,250);
		ajouter_contact.setLocationRelativeTo(this);
	}
	
	//Initialise l'accueil : choix du compte, ajouter nouveau compte..
	public void init_accueil(boolean panUser) {
		if(panUser) { pan.setVisible(false); 
			this.setContentPane(pan_user);
			pan_user.setVisible(true);
		}
		
		pan_user.setBackground(Color.white);
		try {
			FileReader input = new FileReader("identifiant.txt");
			BufferedReader bufRead = new BufferedReader(input);
			String myLine = null;
			
			while ((myLine = bufRead.readLine()) != null) {   
				if(position_user > 520) {
					horizontal_user = 400;
			    	position_user = 120;
			    }
			    String[] array1 = myLine.split(" ");
			    accountList.put(array1[1], array1[0]);
			    JButton user = new JButton(array1[1]);
			    user.setBackground(gris);
			    user.setFont(new Font("TimesRoman", Font.BOLD, 25));
			    user.setSize(180,180);
			    user.setLocation(position_user,horizontal_user);
			    user.addMouseListener(new userSelection());
			    position_user += 200;
			    nbContacts++;
			    pan_user.add(user);
			}
			input.close();
			bufRead.close();
			
			plus_user.setBorder(null);
			plus_user.setSize(64,64);
			if(nbContacts == 0) {
				//Position btn ajout user au milieu
				position_user = 393;
				horizontal_user = 318-60;
				/*
				JLabel label_ajout_user = new JLabel("Ajouter un compte");
				label_ajout_user.setForeground(noir);
				label_ajout_user.setLocation(350,100);
				label_ajout_user.setSize(500,100);
				label_ajout_user.setFont(new Font("TimesRoman", Font.BOLD, 20));
				pan.add(label_ajout_user);*/
			}
			plus_user.setLocation(position_user,horizontal_user+60);
			plus_user.setBackground(Color.WHITE);
			plus_user.addMouseListener(new addUser());
			pan_user.add(plus_user);
			
			JLabel wal = new JLabel("Bienvenu sur WhatsAppLike");
			wal.setForeground(vert);
			wal.setFont(new Font("TimesRoman", Font.BOLD, 30));
			wal.setSize(500,30);
			wal.setLocation(250,50);
			pan_user.add(wal);
			
			ajouter_compte.setSize(500,300);
			ajouter_compte.setLocationRelativeTo(this);
			
			drawFill(pan_user.getGraphics());
		}catch(Exception e) {
			System.out.println(e.toString());
		}
	}
	
	//Gestion clic bouton retour vers l'accueil
	class acceuilListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent arg0) {
			pan.removeAll();
			position_user = 120;
			horizontal_user = 200;
			dernierMessageReçu = "";
			dernierMessageEnvoye = "";
			if(executor != null) {
				executor.shutdown();
			}
			pos_horizontal_message = 200;
			init_accueil(true);
		}
		@Override
		public void mouseEntered(MouseEvent arg0) {
		}
		@Override
		public void mouseExited(MouseEvent arg0) {
		}
		@Override
		public void mousePressed(MouseEvent arg0) {
		}
		@Override
		public void mouseReleased(MouseEvent arg0) {
		}
	}
	
	//Gestion création compte et activation
	class addUser implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent arg0) {
			String pseudo_newUser;
			JPanel pan_dialog = new JPanel(null);
			pan_dialog.setBackground(Color.white);
			pan_dialog.setLayout(null);
			pan_dialog.setBackground(Color.WHITE);
			
			JLabel label_Instruction = new JLabel("Veuillez saisir un email pour recevoir un lien de vérification et votre ID.");
			label_Instruction.setLocation(40,15);
			label_Instruction.setSize(420,15);
			pan_dialog.add(label_Instruction);
			
			JLabel label_Email = new JLabel("Email");
			label_Email.setLocation(75,50);
			label_Email.setSize(100,15);
			pan_dialog.add(label_Email);
			
			JTextField email = new JTextField();
			email.setLocation(75,70);
			email.setSize(350,30);
			pan_dialog.add(email);
			
			JLabel label_Pseudo = new JLabel("Pseudo");
			label_Pseudo.setLocation(75,110);
			label_Pseudo.setSize(100,15);
			pan_dialog.add(label_Pseudo);
			
			JTextField pseudo = new JTextField();
			pseudo.setLocation(75,130);
			pseudo.setSize(350,30);
			pan_dialog.add(pseudo);
			
			JLabel label_Id = new JLabel("Identifiant");
			label_Id.setLocation(75,50);
			label_Id.setSize(100,15);
			label_Id.setVisible(false);
			pan_dialog.add(label_Id);
			
			JTextField id = new JTextField();
			id.setLocation(75,70);
			id.setSize(350,30);
			id.setVisible(false);
			pan_dialog.add(id);
			
			JButton btnEnvoyerMessage = new JButton("Envoyer email");
			btnEnvoyerMessage.setSize(150,30);
			btnEnvoyerMessage.setLocation(175,175);
		    btnEnvoyerMessage.addActionListener ( new ActionListener()  {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(btnEnvoyerMessage.getText() == "Envoyer email") {
						if(true) { //f.inscription(pseudo.getText(),email.getText())
							label_Instruction.setText("Vérifier votre boîte mail. Une fois votre compte activé saisissez votre ID.");
							email.setVisible(false);
							label_Email.setVisible(false);
							
							label_Id.setVisible(true);
							id.setVisible(true);
							btnEnvoyerMessage.setText("Terminer");
						}else {
							label_Instruction.setText("Une erreur est survenu");
							label_Instruction.setForeground(Color.red);
						}
					}else {
						String identifiant = id.getText();
						String pseudo_user = pseudo.getText();
						
						File file;
						try {
							file = new File("identifiant.txt");
							FileOutputStream input =new FileOutputStream(file,true);
							input.write((identifiant+" "+pseudo_user+" \n").getBytes());
							input.close();
							if(position_user > 520) {
								horizontal_user = 400;
						    	position_user = 120;
						    }
							JButton user = new JButton(pseudo_user);
						    user.setBackground(gris);
						    user.setFont(new Font("TimesRoman", Font.BOLD, 25));
						    user.setSize(180,180);
						    user.setLocation(position_user,horizontal_user);
						    user.addMouseListener(new userSelection());
						    position_user += 200;
						    nbContacts++;
						    pan_user.add(user);
						    user.repaint();
						    plus_user.setLocation(position_user,horizontal_user+60);
						    
							ajouter_compte.setVisible(false);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							label_Instruction.setText("Une erreur est survenu");
							label_Instruction.setForeground(Color.red);
						}
					}
				}
			});
		    btnEnvoyerMessage.setBackground(vert);
		    pan_dialog.add(btnEnvoyerMessage);
		    
			ajouter_compte.add(pan_dialog);
			ajouter_compte.setVisible(true);	
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
	
	//Gestion de la sélection du compte
	class userSelection implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			JButton user_selection = (JButton)e.getSource();
			init_user(accountList.get(user_selection.getText()), user_selection.getText());
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
				pan.remove(logo_chat);
				JLabel contact = (JLabel) e.getSource();	
				id_relation_discussion = contactList.get(contact.getText());
				contact_chat.setText(contact.getText());
				contact_chat.setForeground(noir);
				contact_chat.setFont(new Font("TimesRoman", Font.PLAIN, 14));
				contact_chat.setBounds(150, 120,80,30);
				pan.add(contact_chat);
				contact_chat.repaint();
			
				Runnable chatRunnable = new Runnable() {
				    public void run() {
				    	HashMap<String,String> messages = f.getMessage(id_relation_discussion);
				    	if(messages.size() > 0) {
				    		Iterator it = messages.entrySet().iterator();
						    while (it.hasNext()) {
						    	HashMap.Entry pair = (HashMap.Entry)it.next();
						    	String message = parseMessage(pair.getValue().toString());
						    	System.out.println("dernier message envoyé "+dernierMessageEnvoye);
						    	System.out.println("Message loop while "+message);
						    	if(dernierMessageEnvoye.compareToIgnoreCase(message) < 0) {
						    		creer_message_chat(pair.getValue().toString());
							    	dernierMessageReçu = message;
							    	it.remove();
						    	}
						    }	
				    	}
				    }
				};

				executor = Executors.newScheduledThreadPool(1);
				executor.scheduleAtFixedRate(chatRunnable, 0, 5, TimeUnit.SECONDS);
				
				txtMessage.setBounds(100, 600, 350, 30);
			    txtMessage.addKeyListener(new envoieListenerEntrer());
			    pan.add(txtMessage);
			    txtMessage.repaint();
			    
			    JButton btnEnvoyerMessage = new JButton("Envoyer");
			    btnEnvoyerMessage.setBounds(465, 600, 80, 30);
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
		
		public void creer_message_chat(String message) {
			message = parseTemps(message)+" "+parseMessage(message);
			System.out.println(parseCharactereMessage(message));
			JLabel msg = new JLabel("<html>"+parseCharactereMessage(message)+"</html>");
	    	msg.setForeground(noir);
	    	msg.setBounds(100,pos_horizontal_message,420,30);
	    	msg.setFont(new Font("TimesRoman", Font.PLAIN, 14));
	    	pos_horizontal_message += 20;
	    	pan.add(msg);
	    	msg.repaint();
		}
		
		//Gestion du click sur bouton envoyer message
		class envoieListener implements MouseListener{
			@Override
			public void mouseClicked(MouseEvent e) {
				String message = ajouterMetaData(f.getPseudo()+": "+txtMessage.getText());
				if(txtMessage.getText().length() > 0 && f.envoyerMessage(id_relation_discussion, message)) {
					dernierMessageEnvoye = parseMessage(message);
					creer_message_chat(message);
			    	txtMessage.setText("");
			    	System.out.println("Envoi msg "+dernierMessageEnvoye);
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
		
		//Gestion de l'utilisation du bouton entrer pour envoyer un message
		class envoieListenerEntrer implements KeyListener{
			@Override
			public void keyPressed(KeyEvent k) {
				String message = ajouterMetaData(f.getPseudo()+": "+txtMessage.getText());
				if(k.getKeyCode() == KeyEvent.VK_ENTER && txtMessage.getText().length() > 0 
						&& f.envoyerMessage(id_relation_discussion,message)) {
					dernierMessageEnvoye = parseMessage(message);
					creer_message_chat(message);
					txtMessage.setText("");
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {}
			@Override
			public void keyTyped(KeyEvent arg0) {}	
		}
		
		//Gestion click logo ajouter contact
		class ajouterContactListener implements MouseListener{
			@Override
			public void mouseClicked(MouseEvent arg0) {
				JPanel pan_dialog = new JPanel(null);
				pan_dialog.setBackground(Color.white);
				pan_dialog.setLayout(null);
				pan_dialog.setBackground(Color.WHITE);
				
				JLabel titre = new JLabel("Ajouter un contact");
				titre.setBounds(90,15,150,15);
				titre.setForeground(noir);
				titre.setFont(new Font("TimesRoman", Font.PLAIN, 14));
				pan_dialog.add(titre);
				
				JTextField email = new JTextField();
				email.setLocation(75,80);
				email.setSize(350,30);
				pan_dialog.add(email);
				
				JButton btnEnvoyerMessage = new JButton("Ajouter");
				btnEnvoyerMessage.setSize(80,30);
				btnEnvoyerMessage.setLocation(210,130);
			    btnEnvoyerMessage.addActionListener (new ActionListener()  {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						JTextField txtEmail = (JTextField) pan_dialog.getComponent(1);
						if(f.ajouterContact(txtEmail.getText())) {
							System.out.println("OK 123");
							for(int i = 0;i < contactLabel.size();i++) {
								JLabel contact = (JLabel)contactLabel.get(i);
								pan.remove(contact);
							}
							
							int horizontal = 150;
							contactList =  f.afficherContacts();
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
						    ajouter_contact.setVisible(false);
						}else {
							JLabel erreur = new JLabel("Erreurs lors de l'ajout de ce contact");
							erreur.setForeground(Color.red);
						}
					}
				});
			    
			    btnEnvoyerMessage.setBackground(vert);
			    pan_dialog.add(btnEnvoyerMessage);
				ajouter_contact.add(pan_dialog);
				ajouter_contact.setVisible(true);
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
		
		public String ajouterMetaData(String msg) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy");  
			LocalDateTime now = LocalDateTime.now();  
			return "<m>"+msg+"</m><t>"+dtf.format(now)+"</t>";
		}
		
		public String parseMessage(String msg) {
			int debutMessage = msg.indexOf("<m>");
			int finMessage = msg.indexOf("</m>");
			
			return msg.substring(debutMessage+3, finMessage);
		}
		
		public String parseTemps(String msg) {
			int debutMessage = msg.indexOf("<t>");
			int finMessage = msg.indexOf("</t>");
			
			return msg.substring(debutMessage+3, finMessage);
		}
		
		String [] charactere = {"*","_","-"};
		String [][] charactere_associe = {{"<b>","</b>"},{"<i>","</i>"},{"<ins>","</ins>"}};
		public String parseCharactereMessage(String msg) {
			String newMessage = "";
			
			int index_charactere;
			int index_mot;
			int occurence_charactere = 0;
			int j = 0;
			int k = 0;
			
			for(index_charactere=0;index_charactere < charactere.length;index_charactere++) {
				occurence_charactere = 0;
				while(msg.contains(charactere[index_charactere])) {
					String old_char = charactere[index_charactere];
					String new_char = charactere_associe[index_charactere][occurence_charactere];
					if(index_charactere == 0) { old_char = "\\"+old_char;} //ajouter \\* pour que le regex fonctionne
					msg = msg.replaceFirst(old_char, new_char);
					occurence_charactere++;
				}
			}
			
			if(newMessage.length() == 0) { newMessage = msg;}
			return newMessage;
		}
}