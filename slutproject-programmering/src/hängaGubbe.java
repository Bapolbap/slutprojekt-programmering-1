
/**
 * @author pablo.jofre
 *
 */
import java.util.Scanner;
import java.util.Random;
import java.util.InputMismatchException;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.*;

public class hängaGubbe {
	public static String messageForFirstTimeAtCreateStartMenu = "(Default inställningen är att spela med svenska ord)";
	public static String file;
	public static ArrayList<String> wordToGuess = new ArrayList<String>(); //det ordet som användaren ska gissa på
	public static ArrayList<String> wordGuessedSoFar = new ArrayList<String>(); //hur mycket av ordet som användaren har gissat rätt på så länge
	public static ArrayList<String> wrongGuesses = new ArrayList<String>(); //de bokstäver som användaren har gissat på som inte finns i wordToGuess
	public static ArrayList<String> guessedLetters = new ArrayList<String>(); //de bokstaver som spelaren har gissat på
	public static int chosenLang = 1; //gör defaultinställningen till Svenska
	public static int lives = 6; //antal liv innan game over
	public static boolean wasLetterAlreadyGuessed = false;

	public static void main(String[] args) {
		createStartMenu();
		manageStartMenuInput();
		chooseFile();
		initializeWordToGuess(getRandomWordFromFile());
		initializeWordGuessedSoFar(wordToGuess.size());
		gameLoop();
		createEndingMenu();
		}

	public static void createStartMenu() {
		System.out.println("\r\n" + 
				"                                                \r\n" + 
				" _____ 0 0                _____     _   _       \r\n" + 
				"|  |  |___ ___ ___ ___   |   __|_ _| |_| |_ ___ \r\n" + 
				"|     | .'|   | . | .'|  |  |  | | | . | . | -_|\r\n" + 
				"|__|__|__,|_|_|_  |__,|  |_____|___|___|___|___|\r\n" + 
				"              |___|                             \r\n" + 
				"\r\n" +
				"Välkommen till Hänga Gubbe!\r\n" +
				"Det här spelet går ut på att gissa bokstäverna på ett okänt ord, innan nån random person blir avrättad\r\n" +
				"Kom ihåg, om du misslyckas, så är du delvis besvarig för deras död!\r\n" +
				"Vad vill gu göra?\r\n");
		//extrameddelandet kommer bara vara relevant den första gången som spelaren ser den här menyn
		System.out.println("1: Starta Spel " + messageForFirstTimeAtCreateStartMenu);
		System.out.println("2: Inställnignar\r\n3: Avsluta spel");
		//så vi "tar bort" meddelandet här
		messageForFirstTimeAtCreateStartMenu = "(Du använder samma inställningar som i förra spelomgången)";
	}
	
	public static void manageStartMenuInput() {
		switch (getUserInputInt(1, 3)) {
		//fortsätt programmet som vanligt, med defaultinställningarna
		case 1: break;
		//låt användaren ändra språk, om de vill
		case 2: System.out.println("\r\nEfter att du har gjort ditt val, så kommer du automatiskt att starta spelet");
				createLangMenu();
				break;
		//stäng ner programmet
		case 3: System.exit(1);
		}
	}
	
	public static void createLangMenu() {
		System.out.println("\r\nVilket språk vill du att ditt ord ska ha?\r\n1: Svenska\r\n2: Engelska");
		switch (getUserInputInt(1, 2)) {
		case 1: chosenLang = 1; break;
		case 2: chosenLang = 2; break;
		}
	}
	
	public static int getUserInputInt(int minOption, int maxOption) {
		Scanner sc = new Scanner(System.in);
		//ge userInput ett initialt värde så att den kommer in i while loopen
		int userInput = minOption - 1;
		try {
			while (userInput < minOption || userInput > maxOption) {
				//användaren skriver in ett värde
				userInput = sc.nextInt();
			}
		}
		//om användaren skriver en int, så är allt bra
		//annars, gör om metoden
		catch(InputMismatchException e) {
			System.out.println("Snälla skriv den SIFFRA som motsvarar det valet du vill göra");
			//Björn sa att det var dåligt att använda rekursion i sin kod, så jag använder rekursion i min kod :sunglasses_emoji:
			//anropa metoden innuti sig själv, för att låta användaren försöka igen om de skrev fel datatyp
			getUserInputInt(minOption, maxOption);
		}
		return userInput;
	}
	
