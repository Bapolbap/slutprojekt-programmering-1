
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

public class h�ngaGubbe {
	public static String messageForFirstTimeAtCreateStartMenu = "(Default inst�llningen �r att spela med svenska ord)";
	public static String file;
	public static ArrayList<String> wordToGuess = new ArrayList<String>(); //det ordet som anv�ndaren ska gissa p�
	public static ArrayList<String> wordGuessedSoFar = new ArrayList<String>(); //hur mycket av ordet som anv�ndaren har gissat r�tt p� s� l�nge
	public static ArrayList<String> wrongGuesses = new ArrayList<String>(); //de bokst�ver som anv�ndaren har gissat p� som inte finns i wordToGuess
	public static ArrayList<String> guessedLetters = new ArrayList<String>(); //de bokstaver som spelaren har gissat p�
	public static int chosenLang = 1; //g�r defaultinst�llningen till Svenska
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
				"V�lkommen till H�nga Gubbe!\r\n" +
				"Det h�r spelet g�r ut p� att gissa bokst�verna p� ett ok�nt ord, innan n�n random person blir avr�ttad\r\n" +
				"Kom ih�g, om du misslyckas, s� �r du delvis besvarig f�r deras d�d!\r\n" +
				"Vad vill gu g�ra?\r\n");
		//extrameddelandet kommer bara vara relevant den f�rsta g�ngen som spelaren ser den h�r menyn
		System.out.println("1: Starta Spel " + messageForFirstTimeAtCreateStartMenu);
		System.out.println("2: Inst�llnignar\r\n3: Avsluta spel");
		//s� vi "tar bort" meddelandet h�r
		messageForFirstTimeAtCreateStartMenu = "(Du anv�nder samma inst�llningar som i f�rra spelomg�ngen)";
	}
	
	public static void manageStartMenuInput() {
		switch (getUserInputInt(1, 3)) {
		//forts�tt programmet som vanligt, med defaultinst�llningarna
		case 1: break;
		//l�t anv�ndaren �ndra spr�k, om de vill
		case 2: System.out.println("\r\nEfter att du har gjort ditt val, s� kommer du automatiskt att starta spelet");
				createLangMenu();
				break;
		//st�ng ner programmet
		case 3: System.exit(1);
		}
	}
	
	public static void createLangMenu() {
		System.out.println("\r\nVilket spr�k vill du att ditt ord ska ha?\r\n1: Svenska\r\n2: Engelska");
		switch (getUserInputInt(1, 2)) {
		case 1: chosenLang = 1; break;
		case 2: chosenLang = 2; break;
		}
	}
	
	public static int getUserInputInt(int minOption, int maxOption) {
		Scanner sc = new Scanner(System.in);
		//ge userInput ett initialt v�rde s� att den kommer in i while loopen
		int userInput = minOption - 1;
		try {
			while (userInput < minOption || userInput > maxOption) {
				//anv�ndaren skriver in ett v�rde
				userInput = sc.nextInt();
			}
		}
		//om anv�ndaren skriver en int, s� �r allt bra
		//annars, g�r om metoden
		catch(InputMismatchException e) {
			System.out.println("Sn�lla skriv den SIFFRA som motsvarar det valet du vill g�ra");
			//Bj�rn sa att det var d�ligt att anv�nda rekursion i sin kod, s� jag anv�nder rekursion i min kod :sunglasses_emoji:
			//anropa metoden innuti sig sj�lv, f�r att l�ta anv�ndaren f�rs�ka igen om de skrev fel datatyp
			getUserInputInt(minOption, maxOption);
		}
		return userInput;
	}
	
	public static String getUserInputString() {
		Scanner sc = new Scanner(System.in);
		//ge guessedLetter ett initialt v�rde s� att den kommer in i while loopen
		String guessedLetter = "11";
		try {
			while (guessedLetter.length() != 1 || !(guessedLetter.matches("^[a-zA-Z������]*$"))) {
				//l�t anv�ndaren skriva in en String
				guessedLetter = sc.nextLine();
				
				//s�g till anv�ndaren om deras String inte var en bokstav l�ng
				if (guessedLetter.length() != 1) {
					System.out.println("Sn�lla Skriv endast EN bokstav");
				}
				//s�g till anv�ndaren om deras String inneh�ll
				if (!(guessedLetter.matches("^[a-zA-Z������]*$"))) {
					System.out.println("Sn�lla skriv endast en BOKSTAV");
				}
			}
		} catch (Exception e) {
			System.out.println("N�gonting gick fel, sn�lla f�rs�k igen");
			//anropa metoden igen om n�gonting gick fel
			getUserInputString();
		}
		//G�r anv�ndarens gissning till en stor bokstav innan vi returnar den
		//p� s� s�tt s� kommer det inte spela n�gon roll om anv�ndaren skrev in en stor eller liten bokstav
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
			//taget fr�n https://www.educative.io/edpresso/reading-the-nth-line-from-a-file-in-java
			//randomWord blir det ordet som finns i en slumpm�ssigt val rad (fr�n 1 - 100) fr�n file
			randomWord = Files.readAllLines(Paths.get(file)).get(lineFromFile + 1);
		} catch(IOException e) {
			e.printStackTrace();
			System.out.println("N�gonting verkar ha g�tt fel med filerna, whoops");
			randomWord = "whoops";
		}

		//g�r hela Stringen till stora bokst�ver, hj�lper n�r vi ska j�mf�ra med de gissade bokst�verna
		return randomWord.toUpperCase();
	}
	
	public static void initializeWordToGuess(String wordFromFile) {
		//l�gg till en index in i wordToGuess f�r varje bokstav som finns i randomWord
		//fyll den indexen med den respektive bokstaven
		for(int i = 0; i < wordFromFile.length(); i ++) {
			//metoden charAt returnar en char, vi m�ste f�rst omvandla den charen till en string innan vi kan l�gga in den i wordToGuess
			String s = String.valueOf(wordFromFile.charAt(i));
			wordToGuess.add(s);
		}
	}
	
	public static void initializeWordGuessedSoFar(int amountOfIndexesinWordToGuess) {
		//l�gg till en "_ " i wordGuessedSoFar f�r varje index som wordToGuess har
		for(int i = 0; i < amountOfIndexesinWordToGuess; i++) {
			wordGuessedSoFar.add("_ ");
		}
	}
	
	public static void gameLoop() {
		while (lives >= 0) {
			//printa bara currentGameState om spelaren gissade p� en ny bokstav, hj�lper med att g�ra spelfl�det tydligare
			if(!(wasLetterAlreadyGuessed)) {
				drawCurrentGameState();
			}
			//l�t anv�ndaren gissa p� en bokstav, och kolla sedan om den bokstaven fanns i ordet
			compareGuessWithWord(getUserInputString());
			
			if(didUserWin()) {
				break;
			}
		}
		createEndingMessage(didUserWin());
		
	}
	
	public static void drawCurrentGameState() {
		//printa den relevanta h�ngda gubben
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
		//printa ut alla de gissningar som anv�ndaren har f�tt r�tt
		System.out.println("Ordet su ska gissa p� �r: ");
		for(int i = 0; i < wordGuessedSoFar.size(); i ++) {
			System.out.print(wordGuessedSoFar.get(i));
		}
		//System.out.println("\r\n");
		
		//printa ut alla gissningar som spelaren har f�tt fel
		System.out.println("De bokst�ver som inte finns i ordet �r: ");
		for(int i = 0; i < wrongGuesses.size(); i ++) {
			System.out.print(wrongGuesses.get(i));
		}
	}
	
	public static void compareGuessWithWord(String guessedLetter) {
		boolean guessWasInWord = false;
		wasLetterAlreadyGuessed = false;
		
		//kolla om spelaren redan har gissat p� den h�r bokstaven
		for(int i = 0; i < guessedLetters.size(); i++) {
			if(guessedLetter.equals(guessedLetters.get(i))) {
				//om det har gjort det, s� kommer anv�ndaren f� f�rs�ka igen utan att f�rlora n�gra liv
				wasLetterAlreadyGuessed = true;
				System.out.println("Du har redan gissat p� den h�r bokstaven, f�rs�k igen");
				break;
			}
		}
		//l�gg till guessedLetters i en lista som h�ller koll p� alla bokst�ver som spelaren har gissat p�
		guessedLetters.add(guessedLetter);
		
		//k�r endast denna kodbiten om anv�ndaren inte redan har gissat p� bokstaven
		if (!(wasLetterAlreadyGuessed)) {
			for(int i = 0; i < wordToGuess.size(); i++) {
				//kolla om guessedLetter �r lika med n�gon av bokst�verna i wordToGuess
				if (guessedLetter.equals(wordToGuess.get(i))) {
					guessWasInWord = true;
					// om det var det, s� l�gg till guessedLetter i den platsen inom wordGuessedSoFar som motsvarar dess plats i wordToGuess
					wordGuessedSoFar.set(i, guessedLetter + " ");
				}
			}
			//l�gg till guessedLetter i array listan WrongGuesses om den inte fanns i wordToGuess
			if (!guessWasInWord) {
				wrongGuesses.add(guessedLetter + " ");
				lives -= 1;
			}
		}
	}
	
	public static boolean didUserWin() {
		//b�rja med att anta att spelaren har vunnit
		boolean userWon = true;
		String underscore = "_ ";
		//S�k igenom varje index av wordGuessedSoFar, default stringen f�r varje index �r "_ "
		//s� om den stringen inte finns i n�gon av indexerna, s� har anv�ndaren f�tt r�tt p� alla bokst�ver
		for(int i = 0; i < wordGuessedSoFar.size(); i++) {
			if(underscore.equals(wordGuessedSoFar.get(i))) {
				//om n�gon index av wordGuessedSoFar �r "_ ", s�vet vi att spelaren inte har vunnit �n
				userWon = false;
				break;
			}
		}
		
		return userWon;
	}
	
	public static void createEndingMessage(boolean winState) {
		if(winState) {
			//printa det h�r om spelaren vann
			System.out.println("\r\nGrattis, du gjorde det! Personen �r r�ddad, tack vare dig!");
		} else {
			//printta det h�r om spelaren f�rlorade
			System.out.println("Synd... Ditt misslyckande att gissa r�tt p� ett ord har kostat den h�r personen sitt liv\r\n");
		}
		System.out.print("Ordet var: ");
		
		//printa ut varje index av wordToGuess, vilket d� �r hela ordet
		for (int i = 0; i < wordToGuess.size(); i++) {
			System.out.print(wordToGuess.get(i));
		}
	}
	
	public static void createEndingMenu() {
		System.out.println("\r\nVad vill du g�ra nu?\r\n1: Starta om spelet\r\n2: Avsluta programmet");
		switch(getUserInputInt(1, 2)) {
		//k�r om spelet
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