package polyrallye.controlleur;

import java.io.File;
import java.util.TimerTask;

import org.jdom.Element;

import polyrallye.modele.championnat.Championnat;
import polyrallye.modele.championnat.Duree;
import polyrallye.modele.championnat.Etape;
import polyrallye.modele.circuit.Circuit;
import polyrallye.modele.circuit.Portion;
import polyrallye.modele.personnes.Joueur;
import polyrallye.modele.voiture.Conduite;
import polyrallye.modele.voiture.Moteur;
import polyrallye.modele.voiture.StockVoitures;
import polyrallye.modele.voiture.Transmission;
import polyrallye.modele.voiture.Voiture;
import polyrallye.ouie.ActionMenu;
import polyrallye.ouie.Copilote;
import polyrallye.ouie.Klaxon;
import polyrallye.ouie.Radio;
import polyrallye.ouie.SonVoiture;
import polyrallye.ouie.environnement.Crash;
import polyrallye.ouie.liseuse.Liseuse;
import polyrallye.utilitaires.GestionXML;

/**
 * Gestion d'une course (copilotes, environnement, circuit, sons)
 * 
 * @author antoine
 * 
 */
public class Course implements ActionMenu {

    /**
     * Le timer qui excécute la course.
     */
    protected java.util.Timer timerOrganisateur;

    /**
     * Le timer qui compte le temps passé. Celui-ci n'a rien à voir avec le
     * timer précédent.
     */
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
    protected SonVoiture sonVoiture;

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
     * Radio
     */
    protected Radio radio;

    /**
     * Score du joueur pour la course.
     */
    protected double score;

    /**
     * À chaque passage de rapport, il faut arrêter d'accélerer plus ou moins
     * longtemps
     */
    protected int cpTicksPassageRapport;

    /**
     * Portion courante du circuit.
     */
    protected Portion portionCourante;

    /**
     * Distance parcourue sur la section en cours
     */
    protected double distancePortion = 0.0;

    /**
     * Ou en est le conducteur ?
     */
    protected enum typeAction {
        ACCELERATION, FREINAGE, VIRAGE
    };

    /**
     * Ce que doit être en train de faire le conducteur
     */
    protected typeAction actionCourante;

    protected Etape etape;

    protected Championnat championnat;

    public Course(Voiture voiture, Circuit circuit, Etape etape,
            Championnat championnat) {
        this.voiture = voiture;
        this.circuit = circuit;
        this.etape = etape;
        this.championnat = championnat;
    }

    public Course(Voiture voiture) {
        this(voiture, "Herault/Le_Vigan");
    }

    public Course(Voiture voiture, String fichierCircuit) {
        this.voiture = voiture;

        try {
            Element noeud = GestionXML.chargerNoeudRacine(new File("Circuits/"
                    + fichierCircuit + ".osm"));
            circuit = new Circuit(noeud);
        } catch (Exception e) {
            Liseuse.lire("Désolé il y a un problème avec ce circuit");
            Main.logImportant(e.getMessage());
            e.printStackTrace();
        }

    }

