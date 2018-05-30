import java.io.*;
import java.util.*;

public class Mozi {
	static Terem[] moziTermek;
	public static void main(String[] args) {
		mozi_betolt();
		for (int i=0;i<moziTermek.length;i++)
			moziTermek[i].kiir();
		System.out.println("VÉGE");
		/*int[] terem1Szekek = {10,10,10,15,15,15,15,15,15,25};
		int[] terem2Szekek = {10,10,10,20,20,20,30,30,40,40};
		Terem Terem1 = new Terem(10,terem1Szekek,"JÁVOR PÁL TEREM",1990,"CSILLAGOK HÁBORÚJA I.");
		Terem1.kiir();
		Terem1.foglal(5, 5);
		Terem1.foglal(1, 1);
		Terem1.foglal(1, 10);
		Terem1.foglal(10, 25);
		Terem1.foglal(10, 24);
		Terem1.foglal(10, 1);
		Terem1.kiir();
		Terem1.helytorol(1, 1);
		Terem1.kiir();
		System.out.println(String.format("%,6.0f",Terem1.getBevetel())+" Ft");
		Terem Terem2 = new Terem(10,terem2Szekek,"JOHN WILLIAMS TEREM",1590,"VISSZA A JŐVŐBE III.");
		Terem2.kiir();
		Terem2.foglal(5, 5);
		Terem2.foglal(1, 1);
		Terem2.foglal(1, 10);
		Terem2.foglal(10, 25);
		Terem2.foglal(10, 24);
		Terem2.foglal(10, 1);
		Terem2.kiir();
		Terem2.helytorol(1, 1);
		Terem2.kiir();
		System.out.println(String.format("%,6.0f",Terem2.getBevetel())+" Ft");*/
	}

	static void mozi_betolt() {
		int [][] teremAdatok; //A termek sorainak és oszlopainak tárolása
		try {
			RandomAccessFile fajl;
			String egySor;
			int teremDb=0; //Mozitermek száma
			int t=0; //Hanyadik teremnél járunk?
			String seged[]; //String daraboláshoz segéd tömb
			int teremSorok[]; //Melyik teremben hány sor van?
			int teremSzekek[]; //Ebben a tömbben tároljuk melyik sorban hány szék van
			fajl=new RandomAccessFile("mozi_adatok.csv","r");
			egySor=fajl.readLine(); // Az első magyarázó sor, nincs adat benne
			egySor=fajl.readLine(); // A mozi termeinek számát tartalmazó 2. sor
			seged=egySor.split(";");
			teremDb=Integer.parseInt(seged[0]); // db=a mozitermek száma
			moziTermek= new Terem[teremDb];
			//teremAdatok = new int[teremDb][];
			teremSorok= new int[teremDb]; // Ennyi terem sorait kell tárolnunk
			egySor=fajl.readLine(); //3. sor beolvasása, melyik teremben hány sor van
			seged=egySor.split(";");
			for (int i=0;i<teremDb;i++) {//A termek sorainak számát előállítjuk
				teremSorok[i]=Integer.parseInt(seged[i]);
				//teremAdatok[i]=new int[teremSorok[i]];
			}
			t=0;
			egySor=fajl.readLine();
			while (egySor!=null) {
				seged=egySor.split(";");
				teremSzekek = new int[seged.length-1];
				for (int i=0;i<seged.length-1;i++) 
					teremSzekek[i]=Integer.parseInt(seged[i]);
				moziTermek[t]= new Terem(teremSorok[t],teremSzekek,seged[seged.length-1],1990,"CSILLAGOK HÁBORÚJA I.");
				egySor=fajl.readLine();
				t++;
			}
			fajl.close();
			/*for (int i=0;i<teremDb;i++) {
				for (int j=0;j<teremAdatok[i].length;j++)
					//System.out.print(teremAdatok[i][j]+" ");
					System.out.println();
			}*/
			
		}
		catch (IOException e) {
			System.err.println("Hiba történt a fájlművelet közben!");
		}
	}
}
