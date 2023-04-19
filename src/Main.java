import java.io.File;
import java.io.IOException;
import java.net.StandardSocketOptions;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static Scanner scanner;
    public static Random rnd;

    public static int[] convertToInt(String[] str) {
        int len = str.length;
        int[] converted = new int[len];
        for (int i = 0; i < len; i++) {
            converted[i] = Integer.parseInt(str[i]);
        }
        return converted;
    }
    public static int[] boardSize() {
        System.out.println("Enter the board size");
        String board = scanner.next();
        String[] nXm = board.split("X");
        return convertToInt(nXm);
    }
    public static int countShips(int[][] battleships){
        int count = 0;
        for (int i = 0; i < battleships.length; i++) {
            count += battleships[i][0];
        }
        return count;
    }
    public static int[][] battleshipSizes() {
        System.out.println("Enter the battleships sizes");
        String battleshipSizes = scanner.next();
        String[] numberOf = battleshipSizes.split(" ");
        int len = numberOf.length;
        String[][] sizesOf = new String[len][2];
        for (int i = 0; i < len; i++) {
            sizesOf[i] = numberOf[i].split("X");
        }
        int [][] newSizes = new int[len][2];
        for (int i = 0; i < len; i++) {
            newSizes[i] = convertToInt(sizesOf[i]);
        }
        return newSizes;
    }
    public static boolean checkOrientation(int[] x_y_loc) {
        if ((x_y_loc[2] != 0) && (x_y_loc[2] != 1)) return false;
        return true;
    }
    public static boolean checkTile(int x, int y, int[][] board) {
        if ((x < 0) || (x >= board.length) || (y < 0) || y > board[0].length) return false;
        return true;
    }
    public static boolean checkBoundaries(int[] x_y_loc, int size, int[][] board) {
        if (x_y_loc[2] == 0) { // horizontal
            if (x_y_loc[0]+size-1 > board.length) return false;
        } else { // vertical
            if (x_y_loc[1]+size-1 > board[0].length) return false;
        }
        return true;
    }
    public static boolean checkOverlap(int[] x_y_loc, int size, int[][] board) {
        for (int i = 0; i < size; i++) {
            if(x_y_loc[2] == 0) { // horizontal
                if (board[x_y_loc[0]+i][x_y_loc[1]] != 0) return false;
            } else { // vertical
                if (board[x_y_loc[0]][x_y_loc[1]+i] != 0) return false;
            }
        }
        return true;
    }
    public static boolean checkNeighbours(int[] x_y_loc, int size, int[][] board) {
        if (x_y_loc[2] == 0) { // horizontal
            for (int i = 0; i < size + 2; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[x_y_loc[0]-1+i][x_y_loc[1]-1+j] != 0) return false;
                }
            }
        } else { // vertical
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < size+2; j++) {
                    if (board[x_y_loc[0]-1+i][x_y_loc[1]-1+j] != 0) return false;
                }
            }
        }
        return true;
    }
    public static boolean is_Valid(int size, int[] x_y_loc, int[][] board, boolean comp) {
        // check #1
        if (!checkOrientation(x_y_loc)) {
            if (!comp) System.out.println("Illegal orientation, try again!");
            return false;
        }
        // check #2
        if (!checkTile(x_y_loc[0], x_y_loc[1], board)) {
            if (!comp) System.out.println("Illegal tile, try again!");
            return false;
        }
        // check #3
        if (!checkBoundaries(x_y_loc, size, board)) {
            if(!comp) System.out.println("Battleship exceeds the boundaries of the board, try again!");
            return false;
        }
        // check #4
        if (!checkOverlap(x_y_loc, size, board)) {
            if (!comp) System.out.println("Battleship overlaps another battleship, try again!");
            return false;
        }
        // check #5
        if (!checkNeighbours(x_y_loc, size, board)) {
            if (!comp) System.out.println("Adjacent battleship detected, try again!");
            return false;
        }

        return true;
    }
    public static void placeBattleship(int[][] board, int[] x_y_loc, int size) {
        if (x_y_loc[2] == 0) { // horizontal
            for (int i = 0; i < size; i++) {
                board[x_y_loc[0]+i][x_y_loc[1]] = 1;
            }
        } else { // vertical
            for (int i = 0; i < size; i++) {
                board[x_y_loc[0]][x_y_loc[1]+1] = 1;
            }
        }
    }
    public static void putInPlace(int[][] battleships, int[][] board) {
        boolean newShiptoPlace = true;
        for (int i = 0; i < battleships.length; i++) {
            int count = battleships[i][0];
            for (int j = 0; j < count; j++) {
                if (newShiptoPlace)
                    System.out.println("Enter location and orientation for battleship of size " + battleships[i][1]);
                String place = scanner.next();
                String[] split = place.split(", ");
                int[] x_y_loc = convertToInt(split);
                if (!is_Valid(battleships[i][1], x_y_loc, board, false)) {
                    newShiptoPlace = false;
                } else {
                    count--;
                    newShiptoPlace = true;
                    System.out.println("Your current game board:");
                    printBoard(board);
                    placeBattleship(board, x_y_loc, battleships[i][1]);
                }
            }
        }
    }
    public static void placeForComp (int[][] board, int[][] battleships) {
        for (int i = 0; i < battleships.length; i++) {
            int count = battleships[i][0];
            for (int j = 0; j < count; j++) {
                int[] x_y_loc = new int[3];
                x_y_loc[0] = rnd.nextInt(board.length);
                x_y_loc[1] = rnd.nextInt(board[i].length);
                x_y_loc[2] = rnd.nextInt(2);
                if (is_Valid(battleships[i][1], x_y_loc, board, true)) {
                    placeBattleship(board, x_y_loc, battleships[i][1]);
                    count--;
                }
            }
        }
    }
    public static boolean isAttackable(int[][] board, int[][] guesses, int[] tile) {
        if (!checkTile(tile[0], tile[1], board)) {
            System.out.println("Illegal tile, try again!");
            return false;
        }
        if (guesses[tile[0]][tile[1]] != 0) {
            System.out.println("Tile already attacked, try again!");
            return false;
        }
        return true;
    }
    public static boolean checkRight(int[] tile, int[][] board) {
        int x = tile[0]+1;
        while (x < board.length) {
            if (board[x][tile[1]] == 1) return false;
            if (board[x][tile[1]] == 0) return true;
            x++;
        }
        return true;
    }
    public static boolean checkLeft(int[] tile, int[][] board) {
        int x = tile[0]-1;
        while (x >= 0) {
            if (board[x][tile[1]] == 1) return false;
            if (board[x][tile[1]] == 0) return true;
            x--;
        }
        return true;
    }
    public static boolean checkUp(int[] tile, int[][] board) {
        int y = tile[1]-1;
        while (y >= 0) {
            if (board[tile[0]][y] == 1) return false;
            if (board[tile[0]][y] == 0) return true;
            y--;
        }
        return true;
    }
    public static boolean checkDown(int[] tile, int[][] board) {
        int y = tile[1]+1;
        while (y >= 0) {
            if (board[tile[0]][y] == 1) return false;
            if (board[tile[0]][y] == 0) return true;
            y++;
        }
        return true;
    }
    public static boolean isDrownedHorizontal(int[][] board, int[] tile) {
        if (checkTile(tile[0]+1, tile[1], board)) {
            if (!checkRight(tile, board)) return false;
        } else if (checkTile(tile[0]-1, tile[1], board)) {
            if (!checkLeft(tile, board)) return false;
        }
        return true;
    }
    public static boolean isDrownedVertical(int[][] board, int[] tile) {
        if (checkTile(tile[0], tile[1]+1, board)) {
            if (!checkDown(tile, board)) return false;
        } else if (checkTile(tile[0], tile[1]-1, board)) {
            if (!checkUp(tile, board)) return false;
        }
        return true;
    }
    public static boolean isCompletelyDrowned(int[][] board, int[] tile) {
        if (isDrownedHorizontal(board, tile) || isDrownedVertical(board, tile)) return true;
        return false;
    }
    public static int playerAttack(int[][] board, int[][] guesses, int numberOfShips) {
        System.out.println("Your current guessing board: ");
        printBoard(guesses);
        System.out.println("Enter a tile to attack");
        String input = scanner.next();
        String[] tileStr = input.split(", ");
        int[] tile = convertToInt(tileStr);
        if (isAttackable(board, guesses, tile)) {
            if (board[tile[0]][tile[1]] == 0) {
                System.out.println("That is a miss!");
                board[tile[0]][tile[1]] = 1;
                guesses[tile[0]][tile[1]] = 2;
            } else if (board[tile[0]][tile[1]] == 1) {
                System.out.println("That is a hit!");
                board[tile[0]][tile[1]] = 2;
                guesses[tile[0]][tile[1]] = 3;
                if (isCompletelyDrowned(board, tile)) {
                    numberOfShips -= 1;
                    System.out.println("The computer's battleship has been drowned, "
                            + numberOfShips + " more battleships to go!");
                }
            }
        }
        return numberOfShips;
    }
    public static int compAttack(int[][] board, int numberOfShip) {

    }
    public static void printBoard(int[][] board) {
        System.out.print("  ");
        for (int i = 0; i < board.length; i++) {
            System.out.println(i + " ");
        }
        for (int i = 0; i < board.length; i++) {
            for (int j = -1; j < board[i].length; j++) {
                if (j == -1) System.out.print(j+1 + " ");
                else if (board[i][j] == 0) System.out.print("– "); // free space // unguessed
                else if (board[i][j] == 1) System.out.print("# "); // unhit ship
                else if (board[i][j] == 2) System.out.print("X "); // hit ship // incorrect guess
                else if (board[i][j] == 3) System.out.print("V "); // correct guess
            }
        }
        System.out.println();
    }
    public static void battleshipGame() {
        int[] boardSize = boardSize();
        int[][] playerBoard = new int[boardSize[0]][boardSize[1]];
        int[][] compBoard = new int[boardSize[0]][boardSize[1]];
        int[][] guessingBoard = new int[boardSize[0]][boardSize[1]];
        for (int i = 0; i < boardSize[0]; i++) {
            for (int j = 0; j < boardSize[1]; j++) {
                playerBoard[i][j] = 0;
                compBoard[i][j] = 0;
                guessingBoard[i][j] = 0;
            }
        }
        int[][] battleships = battleshipSizes();
        printBoard(playerBoard);
        putInPlace(battleships, playerBoard);
        placeForComp(compBoard, battleships);
        int playerShips = countShips(battleships), compShips = countShips(battleships);
        playerShips = playerAttack(compBoard, guessingBoard, playerShips);
        compShips = compAttack(compBoard, compShips);
    }


    public static void main(String[] args) throws IOException {
        String path = args[0];
        scanner = new Scanner(new File(path));
        int numberOfGames = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Total of " + numberOfGames + " games.");

        for (int i = 1; i <= numberOfGames; i++) {
            scanner.nextLine();
            int seed = scanner.nextInt();
            rnd = new Random(seed);
            scanner.nextLine();
            System.out.println("Game number " + i + " starts.");
            battleshipGame();
            System.out.println("Game number " + i + " is over.");
            System.out.println("------------------------------------------------------------");
        }
        System.out.println("All games are over.");
    }
}