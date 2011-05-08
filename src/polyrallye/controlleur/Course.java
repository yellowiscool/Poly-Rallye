package polyrallye.controlleur;

import java.io.File;
import java.util.TimerTask;

import org.jdom.Element;

import polyrallye.modele.Copilote;
import polyrallye.modele.circuit.Circuit;
import polyrallye.modele.voiture.Conduite;
import polyrallye.modele.voiture.Moteur;
import polyrallye.modele.voiture.Transmission;
import polyrallye.modele.voiture.TypeTerrain;
import polyrallye.modele.voiture.Voiture;
import polyrallye.ouie.ActionMenu;
import polyrallye.ouie.Klaxon;
import polyrallye.ouie.SonMoteur;
import polyrallye.ouie.environnement.Crash;
import polyrallye.ouie.environnement.Environnement;
import polyrallye.ouie.environnement.Terrain;
import polyrallye.ouie.liseuse.Liseuse;
import polyrallye.ouie.utilitaires.Sound;
import polyrallye.utilitaires.GestionXML;
import polyrallye.utilitaires.Multithreading;
import t2s.util.Random;

public class Course implements ActionMenu {

	/**
	 * Le timer qui excécute la course.
	 */
	protected java.util.Timer timerOrganisateur;
	protected org.lwjgl.util.Timer timerCompteur;

	/**
	 * Temps entre chaque tick d'horloge.
	 */
	protected float temps;

	/**
	 * Entrées de la course.
	 */
	protected GestionEntreesCourse entreesCourse;

	/**
	 * Son du moteur.
	 */
	protected SonMoteur sonMoteur;
	
	/**
	 * Voiture conduite.
	 */
	protected Voiture voiture;
	
	/**
	 * Gestion physique de la conduite.
	 */
	protected Conduite conduite;
	
	/**
	 * Circuit parcouru.
	 */
	protected Circuit circuit;

	/**
	 * Bruit du crash.
	 */
	protected Crash crash;
	
	/**
	 * Bruit du klaxon.
	 */
	protected Klaxon klaxon;

	/**
	 * Copilote.
	 */
	protected Copilote copilote;

	/**
	 * Score du joueur pour la course.
	 */
	protected double score;

	public Course(Voiture voiture, Circuit circuit) {
		this.voiture = voiture;
		this.circuit = circuit;
	}
	
	public Course(Voiture voiture) {
		this(voiture, "Calenzana");
	}

	public Course(Voiture voiture, String fichierCircuit) {
		this.voiture = voiture;
		
		try {
			Element noeud = GestionXML.chargerNoeudRacine(new File(
			"Circuits/"+fichierCircuit+".osm"));
			circuit = new Circuit(noeud);
		} catch (Exception e) {
			Liseuse.lire("Désolé il y a un problème avec ce circuit");
			Main.logImportant(e.getMessage());
			e.printStackTrace();
		}

	}
	

