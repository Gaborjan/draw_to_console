
public class Mozi {

	public static void main(String[] args) {
		int[] terem1Szekek = {10,10,10,15,15,15,15,15,15,25};
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
		Terem Terem2 = new Terem(10,terem2Szekek,"JOHN WILLIAMS TEREM",1590,"VISSZA A JŐVŐBE II.");
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
		System.out.println(String.format("%,6.0f",Terem2.getBevetel())+" Ft");
	}

}
