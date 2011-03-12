package polyrallye.ouie.menus;

import polyrallye.modele.Voiture;
import polyrallye.ouie.ActionMenu;
import polyrallye.ouie.Menu;

public class MenuVoitureGarage extends Menu implements ActionMenu {

	protected Voiture voiture;

	public MenuVoitureGarage(Menu menuPrecedent, Voiture v) {
		super(menuPrecedent);

		voiture = v;

		ajouterElement("Spécifications", new ActionMenu() {

			@Override
			public void actionMenu() {
				voiture.lireSpecifications();
			}
		});

		ajouterElement("Tester", null);
		ajouterElement("Vendre", new MenuVenteVoiture(this, voiture));
		ajouterElement("Web", new MenuSources(this, v.getSources()));
	}

	@Override
	public void actionMenu() {
		// Liseuse.lire(voiture.getNom());
		// voiture.getTransmission().calculerRapports();
		lancer();

	}

}
