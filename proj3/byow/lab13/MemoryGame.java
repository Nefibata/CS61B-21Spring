package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        this.rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i =0 ;i<n;i++){
            int x = rand.nextInt(26);
            stringBuilder.append(CHARACTERS[x]);
        }
        return stringBuilder.toString();
    }

    public void drawFrame(String s) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(this.width/2,this.height/2,s);
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        for (int i =0;i<letters.length();i++){
            drawFrame(String.valueOf(letters.charAt(i)));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            drawFrame("");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public String solicitNCharsInput(int n) {
        StringBuilder stringBuilder =new StringBuilder();
        for (int i=0;i<n;i++){
            while (!StdDraw.hasNextKeyTyped()){

            }
            stringBuilder.append(StdDraw.nextKeyTyped());
            drawFrame(stringBuilder.toString());
        }
        return stringBuilder.toString();
    }

    public void startGame() {
        this.gameOver=false;
        this.round=1;
        drawFrame("Round: ");
        while (!gameOver){
            String nowS=generateRandomString(round);

            flashSequence(nowS);

            String userType=solicitNCharsInput(round);
            if (!userType.equals(nowS)){
                gameOver=false;
                break;
            }
            round++;
        }
        drawFrame("Game over! Round:"+String.valueOf(this.round));
    }

}
