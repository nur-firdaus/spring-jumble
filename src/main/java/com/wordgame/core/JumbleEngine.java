package com.wordgame.core;

import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
public class JumbleEngine {

    private static final Logger log = LoggerFactory.getLogger(JumbleEngine.class);

    @Value("${jumble.engine.words.txt.path}")
    private String wordsPath;


    /**
     * From the input `word`, produces/generates a copy which has the same
     * letters, but in different ordering.
     *
     * Example: from "elephant" to "aeehlnpt".
     *
     * Evaluation/Grading:
     * a) pass unit test: JumbleEngineTest#scramble()
     * b) scrambled letters/output must not be the same as input
     *
     * @param word  The input word to scramble the letters.
     * @return  The scrambled output/letters.
     */
    public String scramble(String word) {
        /*
         * Refer to the method's Javadoc (above) and implement accordingly.
         * Must pass the corresponding unit tests.
         */
        if (word == null || word.isEmpty()) {
            return null; //to avoid null pointer error
        }

        // Convert the input word to a char array
        char[] letters = word.toCharArray();

        // Shuffle the letters
        List<Character> letterList = new ArrayList<>();
        for (char c : letters) {
            letterList.add(c);
        }

        // Continue shuffling until the scrambled word is different from the input word
        while (true) {
            Collections.shuffle(letterList);

            // Convert the shuffled list back to a string
            StringBuilder scrambled = new StringBuilder();
            for (char c : letterList) {
                scrambled.append(c);
            }

            // Ensure the scrambled word is different from the input word
            if (!scrambled.toString().equals(word)) {
                return scrambled.toString();
            }
        }
    }

    /**
     * Retrieves the palindrome words from the internal
     * word list/dictionary ("src/main/resources/words.txt").
     *
     * Word of single letter is not considered as valid palindrome word.
     *
     * Examples: "eye", "deed", "level".
     *
     * Evaluation/Grading:
     * a) able to access/use resource from classpath
     * b) using inbuilt Collections
     * c) using "try-with-resources" functionality/statement
     * d) pass unit test: JumbleEngineTest#palindrome()
     *
     * @return  The list of palindrome words found in system/engine.
     * @see https://www.google.com/search?q=palindrome+meaning
     */
    public Collection<String> retrievePalindromeWords() {
        /*
         * Refer to the method's Javadoc (above) and implement accordingly.
         * Must pass the corresponding unit tests.
         */

        List<String> words = loadWords();
        List<String> palindromeWords = new ArrayList<>();
        try{
            for(String word:words){
                if (word.length() > 1 && isPalindrome(word)) {
                    palindromeWords.add(word);
                }
            }
        } catch (Exception e) {
            log.error("error on retrievePalindromeWords {}",e.getMessage());
        }

        return palindromeWords;
    }

    /**
     * Picks one word randomly from internal word list.
     *
     * Evaluation/Grading:
     * a) pass unit test: JumbleEngineTest#randomWord()
     * b) provide a good enough implementation, if not able to provide a fast lookup
     * c) bonus points, if able to implement a fast lookup/scheme
     *
     * @param length  The word picked, must of length.
     * @return  One of the word (randomly) from word list.
     *          Or null if none matching.
     */
    public String pickOneRandomWord(Integer length) {
        /*
         * Refer to the method's Javadoc (above) and implement accordingly.
         * Must pass the corresponding unit tests.
         */

        //simplest ways is to find direct the string inside the words.txt

        List<String> words = loadWords();
        if(length==null){
            return "null";
        }
        try{
            for(String word:words){
                if (word.length() == length) {
                    return word;
                }
            }
        } catch (Exception e) {
            log.error("error on pickOneRandomWord {}",e.getMessage());
        }
        return null;
    }

    /**
     * Checks if the `word` exists in internal word list.
     * Matching is case insensitive.
     *
     * Evaluation/Grading:
     * a) pass related unit tests in "JumbleEngineTest"
     * b) provide a good enough implementation, if not able to provide a fast lookup
     * c) bonus points, if able to implement a fast lookup/scheme
     *
     * @param word  The input word to check.
     * @return  true if `word` exists in internal word list.
     */
    public boolean exists(String word) {
        /*
         * Refer to the method's Javadoc (above) and implement accordingly.
         * Must pass the corresponding unit tests.
         */
        if (word == null || word.isEmpty()) {
            return false;
        }

        List<String> words = loadWords();
        // Case-insensitive lookup
        return words.contains(word.toLowerCase());
    }

