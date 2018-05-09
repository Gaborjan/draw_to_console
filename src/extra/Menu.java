package extra;
import extra.Console;

public class Menu {
  // Az osztályból nem lehet példányt létrehozni:
  private Menu() {
  }
  
 public static int egyszeruMenu(String Menupont[],int db) {
	 int valasz=0;
	 for (int i=0;i<db;i++) 
		 System.out.println(Menupont[i]);
	 System.out.println("Válassz:");
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

/*Egy valós számot a megadott tizedesjegyre kerekít.*/
	static double kerekit(double szam,int tizedes)
	{
		String seged, seged1;
		seged=Integer.toString(tizedes); // A tizedest szöveggé alakítjuk, hogy számformátumot tudjunk készíteni.
		seged1=String.format("%."+seged+"f", szam); // A megkapott szám paramétert a megadott formátum szerint megformázzuk, de az eredmény sztring.
		return Double.valueOf(seged1.replace(',','.')); // Visszaadjuk a megformázott számot számmá alakítva, de ki kell benne cserélni a tizedesvesszõt pontra.
	}

}


 