	public static String getUserInputString() {
		Scanner sc = new Scanner(System.in);
		//ge guessedLetter ett initialt värde så att den kommer in i while loopen
		String guessedLetter = "11";
		try {
			while (guessedLetter.length() != 1 || !(guessedLetter.matches("^[a-zA-ZåäöÅÄÖ]*$"))) {
				//låt användaren skriva in en String
				guessedLetter = sc.nextLine();
				
				//säg till användaren om deras String inte var en bokstav lång
				if (guessedLetter.length() != 1) {
					System.out.println("Snälla Skriv endast EN bokstav");
				}
				//säg till användaren om deras String innehöll
				if (!(guessedLetter.matches("^[a-zA-ZåäöÅÄÖ]*$"))) {
					System.out.println("Snälla skriv endast en BOKSTAV");
				}
			}
		} catch (Exception e) {
			System.out.println("Någonting gick fel, snälla försök igen");
			//anropa metoden igen om någonting gick fel
			getUserInputString();
		}
		//Gör användarens gissning till en stor bokstav innan vi returnar den
		//på så sätt så kommer det inte spela någon roll om användaren skrev in en stor eller liten bokstav
		return guessedLetter.toUpperCase();
	}

	public static void chooseFile() {
		switch (chosenLang) {
		case 1: file = "sweWords.txt"; break;
		case 2: file = "engWords.txt"; break;
		}
	}
	
	public static String getRandomWordFromFile() {
		Random rand = new Random();
		int lineFromFile = rand.nextInt(99);
		String randomWord = "";
		try {
			//taget från https://www.educative.io/edpresso/reading-the-nth-line-from-a-file-in-java
			//randomWord blir det ordet som finns i en slumpmässigt val rad (från 1 - 100) från file
			randomWord = Files.readAllLines(Paths.get(file)).get(lineFromFile + 1);
		} catch(IOException e) {
			e.printStackTrace();
			System.out.println("Någonting verkar ha gått fel med filerna, whoops");
			randomWord = "whoops";
		}

		//gör hela Stringen till stora bokstäver, hjälper när vi ska jämföra med de gissade bokstäverna
		return randomWord.toUpperCase();
	}
	
	public static void initializeWordToGuess(String wordFromFile) {
		//lägg till en index in i wordToGuess för varje bokstav som finns i randomWord
		//fyll den indexen med den respektive bokstaven
		for(int i = 0; i < wordFromFile.length(); i ++) {
			//metoden charAt returnar en char, vi måste först omvandla den charen till en string innan vi kan lägga in den i wordToGuess
			String s = String.valueOf(wordFromFile.charAt(i));
			wordToGuess.add(s);
		}
	}
	
	public static void initializeWordGuessedSoFar(int amountOfIndexesinWordToGuess) {
		//lägg till en "_ " i wordGuessedSoFar för varje index som wordToGuess har
		for(int i = 0; i < amountOfIndexesinWordToGuess; i++) {
			wordGuessedSoFar.add("_ ");
		}
	}
	
	public static void gameLoop() {
		while (lives >= 0) {
			//printa bara currentGameState om spelaren gissade på en ny bokstav, hjälper med att göra spelflödet tydligare
			if(!(wasLetterAlreadyGuessed)) {
				drawCurrentGameState();
			}
			//låt användaren gissa på en bokstav, och kolla sedan om den bokstaven fanns i ordet
			compareGuessWithWord(getUserInputString());
			
			if(didUserWin()) {
				break;
			}
		}
		createEndingMessage(didUserWin());
		
	}
	
	public static void drawCurrentGameState() {
		//printa den relevanta hängda gubben
		switch(lives) {
		case 6: System.out.println(	"  +---+\r\n" + 
									"  |   |\r\n" + 
									"      |\r\n" + 
									"      |\r\n" + 
									"      |\r\n" + 
									"      |\r\n" + 
									"=========");
			break;
		case 5: System.out.println(	"  +---+\r\n" + 
									"  |   |\r\n" + 
									"  O   |\r\n" + 
									"      |\r\n" + 
									"      |\r\n" + 
									"      |\r\n" + 
									"=========");
			break;
		case 4: System.out.println(	"  +---+\r\n" + 
									"  |   |\r\n" + 
									"  O   |\r\n" + 
									"  |   |\r\n" + 
									"      |\r\n" + 
									"      |\r\n" + 
									"=========");
			break;
		case 3: System.out.println(	"  +---+\r\n" + 
									"  |   |\r\n" + 
									"  O   |\r\n" + 
									" /|   |\r\n" + 
									"      |\r\n" + 
									"      |\r\n" + 
									"=========");
			break;
		case 2: System.out.println(	"  +---+\r\n" + 
									"  |   |\r\n" + 
									"  O   |\r\n" + 
									" /|\\  |\r\n" + 
									"      |\r\n" + 
									"      |\r\n" + 
									"=========");
			break;
		case 1: System.out.println(	"  +---+\r\n" + 
									"  |   |\r\n" + 
									"  O   |\r\n" + 
									" /|\\  |\r\n" + 
									" /    |\r\n" + 
									"      |\r\n" + 
									"=========");
			break;
		case 0: System.out.println(	"  +---+\r\n" + 
									"  |   |\r\n" + 
									"  O   |\r\n" + 
									" /|\\  |\r\n" + 
									" / \\  |\r\n" + 
									"      |\r\n" + 
									"=========");
			break;
		}
		//printa ut alla de gissningar som användaren har fått rätt
		System.out.println("Ordet su ska gissa på är: ");
		for(int i = 0; i < wordGuessedSoFar.size(); i ++) {
			System.out.print(wordGuessedSoFar.get(i));
		}
		//System.out.println("\r\n");
		
		//printa ut alla gissningar som spelaren har fått fel
		System.out.println("De bokstäver som inte finns i ordet är: ");
		for(int i = 0; i < wrongGuesses.size(); i ++) {
			System.out.print(wrongGuesses.get(i));
		}
	}
	
