
import java.io.*;
import java.util.*;

public class Hangman {
    private static final Random random = new Random();
    private static final Scanner scanner = new Scanner(System.in);
    private static final int SIZE_BOARD = 5;
    private static final String GAME_OVER = "Игра закончена";
    private static final String YOU_WIN = "Вы победили";
    private static final String GAME_STATE_NOT_FINISHED = "Игра не закончена";

    public static void main(String[] args) {
        boolean isInputUserStartGame;
        do {
            System.out.println("Введите [Y] для начала новой игры, для выхода из приложения введите [N]");
            String inputUser = scanner.next().toUpperCase();
            if (inputUser.equals("Y")) {
                isInputUserStartGame = false;
                startGameRound();
            } else if (inputUser.equals("N")) {
                System.out.println("Вы вышли из игры");
                isInputUserStartGame = true;
            } else {
                isInputUserStartGame = false;
                System.out.println("Вы ввели неверную букву");
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
        char[][] board = new char[SIZE_BOARD][SIZE_BOARD];
        for (int row = 0; row < SIZE_BOARD; row++) {
            for (int col = 0; col < SIZE_BOARD; col++) {
                if (row == 0 & col > 0 & col < SIZE_BOARD - 1) {
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
            File file = new File("./resources/dictionary.txt");
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
            System.out.println(e.getMessage());
        }
        return list.get(randomIndex);
    }

    public static char[] createCellWord(String word) {
        char[] boardWord = new char[word.length()];
        int indexHelpLetter = random.nextInt(word.length());
        for (int i = 0; i < word.length(); i++) {
            boardWord[i] = '*';
            if (i == indexHelpLetter) {
                boardWord[i] = word.charAt(i);
            }
        }
        return boardWord;
    }

    public static void gameLoop(char[][] board, char[] cellWord, String word) {
        int mistakeCounter = 0;
        boolean isGameOver = false;
        List<String> incorrectLetter = new ArrayList<>();
        Set<String> printIncorrectLetter = new HashSet<>();
        System.out.println("Ниже загадано слово, попробуйте его отгадать, для этого введите букву");
        System.out.println(cellWord);
        do {
            String letter = getLetter(cellWord);
            if (isLetterInWord(word, letter)) {
                openLetter(cellWord, letter, word);
                System.out.println("Список введеных неверных букв " + printIncorrectLetter);
                System.out.println("Количество ошибок " + mistakeCounter + "/6");
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
                    mistakeCounter++;
                }
                if (counterIncorrect >= 2) {
                    System.out.println("Вы ввели не верную букву, которую вводили ранее");
                }
                System.out.println("Количество ошибок " + mistakeCounter + "/6");
                System.out.println("Список введеных неверных букв " + printIncorrectLetter);
            }
            drawBodyPart(board, mistakeCounter);
            printBoard(board);
            System.out.println(cellWord);
            String gameState = checkGameState(cellWord, mistakeCounter);
            if (!gameState.equals(GAME_STATE_NOT_FINISHED)) {
                System.out.println(gameState);
                isGameOver = true;
            }
            if (gameState.equals(GAME_OVER)) {
                System.out.println("Было загадоно слово : " + word);
            }
        } while (!isGameOver);
    }

    public static String getLetter(char[] cellWord) {
        do {
            String inputLatter = scanner.next().toLowerCase();
            boolean isCorrectInput = inputLatter.matches("[а-я]");
            for (char letter : cellWord) {
                if (letter == inputLatter.charAt(0) && isCorrectInput) {
                    System.out.println("Эта буква уже отгадана");
                    break;
                }
            }
            if (isCorrectInput) {
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
        List<Integer> indexLetter = getIndexSymbolWord(word, letter);
        for (int i : indexLetter) {
            cellWord[i] = letter.charAt(0);
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
        for (int row = 0; row < SIZE_BOARD; row++) {
            for (int col = 0; col < SIZE_BOARD; col++) {
                System.out.print(board[row][col]);
            }
            System.out.println(print);
        }
    }

    public static void drawBodyPart(char[][] board, int counterMistakes) {
        switch (counterMistakes) {
            case 1 -> board[1][3] = 'O';
            case 2 -> board[2][3] = board[3][3] = '|';
            case 3 -> board[2][2] = '/';
            case 4 -> board[2][4] = '\\';
            case 5 -> board[4][2] = '/';
            case 6 -> board[4][4] = '\\';

        }
    }

    public static String checkGameState(char[] cellWord, int counterMistakes) {
        boolean isStarInWord = true;
        int counterLetter = 0;
        for (int i : cellWord) {
            if (cellWord[i] != '*') {
                counterLetter++;
                if (counterLetter == cellWord.length) {
                    isStarInWord = false;
                }
            }
        }
        if (counterMistakes == 6) {
            return GAME_OVER;
        } else if (!isStarInWord) {
            return YOU_WIN;
        } else {
            return GAME_STATE_NOT_FINISHED;
        }
    }
}