    /**
     * Finds all the words from internal word list which begins with the
     * input `prefix`.
     * Matching is case insensitive.
     *
     * Invalid `prefix` (null, empty string, blank string, non letter) will
     * return empty list.
     *
     * Evaluation/Grading:
     * a) pass related unit tests in "JumbleEngineTest"
     * b) provide a good enough implementation, if not able to provide a fast lookup
     * c) bonus points, if able to implement a fast lookup/scheme
     *
     * @param prefix  The prefix to match.
     * @return  The list of words matching the prefix.
     */
    public Collection<String> wordsMatchingPrefix(String prefix) {
        /*
         * Refer to the method's Javadoc (above) and implement accordingly.
         * Must pass the corresponding unit tests.
         */
        List<String> words = loadWords();
        // Return an empty list for invalid prefix (null, empty, or non-letter)
        if (prefix == null || prefix.trim().isEmpty() || !prefix.matches("[a-zA-Z]+")) {
            return Collections.emptyList();
        }

        // Convert the prefix to lowercase for case-insensitive matching
        String lowerPrefix = prefix.toLowerCase();

        // Filter words that start with the given prefix
        List<String> matchingWords = new ArrayList<>();
        for (String word : words) {
            if (word.startsWith(lowerPrefix)) {
                matchingWords.add(word);
            }
        }

        return matchingWords;
    }

    /**
     * Finds all the words from internal word list that is matching
     * the searching criteria.
     *
     * `startChar` and `endChar` must be 'a' to 'z' only. And case insensitive.
     * `length`, if have value, must be positive integer (>= 1).
     *
     * Words are filtered using `startChar` and `endChar` first.
     * Then apply `length` on the result, to produce the final output.
     *
     * Must have at least one valid value out of 3 inputs
     * (`startChar`, `endChar`, `length`) to proceed with searching.
     * Otherwise, return empty list.
     *
     * Evaluation/Grading:
     * a) pass related unit tests in "JumbleEngineTest"
     * b) provide a good enough implementation, if not able to provide a fast lookup
     * c) bonus points, if able to implement a fast lookup/scheme
     *
     * @param startChar  The first character of the word to search for.
     * @param endChar    The last character of the word to match with.
     * @param length     The length of the word to match.
     * @return  The list of words matching the searching criteria.
     */
    public Collection<String> searchWords(Character startChar, Character endChar, Integer length) {
        /*
         * Refer to the method's Javadoc (above) and implement accordingly.
         * Must pass the corresponding unit tests.
         */
        // Validate input: at least one of the inputs should be valid
        if ((startChar == null || !isValidChar(startChar)) &&
                (endChar == null || !isValidChar(endChar)) &&
                (length == null || length <= 0)) {
            return Collections.emptyList();
        }

        // Convert characters to lowercase for case-insensitive matching
        Character lowerStartChar = startChar != null ? Character.toLowerCase(startChar) : null;
        Character lowerEndChar = endChar != null ? Character.toLowerCase(endChar) : null;

        return getStrings(length, lowerStartChar, lowerEndChar);
    }

    private List<String> getStrings(Integer length, Character lowerStartChar, Character lowerEndChar) {

        List<String> words = loadWords();
        List<String> matchingWords = new ArrayList<>();
        for (String word : words) {
            boolean matchesStart = (lowerStartChar == null || word.charAt(0) == lowerStartChar);
            boolean matchesEnd = (lowerEndChar == null || word.charAt(word.length() - 1) == lowerEndChar);
            boolean matchesLength = (length == null || word.length() == length);

            if (matchesStart && matchesEnd && matchesLength) {
                matchingWords.add(word);
            }
        }
        return matchingWords;
    }

    /**
     * Generates all possible combinations of smaller/sub words using the
     * letters from input word.
     *
     * The `minLength` set the minimum length of sub word that is considered
     * as acceptable word.
     *
     * If length of input `word` is less than `minLength`, then return empty list.
     *
     * Example: From "yellow" and `minLength` = 3, the output sub words:
     *     low, lowly, lye, ole, owe, owl, well, welly, woe, yell, yeow, yew, yowl
     *
     * Evaluation/Grading:
     * a) pass related unit tests in "JumbleEngineTest"
     * b) provide a good enough implementation, if not able to provide a fast lookup
     * c) bonus points, if able to implement a fast lookup/scheme
     *
     * @param word       The input word to use as base/seed.
     * @param minLength  The minimum length (inclusive) of sub words.
     *                   Expects positive integer.
     *                   Default is 3.
     * @return  The list of sub words constructed from input `word`.
     */
    public Collection<String> generateSubWords(String word, Integer minLength) {
        /*
         * Refer to the method's Javadoc (above) and implement accordingly.
         * Must pass the corresponding unit tests.
         */

        // Set default minLength to 3 if it's null
        if (minLength == null) {
            minLength = 3;  // Default minimum length
        }
        // Validate inputs
        if (word == null || word.isEmpty() || minLength < 1 || word.length() < minLength || word.length() == minLength) {
            return Collections.emptyList();
        }

        List<String> words = loadWords();
        return getStrings(word, minLength, words);
    }

