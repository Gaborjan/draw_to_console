/*
 * javalib k�nyvt�r
 * Csomag: extra
 * Console.java
 *
 * Angster Erzs�bet: OO tervez�s �s programoz�s, Java 1. k�tet
 * 2002.09.01.
 *
 * Beolvas�s a konzolr�l:
 *
 * String readLine()
 * String readLine(String str)
 * char readChar()
 * char readChar(String str)
 * int readInt()
 * int readInt(String str)
 * int readLong()
 * int readLong(String str)
 * double readDouble()
 * double readDouble(String str)
 * void pressEnter()
 */

package extra;
import java.io.*;

public class Console {
  // Az osztályból nem lehet példányt létrehozni:
  private Console() {
  }

  /* Pufferező karakterfolyam, melynek forráshelye a konzol.
   * A readLine metódus használja.
   */
  private static BufferedReader be =
     new BufferedReader(new InputStreamReader(System.in));

  // ---------------------------------------------------------
  // String beolvasasa sor végéig:
  public static String readLine() {
    String beString = "";
    try {
      beString = be.readLine();
    }
    catch (IOException e) {
    }
    return beString;
  }

  // ---------------------------------------------------------
  // String beolvasasa sor végéig, előtte prompt:
  public static String readLine(String str) {
    System.out.print(str);
    return readLine();
  }

  // ---------------------------------------------------------
  // Karakter beolvasása:
  public static char readChar() {
    while(true) {
      try {
        return readLine().charAt(0);
      }
      catch(IndexOutOfBoundsException e) {
        System.out.println("Nem karakter! Újra!");
      }
    }
  }

  // ---------------------------------------------------------
  // Karakter beolvasása, előtte prompt:
  public static char readChar(String str) {
    System.out.print(str);
    return readChar();
  }

  // ---------------------------------------------------------
  // Egész (int) beolvasása:
  public static int readInt() {
    while(true) {
      try {
        return Integer.parseInt(readLine().trim());
      }
      catch(NumberFormatException e) {
        System.out.println("Nem egész! Újra!");
      }
    }
  }

  // ---------------------------------------------------------
  // Egész (int) beolvasása, előtte prompt:
  public static int readInt(String str) {
    while(true) {
      System.out.print(str);
      try {
        return Integer.parseInt(readLine().trim());
      }
      catch(NumberFormatException e) {
        System.out.println("Nem egész! Újra!");
      }
    }
  }

  // ---------------------------------------------------------
  // Egész (long) beolvasása:
  public static long readLong() {
    while(true) {
      try {
        return Long.parseLong(readLine().trim());
      }
      catch(NumberFormatException e) {
        System.out.println("Nem egész! Újra!");
      }
    }
  }

  // ---------------------------------------------------------
  // Eg�sz (long) beolvas�sa, el�tte prompt:
  public static long readLong(String str) {
    while(true) {
      System.out.print(str);
      try {
        return Long.parseLong(readLine().trim());
      }
      catch(NumberFormatException e) {
        System.out.println("Nem egesz! Ujra!");
      }
    }
  }

  // ---------------------------------------------------------
  // Valós (double) beolvasása:
  public static double readDouble() {
    while(true) {
      try {
        return Double.parseDouble(readLine().trim());
      }
      catch(NumberFormatException e) {
        System.out.println("Nem valós! Újra!");
      }
    }
  }

  // ---------------------------------------------------------
  // Valós (double) beolvasása, előtte prompt:
  public static double readDouble(String str) {
    while(true) {
      System.out.print(str);
      try {
        return Double.parseDouble(readLine().trim());
      }
      catch(NumberFormatException e) {
        System.out.println("Nem valós! Újra!");
      }
    }
  }

  // ---------------------------------------------------------
  // Várás az ENTER lenyomására:
  public static void pressEnter() {
    System.out.print("<ENTER>");
    readLine();
  }
}