    public void FinDeCourse() throws Exception {
        etape.setClassement(new Duree((int) timerCompteur.getTime()),
                StockVoitures.getVoitureParNom(voiture.getNomComplet()));
        Etape.EnregistrerEtape(etape);

        
        if (championnat.getClassement().get(0).getPersonne().getNom().equals(
                Joueur.session.getNom())) {
            championnat.RemisePrix();

            Liseuse
                    .lire("vous avez gagné le championnat et vous avez remporté la voiture "
                            + championnat.getVoitureGagne()
                            + " et "
                            + championnat.getArgentGagne() + " euros");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see polyrallye.ouie.ActionMenu#actionMenu()
     */
    /*
     * (non-Javadoc)
     * 
     * @see polyrallye.ouie.ActionMenu#actionMenu()
     */
    @Override
    public void actionMenu() {
        if (circuit == null)
            return;

        entreesCourse = new GestionEntreesCourse();

        Main.changerGestionEntrees(entreesCourse);

        System.out.println(circuit);
        // Lancement de l'envirronnement sonore propre au circuit.
        circuit.changeTerrain("asphalt");
        // Création du son du moteur
        sonVoiture = new SonVoiture(voiture);

        // Création du moteur physique
        conduite = new Conduite(voiture);
        // Le klaxon, c'est important
        klaxon = new Klaxon(voiture.getNomComplet());

        // Creation copilote
        copilote = new Copilote();

        // Creation radio
        radio = new Radio();

        // Si on part en première, c'est mieux
        voiture.getTransmission().setPremiere();

        // Timer qui s'occupe de faire le travail 50 fois par secondes
        timerOrganisateur = new java.util.Timer();

        // Timer qui sert à compter le temps passé,
        // ce qui n'a rien à voir avec le timer précedent,
        // bien qu'ils aient le même nom
        timerCompteur = new org.lwjgl.util.Timer();

        org.lwjgl.util.Timer.tick();

        score = voiture.getMoteur().getPuissanceMax();

        final Moteur moteur = voiture.getMoteur();
        moteur.reset();

        portionCourante = circuit.nextPortion();

        actionCourante = typeAction.ACCELERATION;

        TimerTask tt = new TimerTask() {

            @Override
            public void run() {

                if (entreesCourse.isEchap()) {
                    circuit.stop();
                    sonVoiture.stop();
                    timerOrganisateur.cancel();
                    klaxon.delete();
                    // radio.delete();
                    copilote.delete();
                    Main
                            .changerGestionEntrees(GestionEntreesMenu
                                    .getInstance());
                }

                // Gestion du temps
                org.lwjgl.util.Timer.tick();
                float tempsTmp = timerCompteur.getTime();
                float tempsTick = tempsTmp - temps;
                temps = tempsTmp;

                double distanceParcourue = conduite.tick(tempsTick);

                final double vitesse = conduite.getVitesseLineaire();
                final double position = conduite.getDistanceParcourue();

                // Gestion du klaxon
                if (entreesCourse.isKlaxon()) {
                    klaxon.pouet();
                } else {
                    klaxon.pasPouet();
                }

                // Gestion copilote
                if (entreesCourse.isCopilotte()) {
                    copilote.togglePipelette();
                    entreesCourse.copiloteChecked();
                }

                // Gestion Radio
                if (entreesCourse.isRadio()) {
                    radio.toggleRadio();
                    entreesCourse.radioChecked();
                } else if (entreesCourse.isStation()) {
                    radio.changeStation();
                    entreesCourse.stationChecked();
                } else if (entreesCourse.isVLD()) {
                    radio.diminuerSon();
                    entreesCourse.vldChecked();
                } else if (entreesCourse.isVLU()) {
                    radio.augmenterSon();
                    entreesCourse.vluChecked();
                }

                // Gestion de l'accélérateur
                voiture.getMoteur().setAccelere(
                        entreesCourse.isAccelere()
                                && cpTicksPassageRapport == 0);

                // Il faut bien accélerer à nouveau à un moment ou à un autre
                if (cpTicksPassageRapport > 0)
                    --cpTicksPassageRapport;

                double sonFrottement = 0.0;

                // Et du frein
                if (entreesCourse.isFreine()) {
                    conduite.setFreinage(true);
                    if (vitesse > 0.0) {
                        if (vitesse < 1.0) {
                            sonVoiture.sonFreinage();
                        } else {
                            sonFrottement = 0.22;
                        }
                    }
                } else {
                    conduite.setFreinage(false);
                }

                // Maintenant, du passage des vitesses
                Transmission t = voiture.getTransmission();

                final double regimeMoteur = moteur.getRegimeCourant();

                final double regimePuissanceMax = moteur
                        .getRegimePuissanceMax();

                // Ceci est une belle condition avec des appels de méthodes <3
                if (((entreesCourse.isRapportSup() || (entreesCourse.automatique && (regimeMoteur > (regimePuissanceMax + 250.0) || moteur
                        .isRupteurEnclanche()))) && t.passerVitesse())
                        || ((entreesCourse.isRapportInf() || (entreesCourse.automatique && regimeMoteur < (regimePuissanceMax + 250)
                                * t.getCoeffBoiteAutomatique())) && t
                                .retrograder())) {
                    sonVoiture.passageRapport();
                    cpTicksPassageRapport = 5;
                }

                if (entreesCourse.isGauche() || entreesCourse.isDroite()) {
                    sonFrottement = 0.4;
                }

                if (conduite.isPatinage()) {
                    sonFrottement = 0.65;
                }

                if (sonFrottement > 0.0) {
                    circuit.playFrottement((float) sonFrottement);
                } else {
                    circuit.stopFrottement();
                }

                circuit.setDistance(position);
                circuit.setVitesse(vitesse);

                sonVoiture.setRegime((float) voiture.getMoteur()
                        .getRegimeCourant(), entreesCourse.isAccelere());

                // Gestion des portions du circuit

                distancePortion += distanceParcourue;

                Main.logDebug("Distance parcourue: " + position, 0);
                Main.logDebug("Distance portion: " + distancePortion, 1);

                double diff = portionCourante.getLongueur() - distancePortion;

                double vitesseMaxVirage = conduite
                        .getVitesseMaxPourVirage(portionCourante.getAngle());

                double distanceFreinage = conduite
                        .getDistanceFreinage(vitesseMaxVirage) * 3.0;

                // distanceFreinage = (distanceFreinage/vitesse + 0.5) *
                // vitesse;

                double tempsFreinage = distanceFreinage / vitesse;

                Main.logDebug("Angle virage: " + portionCourante.getAngle(), 2);
                Main.logDebug("Vitesse max virage: " + vitesseMaxVirage * 3.6,
                        3);
                Main.logDebug("Distance Freinage: " + distanceFreinage, 4);
                Main.logDebug("Temps freinage: " + tempsFreinage, 12);

                if (distanceFreinage >= diff) {
                    actionCourante = typeAction.FREINAGE;
                    entreesCourse.freine = true;
                } else {
                    entreesCourse.freine = false;
                }

                if (diff < 0.0) {
                    // Une petite marge
                    Main.logImportant("FIAL: "
                            + conduite.getDistanceFreinage(vitesseMaxVirage));

                    entreesCourse.freine = false;

                    portionCourante = circuit.nextPortion();

                    if (portionCourante == null) {
                        timerOrganisateur.cancel();
                        Liseuse.lire("Ahah");
                    } else {
                        actionCourante = typeAction.ACCELERATION;
                        distancePortion = -diff;
                        switch (portionCourante.getType()) {
                        case GAUCHE:
                            copilote.playGauche();
                            Main.logImportant("<= GAUCHE");
                            break;
                        case DROITE:
                            copilote.playDroite();
                            Main.logImportant("DROITE =>");
                            break;
                        }
                    }
                }

                /*
                 * Portion temp; if (contenu.element().getLongueur() <= d) {
                 * temp = contenu.poll(); while (temp != null &&
                 * !temp.getType().equals("virage")) { Evenement temp2 =
                 * (Evenement) temp; temp2.exec(); temp = contenu.poll(); } }
                 */
            }
        };

        // Démarrage des sons
        circuit.start();
        sonVoiture.play();
        // radio.start();

        // À 50Hz, comme le courant EDF
        timerOrganisateur.schedule(tt, 0, 20);// 20

        temps = timerCompteur.getTime();

        Main.logInfo("La course est lancée");
    }

}
