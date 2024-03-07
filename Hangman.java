package Hangman;


import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import static java.lang.String.valueOf;


public class Hangman {
    private static Random random = new Random();
    private static Scanner scanner = new Scanner(System.in);

    private static int ROW_COUNT = 5;
    private static int COL_COUNT = 5;

    private static String HEAD = "O";
    private static String BODY = "|";
    private static String RIGHT_HAND = "/";
    private static String LEFT_HAND = "\\";
    private static String RIGHT_LEG = "\\";
    private static String LEFT_LEG = "/";
    private static String GAME_OVER = "Игра закончена";
    private static String YOU_WIN = "Вы победили";
    private static String GAME_STATE_NOT_FINISHED = "Игра не закончена";

    public static void main(String[] args) throws IOException {
        boolean isInputUserStartGame = true;
        do {
            System.out.println("Введите Y для начала новой игры, для выхода из приложения введите N");
            String inputUser = scanner.next().toUpperCase();
            if (inputUser.matches("[Y,N]")) {
                if (inputUser.equals("Y")) {
                    isInputUserStartGame = false;
                    startGameRound();
                } else {
                    System.out.println("Вы вышли из игры");

                }
            }
        } while (!isInputUserStartGame);
    }


    public static void startGameRound() throws IOException {
        System.out.println("Начало нового игры");
        char[][] board = createBoard();
        String word = getWord();
        StringBuilder cellWord = createCellWord(word);
        gameLoop(board, cellWord, word);
    }

    public static char[][] createBoard() {
        char[][] board = new char[ROW_COUNT][COL_COUNT];
        for (int row = 0; row < ROW_COUNT; row++) {
            for (int col = 0; col < COL_COUNT; col++) {
                if (row == 0 & col > 0 & col < COL_COUNT - 1) {
                    board[row][col] = '-';
                } else if (row > 0 && col == 1) {
                    board[row][col] = '|';
                } else {
                    board[row][col] = ' ';
                }
            }
        }
        return board;
    }

    public static String getWord() throws IOException {
        ArrayList<String> list = new ArrayList<>();
        int randomIndex = 0;
        try {
            File file = new File("src/Hangman/resources/dictionary.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            list.add(line);
            while (line != null) {
                line = reader.readLine();
                if (line != null) {
                    list.add(line);
                }
            }
            randomIndex = random.nextInt(list.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list.get(randomIndex);
    }

    public static StringBuilder createCellWord(String word) {
        StringBuilder display = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            display.append("*");
        }


        return display;
    }

    public static void gameLoop(char[][] board, StringBuilder cellWord, String word) throws IOException {
        int counterMistakes = 0;
        do {
            String letter = getLetter(cellWord);
            if (isLetterInWord(word, letter)) {
                openLetter(cellWord, letter, word);
                printCellWord(cellWord);
                printBoard(board);
                System.out.printf("Количество ошибок %d" + "\n", counterMistakes);
            } else {
                counterMistakes++;

                drawBodyPart(board, counterMistakes);
                System.out.printf("Количество ошибок %d" + "\n", counterMistakes);
                printBoard(board);
                printCellWord(cellWord);

            }
            String gameState = checkGameState(board, cellWord, counterMistakes);
            if (!gameState.equals(GAME_STATE_NOT_FINISHED)) {
                System.out.println(gameState);
                return;
            }
        } while (true);
    }

    public static String getLetter(StringBuilder cellWord) {
        System.out.println("Введите букву");
        do {
            String inputLatter = scanner.next().toUpperCase();
            boolean correctInput = inputLatter.matches("[а-я]");
            for (int i = 0; i < cellWord.length(); i++) {
                String letter = cellWord.substring(i);
                if (letter.equals(inputLatter) && correctInput) {
                    System.out.println("Вы ввели букву, которую отгадали ранее, введите другую букву");
                    break;
                }
            }
            if (correctInput) {
                return inputLatter;
            } else {
                System.out.println("Не корректный ввод, введите букву от а до я");
            }
        } while (true);

    }

    public static boolean isLetterInWord(String word, String letter) {
        char[] chars = word.toCharArray();
        for (int i = 0; i < word.length(); i++) {
            if (chars[i] == letter.charAt(0)) {
                return true;
            }
        }
        return false;
    }

    public static void showIncorrectInput(String letter) {

    }

    public static void openLetter(StringBuilder cellWord, String letter, String word) {
        ArrayList<Integer> indexLetter = getIndexSymbolWord(word, letter); // получить индексы отгаданных букв
        for (int i : indexLetter) {
            for (int j = 0; j < cellWord.length(); j++) { // дойти до полученного индекса
                if (j == indexLetter.get(i)) { // если дошли до полученного индекса
                    cellWord.insert(j, letter); // заменить подстроку по индексу j в cellWord на букву letter
                }
            }
        }
    }

    public static ArrayList<Integer> getIndexSymbolWord(String word, String letter) {
        ArrayList<Integer> listIndexLetter = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == letter.charAt(0)) {
                listIndexLetter.add(i);
            }
        }
        return listIndexLetter;
    }

    public static void printCellWord(StringBuilder cellWord) {
        String printCellWord = "|";
        for (int i = 0; i < cellWord.length(); i++) {
            printCellWord += cellWord[i];
        }
        printCellWord += "|";
        System.out.println(printCellWord);
    }

    public static void printBoard(char[][] board) {
        StringBuilder print = new StringBuilder();
        for (int row = 0; row < ROW_COUNT; row++) {
            for (int col = 0; col < COL_COUNT; col++) {
                System.out.print(board[row][col]);
            }

            System.out.println(print);

        }
    }

    public static void drawBodyPart(char[][] board, int counterMistakes) {
        switch (counterMistakes) {
            case 1:
                board[1][3] = 'O';
                break;
            case 2:
                board[2][3] = '|';
                board[3][3] = '|';
                break;
            case 3:
                board[2][2] = '/';
                break;
            case 4:
                board[2][4] = '\\';
                break;
            case 5:
                board[4][2] = '/';
                break;
            case 6:
                board[4][4] = '\\';
                break;
        }
    }

    public static String checkGameState(char[][] board, StringBuilder cellWord, int counterMistakes) {
        String result = String.join("", cellWord);

        if (result.contains("*") && counterMistakes == 6) {
            return GAME_OVER;
        } else if (!result.contains("*")) {
            return YOU_WIN;
        } else {
            return GAME_STATE_NOT_FINISHED;
        }
    }

}






