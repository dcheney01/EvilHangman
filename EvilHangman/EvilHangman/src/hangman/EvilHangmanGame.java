package hangman;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Scanner;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame {
    HashMap<String, SortedSet<String>> wordMap = new HashMap<>() {
        @Override
        public SortedSet<String> get(Object key) {
            SortedSet<String> set = super.get(key);
            if (set == null) {
                set = new TreeSet<>();
            }
            put((String) key, set);
            return set;
        }
    };


    SortedSet<Character> guessedLetters = new TreeSet<>();
    String currGuess = "";
    boolean correctGuess;


    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        guessedLetters.clear();
        wordMap.clear();
        Scanner scanner = new Scanner(dictionary);
        scanner.useDelimiter("[\\s,]+");
        SortedSet<String> temp = new TreeSet<>();

        while (scanner.hasNext()) { //get all of the words that match from the file
            String next = scanner.next();
            if (next.length() == wordLength) {
                temp.add(next.toLowerCase());
            }
        }
        if (temp.isEmpty()) { throw new EmptyDictionaryException(); }
        currGuess = new String(new char[wordLength]).replace('\0','-');
        wordMap.put(currGuess,temp);
    }


    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        if(guessedLetters.contains(Character.toLowerCase(guess))) { throw new GuessAlreadyMadeException(); }
        guessedLetters.add(Character.toLowerCase(guess));
        SortedSet<String> temp = wordMap.get(currGuess);
        wordMap.clear();
        for (String str : temp) {
            StringBuilder tempKey = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                if (guessedLetters.contains(str.charAt(i))) {
                    tempKey.append(str.charAt(i));
                } else {
                    tempKey.append('-');
                }

            }
            wordMap.get(tempKey.toString()).add(str);
        }
        String prevGuess = currGuess;
        currGuess = getSurvivor(guess);
        correctGuess = !currGuess.equals(prevGuess);
        clearMap(currGuess);
        return wordMap.get(currGuess);
    }

    public String getSurvivor(char guess) { //return key of the survivor
        if (getLargest().size() == 1) {
            return getLargest().first();
        }

        SortedSet<String> temp = new TreeSet<>();
        int maxCount = 0; //count the max -
        for (String key : wordMap.keySet()) {
            if (!key.contains(Character.toString(guess))) {
                return key;
            } else {
                if (count(key, '-') > maxCount) {
                    maxCount = count(key, '-');
                    temp.clear();
                    temp.add(key);
                } else if (count(key, '-') == maxCount) {
                    temp.add(key);
                }
            }
        }
        if (temp.size() == 1) {
            return temp.first();
        }

        //rightmost

        for (int i = currGuess.length()-1; i > 0; i--) {
            temp.clear();
            for (String key : wordMap.keySet()) {
                if (key.charAt(i) == guess) {
                    temp.add(key);
                }

            }
            if (temp.size() == 1) { return temp.first(); }
        }

        return null;
    }

    public SortedSet<String> getLargest() {
        int maxCount = 0;
        SortedSet<String> temp = new TreeSet<>();

        for (String key : wordMap.keySet()) {
            if (wordMap.get(key).size() > maxCount) {
                maxCount = wordMap.get(key).size();
                temp.clear();
                temp.add(key);
            }
            else if (wordMap.get(key).size() == maxCount) {
                temp.add(key);
            }
        }
        return temp;
    }

    public void clearMap(String key) {
        SortedSet<String> temp = wordMap.get(key);
        wordMap.clear();
        wordMap.put(key, temp);
    }

    public int count(String str, char c) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }

    public String getWord() { return currGuess; }

    public String getFinalWord() { return wordMap.get(currGuess).first(); }

    @Override
    public SortedSet<Character> getGuessedLetters() {
        return guessedLetters;
    }
}
