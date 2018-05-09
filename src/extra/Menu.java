package extra;
import extra.Console;

public class Menu {
  // Az oszt�lyb�l nem lehet p�ld�nyt l�trehozni:
  private Menu() {
  }
  
 public static int egyszeruMenu(String Menupont[],int db) {
	 int valasz=0;
	 for (int i=0;i<db;i++) 
		 System.out.println(Menupont[i]);
	 System.out.println("V�lassz:");
	 valasz = extra.Console.readInt();
	 return valasz;
 }
 
 public static int egesz_Beolvas(String uzenet, int min, int max, String hibaUzenet) {
	 int seged;
	 do {
		seged=Console.readInt(uzenet);
	 	if ((seged<min || seged>max) && !(hibaUzenet.isEmpty()))
	 		System.out.println(hibaUzenet);
	 } while (seged<min || seged>max);
	 return seged;
 }

/*Egy val�s sz�mot a megadott tizedesjegyre kerek�t.*/
	static double kerekit(double szam,int tizedes)
	{
		String seged, seged1;
		seged=Integer.toString(tizedes); // A tizedest sz�vegg� alak�tjuk, hogy sz�mform�tumot tudjunk k�sz�teni.
		seged1=String.format("%."+seged+"f", szam); // A megkapott sz�m param�tert a megadott form�tum szerint megform�zzuk, de az eredm�ny sztring.
		return Double.valueOf(seged1.replace(',','.')); // Visszaadjuk a megform�zott sz�mot sz�mm� alak�tva, de ki kell benne cser�lni a tizedesvessz�t pontra.
	}

}


 