	public static void compareGuessWithWord(String guessedLetter) {
		boolean guessWasInWord = false;
		wasLetterAlreadyGuessed = false;
		
		//kolla om spelaren redan har gissat på den här bokstaven
		for(int i = 0; i < guessedLetters.size(); i++) {
			if(guessedLetter.equals(guessedLetters.get(i))) {
				//om det har gjort det, så kommer användaren få försöka igen utan att förlora några liv
				wasLetterAlreadyGuessed = true;
				System.out.println("Du har redan gissat på den här bokstaven, försök igen");
				break;
			}
		}
		//lägg till guessedLetters i en lista som håller koll på alla bokstäver som spelaren har gissat på
		guessedLetters.add(guessedLetter);
		
		//kör endast denna kodbiten om användaren inte redan har gissat på bokstaven
		if (!(wasLetterAlreadyGuessed)) {
			for(int i = 0; i < wordToGuess.size(); i++) {
				//kolla om guessedLetter är lika med någon av bokstäverna i wordToGuess
				if (guessedLetter.equals(wordToGuess.get(i))) {
					guessWasInWord = true;
					// om det var det, så lägg till guessedLetter i den platsen inom wordGuessedSoFar som motsvarar dess plats i wordToGuess
					wordGuessedSoFar.set(i, guessedLetter + " ");
				}
			}
			//lägg till guessedLetter i array listan WrongGuesses om den inte fanns i wordToGuess
			if (!guessWasInWord) {
				wrongGuesses.add(guessedLetter + " ");
				lives -= 1;
			}
		}
	}
	
	public static boolean didUserWin() {
		//börja med att anta att spelaren har vunnit
		boolean userWon = true;
		String underscore = "_ ";
		//Sök igenom varje index av wordGuessedSoFar, default stringen för varje index är "_ "
		//så om den stringen inte finns i någon av indexerna, så har användaren fått rätt på alla bokstäver
		for(int i = 0; i < wordGuessedSoFar.size(); i++) {
			if(underscore.equals(wordGuessedSoFar.get(i))) {
				//om någon index av wordGuessedSoFar är "_ ", såvet vi att spelaren inte har vunnit än
				userWon = false;
				break;
			}
		}
		
		return userWon;
	}
	
	public static void createEndingMessage(boolean winState) {
		if(winState) {
			//printa det här om spelaren vann
			System.out.println("\r\nGrattis, du gjorde det! Personen är räddad, tack vare dig!");
		} else {
			//printta det här om spelaren förlorade
			System.out.println("Synd... Ditt misslyckande att gissa rätt på ett ord har kostat den här personen sitt liv\r\n");
		}
		System.out.print("Ordet var: ");
		
		//printa ut varje index av wordToGuess, vilket då är hela ordet
		for (int i = 0; i < wordToGuess.size(); i++) {
			System.out.print(wordToGuess.get(i));
		}
	}
	
	public static void createEndingMenu() {
		System.out.println("\r\nVad vill du göra nu?\r\n1: Starta om spelet\r\n2: Avsluta programmet");
		switch(getUserInputInt(1, 2)) {
		//kör om spelet
		case 1: resetGlobalVariables();
				main(null);
				break;
		//avsluta programmet
		case 2: System.exit(1);
		}
	}
	
	public static void resetGlobalVariables() {
		wordToGuess.clear();
		wordGuessedSoFar.clear();
		guessedLetters.clear();
		wrongGuesses.clear();
		lives = 6;
	}
}