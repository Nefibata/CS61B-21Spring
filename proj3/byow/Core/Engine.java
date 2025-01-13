package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private static final int ww=40;
    private static final int wh=40;
    private static final Random R= new Random();

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        memu();
        char s=getInputC();
        switch (s){
            case 'n' :
            case 'N':
                world = newGame();
                break;
            case 'l' :
            case 'L' :
                break;
            case 'Q':
            case 'q':
                break;


        }
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(world);

    }

    private TETile[][] newGame() {
        TETile[][] world;
        drawFrame("random seed",wh/2,ww/2);
        String seed=getInputS();
        world=interactWithInputString("n"+seed+"s");
        return world;
    }

    private char getInputC() {
        while (!StdDraw.hasNextKeyTyped()){

        }
        return StdDraw.nextKeyTyped();
    }
    private String getInputS(){
        StringBuilder stringBuilder =new StringBuilder();
        while (true){
            while (!StdDraw.hasNextKeyTyped()){

            }
            char s=StdDraw.nextKeyTyped();
            if (s=='s'||s=='S')break;
            stringBuilder.append(s);
        }
        return stringBuilder.toString();

    }

    private void memu() {
        StdDraw.setCanvasSize(ww*16,  wh*16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, ww);
        StdDraw.setYscale(0, wh);
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(ww/2,ww/1.2,"THE GAME");
        StdDraw.text(ww/2,wh/2.1,"new game(n)");
        StdDraw.text(ww/2,wh/2.5,"load game(l)");
        StdDraw.text(ww/2,wh/3,"quit(q)");
        StdDraw.show();
    }

    public void drawFrame(String s,int x,int y) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(x,y,s);
        StdDraw.show();
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        TETile[][] finalWorldFrame ;
        String seed = input.substring(1,input.length()-1);
        finalWorldFrame = seedMakeWorld(Integer.parseInt(seed));

        return finalWorldFrame;
    }

    //rooms 0 x坐标 1 y坐标 2 x长 3 y长

    private TETile[][] seedMakeWorld(long seed){
        TETile[][] world =new TETile[WIDTH][HEIGHT];
        filedNothing(world);
        R.setSeed(seed);
        List<int[]> rooms=addRooms(world);
        addCorridor(rooms,world);
        for (int i=0;i<WIDTH;i++){
            for (int j=0;j<HEIGHT;j++){
                if (isWall(i,j,world)){
                    world[i][j]=Tileset.WALL;
                }
            }
        }
        return world;

    }

    private boolean isWall(int i, int j, TETile[][] world) {
        boolean a=false;
        boolean b = false;
        boolean c= false;
        boolean d=false;
        boolean fl=world[i][j].description().equals(Tileset.FLOOR.description());
        if (fl)return false;
        if (i-1>=0){
            a=world[i-1][j].description().equals(Tileset.FLOOR.description());
        }
        if (i+1<WIDTH){
            b=world[i+1][j].description().equals(Tileset.FLOOR.description());
        }
        if (j+1<HEIGHT){
            c=world[i][j+1].description().equals(Tileset.FLOOR.description());
        }
        if (j-1>=0){
            d=world[i][j-1].description().equals(Tileset.FLOOR.description());
        }
        return a||b||c||d;
    }


    private void addCorridor(List<int[]> rooms, TETile[][] world) {
        Deque<int[]> temp= new LinkedList<>(rooms);
        int[] cuR=temp.removeFirst();
        while (!temp.isEmpty()){
            int[] link=temp.removeFirst();
            int max=Math.max(cuR[0],link[0]);
            int min=Math.min(cuR[0],link[0]);
            for (int i=min;i<max;i++){
                world[i][cuR[1]]=Tileset.FLOOR;
            }
            max=Math.max(cuR[1],link[1]);
            min=Math.min(cuR[1],link[1]);
            for (int i=min;i<max;i++){
                world[Math.min(cuR[0],link[0])][i]=Tileset.FLOOR;
            }
            cuR=link;
        }
    }

    private List<int[]> addRooms(TETile[][] world) {
        List<int[]> rooms=new ArrayList<>();
        for (int rs=0;rs<R.nextInt(20)+10;rs++){
            TETile[][] room=randomRoom();
            int xR=R.nextInt(WIDTH-room.length-1)+1;
            int yR=R.nextInt(HEIGHT-room[0].length-1)+1;
            int[] temp=new int [4];
            temp[0]=xR;
            temp[1]=yR;
            temp[2]= room.length;
            temp[3]= room[0].length;
            if (OverrideCheck(room, world,xR,yR)){
                rooms.add(temp);
                for (int i=xR;i<room.length+xR;i++){
                    for (int j =yR;j<room[0].length+yR;j++){
                        world[i][j]=room[i-xR][j-yR];
                    }
                }
            }
        }
        return rooms;
    }
    private  void filedNothing(TETile[][] world){
        for (int i=0;i<WIDTH;i++){
            for (int j=0;j<HEIGHT;j++){
                world[i][j]=Tileset.NOTHING;
            }
        }
    }
    private TETile[][] randomRoom(){
        int xSize = R.nextInt(6);
        int ySize = R.nextInt(6);
        TETile[][] room = new TETile[xSize+1][ySize+1];
        for (int i=0;i<xSize+1;i++){
            for (int j =0;j<ySize+1;j++){
                room[i][j]=Tileset.FLOOR;
            }
        }
        return room;
    }
    private boolean OverrideCheck(TETile[][] room,TETile[][] world,int x ,int y){
        for (int i=x;i<room.length;i++){
            for (int j =y;j<room[0].length;j++){
                if (!world[i][j].description().equals(Tileset.NOTHING.description()) ) return false;
            }
        }
        return true;
    }
}
