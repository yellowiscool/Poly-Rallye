package polyrallye.utilitaires;

/**
 * Quelques fonctions de cartographies bien utiles.
 * 
 */
public abstract class Cartographie {

	/**
	 * Distance entre deux points de coordonées géographiques.
	 * 
	 * Il s'agit de formules adaptées à partir du logiciel libre JOSM, un
	 * éditeur de cartes OpenStreetMap en Java.
	 * 
	 * La méthode utilisée est celle de Haversine.
	 * 
	 * @return Distance en mètres.
	 */
	public static double distance(double latA, double lonA, double latB,
			double lonB) {
		double R = 6378135;
		double sinHalfLat = Math.sin(Math.toRadians(latB - latA) / 2);
		double sinHalfLon = Math.sin(Math.toRadians(lonB - lonA) / 2);
		double d = 2
				* R
				* Math.asin(Math.sqrt(sinHalfLat * sinHalfLat
						+ Math.cos(Math.toRadians(latA))
						* Math.cos(Math.toRadians(latB)) * sinHalfLon
						* sinHalfLon));

		// For points opposite to each other on the sphere,
		// rounding errors could make the argument of asin greater than 1
		// (This should almost never happen.)
		if (java.lang.Double.isNaN(d)) {
			System.err.println("Error: NaN in greatCircleDistance");
			d = Math.PI * R;
		}
		return d;
	}

	/**
	 * Angle entre trois points, calculés de manière ultra non précise.
	 * 
	 * JOSM proposait des fonctions énormément plus précises, mais très
	 * complexes. C'est inadapté dans notre projet, les valeurs étant
	 * énormément arrondies.
	 * 
	 * En fait, le cotés sphérique est totalement laissé de cotés.
	 * 
	 * @return Angle en degrés
	 */
	public static double angle(double latA, double lonA, double latB,
			double lonB, double latC, double lonC) {

		double m0 = (lonB - lonA) / (latB - latA);
		double m1 = (lonC - lonB) / (latC - latB);

		return Math.toDegrees(Math.atan((m1 - m0) / (1 + m1 * m0)));
	}
}
