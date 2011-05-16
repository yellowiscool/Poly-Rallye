package polyrallye.ouie;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import polyrallye.controlleur.Main;
import polyrallye.ouie.environnement.Sfx;
import polyrallye.ouie.utilitaires.Sound;
import polyrallye.utilitaires.GestionXML;

public class Copilote {

	// Id du copilote
	private int id;

	private List<Sound> gauche;
	private List<Sound> droite;
	private List<Sound> freine;
<<<<<<< HEAD
=======
	private List<Sound> ok;
>>>>>>> 6e0edfee5c5e6f545c6c774cc480b8665a636837

	private Sfx bullshit;
	private boolean isPipelette;

	private Random random;

	public Copilote() {

		random = new Random();

		// Sélection d'un copilote
		String rep = "Sons/copilote/";
		int nb = (new File(rep).list()).length;
		id = random.nextInt(nb) + 1;
		rep += id + "/";

		isPipelette = false;

		// Manifeste…
		int nbGauche = 0;
		int nbDroite = 0;
		int nbFreine = 0;
		int nbSfx = 0;
<<<<<<< HEAD
=======
		int nbOk = 0;
>>>>>>> 6e0edfee5c5e6f545c6c774cc480b8665a636837

		BufferedReader mani = null;
		// On lit le fichier comme d'ab
		try {
			mani = new BufferedReader(new FileReader(rep + "manifeste.cfg"));
			String line = null;
			try {
				while ((line = mani.readLine()) != null) {
					if (line.contains("gauche")) {
						nbGauche = GestionXML.getInt(line.substring(line
								.indexOf(" ")));
					} else if (line.contains("sfx")) {
						nbSfx = GestionXML.getInt(line.substring(line
								.indexOf(" ")));
					} else if (line.contains("ok")) {
						nbOk = Integer
								.valueOf(line.substring(line.indexOf(" ") + 1));
					} else if (line.contains("droite")) {
						nbDroite = GestionXML.getInt(line.substring(line
								.indexOf(" ")));
					} else if (line.contains("freine")) {
						nbFreine = GestionXML.getInt(line.substring(line
								.indexOf(" ")));
					}

				}
			} catch (IOException e) {
				System.out.println("Erreur lecture fichier");
			}
		} catch (FileNotFoundException e) {
			System.out.println("Erreur chargement fichier");
		}

		try {
			mani.close();
		} catch (IOException e) {
		}

		gauche = new ArrayList<Sound>(nbGauche);
		droite = new ArrayList<Sound>(nbDroite);
		freine = new ArrayList<Sound>(nbFreine);
<<<<<<< HEAD
=======
		ok = new ArrayList<Sound>(nbOk);
>>>>>>> 6e0edfee5c5e6f545c6c774cc480b8665a636837
		Main.logImportant("" + nbDroite);
		for (int i = 1; i <= nbGauche; ++i) {
			gauche.add(new Sound(rep + "gauche_" + i + ".wav"));
		}

		for (int i = 1; i <= nbDroite; ++i) {
			droite.add(new Sound(rep + "droite_" + i + ".wav"));
		}

		for (int i = 1; i <= nbFreine; ++i) {
			freine.add(new Sound(rep + "freine_" + i + ".wav"));
		}
<<<<<<< HEAD
=======
		for (int i = 1; i <= nbOk; ++i) {
			ok.add(new Sound(rep + "ok_" + i + ".wav"));
		}
>>>>>>> 6e0edfee5c5e6f545c6c774cc480b8665a636837

		if (nbSfx != 0) {
			bullshit = new Sfx(rep + "sfx/", nbSfx, 3, true, 3.0f);
		} else {
			bullshit = new Sfx();
		}
		bullshit.start();
		bullshit.pause(true);

	}

	public void togglePipelette() {
		String rep = "Sons/copilote/" + id + "/";
		if (isPipelette) {
			isPipelette = false;
			bullshit.pause(true);

			if (new File(rep + "stfu.wav").exists()) {
				Sound stfu = new Sound(rep + "stfu.wav");
				stfu.playAndDelete();
			}
		} else {
			isPipelette = true;
			bullshit.pause(false);
		}
	}

<<<<<<< HEAD
=======
	public void playOk() {
		String rep = "Sons/copilote/" + id + "/";
		Random random = new Random();
		if (isPipelette)
			bullshit.pause(true);
		gauche.get(random.nextInt(gauche.size())).play();
		if (isPipelette)
			bullshit.pause(false);
	}

>>>>>>> 6e0edfee5c5e6f545c6c774cc480b8665a636837
	public void playCrash() {
		String rep = "Sons/copilote/" + id + "/";
		if (new File(rep + "crash.wav").exists()) {
			Sound crash = new Sound(rep + "crash.wav");
			crash.playAndWait();
			crash.delete();
		}

	}

	public void playGauche() {
<<<<<<< HEAD
		/*
		 * if (isPipelette) bullshit.pause(true);*
		 */

		gauche.get(random.nextInt(gauche.size())).play();

		/*
		 * if (isPipelette) bullshit.pause(false);
		 */
	}

	public void playDroite() {
		/*
		 * if (isPipelette) bullshit.pause(true);
		 */

		droite.get(random.nextInt(droite.size())).play();

		/*
		 * if (isPipelette) bullshit.pause(false);
		 */
=======
		if (isPipelette)
			bullshit.pause(true);

		gauche.get(random.nextInt(gauche.size())).play();

		if (isPipelette)
			bullshit.pause(false);
	}

	public void playDroite() {
		if (isPipelette)
			bullshit.pause(true);

		droite.get(random.nextInt(droite.size())).play();

		if (isPipelette)
			bullshit.pause(false);
>>>>>>> 6e0edfee5c5e6f545c6c774cc480b8665a636837
	}

	public void playFreine() {
		if (isPipelette)
			bullshit.pause(true);

		freine.get(random.nextInt(freine.size())).play();

		if (isPipelette)
			bullshit.pause(false);

	}

	public void delete() {
		for (Sound s : gauche) {
			s.delete();
		}
		for (Sound s : droite) {
			s.delete();
		}
		for (Sound s : freine) {
			s.delete();
		}
		bullshit.tuer();
	}

}