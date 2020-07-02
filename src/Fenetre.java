import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
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
	JLabel user_selection;
	JLabel user_name;
	int pos_horizontal_message = 200;
	int id_relation_discussion = 0;
	int position_user = 120;
	int horizontal_user = 200;
	int nbContacts = 0;
	private HashMap<String,Integer> contactList;
	private HashMap<String,String> accountList = new HashMap<String,String>();
	private ArrayList contactLabelList = new ArrayList<>();
	Color vert = new Color(72, 117, 73);//18, 234, 166
	Color vert_welcome = new Color(107, 196, 166);
	Color noir = new Color(96, 100, 110);
	Color gris = new Color(222, 220, 213); 
	Color bleu = new Color(152, 188, 245);
	Color dark = new Color(52, 58, 64);
	Color dark_light = new Color(129,134,140);
	Color red = new Color(190,65,65);
	Color btn = new Color(235, 139, 53);
	JDialog ajouter_contact = new JDialog(this,"Ajouter un contact",true);
	JDialog ajouter_compte = new JDialog(this,"Ajouter un compte",true);
	JDialog connexion = new JDialog(this,"Saisir identifiant",true);
	JDialog supprimer_contact_dialog = new JDialog(this,"Supprimer le contact",true);
	String dernierMessageReçu = "";
	String dernierMessageEnvoye = "";
	String contact_discussion = "";
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
		g.setColor(dark);
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
		pan.setBackground(dark);
		
		JButton btnRetour = new JButton(new ImageIcon("img\\left-arrow.png"));
		btnRetour.setBorder(null);
		btnRetour.setBackground(null);
		btnRetour.setBounds(25, 20, 32, 32);
		btnRetour.addMouseListener(new acceuilListener());
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
		logo_plus.setBackground(null);
		logo_plus.addMouseListener(new ajouterContactListener());
		pan.add(logo_plus);
		
		JLabel wal = new JLabel("WhatsAppLike");
		wal.setForeground(vert);
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
			contactLabel.setForeground(Color.white);
			contactLabel.setFont(new Font("TimesRoman", Font.BOLD, 15));
			contactLabel.setBounds(650, horizontal, 100, 30);
			contactLabel.addMouseListener(new contactListener());
			pan.add(contactLabel);
			contactLabelList.add(contactLabel);
			
			horizontal += 35;
	        it.remove();
	    }
	    ajouter_contact.setSize(500,250);
		ajouter_contact.setLocationRelativeTo(this);
		supprimer_contact_dialog.setSize(500,150);
		supprimer_contact_dialog.setLocationRelativeTo(this);
	}
	
	//Initialise l'accueil : choix du compte, ajouter nouveau compte..
	public void init_accueil(boolean panUser) {
		if(panUser) { pan.setVisible(false); 
			this.setContentPane(pan_user);
			pan_user.setVisible(true);
		}
		
		pan_user.setBackground(dark);
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
			    JLabel user = new JLabel(new ImageIcon("img\\person.png"));
			    user.setSize(64,64);
			    user.setLocation(position_user,horizontal_user);
			    user.addMouseListener(new userSelection());
			    pan_user.add(user);
			    
			    JLabel user_name = new JLabel(array1[1]);
			    user_name.setFont(new Font("TimesRoman", Font.BOLD, 16));
			    user_name.setForeground(Color.white);
			    user_name.setSize(100,20);
			    user_name.setLocation(position_user+15,horizontal_user+70);
			    user.setLabelFor(user_name);
			    pan_user.add(user_name);
			    
			    position_user += 100;
			    nbContacts++;
			}
			input.close();
			bufRead.close();
			
			plus_user.setBorder(null);
			plus_user.setSize(64,64);
			
			if(nbContacts == 0) {
				//Position btn ajout user au milieu
				position_user = 393;
				horizontal_user = 318-60;
				
				JLabel label_ajout_user = new JLabel("Ajouter un compte");
				label_ajout_user.setForeground(Color.white);
				label_ajout_user.setLocation(350,180);
				label_ajout_user.setSize(500,100);
				label_ajout_user.setFont(new Font("TimesRoman", Font.BOLD, 20));
				pan_user.add(label_ajout_user);
			}
			
			plus_user.setLocation(position_user,horizontal_user);
			plus_user.setBackground(null);
			plus_user.addMouseListener(new addUser());
			pan_user.add(plus_user);
			
			JLabel wal = new JLabel("Bienvenue sur WhatsAppLike");
			wal.setForeground(vert_welcome);
			wal.setFont(new Font("TimesRoman", Font.BOLD, 30));
			wal.setSize(500,30);
			wal.setLocation(250,50);
			pan_user.add(wal);
			
			ajouter_compte.setSize(500,300);
			ajouter_compte.setLocationRelativeTo(this);
			connexion.setSize(500,250);
			connexion.setLocationRelativeTo(this);
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
				if(executor.isShutdown()) {
					System.out.println("SHUTDOWN");
				}else {
					executor.shutdown();
				}
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
			pan_dialog.setBackground(dark);
			
			JLabel label_Instruction = new JLabel("Veuillez saisir un email pour recevoir un lien de vérification et votre ID.");
			label_Instruction.setForeground(Color.white);
			label_Instruction.setLocation(40,15);
			label_Instruction.setSize(420,15);
			pan_dialog.add(label_Instruction);
			
			JLabel label_Email = new JLabel("Email");
			label_Email.setForeground(Color.white);
			label_Email.setLocation(75,50);
			label_Email.setSize(100,15);
			pan_dialog.add(label_Email);
			
			JTextField email = new JTextField();
			email.setLocation(75,70);
			email.setSize(350,30);
			pan_dialog.add(email);
			
			JLabel label_Pseudo = new JLabel("Pseudo");
			label_Pseudo.setForeground(Color.white);
			label_Pseudo.setLocation(75,110);
			label_Pseudo.setSize(100,15);
			pan_dialog.add(label_Pseudo);
			
			JTextField pseudo = new JTextField();
			pseudo.setLocation(75,130);
			pseudo.setSize(350,30);
			pan_dialog.add(pseudo);
			
			JLabel label_Id = new JLabel("Identifiant");
			label_Id.setForeground(Color.white);
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
						if(f.inscription(pseudo.getText(),email.getText())) { //f.inscription(pseudo.getText(),email.getText())
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
						    user.setBackground(btn);
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
			JPanel pan_dialog = new JPanel(null);
			pan_dialog.setBackground(dark);
			JLabel label_Instruction = new JLabel("Veuillez saisir votre identifiant pour accéder au compte");
			label_Instruction.setForeground(Color.white);
			label_Instruction.setLocation(40,15);
			label_Instruction.setSize(420,15);
			pan_dialog.add(label_Instruction);
			
			JLabel label_Id = new JLabel("Identifiant");
			label_Id.setForeground(Color.white);
			label_Id.setLocation(75,70);
			label_Id.setSize(100,15);
			pan_dialog.add(label_Id);
			
			JTextField id = new JTextField();
			id.setLocation(75,90);
			id.setSize(350,30);
			pan_dialog.add(id);
			
			user_selection = (JLabel)e.getSource();
			user_name = (JLabel)user_selection.getLabelFor();
			JButton btnEnvoyerMessage = new JButton("Se connecter");
			btnEnvoyerMessage.setSize(150,30);
			btnEnvoyerMessage.setLocation(175,175);
			btnEnvoyerMessage.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(id.getText().equals(accountList.get(user_name.getText()))){
						connexion.setVisible(false);
						id.setText("");
						label_Instruction.setText("Veuillez saisir votre identifiant pour accéder au compte");
						label_Instruction.setForeground(Color.white); 
						init_user(accountList.get(user_name.getText()),user_name.getText());
					}else {
						label_Instruction.setText("Identifiant incorrecte");
						label_Instruction.setForeground(Color.red);
					}
				}
			});
			btnEnvoyerMessage.setBackground(vert);
		    pan_dialog.add(btnEnvoyerMessage);
		    
			connexion.add(pan_dialog);
			connexion.setVisible(true);	
			connexion.addWindowListener(new WindowAdapter() {
				 public void windowClosed(WindowEvent e)
				  {
					label_Instruction.setText("Veuillez saisir votre identifiant pour accéder au compte");
					label_Instruction.setForeground(Color.white); 
					id.setText("");
					pan_dialog.removeAll();
				  }
				 
				 public void windowClosing(WindowEvent e) {
					label_Instruction.setText("Veuillez saisir votre identifiant pour accéder au compte");
					label_Instruction.setForeground(Color.white); 
					id.setText("");
				 }
			});
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
			pos_horizontal_message = 200;
			pan.remove(logo_chat);
			//pan.repaint();
			JLabel contact = (JLabel) e.getSource();
			contact_discussion = contact.getText();
			id_relation_discussion = contactList.get(contact.getText());
			contact_chat.setText(contact.getText());
			contact_chat.setForeground(Color.white);
			contact_chat.setFont(new Font("TimesRoman", Font.BOLD, 15));
			contact_chat.setBounds(150,110,200,30);
			pan.add(contact_chat);
			contact_chat.repaint();
			System.out.println(contact_discussion);
			JButton supprimer_contact = new JButton(new ImageIcon("img\\delete.png"));
			supprimer_contact.setSize(32,32);
			supprimer_contact.setLocation(518,106);
			supprimer_contact.setBorder(null);
			supprimer_contact.setBackground(null);
			supprimer_contact.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					JPanel pan_dialog = new JPanel(null);
					pan_dialog.setBackground(dark);
					pan_dialog.revalidate();
					pan_dialog.repaint();
					
					JLabel label_Instruction = new JLabel("Supprimer le contact de votre liste de contacts ?");//"Supprimer "+contact_discussion+" de votre liste de contacts ?"
					System.out.println(label_Instruction.getText());
					label_Instruction.setForeground(Color.white);
					label_Instruction.setLocation(40,15);
					label_Instruction.setSize(420,15);
					pan_dialog.add(label_Instruction);
					
					JButton btnSupprimerContact = new JButton("Supprimer");
					btnSupprimerContact.setSize(150,30);
					btnSupprimerContact.setLocation(80,70);
					btnSupprimerContact.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							if(f.supprimerContact(id_relation_discussion)) {
								supprimer_contact_dialog.dispose();
								pan.removeAll();
								init_user(accountList.get(user_name.getText()),user_name.getText());
							}else {
								label_Instruction.setText("Impossible de supprimer le contact");
							}
						}
					});
					btnSupprimerContact.setBackground(red);
				    pan_dialog.add(btnSupprimerContact);
				    
				    JButton btnAnnuler = new JButton("Annuler");
				    btnAnnuler.setSize(150,30);
				    btnAnnuler.setLocation(260,70);
				    btnAnnuler.setBackground(Color.lightGray);
				    pan_dialog.add(btnAnnuler);
				    
					supprimer_contact_dialog.add(pan_dialog);
					supprimer_contact_dialog.setVisible(true);
					supprimer_contact_dialog.addWindowListener(new WindowAdapter() {
						 public void windowClosed(WindowEvent e)
						  {
							pan_dialog.removeAll();
						  }
						 
						 public void windowClosing(WindowEvent e) {
							 pan_dialog.removeAll();
						 }
					});
				}
			});
		    pan.add(supprimer_contact);
		    supprimer_contact.repaint();
		    
		    JLabel pp_contact = new JLabel(new ImageIcon("img\\person_2.png"));
		    pp_contact.setSize(32,32);
		    pp_contact.setLocation(112,106);
		    pan.add(pp_contact);
		    pp_contact.repaint();
		    
			read_historique();
			
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
					    	if(!dernierMessageEnvoye.equals(message)) {
					    		System.out.println("TIMER "+message);
					    		creer_message_chat(pair.getValue().toString());
					    		save_msg_historique(pair.getValue().toString());
					    		dernierMessageEnvoye = message;
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
		    btnEnvoyerMessage.setBackground(bleu);
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
		JLabel msg = new JLabel("<html>"+parseCharactereMessage(message)+"</html>");
    	msg.setForeground(Color.white);
    	msg.setBounds(100,pos_horizontal_message,420,30);
    	msg.setFont(new Font("TimesRoman", Font.PLAIN, 14));
    	pos_horizontal_message += 20;
    	pan.add(msg);
    	
    	msg.repaint();
	}
	
	public void save_msg_historique(String message) {
		try {
			System.out.println(user_name.getText());
			File dir = new File("user_data\\"+user_name.getText());
			dir.mkdir();
			File f  = new File("user_data\\"+user_name.getText()+"\\"+contact_discussion+".txt");
			f.createNewFile();
			
			FileOutputStream input = new FileOutputStream(f,true);
			input.write((message+" \n").getBytes());
			input.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void read_historique() {
		try {
			FileReader input = new FileReader("user_data\\"+user_name.getText()+"\\"+contact_discussion+".txt");
			BufferedReader bufRead = new BufferedReader(input);
			String myLine = null;
			
			while ((myLine = bufRead.readLine()) != null) {  
				System.out.println("HISTORIQUE "+myLine);
				creer_message_chat(myLine);
				dernierMessageEnvoye = parseMessage(myLine);
			}
			input.close();
			bufRead.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//Gestion du click sur bouton envoyer message
	class envoieListener implements MouseListener{
		@Override
		public void mouseClicked(MouseEvent e) {
			String message = ajouterMetaData(f.getPseudo()+": "+txtMessage.getText());
			if(txtMessage.getText().length() > 0 && f.envoyerMessage(id_relation_discussion, message)) {
				dernierMessageEnvoye = parseMessage(message);
				creer_message_chat(message);
				save_msg_historique(message);
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
				save_msg_historique(message);
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
			pan_dialog.setBackground(dark);
			
			JLabel titre = new JLabel("Ajouter un contact");
			titre.setBounds(90,15,150,15);
			titre.setForeground(Color.white);
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
						for(int i = 0;i < contactLabelList.size();i++) {
							JLabel contact = (JLabel)contactLabelList.get(i);
							System.out.println("Supprimer "+i);
							pan.remove(contact);
						}
						pan.repaint();
						
						int horizontal = 150;
						contactList =  f.afficherContacts();
						Iterator it = contactList.entrySet().iterator();
						contactLabelList.clear();
					    while (it.hasNext()) {
					    	
					        HashMap.Entry pair = (HashMap.Entry)it.next();
					        JLabel contactLabel = new JLabel(pair.getKey().toString(),JLabel.RIGHT);
							contactLabel.setForeground(Color.white);
							contactLabel.setFont(new Font("TimesRoman", Font.BOLD, 15));
							contactLabel.setBounds(650, horizontal, 100, 30);
							contactLabel.addMouseListener(new contactListener());
							pan.add(contactLabel);
							contactLabelList.add(contactLabel);
							
							horizontal += 35;
					        it.remove();
					    }
					    pan.repaint();
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
		int index_charactere;
		int occurence_charactere = 0;
		int j = 0;
		int k = 0;
		boolean allCharReplace = false;
		
		for(index_charactere=0;index_charactere < charactere.length;index_charactere++) {
			occurence_charactere = 0;
			while(msg.contains(charactere[index_charactere]) && allCharReplace != true) {
				String old_char = charactere[index_charactere];
				String new_char = charactere_associe[index_charactere][occurence_charactere];
				if(index_charactere == 0) { old_char = "\\"+old_char;} //ajouter \\* pour que le regex fonctionne
				String temp_msg = msg;
				msg = msg.replaceFirst(old_char, new_char);
				
				//gerer le cas ou une étoile est seul, ex: *gras
				if(occurence_charactere == 0 && !msg.contains(charactere[index_charactere])) {
					msg = temp_msg;
					allCharReplace = true;
				}
				
				if(occurence_charactere == 0) {occurence_charactere = 1;}
				else { occurence_charactere = 0;}
			}
		}
		
		return msg;
	}
}