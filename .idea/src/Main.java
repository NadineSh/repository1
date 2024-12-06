

import java.util.Scanner;
import java.util.Random;

interface Dictionary {
    String randomWord();
}

class SimpleDictionary implements Dictionary {
    private final String[] words = {"hello", "world", "java", "hangman", "programming"};

    @Override
    public String randomWord() {
        Random random = new Random();
        return words[random.nextInt(words.length)];
    }
}

class Session {
    private final String answer;
    private final char[] userAnswer;
    private final int maxAttempts;
    private int attempts;

    public Session(String answer, int maxAttempts) {
        this.answer = answer;
        this.userAnswer = new char[answer.length()];
        for (int i = 0; i < userAnswer.length; i++) {
            userAnswer[i] = '*';
        }
        this.maxAttempts = maxAttempts;
        this.attempts = 0;
    }

    public GuessResult guess(char guess) {
        boolean hit = false;
        for (int i = 0; i < answer.length(); i++) {
            if (answer.charAt(i) == guess && userAnswer[i] == '*') {
                userAnswer[i] = guess;
                hit = true;
            }
        }

        if (!hit) {
            attempts++;
        }

        if (String.valueOf(userAnswer).equals(answer)) {
            return new GuessResult.Win(userAnswer, attempts, maxAttempts, "You won!");
        } else if (attempts >= maxAttempts) {
            return new GuessResult.Defeat(userAnswer, attempts, maxAttempts, "You lost! The word was: " + answer);
        } else if (hit) {
            return new GuessResult.SuccessfulGuess(userAnswer, attempts, maxAttempts, "Hit!");
        } else {
            return new GuessResult.FailedGuess(userAnswer, attempts, maxAttempts, "Missed, mistake " + attempts + " out of " + maxAttempts + ".");
        }
    }

    public GuessResult giveUp() {
        return new GuessResult.Defeat(userAnswer, attempts, maxAttempts, "You gave up! The word was: " + answer);
    }

    public char[] getUserAnswer() {
        return userAnswer.clone();
    }
}

interface GuessResult {
    char[] state();
    int attempt();
    int maxAttempts();
    String message();

    record Defeat(char[] state, int attempt, int maxAttempts, String message) implements GuessResult {}
    record Win(char[] state, int attempt, int maxAttempts, String message) implements GuessResult {}
    record SuccessfulGuess(char[] state, int attempt, int maxAttempts, String message) implements GuessResult {}
    record FailedGuess(char[] state, int attempt, int maxAttempts, String message) implements GuessResult {}
}

class ConsoleHangman {
    public void run() {
        Scanner scanner = new Scanner(System.in);
        Dictionary dictionary = new SimpleDictionary();
        String word = dictionary.randomWord();
        Session session = new Session(word, 5);

        System.out.println("Welcome to Hangman!");
        System.out.println("Guess the word:");

        while (true) {
            System.out.println("The word: " + String.valueOf(session.getUserAnswer()));
            System.out.print("Guess a letter: ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("quit")) {
                printState(session.giveUp());
                break;
            } else if (input.length() != 1) {
                System.out.println("Please enter a single letter.");
                continue;
            }

            char guess = input.charAt(0);
            GuessResult result = session.guess(guess);
            printState(result);

            if (result instanceof GuessResult.Win || result instanceof GuessResult.Defeat) {
                break;
            }
        }

        scanner.close();
    }

    private void printState(GuessResult guess) {
        System.out.println(guess.message());
    }

}