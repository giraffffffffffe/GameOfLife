/**
 * @author Anne Carey-Smith
 * @version 5-October-2023
 *
 * This is a simulation based off Conway's Game of Life. This game cannot be won and there is no aim.
 * This version of Conway's Game of Life is not played in an infinite world. The simulation happens on a grid.
 * The user can manually change cells or advance turns to change the grid.
 * The users can also save files and load them. These files should be in the .gol format.
 * This is an example of the layout of a .gol file:
 * 4
 * 0110
 * 0000
 * 0100
 * 1001
 *
 * The rules for the game are as follows: (they are also repeated before turn())
 * Any live cell with fewer than two live neighbours dies, as if by underpopulation.
 * Any live cell with two or three live neighbours lives on to the next generation.
 * Any live cell with more than three live neighbours dies, as if by overpopulation.
 * Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.
 */

//imports all the things that are needed
package main.java;
import java.util.Scanner; //used for the keyboard
import java.io.IOException; //used in most try statments (not all)

//the following are for the use of files specifcally
import java.io.File;
import java.io.FileWriter;

public class GameOfLife {
    public final int MAX_SIZE = 50; //this will be the maximum width and height of the grid. Otherwise it is too big for the user to see
    public final int MIN_SIZE = 10; //this will be the minimum width and height of the grid. Otherwise it is too small to be used
    public int gridSize; // variable will be determined by what the user inputs or the by the first line of a .gol file
    public boolean[][] grid = new boolean[MAX_SIZE][MAX_SIZE]; //this array is the same size as the grid and will hold the information to whether the cell is alive or dead.
    public boolean[][] changeGrid = new boolean[MAX_SIZE][MAX_SIZE]; //this array is what is used to record whether the cell is changing each turn.
    public final char ON = '■'; //character used when a cell is 'alive' (The words 'alive' and 'on' are used interchagably in reference to cells)
    public final char OFF = '□'; //character used when a cell is 'dead' (The words 'dead' and 'off' are used interchagably in reference to cells)
    public final int MAX_TURNS = 50; //max amount of turns the player can advance at any one time
    public final float DEFAULT_SECONDS_BETWEEN_TURNS = 0.5f; //this is the default amount of time between turns
    public float secondsBetweenTurns = DEFAULT_SECONDS_BETWEEN_TURNS; //this is the amount of time between turns. This is adjustable
    public boolean firstCellChange = true; //This allows the text to be different the first time the user changes a cell.
    public final float MAX_TURN_REST = 10f; //this is the max amount of time between turns, any more than this, and it takes far too long.
    public final float DANGER_REFRESH_TIME = 1.333333f; //this is the amount of time it is recommended to keep the refresh rate above, anything less than this can effect photosensitive viewers.


    Scanner kb = new Scanner(System.in); //keyboard initialisation

    public static void main(String[] args) { //initiates program
        GameOfLife game = new GameOfLife();
    }

    public GameOfLife() {
        // this is what the game opens with
        System.out.println("Welcome to Conway's game of life. To see instructions, press 'i'. \nTo start the game, enter how large you want the grid to be (less than "+MAX_SIZE+" and more than "+MIN_SIZE+") or enter 'l' to load a save file");
        welcome(); // this method starts the game

    }