    private static List<String> getStrings(String word, Integer minLength, List<String> words) {

        word=word.toLowerCase();
        Set<String> uniqueWords = new HashSet<>(words);

        List<String> matchingWords = new ArrayList<>();
        for (String candidate : uniqueWords) {
            if(!candidate.equals(word)) {
                if (candidate.length() >= minLength && isCharacterMatchWithFrequency(word, candidate.toLowerCase())) {
                    matchingWords.add(candidate);
                }
            }
        }
        log.info("validSubWords {}",matchingWords);
        return matchingWords;
    }

    public static boolean isCharacterMatchWithFrequency(String word, String candidate) {

        HashMap<Character, Integer> wordCharCount = getCharFrequency(word);

        for (char c : candidate.toCharArray()) {
            if (!wordCharCount.containsKey(c) || wordCharCount.get(c) == 0) {
                return false; // Character not present or used too many times
            }
            wordCharCount.put(c, wordCharCount.get(c) - 1); // Decrease the frequency count
        }
        return true;
    }

    public static HashMap<Character, Integer> getCharFrequency(String word) {
        HashMap<Character, Integer> charCount = new HashMap<>();
        for (char c : word.toCharArray()) {
            charCount.put(c, charCount.getOrDefault(c, 0) + 1);
        }
        return charCount;
    }

    /**
     * Recursively generates combinations of letters to form words of a given length.
     *
     * @param subWords   The set to store generated sub-words.
     * @param letters    The array of letters to use.
     * @param current    The current combination being constructed.
     * @param length     The target length of the sub-word.
     * @param index      The current index in the letter array.
     */
    private void generateCombinations(Set<String> subWords, char[] letters, String current, int length, int index) {
        if (current.length() == length) {
            subWords.add(current);  // If the current combination matches the target length, add it
            return;
        }

        for (int i = index; i < letters.length; i++) {
            generateCombinations(subWords, letters, current + letters[i], length, i + 1);  // Recursive call
        }
    }

    /**
     * Creates a game state with word to guess, scrambled letters, and
     * possible combinations of words.
     *
     * Word is of length 6 characters.
     * The minimum length of sub words is of length 3 characters.
     *
     * @param length     The length of selected word.
     *                   Expects >= 3.
     * @param minLength  The minimum length (inclusive) of sub words.
     *                   Expects positive integer.
     *                   Default is 3.
     * @return  The game state.
     */
    public GameState createGameState(Integer length, Integer minLength) {
        Objects.requireNonNull(length, "length must not be null");
        if (minLength == null) {
            minLength = 3;
        } else if (minLength <= 0) {
            throw new IllegalArgumentException("Invalid minLength=[" + minLength + "], expect positive integer");
        }
        if (length < 3) {
            throw new IllegalArgumentException("Invalid length=[" + length + "], expect greater than or equals 3");
        }
        if (minLength > length) {
            throw new IllegalArgumentException("Expect minLength=[" + minLength + "] greater than length=[" + length + "]");
        }
        String original = this.pickOneRandomWord(length);
        if (original == null) {
            throw new IllegalArgumentException("Cannot find valid word to create game state");
        }
        String scramble = this.scramble(original);
        Map<String, Boolean> subWords = new TreeMap<>();
        for (String subWord : this.generateSubWords(original, minLength)) {
            subWords.put(subWord, Boolean.FALSE);
        }
        return new GameState(original, scramble, subWords);
    }

    /**
     * * Loads the words from "src/main/resources/words.txt".
     */
    public List<String> loadWords() {
        List<String> words = new ArrayList<>();
        Path path = Paths.get(wordsPath);

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String word;
            while ((word = reader.readLine()) != null) {
                words.add(word.trim());
            }
        } catch (IOException e) {
            log.error("error on function loadWords{}",e.getMessage());
        }
        return words;
    }

    /**
     * Util method to check if a word is a palindrome.
     *
     * @param word The input word to check.
     * @return true if the word is a palindrome, false otherwise.
     */
    public boolean isPalindrome(String word) {
        try {
            int len = word.length();
            for (int i = 0; i < len / 2; i++) {
                if (word.charAt(i) != word.charAt(len - i - 1)) {
                    return false;
                }
            }
            return true;
        }catch (Exception e){
            log.error("error on function isPalindrome{}",e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a character is a valid letter ('a' to 'z').
     */
    public boolean isValidChar(Character c) {
        return c != null && Character.isLetter(c);
    }

}
