import java.io.File;
import java.io.IOException;
import java.net.StandardSocketOptions;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static Scanner scanner;
    public static Random rnd;

    public static final int FREE_SPACE = 0;
    public static final int UN_GUESSED = 0;
    public static final int UN_HIT_SHIP = 1;
    public static final int HIT_SHIP = 2;
    public static final int INCORRECT_GUESS = 2;
    public static final int CORRECT_GUESS = 3;
    public static final int COMP_GUESS_UNSUCCESSFUL = 4;

    /**
     * turns String of numbers to int
     * @param str String of numbers
     * @return converted String
     */
    public static int[] convertToInt(String[] str) {
        int len = str.length;
        int[] converted = new int[len];
        for (int i = 0; i < len; i++) {
            converted[i] = Integer.parseInt(str[i]);
        }
        return converted;
    }

    /**
     * extracts board sizes from input String
     * @return array of board sizes
     */
    public static int[] boardSize() {
        System.out.println("Enter the board size");
        String board = scanner.next();
        String[] nXm = board.split("X");
        return convertToInt(nXm);
    }

    /**
     * count number of total battleships on the board
     * @param battleships inventory of all battleship
     * @return number of battleships
     */
    public static int countShips(int[][] battleships){
        int count = 0;
        for (int i = 0; i < battleships.length; i++) {
            count += battleships[i][0];
        }
        return count;
    }

    /**
     * receives all battleships sizes and quantity
     * @return inventory of all battleships
     */
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

    /**
     * validate orientation
     * @param col_row_loc array of col, row, orientation
     * @return is valid orientation
     */
    public static boolean checkOrientation(int[] col_row_loc) {
        if ((col_row_loc[2] != 0) && (col_row_loc[2] != 1)) return false;
        return true;
    }

    /**
     * validate tile
     * @param col of tile
     * @param row of tile
     * @param board game board
     * @return is valid tile
     */
    public static boolean checkTile(int col, int row, int[][] board) {
        if ((col < 0) || (col >= board.length) || (row < 0) || row >= board[0].length) return false;
        return true;
    }

    /**
     * validate battleship in range
     * @param col_row_loc array of col, row, orientation
     * @param size size of battleship
     * @param board game board
     * @return is battleship in range
     */
    public static boolean checkBoundaries(int[] col_row_loc, int size, int[][] board) {
        if (col_row_loc[2] == 0) { // horizontal
            if (col_row_loc[1]+size >= board[0].length) return false;
        } else { // vertical
            if (col_row_loc[0]+size-1 > board.length) return false;
        }
        return true;
    }

    /**
     * validate empty spot to place battleship
     * @param col_row_loc array of col, row, orientation
     * @param size size of battleship
     * @param board game board
     * @return is battleship overlapping abother
     */
    public static boolean checkOverlap(int[] col_row_loc, int size, int[][] board) {
        for (int i = 0; i < size; i++) {
            if (col_row_loc[2] == 0) { // horizontal
                if (col_row_loc[0]+i < board.length) {
                    if (board[col_row_loc[0]+i][col_row_loc[1]] != 0) return false;
                }
            } else { // vertical
                if (col_row_loc[1]+i < board[0].length) {
                    if (board[col_row_loc[0]][col_row_loc[1]+i] != 0) return false;
                }
            }
        }
        return true;
    }

    /**
     * validate no other battleship surrounding
     * @param col_row_loc array of col, row orientation
     * @param size size of battleship
     * @param board game board
     * @return are there other battleships around
     */
    public static boolean checkNeighbours(int[] col_row_loc, int size, int[][] board) {
        if (col_row_loc[2] == 0) { // horizontal
            for (int i = 0; i < size + 2; i++) {
                for (int j = 0; j < 3; j++) {
                    if (checkTile(col_row_loc[0]-1+i, col_row_loc[1]-1+j, board)){
                        if (board[col_row_loc[0]-1+i][col_row_loc[1]-1+j] != 0) return false;
                    }
                }
            }
        } else { // vertical
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < size+2; j++) {
                    if (checkTile(col_row_loc[0]-1+i, col_row_loc[1]-1+j, board)) {
                        if (board[col_row_loc[0]-1+i][col_row_loc[1]-1+j] != 0) return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * validate battleship placement
     * @param size size of battleship
     * @param col_row_loc array of col, row, orientation
     * @param board game board
     * @param comp is computer board
     * @return is valid to place battleship
     */
    public static boolean isValid(int size, int[] col_row_loc, int[][] board, boolean comp) {
        // check #1
        if (!checkOrientation(col_row_loc)) {
            if (!comp) System.out.println("Illegal orientation, try again!");
            return false;
        }
        // check #2
        if (!checkTile(col_row_loc[0], col_row_loc[1], board)) {
            if (!comp) System.out.println("Illegal tile, try again!");
            return false;
        }
        // check #3
        if (!checkBoundaries(col_row_loc, size, board)) {
            if(!comp) System.out.println("Battleship exceeds the boundaries of the board, try again!");
            return false;
        }
        // check #4
        if (!checkOverlap(col_row_loc, size, board)) {
            if (!comp) System.out.println("Battleship overlaps another battleship, try again!");
            return false;
        }
        // check #5
        if (!checkNeighbours(col_row_loc, size, board)) {
            if (!comp) System.out.println("Adjacent battleship detected, try again!");
            return false;
        }

        return true;
    }

    /**
     * places a battleship
     * @param board game board
     * @param col_row_loc array of col, row, orientation
     * @param size size of battleship
     */
    public static void placeBattleship(int[][] board, int[] col_row_loc, int size) {
        if (col_row_loc[2] == 0) { // horizontal
            for (int i = 0; i < size; i++) {
                board[col_row_loc[0]][col_row_loc[1]+i] = 1;
            }
        } else { // vertical
            for (int i = 0; i < size; i++) {
                board[col_row_loc[0]+i][col_row_loc[1]] = 1;
            }
        }
    }

    /**
     * place all battleships on player game board
     * @param battleships inventory of all battleships
     * @param board game board
     */
    public static void putInPlace(int[][] battleships, int[][] board) {
        boolean newShiptoPlace = true;
        for (int i = 0; i < battleships.length; i++) {
            int count = battleships[i][0];
            while (count > 0) {
                if (newShiptoPlace)
                    System.out.println("Enter location and orientation for battleship of size " + battleships[i][1]);
                String col = scanner.next();
                String row = scanner.next();
                String orientation = scanner.next();
                //String input = scanner.nextLine();
                //String[] split = input.split(", ");
                int[] col_row_loc = new int[3];
                col_row_loc[0] = Integer.parseInt(col.substring(0, col.indexOf(',')));
                col_row_loc[1] = Integer.parseInt(row.substring(0, row.indexOf(',')));
                col_row_loc[2] = Integer.parseInt(orientation);
                if (!isValid(battleships[i][1], col_row_loc, board, false)) {
                    newShiptoPlace = false;
                } else {
                    count--;
                    newShiptoPlace = true;
                    System.out.println("Your current game board:");
                    placeBattleship(board, col_row_loc, battleships[i][1]);
                    printBoard(board);
                }
            }
        }
    }

    /**
     * place all battleships on computer game board
     * @param board game board
     * @param battleships inventory of all battleships
     */
    public static void placeForComp (int[][] board, int[][] battleships) {
        for (int i = 0; i < battleships.length; i++) {
            int count = battleships[i][0];
            for (int j = 0; j < count; j++) {
                int[] col_row_loc = new int[3];
                col_row_loc[0] = rnd.nextInt(board.length);
                col_row_loc[1] = rnd.nextInt(board[i].length);
                col_row_loc[2] = rnd.nextInt(2);
                if (isValid(battleships[i][1], col_row_loc, board, true)) {
                    placeBattleship(board, col_row_loc, battleships[i][1]);
                    count--;
                }
            }
        }
    }

    /**
     * validate attackable battleship
     * @param board game board
     * @param guesses guessing board
     * @param tile array of col, row
     * @return is attackable
     */
    public static boolean isAttackable(int[][] board, int[][] guesses, int[] tile) {
        if (!checkTile(tile[0], tile[1], board)) {
            System.out.println("Illegal tile, try again!");
            return false;
        } else if (guesses[tile[0]][tile[1]] != 0) {
            System.out.println("Tile already attacked, try again!");
            return false;
        }
        return true;
    }

    /**
     * check if the tiles to the right are a hit battleship
     * @param tile tile position
     * @param board game board
     * @return if there is a hit battleship to the right
     */
    public static boolean isRightDrowned(int[] tile, int[][] board) {
        int col = tile[1]+1;
        while (col < board[0].length) {
            if (board[tile[0]][col] == 1) return false;
            if (board[tile[0]][col] == 0 || board[tile[0]][col] == 4) return true;
            col++;
        }
        return true;
    }

    /**
     * check if the tiles to the left are a hit battleship
     * @param tile tile position
     * @param board game board
     * @return if there is a hit battleship to the left
     */
    public static boolean isLeftDrowned(int[] tile, int[][] board) {
        int col = tile[1]-1;
        while (col >= 0) {
            if (board[tile[0]][col] == UN_HIT_SHIP) return false;
            if (board[tile[0]][col] == 0 || board[tile[0]][col] == 4) return true;
            col--;
        }
        return true;
    }

    /**
     * check if the tiles above are a hit battleship
     * @param tile tile position
     * @param board game board
     * @return if there is a hit battleship above
     */
    public static boolean isUpDrowned(int[] tile, int[][] board) {
        int row = tile[0]-1;
        while (row >= 0) {
            if (board[row][tile[1]] == 1) return false;
            if (board[row][tile[1]] == 0 || board[row][tile[1]] == 4) return true;
            row--;
        }
        return true;
    }

    /**
     * check if the tiles below are in a hit battleship
     * @param tile tile position
     * @param board game board
     * @return if there is a hit battleship below
     */
    public static boolean isDownDrowned(int[] tile, int[][] board) {
        int row = tile[0]+1;
        while (row < board.length) {
            if (board[row][tile[1]] == 1) return false;
            if (board[row][tile[1]] == 0 || board[row][tile[1]] == 4) return true;
            row++;
        }
        return true;
    }

    /**
     * check if all tiles in horizontal ship are hit
     * @param board game board
     * @param tile tile position
     * @return is battleship drowned
     */
    public static boolean isDrownedHorizontal(int[][] board, int[] tile) {
        boolean isRight = true;
        boolean isLeft = true;
        if (checkTile(tile[0], tile[1]+1, board)) {
            isRight = isRightDrowned(tile, board);
        }
        if (checkTile(tile[0], tile[1]-1, board)) {
            isLeft = isLeftDrowned(tile, board);
        }
        return (isRight && isLeft);
    }

    /**
     * check if all tiles in vertical ship are hit
     * @param board game board
     * @param tile tile position
     * @return is battleship drowned
     */
    public static boolean isDrownedVertical(int[][] board, int[] tile) {
        boolean isUp = true;
        boolean isDown = true;
        if (checkTile(tile[0]+1, tile[1], board)) {
            isDown = isDownDrowned(tile, board);
        }
        if (checkTile(tile[0]-1, tile[1], board)) {
            isUp = isUpDrowned(tile, board);
        }
        return (isUp && isDown);
    }

    /**
     * validate battleship completely hit
     * @param board game board
     * @param col col index
     * @param row row index
     * @return is battleship completely hit
     */
    public static boolean isCompletelyDrowned(int[][] board, int col, int row) {
        int[] tile = new int[2];
        tile[0] = col;
        tile[1] = row;
        int orientation = findOrientation(board, col, row);
        if (orientation == 0) {
            if (isDrownedHorizontal(board, tile)){
                return true;
            }
        }
        if (orientation == 1) {
            if (isDrownedVertical(board, tile)){
                return true;
            }
        }
        return false;
    }

    public static int findOrientation(int[][] board, int col, int row) {
        if (col + 1 < board.length && (board[col + 1][row] == 1 || board[col + 1][row] == 2)){
            return 1; //vertical
        }
        if (col - 1 >= 0 && (board[col - 1][row] == 1 || board[col - 1][row] == 2)){
            return 1; //vertical
        }
        if (row + 1 < board[0].length && (board[col][row + 1] == 1 || board[col][row + 1] == 2)){
            return 0; //horizontal
        }
        if (row - 1 >= 0 && (board[col][row - 1] == 1 || board[col][row - 1] == 2)){
            return 0; //horizontal
        }
        return 0;
    }

    /**
     * one turn of player
     * @param board player game board
     * @param guesses guessing board
     * @param numberOfShips battleships left to hit
     * @return number of battleships left on computer game board
     */
    public static int playerAttack(int[][] board, int[][] guesses, int numberOfShips) {
        boolean cont = false;
        System.out.println("Your current guessing board: ");
        printBoard(guesses);
        System.out.println("Enter a tile to attack");
        do {
            String col = scanner.next();
            String row = scanner.next();
            int[] tile = new int[2];
            tile[0] = Integer.parseInt(col.substring(0, col.indexOf(',')));
            tile[1] = Integer.parseInt((row));
            if (isAttackable(board, guesses, tile)) {
                cont = false;
                if (board[tile[0]][tile[1]] == 0) {
                    System.out.println("That is a miss!");
                    guesses[tile[0]][tile[1]] = 2;
                } else if (board[tile[0]][tile[1]] == 1) {
                    System.out.println("That is a hit!");
                    board[tile[0]][tile[1]] = 2;
                    guesses[tile[0]][tile[1]] = 3;
                    if (isCompletelyDrowned(board, tile[0], tile[1])) {
                        numberOfShips -= 1;
                        System.out.println("The computer's battleship has been drowned, "
                                + numberOfShips + " more battleships to go!");
                    }
                }
            } else cont = true;
        } while (cont);
        return numberOfShips;
    }

    // finished comp attack --lihi
    /**
     * one turn of computer
     * @param board computer game board
     * @param numberOfShips battleships left to hit
     * @return number of battleship left on player game board
     */
    public static int compAttack(int[][] board, int numberOfShips) {
        boolean validTile = false;
        int col = 0 , row = 0;
        while (!validTile) {
            col = rnd.nextInt(board.length);
            row = rnd.nextInt(board[0].length);
            validTile = (board[col][row] == 1) || (board[col][row] == 0); // un hit ship or empty space --> good tile
        }
        System.out.println("The computer Attacked (" + col + ", " + row + ")");
        if (board[col][row] == 0) { // empty space
            System.out.println("That is a miss!");
            board[col][row] = 4;
        } else if (board[col][row] == 1) { // un hit ship
            System.out.println("That is a hit!");
            board[col][row] = 2;
            if (isCompletelyDrowned(board, col, row)) {
                numberOfShips -= 1;
                System.out.println("Your battleship has been drowned, you have left "
                        + numberOfShips + " more battleships!");
            }
        }
        return numberOfShips;
    }

    /**
     * check if all battleship has been drowned
     * @param board opponent game board
     * @return are all battleship been hit
     */
    public static boolean checkWinner(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == UN_HIT_SHIP) return false; // there is an un hit tile
            }
        }
        return true;
    }

    /**
     * print game board
     * @param board game board
     */
    public static void printBoard(int[][] board) {
        System.out.print("   ");

        for (int i = 0; i < board[0].length; i++) {
            if (i < 10) System.out.print("  " + i);
            else if (i >= 10 && i < 100) System.out.print(" " + i);
            else if (i >= 100) System.out.print(i);
        }
        System.out.println();

        for (int i = 0; i < board.length; i++) {
            if (i < 10) System.out.print("  " + i);
            else if (i >= 10 && i < 100) System.out.print(" " + i);
            else if (i >= 100) System.out.print(i);
            for (int j = 0; j < board[i].length; j++) {           //*game board    //*guessing board
                if (board[i][j] == FREE_SPACE) System.out.print("  –");      // free space    // un guessed
                else if (board[i][j] == UN_HIT_SHIP) System.out.print("  #"); // un hit ship   // un hit ship
                else if (board[i][j] == HIT_SHIP) System.out.print("  X"); // hit ship      // incorrect guess
                else if (board[i][j] == CORRECT_GUESS) System.out.print("  V");                  // correct guess
                else if (board[i][j] == COMP_GUESS_UNSUCCESSFUL) System.out.print("  –"); // comp guess unsuccessful
            }
            System.out.println();
        }
        System.out.println();
    }

    // incomplete -- not entirely sure what's left --lihi
    /**
     * single game manager
     */
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
        int playerNeedToHit = countShips(battleships), compNeedToHit = countShips(battleships);

        // player --> comp --> player --> comp ...
        // change name -- too confusing
        do {
            playerNeedToHit = playerAttack(compBoard, guessingBoard, playerNeedToHit);
            if (checkWinner(compBoard) && playerNeedToHit == 0) break;
            compNeedToHit = compAttack(playerBoard, compNeedToHit);
            if (checkWinner(playerBoard) && compNeedToHit == 0) break;
            System.out.println("Your current game board: ");
            printBoard(playerBoard);
        } while (true);

        if (checkWinner(compBoard)) System.out.println("You won the game!");
        if (checkWinner(playerBoard)) System.out.println("You lost):");

        String str = scanner.nextLine();
    }


    public static void main(String[] args) throws IOException {
        String path = "C:\\Users\\papod\\Downloads\\HW0_files\\HW0_input.txt";
        scanner = new Scanner(new File(path));
        int numberOfGames = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Total of " + numberOfGames + " games.");

        for (int i = 1; i <= numberOfGames; i++) {
            String str = scanner.nextLine();
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