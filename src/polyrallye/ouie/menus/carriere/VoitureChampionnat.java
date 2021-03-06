package polyrallye.ouie.menus.carriere;

import java.util.List;
import java.util.ListIterator;

import polyrallye.modele.championnat.Championnat;
import polyrallye.modele.personnes.Joueur;
import polyrallye.modele.voiture.Voiture;
import polyrallye.ouie.ActionMenu;
import polyrallye.ouie.Menu;
import polyrallye.ouie.liseuse.Liseuse;
import polyrallye.ouie.menus.LancementCourse;
import polyrallye.ouie.utilitaires.Sound;

/**
 * VoitureChampionnat : Permet, à partir d'une étape d'un championnat, de
 * choisir une voiture qui est dans le garage et ensuite de lancer la course.
 * 
 * @author ochi
 * 
 */
public class VoitureChampionnat extends Menu implements ActionMenu {

    protected static Sound musique;

    protected String circuit;
    protected polyrallye.modele.championnat.Etape etape;
    protected Championnat champ;

    static {
//        musique = new Sound("Sons/foret/jour_6.wav");
//        musique.setLoop(true);
    }

    public VoitureChampionnat(Menu menuPrecedent, String circuit,
            polyrallye.modele.championnat.Etape etape,Championnat champ) {
        super(menuPrecedent);
        this.etape = etape;
        this.champ = champ;
        this.circuit = circuit;

        messageMenuVide = "Vous n'avez aucune voiture dans votre garage. Pour obtenir des voitures, vous pouvez en acheter en magasin.";
    }

    public Sound getMusique() {
        return musique;
    }

    @Override
    public void actionMenu() {

        Liseuse
                .lire("Vous pouvez sélectionner vos voitures qui sont dans votre garage.");

        lancer();
    }

    @Override
    public void remplir() {
        List<Voiture> l = Joueur.session.getGarage().getVoitures();
        ListIterator<Voiture> i = l.listIterator(l.size());


                while (i.hasPrevious()) {
                        Voiture v = i.previous();
                        ajouterElement(v.getNomComplet(), new LancementCourse(v, circuit, etape, champ));
                }

        }
    }

