package hangman;

import java.util.*;
import java.io.*;

public class EvilHangman {

    public static void main(String[] args) {

        File file = new File(args[0]);
        int wordLength = Integer.parseInt(args[1]);
        int guesses = Integer.parseInt(args[2]);

        EvilHangmanGame game = new EvilHangmanGame();
        boolean answer = false;
        Scanner in = new Scanner(System.in);

        try {
            game.startGame(file, wordLength);
        }
        catch (EmptyDictionaryException ed) {
            System.out.println("File is empty or does not contain any" +
                    "words of length " + wordLength);
        }
        catch (IOException io) { System.out.println("Input Error"); }

        while (guesses > 0) {
            System.out.println("You have " + guesses + " guesses left");
            System.out.println("Used letters: " + game.getGuessedLetters().toString());
            System.out.println("Word: " + game.getWord());
            System.out.print("Enter guess: ");
            char c = in.next().charAt(0);
            if (!Character.isLetter(c)) {
                System.out.println("Invalid input. Please input a letter\n");
                continue;
            }
            System.out.println(c);

            try {
                game.makeGuess(c);
                if (game.correctGuess) {
                    System.out.println("Yes, there is " + game.count(game.currGuess, c) +
                            " " + c + "(s)");
                    if (!game.currGuess.contains("-")) {
                        answer = true;
                        System.out.println("Good job, you got the word. The word is: " +
                                game.currGuess);
                        break;
                    }
                }
                else {
                    System.out.println("Sorry, there are no " + c + "'s");
                    guesses--;
                }

            }
            catch (GuessAlreadyMadeException gm) {
                System.out.println("You already made that guess. Try again\n");
            }
            System.out.println();
        }

        if (!answer) {
            System.out.println("You lose! the word was: " + game.getFinalWord());
        }
    }

}
