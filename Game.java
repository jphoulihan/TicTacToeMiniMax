import java.util.*;

public class CS210Project {
    //integer array lists used to keep track of moves made by the player and cpu
    static ArrayList<Integer> playerPositions = new ArrayList<>();
    static ArrayList<Integer> cpuPositions = new ArrayList<>();
    static int playerEasyScore = 0;
    static int cpuScore = 0;

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        int[][] boardGuide = new int[3][3];
        char[][] board = new char[3][3];
        boolean playAgain = true;

        int count = 1;
        for (int i = 0; i < boardGuide.length; i++) {
            for (int j = 0; j < boardGuide.length; j++) {
                boardGuide[i][j] = count++;
            }
        }

        //populating the game board with underscores, these will represent available spaces to place a letter
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                board[i][j] = '_';
            }
        }

        //for this and all subsequent methods comments can be found inside the methods themselves
        printBoardRep(boardGuide);

        //series of instructions for gameplay
        System.out.println("*******************");
        System.out.println("Level of Difficulty");
        System.out.println("*******************");
        System.out.println("Easy: You make the first move."+"\n"+"You will be prompted to try again, if the position entered is occupied or out of bounds.");
        System.out.println();
        System.out.println("Hardcore: You make the first move."+"\n"+"You will lose your turn, if the position entered is occupied or out of bounds.");
        System.out.println("*******************");
        System.out.println();
        System.out.println("*******************");

        int dif;
        while(playAgain) {
            System.out.println("Enter 1 for Easy. Enter 2 for Hardcore:");
            dif = sc.nextInt();
            System.out.println("*******************");

            //if statement leading to a while loop which contains all the components of easy mode.
            if (dif == 1) {
                System.out.println("You have selected easy, enjoy your game.");
                System.out.println("*******************");
                System.out.println();
                printBoard(board);
                System.out.println();
                while (true) {

                    System.out.println("Place an X in a position on the board, using numbers 1-9");

                    System.out.println("Enter number: ");
                    int playerPos = sc.nextInt();

                    //User moves input must be a new position and be within the number range, else user is prompted again
                    while (playerPositions.contains(playerPos) || cpuPositions.contains(playerPos) || playerPos < 1 || playerPos > 9) {
                        System.out.println("Not a valid position, please try again");
                        playerPos = sc.nextInt();
                    }

                    placeLetter(board, playerPos, "P1");
                    String result = checkWinner();

                    //a result string longer than 0 is a completed game
                    if (result.length() > 0) {
                        System.out.println(result);
                        printBoard(board);
                        break;
                    }

                    //CPU randomly moves, input must be a new position and be within the number range
                    Random r = new Random();
                    int cpuPos = r.nextInt(9) + 1;

                    while (playerPositions.contains(cpuPos) || cpuPositions.contains(cpuPos)) {
                        cpuPos = r.nextInt(9) + 1;
                    }

                    placeLetter(board, cpuPos, "cpu");
                    printBoard(board);

                    result = checkWinner();

                    if (result.length() > 0) {
                        if (!result.equals(" Draw!")) {
                            System.out.println();
                            System.out.println(result);
                            printBoard(board);
                            break;
                        }
                    }
                }
                //else if sends the user to a while loop containing all the components of the hardcore mode.
            } else if (dif == 2) {
                System.out.println("You have selected hardcore, good luck.");
                System.out.println("*******************");

                while (true) {

                    printBoard(board);

                    System.out.println();
                    System.out.println("Place an X in a position on the board, using numbers 1-9");
                    System.out.println("Enter number: ");
                    int playerPos = sc.nextInt();
                    placePlayerHard(board, playerPos);

                    Move bestMove = findMove(board);
                    placeAI(board, bestMove.row, bestMove.col);

                    String win = checkAIWin(board);
                    String draw = checkDraw(board);

                    if (win.length() > 0) {
                        printBoard(board);
                        System.out.println();
                        System.out.println(win);
                        cpuScore++;
                        break;
                    }

                    if (draw.length() > 0) {
                        printBoard(board);
                        System.out.println();
                        System.out.println(draw);
                        break;
                    }

                }
            }

            System.out.println();
            System.out.println("Player score: "+playerEasyScore);
            System.out.println("Computer score: "+cpuScore);
            System.out.println("Enter 1 to continue. Enter 2 to quit.");
            System.out.println("Please enter: ");
            int goAgain = sc.nextInt();

            switch (goAgain) {
                case 1 -> {
                    playerPositions.clear();
                    cpuPositions.clear();
                    for (int i = 0; i < board.length; i++) {
                        for (int j = 0; j < board.length; j++) {
                            board[i][j] = '_';
                        }
                    }
                    System.out.println("Let's go!!");
                    playAgain = true;
                }
                case 2 -> playAgain = false;
            }
        }

    }

    public static void printBoardRep(int[][] board) {
    //method prints an int 2D array illustrating each board position with numbers 1-9
        System.out.println("*********");
        System.out.println("  T-T-T");
        System.out.println("*********");
        System.out.println("--Board--");
        System.out.println("Positions");
        System.out.println("---1-9---");
        System.out.println("*********");

        for (int[] ints : board) {
            System.out.print('|');
            for (int j = 0; j < board.length; j++) {
                System.out.print(ints[j]);
                System.out.print('|');
            }
            System.out.println();
        }
        System.out.println("*********");
    }

    public static void printBoard(char[][] board) {
    //takes the char 2D array as input and is used to print the updated board state as the game progresses
        System.out.print("*******");
        System.out.println();
        for (char[] chars : board) {
            System.out.print('|');
            for (int j = 0; j < board.length; j++) {
                System.out.print(chars[j]);
                System.out.print('|');
            }
            System.out.println();
        }
        System.out.print("*******");
    }

    //*************Easy mode methods begin here****************
    public static void placeLetter(char[][] board, int pos, String player) {
    //char symbol is assigned X or O depending on the String player. The integer entered is then added
    //to the relevant ArrayList to keep track of occupied positions. Switch statement has 9 cases each one representing a board index.
    //board index is assigned letter X or O.

        char symbol = 'X';
        if (player.equals("P1")) {
            playerPositions.add(pos);
        } else if (player.equals("cpu")) {
            symbol = 'O';
            cpuPositions.add(pos);
        }

        switch (pos) {
            case 1 -> board[0][0] = symbol;
            case 2 -> board[0][1] = symbol;
            case 3 -> board[0][2] = symbol;
            case 4 -> board[1][0] = symbol;
            case 5 -> board[1][1] = symbol;
            case 6 -> board[1][2] = symbol;
            case 7 -> board[2][0] = symbol;
            case 8 -> board[2][1] = symbol;
            case 9 -> board[2][2] = symbol;
        }
    }

    public static String checkWinner() {
    //8 lists declared, each assigned a winning combination of three integers
    //then added to a list (of lists) that is looped through and checked against the integer Arraylist of the user and the cpu
    //checking if they contain all of the integers of the current list to determine a win, a draw or for the game to continue if the
    //String returned is empty

        List topRow = Arrays.asList(1, 2, 3);
        List midRow = Arrays.asList(4, 5, 6);
        List bottomRow = Arrays.asList(7, 8, 9);
        List lCol = Arrays.asList(1, 4, 7);
        List mCol = Arrays.asList(2, 5, 8);
        List rCol = Arrays.asList(3, 6, 9);
        List diagonalOne = Arrays.asList(1, 5, 9);
        List diagonalTwo = Arrays.asList(3, 5, 7);

        List<List> win = new ArrayList<>();
        win.add(topRow);
        win.add(midRow);
        win.add(bottomRow);
        win.add(lCol);
        win.add(mCol);
        win.add(rCol);
        win.add(diagonalOne);
        win.add(diagonalTwo);

        for (List l : win) {
            if (playerPositions.containsAll(l)) {
                playerEasyScore++;
                return "You win!";
            } else if (cpuPositions.containsAll(l)) {
                cpuScore++;
                return "You lose!";
            }
        }

        if (playerPositions.size() + cpuPositions.size() == 9) {
            return " Draw!";
        }

        return "";
    }

    //***********Hardcore mode methods and class begin here***************

    public static void placePlayerHard(char[][] board, int pos) {
    //A slightly altered placePlayer method which checks to see if the desired location is occupied by the opponent or not
    //this ensures no opponent moves are overwritten and enables the feature of losing a turn with invalid position input.
        char symbol = 'X';
        switch (pos) {
            case 1:
                if(board[0][0] != 'O')
                    board[0][0] = symbol;
                break;
            case 2:
                if(board[0][1] != 'O')
                    board[0][1] = symbol;
                break;
            case 3:
                if(board[0][2] != 'O')
                    board[0][2] = symbol;
                break;
            case 4:
                if(board[1][0] != 'O')
                    board[1][0] = symbol;
                break;
            case 5:
                if(board[1][1] != 'O')
                    board[1][1] = symbol;
                break;
            case 6:
                if(board[1][2] != 'O')
                    board[1][2] = symbol;
                break;
            case 7:
                if(board[2][0] != 'O')
                    board[2][0] = symbol;
                break;
            case 8:
                if(board[2][1] != 'O')
                    board[2][1] = symbol;
                break;
            case 9:
                if(board[2][2] != 'O')
                    board[2][2] = symbol;
                break;
        }
    }

    public static void placeAI(char[][] board, int row, int col) {
    //input row and col are the indices of the optimal move outputted by the minimax algorithm
    //loop checks board for empty spaces that have the matching indices of row col.
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if(board[i][j] == '_'){
                    if(i == row && j == col){
                        board[i][j] = 'O';
                        return;
                    }
                }
            }
        }

    }

    static class Move {
        //instantiated in the findBestMove method to represent indices of the 2D array board
        int row, col;
    }

    //user and cpu characters
    static char cpu = 'O';
    static char opponent = 'X';

    static Boolean checkSpaces(char[][] board) {
        //checks each index for availability for each character on the board
        for (char[] chars : board) {
            for (int j = 0; j < board.length; j++) {
                if (chars[j] == '_')
                    return true;
            }
        }
        return false;
    }

    static int evaluateBoardState(char[][] board) {
        //evaluates the board and attributes value as per the winning combinations of Tic-Tac-Toe
        //returns +10 for a cpu win and -10 for opponent (user) win, otherwise returns 0
        // rows check for win
        for (int row = 0; row < 3; row++) {
            if (board[row][0] == board[row][1] && board[row][1] == board[row][2]) {
                if (board[row][0] == cpu)
                    return +10;
                else if (board[row][0] == opponent)
                    return -10;
            }
        }

        //cols check for win
        for(int col = 0; col < 3; col++){
            if(board[0][col] == board[1][col] && board[1][col] == board[2][col]){
                if(board[0][col] == cpu) return +10;
                else if(board[0][col] == opponent) return -10;
            }
        }

        //diagonals check for win left to right
        if(board[0][0] == board[1][1] && board[1][1] == board[2][2]){
            if(board[0][0] == cpu) return +10;
            else if(board[0][0] == opponent) return -10;
        }

        //diagonals check for win right to left
        if(board[0][2] == board[1][1] && board[1][1] == board[2][0]){
            if(board[0][2] == cpu) return +10;
            else if(board[0][2] == opponent) return -10;
        }



        return 0;
    }

    static int minimax(char[][] board, int depth, Boolean isMax) {
        //checks all possible moves of current board state then backtracks and returns best
        int score = evaluateBoardState(board);
        if(score == 10) return score;
        if(score == -10) return score;
        if(!checkSpaces(board)) return 0;

        int best;
        if(isMax){
            best = -1000;

            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    if(board[i][j]=='_'){
                        //play move
                        board[i][j] = cpu;
                        //recursive call and choose max
                        best = Math.max(best, minimax(board, depth+1, false));
                        //reverse move
                        board[i][j] = '_';
                    }
                }
            }
        } else {

            best = 1000;

            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    if(board[i][j]=='_'){
                        //play move
                        board[i][j] = opponent;
                        //recursive call and choose max
                        best = Math.min(best, minimax(board, depth+1, true));
                        //undo move
                        board[i][j] = '_';
                    }
                }
            }
        }
        return best;
    }

    static Move findMove(char[][] board){
        //evaluating board state using minimax in this method and returning best move cpu can make
        int bestVal = -1000;
        Move bestMove = new Move();
        bestMove.row = -1;
        bestMove.col = -1;

        /* loop through empty spaces, call minimax, place 0 in optimal space. */
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                if(board[i][j] == '_'){
                    board[i][j] = cpu;

                    int moveValue = minimax(board, 0, false);

                    board[i][j] = '_';
                    //swap if current is greater than best
                    if(moveValue > bestVal){
                        bestMove.row = i;
                        bestMove.col = j;
                        bestVal = moveValue;
                    }
                }
            }
        }
        return bestMove;
    }

    static String checkAIWin(char[][] board){

        //rows check for win
        for (int row = 0; row < 3; row++) {
            if (board[row][0] == board[row][1] && board[row][1] == board[row][2]) {
                if (board[row][0] == 'O')
                    return "Opponent wins!";
            }
        }
        //cols check for win
        for(int col = 0; col < 3; col++){
            if(board[0][col] == board[1][col] && board[1][col] == board[2][col]){
                if(board[0][col] == 'O')
                    return "Opponent wins!";
            }
        }
        //diagonals check for win left to right
        if(board[0][0] == board[1][1] && board[1][1] == board[2][2]){
            if(board[0][0] == 'O')
                return "Opponent wins!";
        }
        //diagonals check for win right to left
        if(board[0][2] == board[1][1] && board[1][1] == board[2][0]){
            if(board[0][2] == 'O')
                return "Opponent wins!";
        }
        return "";
    }

    static String checkDraw(char[][] board){
        int count = 0;
        for (char[] chars : board) {
            for (int j = 0; j < board.length; j++) {
                if (chars[j] == '_')
                    count++;
            }
        }
        if(count == 0) return "Draw Game!";
        else return "";
    }

}