    public void welcome(){ //this runs from GameOfLife and gridDraw (if 'r' is inputted)
        String input  = kb.nextLine();
        input = input.toLowerCase(); // ensures user input is lowercase
        try {
            gridSize = Integer.parseInt(input); // if the user inputted a number, this sets the gridSize to be that number
            if (gridSize > MAX_SIZE) { // this ensures that the grid stays a manageable size even if the user inputs a large number
                System.out.println("That's larger than the maximum. Setting gridsize to the maximum...");
                gridSize = MAX_SIZE;
            }

            if (gridSize < MIN_SIZE){ // this ensures that the grid is usable
                System.out.println("That's smaller than the minimum. Setting gridsize to the minimum...");
                gridSize = MIN_SIZE;
            }

            for (int i = 0; i < gridSize; i++) { // makes the right amount of rows
                for (int j = 0; j < gridSize; j++) { // makes the right amount of columns
                    grid[i][j] = false; // assigns that cell as 'off' so that no cells are on to begin with
                }
            }

            gridDraw(true); // method that draws the grid and continues the game

        } catch(NumberFormatException notInt){ //if the input is not a number, this catch runs
            if (input.equals("i")){
                info(true); // if the user inputted 'i', an infomation page is shown
            } else if (input.equals("l")){ // if the user inputted 'l', this runs
                System.out.println("Please enter the name of the file you would like to load");
                String fileName = kb.nextLine();
                loadFile(fileName);
            } else {
                System.out.println("That is not a valid input. \n  To see instructions, press 'i'. \nTo start the game, enter how large you want the grid to be (less than "+MAX_SIZE+" and more than "+MIN_SIZE+") or enter 'l' to load a save file.");
                welcome();
            }
        }
    }
    public void info(boolean start) { // if start is true, then welcome() is run afterwards, if false, gridDraw(true)
        // the infomation
        System.out.println("This is a simulator based off a set of rules designed by John Conway. \nIt is designed to simulate a colony and can be used to make recurring patterns, interesting shapes and is made to just generally have fun with.\nThe rules are as follows:\nAny live cell with fewer than two live neighbours dies, as if by underpopulation. \nAny live cell with two or three live neighbours lives on to the next generation. \nAny live cell with more than three live neighbours dies, as if by overpopulation. \nAny dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.\n\n.gol files can be run in this program. \nThis is an example of the layout of a .gol file: \n4 \n0110 \n0000 \n0100 \n1001 \n\nThe first number is the gridsize and the rest of the numbers represent the cells. '0' means the cell is dead and '1' means the cell is alive. \nIn this program, 'b.gol' or 'B.gol' will not work for filenames. \nThe .gol file must be in the same folder as the java files \n\nJust follow the prompts and have fun!");
        if (start) { // if the player came from the GameOfLife() method, that means the player hasn't started the game yet.
            System.out.println("\nTo start the game, enter how large you want the grid to be. This number has to be less than " + MAX_SIZE + " and more than " + MIN_SIZE + ". 35 is recommended if you are unsure.\nTo load a save file, enter 'l'");
            welcome(); //runs the welcome method
        } else {
            System.out.println("When you wish to continue, enter anything");
            kb.nextLine(); // this waits until something is entered
            gridDraw(true); // does to the grid draw method to continue the game play.
        }
    }