	@Override
	public void actionMenu() {
		if (circuit == null) return;
		
		entreesCourse = new GestionEntreesCourse();

		Main.changerGestionEntrees(entreesCourse);

		// Lancement de l'envirronnement sonore propre au circuit.
		circuit.changeTerrain("asphalt");
		circuit.start();
		
		// Création du son du moteur
		sonMoteur = new SonMoteur(voiture);
		sonMoteur.play();

		// Création du moteur physique
		conduite = new Conduite(voiture);
		
		// Le klaxon, c'est important
		klaxon = new Klaxon(voiture.getNomComplet());

		// Si on part en première, c'est mieux
		voiture.getTransmission().setPremiere();
		
		// Timer qui s'occupe de faire le travail 50 fois par secondes
		timerOrganisateur = new java.util.Timer();
		
		// Timer qui sert à compter le temps passé,
		// ce qui n'a rien à voir avec le timer précedent,
		// bien qu'ils aient le même nom
		timerCompteur = new org.lwjgl.util.Timer();

		org.lwjgl.util.Timer.tick();
		temps = timerCompteur.getTime();

		Main.logInfo("La course est lancée");

		score = voiture.getMoteur().getPuissanceMax();

		TimerTask tt = new TimerTask() {

			@Override
			public void run() {
				
				if (entreesCourse.isEchap()) {
					circuit.stop();
					sonMoteur.stop();
					timerOrganisateur.cancel();
					Main
							.changerGestionEntrees(GestionEntreesMenu
									.getInstance());
				}
				
				// Gestion du temps
				org.lwjgl.util.Timer.tick();
				float tempsTmp = timerCompteur.getTime();
				float tempsTick = tempsTmp - temps;
				temps = tempsTmp;

				conduite.tick(tempsTick);
				
				// Gestion du klaxon
				if (entreesCourse.isKlaxon()) {
					klaxon.pouet();
				} else {
					klaxon.pasPouet();
				}
				
				// Gestion de l'accélérateur
				voiture.getMoteur().setAccelere(entreesCourse.isAccelere());
				
				// Et du frein
				conduite.setFreinage(entreesCourse.isFreine());
				
				// Maintenant, du passage des vitesses
				Transmission t = voiture.getTransmission();
				
				if (((entreesCourse.isRapportSup()||voiture.getMoteur().isRupteurEnclanche()) && t.passerVitesse()) || (entreesCourse.isRapportInf() && t.retrograder())) {
					sonMoteur.passageRapport();
				}
				

				/*if (entreesCourse.isAccelere()) {
					conduite.acceleration(TypeTerrain.ASPHALT);
					voiture.getMoteur().regimeCourant();
				}
				
				/*if (entreesCourse.isAccelere()) {
					double xa = 20;
					double xb = 1000;
					double ya = 0.5;
					double yb = 2.5;

					double plus = t.getCoefCourant()
							* (ya + (score - xa) * ((yb - ya) / (xb - xa)));

					regime += plus;

					// System.out.println(plus);

				} else {
					regime -= 27.0f;
				}

				if (entreesCourse.isFreine()) {
					regime -= 50.0f;

					/*
					 * if (devonsNousTourner > 1) { if (virageDroite == false) {
					 * gauche.play(); } else { droite.play(); } }
					 */
				/*}

				if (entreesCourse.isGauche() || entreesCourse.isDroite()) {
					regime -= 35.0f;
					terrain.playTourne();
					/*
					 * // Si ça fait trop longtemps que l'on freine if
					 * (--devonsNousTourner < 0) { if (devonsNousTourner < 80) {
					 * // CRASH regime = 8000; // Pour avoir un retour sonore
					 * hein } } else { if ((entreesCourse.isGauche() &&
					 * virageDroite == false) || entreesCourse.isDroite() &&
					 * virageDroite) { if (devonsNousTourner == 0) { ok.play();
					 * devonsNousTourner = 10000000; } } else { // CRASH regime
					 * = 8000; } }
					 */
				/*} else {
					terrain.stopTourne();
				}

				/*if (entreesCourse.isRapportInf()) {
					if (t.getRapportCourant() > 1 && t.retrograder()) {
						regime *= 1.2f;
						sMoteur.passageRapport();
					}
				}

				if (entreesCourse.isRapportSup()) {
					if (t.passerVitesse()) {
						regime *= 0.625f;
						sMoteur.passageRapport();
					}
				}
*/
				/*Moteur m = voiture.getMoteur();
				if (regime < 850) {
					regime = 850;
				} else if (regime > m.getRegimeRupteur()) {
					boolean rupteur = true;
					if (entreesCourse.automatique) {
						if (t.passerVitesse()) {
							rupteur = false;
							regime *= 0.625f;
							sonMoteur.passageRapport();
							System.out.println("CANARD DE MERDE");
						}
					}

					if (rupteur) {
						System.out.println(m.getRegimeRupteur());
						regime = m.getRegimeRupteur() - 250;
					}
				}

				if (entreesCourse.automatique
						&& regime < m.getRegimeRupteur() * 0.625) {
					if (t.getRapportCourant() > 1) {
						t.retrograder();
						regime *= 1.2f;
						sonMoteur.passageRapport();
						System.out.println("CANARD DE MERDE 2");
					}
				}

				
				*/
				sonMoteur.setRegime((float) voiture.getMoteur().getRegimeCourant(), entreesCourse.isAccelere());
				// terrain.setVitesse(regime / 3.0f);
				// TODO mettre le code de abdoul (oui monsieur)*/
			}
		};

		// À 50Hz, comme le courant EDF
		timerOrganisateur.schedule(tt, 0, 20);//20

	/*		canard2 = new Thread() {
			public void run() {

				while (true) {

					long d = Random.unsignedDelta(4, 10) * 1000;

					Main.logImportant("" + d);

					Multithreading.dormir(d);

					freine.play();

					virageDroite = Random.unsignedDelta(1, 2) == 1;

					devonsNousTourner = Random.unsignedDelta(120, 400);

					if (Random.unsignedDelta(1, 2) == 1) {
						gauche.play();
						if (!hasTourned("gauche")) {
							megaCrash();

						}
					} else {
						droite.play();
						if (!hasTourned("droite")) {
							megaCrash();
						}
					}
				}
			}

			protected boolean hasTourned(String sens) {
				double time = 0;
				while (time < 2) {
					if ((sens.equals("gauche") && entreesCourse.isGauche())
							|| (sens.equals("droite") && entreesCourse
									.isDroite()))
						return true;
					time += 0.1;
					Multithreading.dormir(100);
				}
				return false;
			}

			protected void megaCrash() {
				//regime = 10;
				sonMoteur.setRegime(10, false);
				score *= 0.25;

				Transmission t = voiture.getTransmission();
				while (t.getRapportCourant() > 1) {
					t.retrograder();
				}
				sonMoteur.passageRapport();
				crash.play();
			}
		};

		canard2.start();
		*/
	}
}
