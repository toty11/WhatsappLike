import javax.swing.JFrame;

public class Fenetre_secondaire extends JFrame {
	public Fenetre_secondaire(){
		this.setTitle("WAL");
		this.setSize(600,300);
		this.setLocationRelativeTo(null);
		this.setContentPane(new Panneau_secondaire());
		this.setResizable(false);
		this.setVisible(true);
	}
}