        //method that draws the grid. If 'cont' is false, the method ends without needing user input. This is so the advance turns method can run.
        // if 'cont' is true, the method continues on and asks the user for input.
        public void gridDraw(boolean cont) {
            // this draws the grid

            //this prints the first line of numbers (the x-axis)
            System.out.print("  "); // this space needs to be at the beginning to get the numbers to line up right
            for(int i = 1; i < (gridSize+1); i++) { // this section of code prints out a row of numbers for the user to refer to. The '1' and '+1' is because gridSize starts at 0 whereas my coordinates don't
                if (i <= 9) {
                    System.out.print(i + "  "); // the numbers with 1 digit need 2 spaces to make them line up right.
                } else {
                    System.out.print(i + " "); // the numbers with 2 digits need 1 space to make them line up right.
                }
            }
            System.out.println();

            // this prints the rest of the grid
            // each line starts with number and then has the right amount of characters to show the cells
            for (int i = 0; i < gridSize; i++) { //makes the right amount of rows
                i++; // this is so the numbers on the y-axis start at 1 not 0.
                if (i <= 9) {
                    System.out.print(i + "  "); // the numbers with 1 digit need 2 spaces to make them line up right.
                } else {
                    System.out.print(i + " "); // the numbers with 2 digits need 1 space to make them line up right.
                }
                i--;
                for (int j = 0; j < gridSize; j++) { //makes the right amount of columns
                    render(i, j); // this method prints the right character for whether the cell is alive or dead
                }
                System.out.println(); // prints a new line
            }

            System.out.println(); // leaves a gap between the grid and the instructions

            if(cont) { // if the user hasn't asked for more turns (e.g. it's the third turn out of three)
                System.out.println("To advance turns, enter 'a'. To manually change cells, enter 'c'.\nTo load a save file, enter 'l'. To save current state, enter 's'. \nTo view the instructions, enter 'i'. To restart the game, enter 'r'. To quit, enter 'q'.");
                switch (kb.nextLine().toLowerCase()) { // this compares the lowercase version of whatever the user inputted to all the cases
                    case "a":
                        turn(); // if the user's input was 'a' or 'A', it runs the advance turn method
                        break;
                    case "c":
                        changeCells(); // if the user's input was 'c' or 'C', it runs the changeCells() method
                        break;
                    case "i":
                        info(false); // et c.
                        break;
                    case "r":
                        System.out.println("Welcome to Conway's game of life. To see instructions, press 'i'. To start the game, enter how large you want the grid to be (less than "+MAX_SIZE+" and more than "+MIN_SIZE+"). To load a save file, enter 'l'.");
                        welcome(); // this function is the beginning of the game
                        break;
                    case "q":
                        System.out.println("Quitting..."); // if the user's input was 'q' or 'Q',there is nothing else for the game to do so it stops the program
                        break;
                    case "l":
                        System.out.println("Enter 'b' to go back to the menu or the name of the file you would like to open.");
                        String fileName = kb.nextLine();
                        loadFile(fileName);
                        break;
                    case "s":
                        saveFile();
                        break;
                    default:  // if the user's input was not 'a', 'i', 'r' or 'c', this whole method (gridDraw()) runs again
                        System.out.println("Invalid input");
                        gridDraw(true);
                        break;
                }
            }
        }

        public void render(int i, int j){ // runs for each cell when the grid is drawn (from gridDraw)
            if (changeGrid[i][j]) { // if changeGrid is true it means that the cell is either going from on to off or the other way round.
                grid[i][j] = !grid[i][j];
                changeGrid[i][j] = false; // resets the changeGrid variable, so that it works for the next time the grid is drawn
            }
            if (grid[i][j]) {
                System.out.print(ON + "  "); //this prints out the 'alive' character if the cell is alive
            } else {
                System.out.print(OFF + "  "); // this prints out the 'dead' character if the cell is dead
            }
        }

