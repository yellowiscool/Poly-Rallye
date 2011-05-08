package polyrallye.controlleur;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author Antoine Pultier
 *
 *	Gestion des entrées clavier des courses.
 */
public class GestionEntreesCourse extends KeyAdapter {

	protected boolean accelere;
	protected boolean freine;
	protected boolean gauche;
	protected boolean droite;
	protected boolean rapportSup;
	protected boolean rapportInf;
	protected boolean klaxon;
	protected boolean echap;
	protected boolean automatique = true;
	
	@Override
	public void keyPressed(KeyEvent touche) {
		switch (touche.getKeyCode()) {
		case KeyEvent.VK_UP:
			accelere = true;
			break;
		case KeyEvent.VK_DOWN:
			freine = true;
			break;
		case KeyEvent.VK_LEFT:
			gauche = true;
			break;
		case KeyEvent.VK_RIGHT:
			droite = true;
			break;
		case KeyEvent.VK_SHIFT:
			rapportSup = true;
			break;
		case KeyEvent.VK_CONTROL:
			rapportInf = true;
			break;
		case KeyEvent.VK_K:
		case KeyEvent.VK_SPACE:
			klaxon = true;
			break;
		case KeyEvent.VK_ESCAPE:
			echap = true;
			break;
		case KeyEvent.VK_A:
			automatique = !automatique;
			break;
		case KeyEvent.VK_D:
			Main.basculerAffichageConsole();
			break;
		case KeyEvent.VK_V:
			Main.debug = !Main.debug;
			break;
		}
	}
	
	@Override
	public void keyReleased(KeyEvent touche) {
		switch (touche.getKeyCode()) {
		case KeyEvent.VK_UP:
			accelere = false;
			break;
		case KeyEvent.VK_DOWN:
			freine = false;
			break;
		case KeyEvent.VK_LEFT:
			gauche = false;
			break;
		case KeyEvent.VK_RIGHT:
			droite = false;
			break;
		case KeyEvent.VK_K:
		case KeyEvent.VK_SPACE:
			klaxon = false;
			break;
		// Pour les passages de rapports, c'est des évènements
			// à réinitialiser sois même
		/*case KeyEvent.VK_SHIFT:
			rapportSup = true;
			break;
		case KeyEvent.VK_CONTROL:
			rapportInf = true;
			break;*/
			
		}
	}

	public boolean isAccelere() {
		return accelere;
	}

	public boolean isFreine() {
		return freine;
	}

	public boolean isGauche() {
		return gauche;
	}

	public boolean isDroite() {
		return droite;
	}

	public boolean isRapportSup() {
		boolean tmp = rapportSup;
		rapportSup = false;
		return tmp;
	}

	public boolean isRapportInf() {
		boolean tmp = rapportInf;
		rapportInf = false;
		return tmp;
	}
	
	public boolean isKlaxon() {
		return klaxon;
	}

	public boolean isEchap() {
		return echap;
	}
}
