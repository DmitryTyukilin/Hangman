package Hangman;


import java.io.*;
import java.util.*;

import static java.lang.String.valueOf;


public class Hangman {
    private static Random random = new Random();
    private static Scanner scanner = new Scanner(System.in);
    private static final int ROW_COUNT = 5;
    private static final int COL_COUNT = 5;
    private static String GAME_OVER = "Игра закончена";
    private static String YOU_WIN = "Вы победили";
    private static String GAME_STATE_NOT_FINISHED = "Игра не закончена";

    public static void main(String[] args) throws IOException {
        boolean isInputUserStartGame = true;
        do {
            System.out.println("Введите y для начала новой игры, для выхода из приложения введите n");
            String inputUser = scanner.next().toLowerCase();
            if (inputUser.matches("[y,n]")) {
                if (inputUser.equals("y")) {
                    isInputUserStartGame = false;
                    startGameRound();
                } else if (inputUser.equals("n")) {
                    System.out.println("Вы вышли из игры");
                    isInputUserStartGame = true;
                }
            } else {
                isInputUserStartGame = false;
                System.out.println("Вы ввели не верную букву");
            }

        } while (!isInputUserStartGame);
    }

    public static void startGameRound() {
        System.out.println("Начало новой игры");
        char[][] board = createBoard();
        String word = getWord();
        char[] cellWord = createCellWord(word);
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

    public static String getWord() {
        ArrayList<String> list = new ArrayList<>();
        int randomIndex = 0;
        try {
            File file = new File("./src/Hangman/resources/dictionary.txt");
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

    public static char[] createCellWord(String word) {
        char[] boardWord = new char[word.length()];
        int indexHelpLetter = random.nextInt(0, word.length());
        for (int i = 0; i < word.length(); i++) {
            boardWord[i] = '*';
            if (i == indexHelpLetter) {
                boardWord[i] = word.charAt(i);
            }
        }
        return boardWord;
    }

    public static void gameLoop(char[][] board, char[] cellWord, String word) {
        int counterMistakes = 0;
        List<String> incorrectLetter = new ArrayList<>();
        Set<String> printIncorrectLetter = new HashSet<>();
        System.out.println("Ниже загадано слово, попробуйте его отгадать, для этого введите букву");
        System.out.println(cellWord);
        do {
            String letter = getLetter(cellWord);
            if (isLetterInWord(word, letter)) {
                openLetter(cellWord, letter, word);
                System.out.println("Список введеных не верных букв " + printIncorrectLetter);
            } else {
                incorrectLetter.add(letter); // добавить букву в список не правильных
                printIncorrectLetter.add(letter); // добавить букву в список не правильных для печати в ед.экземпляре
                int counterIncorrect = 0; // для подсчета сколько одинаковых не правильных букв в списке
                for (String checkLetter : incorrectLetter) {
                    if (checkLetter.equals(letter)) {
                        counterIncorrect++;
                    }
                }
                if (counterIncorrect <= 1) {
                    counterMistakes++;
                }
                if (counterIncorrect >= 2) {
                    System.out.println("Вы ввели не верную букву, которую вводили ранее");
                }
                System.out.println("Количество ошибок " + counterMistakes);
                System.out.println("Список введеных не верных букв " + printIncorrectLetter);

            }
            drawBodyPart(board, counterMistakes);
            printBoard(board);
            System.out.println(cellWord);
            String gameState = checkGameState(cellWord, counterMistakes);
            if (!gameState.equals(GAME_STATE_NOT_FINISHED)) {
                System.out.println(gameState);
                return;
            }
        } while (true);
    }

    public static String getLetter(char[] cellWord) {
        do {
            String inputLatter = scanner.next().toLowerCase();
            boolean correctInput = inputLatter.matches("[а-я]");
            for (int i = 0; i < cellWord.length; i++) {
                char letter = cellWord[i];
                if (letter == inputLatter.charAt(0) && correctInput) {
                    System.out.println("Эта буква уже отгадана");
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

    public static void openLetter(char[] cellWord, String letter, String word) {
        List<Integer> indexLetter = getIndexSymbolWord(word, letter); // получить индексы отгаданных букв
        for (int i : indexLetter) {
            for (int j = 0; j < cellWord.length; j++) { // дойти до полученного индекса
                if (j == i) {
                    cellWord[i] = letter.charAt(0); // заменить подстроку по индексу j в cellWord на букву letter
                }
            }
        }
    }

    public static List<Integer> getIndexSymbolWord(String word, String letter) {
        List<Integer> listIndexLetter = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == letter.charAt(0)) {
                listIndexLetter.add(i);
            }
        }
        return listIndexLetter;
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

    public static String checkGameState(char[] cellWord, int counterMistakes) {
        boolean starInWord = true;
        int counterLetter = 0;
        for (int i = 0; i < cellWord.length; i++) {
            if (cellWord[i] != '*') {
                counterLetter++;
                if (counterLetter == cellWord.length) {
                    starInWord = false;
                }
            }
        }
        if (counterMistakes == 6) {
            return GAME_OVER;
        } else if (!starInWord) {
            return YOU_WIN;
        } else {
            return GAME_STATE_NOT_FINISHED;
        }
    }
}