        public void changeCells() { // this method lets the user manually change cells
            if (firstCellChange){ // this is the first time the user changes a cell (resets after each time the user finishs changing cells)
                System.out.println("Please enter the x-coordinate of the cell you would like to change.");
            }else {
                System.out.println("Enter 's' to stop changing cells or enter the x-coordinate of the cell you would like to change.");
            }
            String input = kb.nextLine().toLowerCase(); //'input' is the next thing the user inputs. It will be changed to lower case
            if(!firstCellChange && input.equals("s")){
                firstCellChange = true; // resets firstCellChange
                gridDraw(true);
            }
            int j = 1; //'j' is the y co-ordinate. This line initialises the 'j' variable
            try {
                j = Integer.parseInt(input); // tries to turn the input into an integer
            }catch(NumberFormatException notInt){ // if the user didn't enter a number, an error is printed out and the change cells method is run again
                System.out.println("Invalid input");
                changeCells();
            }
            if (j > gridSize || j == 0){ // if the y co-ordinate is not a number within the boundaries of the grid, an error is printed and the change cells method is run again
                System.out.println("That co-ordinate isn't on the grid");
                changeCells();
            }
            System.out.println("Please enter the y-coordinate of the cell you would like to change.");
            input = kb.nextLine();
            int i = 1; //'i' is the x co-ordinate. This line initialises the 'i' variable
            try {
                i = Integer.parseInt(input); // tries to turn the input into an integer
            }catch(NumberFormatException notInt){ // if the user didn't enter a number, an error is printed out and the change cells method is run again
                System.out.println("Invalid input");
                changeCells();
            }
            if (i > gridSize|| i == 0){ // if the x co-ordinate is not a number within the boundaries of the grid, an error is printed and the change cells method is run again
                System.out.println("That coordinate isn't on the grid");
                changeCells();
            }
            changeGrid[i-1][j-1] = true; //the -1 is because the changeGrid starts at 0 but the numbers down the side start at 1.
            firstCellChange = false; // for the next time changeCells is run, it will print out an option to stop changing cells
            gridDraw(false); // it draws the grid but doesn't give the user the input options'
            changeCells();
        }
        public void changeTurnRest(){
            System.out.println("Enter 'd' for default time ("+DEFAULT_SECONDS_BETWEEN_TURNS+"s) or enter the amount of seconds you would like between the turns. This must be less than "+MAX_TURN_REST+"s. \nWARNING: Anything less than "+DANGER_REFRESH_TIME+"s could effect photosensitive users.");
            String input = kb.nextLine();
            float time = DEFAULT_SECONDS_BETWEEN_TURNS; //initialises time variable to default in case something goes wrong
            try {
                time = Float.parseFloat(input); // tries to change the string into a float
            } catch(NumberFormatException notInt){ // if the user didn't enter a number
                if(input.equalsIgnoreCase("d")){ // it checks to see if 'd' or 'D' was entered and if so, resets the time between turns
                    secondsBetweenTurns = DEFAULT_SECONDS_BETWEEN_TURNS;
                    turn(); // goes to the turn() method and continues the game
                } else { // if something else was entered, the changeTurnRest method re-runs
                    System.out.println("Invalid input");
                    changeTurnRest();
                }
            }
            if(time > MAX_TURN_REST) { // if the user's input was a number larger than the MAX_TURN_REST, the user can choose to use the MAX_TURN_REST or re-enter the rest time.
                System.out.println("This is more than "+MAX_TURN_REST+"s. To make the time between turns "+MAX_TURN_REST+"s, enter 't'. To re-input the rest time, enter any other input.");
                input = kb.nextLine().toLowerCase(); // ensures the user's input is in lowercase
                if(input.equals("t")){ // if the user entered 't'
                    secondsBetweenTurns = MAX_TURN_REST;
                    System.out.println("The time between turns is "+secondsBetweenTurns+"s.");
                    turn(); // goes to the turn() method and continues the game
                } else { // if the user entered anything else
                    changeTurnRest(); // re-runs this method
                }
            } else if(time< DANGER_REFRESH_TIME){ // if the refresh time is smaller than this, it can trigger seizures or other physical reactions in photosensitive users
                System.out.println("Are you sure you would like to continue? "+time+" is smaller than "+DANGER_REFRESH_TIME+"s? (y/n)");
                input = kb.nextLine().toLowerCase(); // ensures that the users' input is in lower case
                if(input.equals("y")){
                    secondsBetweenTurns = time; //the number the user entered is now the seconds between turns
                } else if(input.equals("n")){
                    changeTurnRest();
                } else { // if the user enters anything else, they are redirected to the beginning of this method with an error message
                    System.out.println("Invalid input");
                    changeTurnRest();
                }

            }else{
                secondsBetweenTurns = time; //the number the user entered is now the seconds between turns
                System.out.println("The time between turns is now "+secondsBetweenTurns+"s.");
                turn(); // the game continues from the turn() method
            }
        }

