package Hangman;


import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


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
        newGameRound();
    }
    public static void newGameRound() throws IOException {
        do {
            System.out.println("Для начала нового раунда нажмите Y, для выхода из приложения N");
            String inputUser = scanner.next();
            boolean inputUserCorrect = inputUser.matches("[Y,N]");
            if (inputUserCorrect) {
                if (inputUser.equals("Y")) {
                    startGameRound();
                } else return;
            }
        } while (true);
    }

    public static void startGameRound() throws IOException {
        System.out.println("Начало нового раунда");
        String[][] board = createBoard();
        String word = getWord();
        String[] cellWord = createCellWord(word);
        gameLoop(board, cellWord, word);
    }

    public static void gameLoop(String[][] board, String[] cellWord, String word) throws IOException {
        int counterMistakes = 0;
        do {
            String letter = getLetter(cellWord);
            if (isLetterInWord(word, letter)) {
                openLetter(cellWord, letter, word);
                printCellWord(cellWord);
            } else {
                counterMistakes++;
                drawBodyPart(board, counterMistakes);
                System.out.printf("Количество ошибок %d" + "\n", counterMistakes);
                printBoard(board);

            }
            String gameState = checkGameState(board, cellWord);
            if (!gameState.equals(GAME_STATE_NOT_FINISHED)) {
                System.out.println(gameState);
                return;
            }
        } while (true);
    }

    public static String getWord() throws IOException {
        ArrayList<String> list = new ArrayList<>();
        int randomIndex = 0;
        try {
            File file = new File("src/Hungman/resources/dictionary.txt");
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

    public static String[] createCellWord(String word) {
        String[] display = new String[word.length()];
        for (int i = 0; i < word.length(); i++) {
            display[i] = "*";
        }
        return display;
    }

    public static String[][] createBoard() {
        String[][] board = new String[ROW_COUNT][COL_COUNT];
        for (int row = 0; row < ROW_COUNT; row++) {
            for (int col = 0; col < COL_COUNT; col++) {
                if (row == 0 & col > 0 & col < COL_COUNT - 1) {
                    board[row][col] = "-";
                } else if (row > 0 && col == 1) {
                    board[row][col] = "|";
                } else {
                    board[row][col] = " ";
                }
            }
        }
        return board;
    }

    public static String getLetter(String[] cellWord) {
        System.out.println("Введите букву");
        do {
            String inputLatter = scanner.next();
            boolean correctInput = inputLatter.matches("[а-я]");
            for (int i = 0; i < cellWord.length; i++) {
                if (cellWord[i].equals(inputLatter) && correctInput) {
                    System.out.println("Введена угаданная буква");
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

    public static void openLetter(String[] cellWord, String letter, String word) {
        ArrayList<Integer> indexLetter = getIndexSymbolWord(word, letter);
        for (int i = 0; i < indexLetter.size(); i++) {
            for (int j = 0; j < cellWord.length; j++) {
                if (j == indexLetter.get(i)) {
                    cellWord[j] = letter;
                }
            }
        }
    }

    public static String checkGameState(String[][] board, String[] cellWord) {
        String result = String.join("", cellWord);
        boolean contains = result.contains("*");
        if (contains && board[4][4].equals(RIGHT_LEG)) {
            return GAME_OVER;
        } else if (!contains) {
            return YOU_WIN;
        } else {
            return GAME_STATE_NOT_FINISHED;
        }
    }

    public static void printCellWord(String[] cellWord) {
        String printCellWord = "|";
        for (int i = 0; i < cellWord.length; i++) {
            printCellWord += cellWord[i];
        }
        printCellWord += "|";
        System.out.println(printCellWord);
    }

    public static void printBoard(String[][] board) {
        for (int row = 0; row < ROW_COUNT; row++) {
            String line = "| ";
            for (int col = 0; col < COL_COUNT; col++) {
                line += board[row][col] + " ";
            }
            line += "|";
            System.out.println(line);
        }
    }

    public static void drawBodyPart(String[][] board, int counterMistakes) {
        switch (counterMistakes) {
            case 1:
                board[1][3] = HEAD;
                break;
            case 2:
                board[2][3] = BODY;
                board[3][3] = BODY;
                break;
            case 3:
                board[2][2] = LEFT_HAND;
                break;
            case 4:
                board[2][4] = RIGHT_HAND;
                break;
            case 5:
                board[4][2] = LEFT_LEG;
                break;
            case 6:
                board[4][4] = RIGHT_LEG;
                break;
        }
    }

    public static ArrayList<Integer> getIndexSymbolWord(String word, String letter) {
        ArrayList<Integer> listIndexLetter = new ArrayList<>();
        int counter = 0;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == letter.charAt(0)) {
                listIndexLetter.add(i);
            }
        }
        return listIndexLetter;
    }

    }