        /* Any live cell with fewer than two live neighbours dies, as if by underpopulation.
         * Any live cell with two or three live neighbours lives on to the next generation.
         * Any live cell with more than three live neighbours dies, as if by overpopulation.
         * Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.
         */
        public void turn() { // this method runs when the user wants to advance turns
            System.out.println("Either enter then amount of turns you would like to advance (Less than "+ MAX_TURNS +"), or 't' to change the amount of time between turns,");
            String input = kb.nextLine().toLowerCase();

            //if the user wants to adjust the time between turns
            if (input.equals("t")){
                changeTurnRest();
            }

            int numberOfTurns = 0; // initialises variable
            try {
                numberOfTurns = Integer.parseInt(input); // tries to turn the string in to an integer
            }catch(NumberFormatException notInt){ // if the user didn't enter a number the turn method re-runs
                System.out.println("Invalid input");
                turn();
            }
            if (numberOfTurns == 0){ //if the user inputs '0', the grid redraws with no change.
                System.out.println("No turns advanced.");
                gridDraw(true);
            }
            // if the number of turns will take too long to advance
            if (numberOfTurns > MAX_TURNS){
                System.out.println("This is more than "+ MAX_TURNS +" turns. Press 'a' to advance "+ MAX_TURNS +" turns. Press any key to re-enter the amount of turns you would like to advance");
                input = kb.nextLine().toLowerCase();
                if (input.equals("a")){
                    numberOfTurns = MAX_TURNS;
                }else{
                    turn(); // if anythig that wasn't 'a' or 'A' was entered, the turn method re-runs
                }
            }

            // in this section, i have used 'cellA' to indecate a cell that is adjacent to the cell in question 'cellQ'
            for(int t = 0;  t < numberOfTurns; t++) { // this for loop runs for the amount of turns the users wants
                // these two for loops ensure that each cell is checked
                for (int i = 0; i < gridSize; i++) {
                    for (int j = 0; j < gridSize; j++) {
                        int alive = 0; // sets the amount of alive cellA surrounding cellQ to 0
                        // these two for loops check each cellA
                        for (int countI = -1; countI < 2; countI++) {
                            for (int countJ = -1; countJ < 2; countJ++) {
                                if (countI == 0 && countJ == 0) { //this is so cellQ isn't counted
                                    // System.out.println("Cell at coordinate ("+j+","+i+") checked");

                                    // runs for each cellA
                                    // if cellA is on the grid (not off the edges) and is alive, the count of how many cellA that surrond cellQ are alive increases by 1
                                } else if (((j + countJ) > 0 && (j + countJ) < gridSize) && ((i + countI) > 0 && (i + countI) < gridSize) && (grid[i + countI][j + countJ])) {
                                    alive++;
                                }
                            }
                        }

                        // if cellQ is alive and less than 2 or more than three cellA are alive, cellQ changes state
                        if (grid[i][j] && (alive < 2 || alive > 3)) {
                            changeGrid[i][j] = true;
                            // if cellQ i dead and there are exactly 3 alive cellA, cellQ changes state
                        } else if ((!grid[i][j]) && (alive == 3)) {
                            changeGrid[i][j] = true;
                        }
                    }
                }

                //if it is not the last turn, it draws the grid and then comes back and runs the for loop in line 310 again. Otherwise, it runs the gridDraw method and then continues
                gridDraw(t >= (numberOfTurns - 1));

                //this leaves a small amount of time (defined in secondsBetweenTurns) between each turn
                try {
                    Thread.sleep((long) (secondsBetweenTurns * 1000));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // this runs when the user wants to load a file
        public void loadFile(String fileName){
            if(fileName.equalsIgnoreCase("b")){ // this means that the user wants to go back to the menu
                gridDraw(true);
            }
            File fileAsked = new File(fileName+".gol");
            try{
                Scanner readFile = new Scanner(fileAsked);
                gridSize = Integer.parseInt(readFile.nextLine()); // the first line in a .gol file is the grid size
                if (gridSize > MAX_SIZE || gridSize < MIN_SIZE){
                    System.out.println("This file is not compatible with this program due to an incompatible grid size \nThe maximum grid size for this program is "+MAX_SIZE+"\nThe minimum grid size is "+MIN_SIZE);
                    gridDraw(true);
                }
                try {
                    for (int i = 0; i < gridSize; i++) { //makes the right amount of rows
                        String line = readFile.nextLine();
                        for (int j = 0; j < gridSize; j++) { //makes the right amount of columns
                            if (line.charAt(j) == '0') { // if the character is a '0', the cell is dead
                                grid[i][j] = false; // assigns that cell as 'off'.
                            } else if (line.charAt(j) == '1') { // if the character is a '1', the cell is alive
                                grid[i][j] = true; // assigns that cell as 'off'.
                            } else { // if there are any other characters (not '1' or '0')
                                fileNotCompatiableError(fileName);
                            }
                        }
                    }
                }catch(StringIndexOutOfBoundsException e){ // if a line ends early and there's no char or if there isn't enough lines
                    fileNotCompatiableError(fileName);
                }

                readFile.close(); // closes file reader
                gridDraw(true);
            }catch(IOException e){ // if the file 'fileName.gol' could not be found, this catch runs
                System.out.println("That file could not be found. \nTo go back, enter 'b' or enter the name of the file you would like to open.");
                fileName = kb.nextLine();
                if (fileName.equalsIgnoreCase("b")){
                    gridDraw(true);
                }else{
                    loadFile(fileName);
                    e.printStackTrace();
                }
            }

        }

        // this is a separate method because I have to use it twice. I can't just go back to gridDraw() because the grid array might be only half written over which would cause errors
        public void fileNotCompatiableError(String fileName){
            System.out.println("This file is not compatible with this program.\nEnter 'l' to load a different file or enter how wide you would like the grid to be.");
            String input = kb.nextLine().toLowerCase();
            if (input.equals("l")){
                System.out.println("Please enter the name of the file you would like to load");
                fileName = kb.nextLine();
                loadFile(fileName);
            } else {
                try{
                    gridSize = Integer.parseInt(input); // tries to turn a string into a int
                    if (gridSize > MAX_SIZE) { //this ensures that the grid stays a manageable size even if the user inputs a large number
                        gridSize = MAX_SIZE;
                    }
                    if (gridSize < MIN_SIZE){ //this ensures that the grid stays a manageable size even if the user inputs a small number
                        gridSize = MIN_SIZE;
                    }
                    for (int i = 0; i < gridSize; i++) { //makes the right amount of rows
                        for (int j = 0; j < gridSize; j++) { //makes the right amount of columns
                            grid[i][j] = false; // assigns that cell as 'off'.
                        }
                    }
                } catch (NumberFormatException notInt){
                    System.out.println("Invalid input");
                    loadFile(fileName);
                }
            }
        }

        // this method saves a state
        public void saveFile(){
            File fileName = null; // initialises file
            boolean foundFileName = false; // initialises variable (this variable is whether a number that is not in use has been found)
            // this goes through numbers until it finds one that is not in use
            for (int i=0; !foundFileName; i++){
                fileName = new File("sf"+i+".gol"); // file name will look like sf#.gol e.g. sf3.gol
                if (!fileName.exists()){ // when it finds a number not in use, it sets foundFileName to 'true'
                    foundFileName = true;
                }
            }

            try {
                if (fileName.createNewFile()){
                    System.out.println("File created: " + fileName.getName());
                } else {
                    System.out.println("File already exists.");
                }
            } catch (IOException e) {
                System.out.println("An error occurred when creating the file");
                e.printStackTrace();
                gridDraw(true);
            }

            // this builds what goes into the file
            StringBuilder buildFile = new StringBuilder();
            buildFile.append(gridSize).append("\n"); // the first line will be the grid size
            for (int i = 0; i < gridSize; i++){ // then the rest are just rows of 1s and 0s to show whether the cells are on or off
                for (int j = 0; j < gridSize; j++){
                    if (grid[i][j]){
                        buildFile.append("1"); // if the cell is alive, a '1' is added to the file
                    }else{
                        buildFile.append("0"); // if the cell is dead, a '0' is added to the file
                    }
                }
                buildFile.append("\n"); // adds a new line after each row
            }
            // this writes the text block that was just made to the file
            try {
                FileWriter writer = new FileWriter(fileName); // creates the writer
                writer.write(buildFile.toString()); // writes the text into the file
                writer.close(); // closes writer
                System.out.println("State saved!");
                gridDraw(true);
            } catch (IOException e){ // if something goes wrong
                System.out.println("Something went wrong while writing to the file)");
                e.printStackTrace();
                gridDraw(true);
            }
        }
    }
