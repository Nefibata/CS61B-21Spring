package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private static final Random R= new Random();
    private TETile[][] world = new TETile[WIDTH][HEIGHT];
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        ter.memu();
        char s=getInputC();
        switch (s){
            case 'n' :
            case 'N':
                world = newGame();
                gameRun(world);
                break;
            case 'l' :
            case 'L' :
                world=loadGame();
                gameRun(world);
                break;
            case 'Q':
            case 'q':
                quitAndSave(world);
                break;
        }

    }

    private TETile[][] loadGame() {
        TETile[][] load =new TETile[WIDTH][HEIGHT];
        File f=new File(System.getProperty("user.dir")+"\\byow\\Core\\savefile.txt");
        Scanner sc = null;
        try {
            sc = new Scanner(new FileReader(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (int i=HEIGHT-1;i>=0;i--){
            String s=sc.nextLine();
            for (int j=0;j<WIDTH;j++){
                    switch (s.charAt(j)){
                        case ' ':
                            load[j][i]=Tileset.NOTHING;
                            break;
                        case '#':
                            load[j][i]=Tileset.WALL;
                            break;
                        case '@':
                            load[j][i]=new TETile('@', new Color(255, 255, 255), Color.black,
                                    "player");
                            break;
                        case '·':
                            load[j][i]=Tileset.FLOOR;
                            break;
                        default:
                            load[j][i]=Tileset.LOCKED_DOOR;
                            break;
                    }
                    
                }
            }

        return load;

    }

    private void gameRun(TETile[][] world) {
        int [] player=getPlayer(world);
        ter.initialize(WIDTH+5, HEIGHT+5,5,5);
        while (true){
            int mx= (int) StdDraw.mouseX();
            int my= (int) StdDraw.mouseY();
            ter.renderFrame(world,world[mx-5][my-5].description(),1,1);
            char c=getInputC();
            switch (c){
                case 'a':
                case 'A':
                    go(player,world,player[0]-1,player[1]);
                    break;
                case 's':
                case 'S':
                    go(player,world,player[0],player[1]-1);
                    break;
                case 'D':
                case 'd':
                    go(player,world,player[0]+1,player[1]);
                    break;
                case 'w':
                case 'W':
                    go(player,world,player[0],player[1]+1);
                    break;
                case ':':
                    char q=getInputC();
                    if (q=='q'||q=='Q'){
                        quitAndSave(world);
                        return;
                    }
                    break;

            }
        }

    }

    private int[] getPlayer(TETile[][] world) {
        int [] player=new int[2];
        for (int i=0;i<WIDTH;i++){
            for (int j=0;j<HEIGHT;j++){
                if (world[i][j].description().equals("player")){
                    player[0]=i;
                    player[1]=j;
                    return player;
                }
            }
        }
        return player;
    }

    private void quitAndSave(TETile[][] world) {
        File f=new File(System.getProperty("user.dir")+"\\byow\\Core\\savefile.txt");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String s=TETile.toString(world);
        FileWriter writer = null;
        try {
            writer = new FileWriter(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writer.write(s);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void go(int[] player, TETile[][] world,int x,int y) {
        if (x<0||x>=WIDTH||y<0||y>=HEIGHT)return;
        if (world[x][y].description().equals(Tileset.FLOOR.description())){
            world[x][y]=world[player[0]][player[1]];
            world[player[0]][player[1]]=Tileset.FLOOR;
            player[0]=x;
            player[1]=y;
        }
    }

    private int[] addPlayer(TETile[][] world) {
        while (true){
            int i =R.nextInt(WIDTH);
            int j =R.nextInt(HEIGHT);
            if (world[i][j].description().equals(Tileset.FLOOR.description())){
                world[i][j]=new TETile('@', new Color(255, 255, 255), Color.black,
                        "player");
                return new int[]{i,j};
            }

        }


    }

    private TETile[][] newGame() {
        TETile[][] world;
        ter.drawFrame("random seed",40/2,40/2);
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
        String seed = getInputSeed(input);
        if (seed==null||seed.equals("")){
            finalWorldFrame=MakeWorld();
        }else {
            finalWorldFrame = seedMakeWorld(Long.parseLong(seed));
        }
        world=finalWorldFrame;
        addPlayer(finalWorldFrame);
        String op=getInputOp(input);
        playerOpstring(op);
        return finalWorldFrame;
    }

    private void playerOpstring(String op) {
        if (op.length()==0)return;
        int [] player=getPlayer(world);
        for (int i=0;i<op.length();i++){
            switch (op.charAt(i)){
                case 'a':
                case 'A':
                    go(player,world,player[0]-1,player[1]);
                    break;
                case 's':
                case 'S':
                    go(player,world,player[0],player[1]-1);
                    break;
                case 'D':
                case 'd':
                    go(player,world,player[0]+1,player[1]);
                    break;
                case 'w':
                case 'W':
                    go(player,world,player[0],player[1]+1);
                    break;
                case 'l':
                case 'L':
                    world=loadGame();
                    player=getPlayer(world);
                    break;
                case ':':
                    char q=op.charAt(i+1);
                    if (q=='q'||q=='Q'){
                        quitAndSave(world);
                    }
                    break;

            }
        }
    }

    private String getInputOp(String input) {
        String opt=input.substring(1);
        StringBuilder sb=new StringBuilder();
        boolean flag=false;
        for (int i=0;i<opt.length();i++){
            if (flag)sb.append(opt.charAt(i));
            if ((opt.charAt(i)=='S'||opt.charAt(i)=='s')&& !flag){
                flag=true;
            }
        }
        return sb.toString();
    }

    private String getInputSeed(String input) {
        if (!(input.charAt(0) =='N'||input.charAt(0) =='n'))return "";
        String seedt=input.substring(1);
        StringBuilder sb=new StringBuilder();
        for (int i=0;i<seedt.length();i++){
            if (seedt.charAt(i)=='S'||seedt.charAt(i)=='s'){
                return sb.toString();
            }
            sb.append(seedt.charAt(i));
        }
        return sb.toString();
    }

    //rooms 0 x坐标 1 y坐标 2 x长 3 y长

    private TETile[][] seedMakeWorld(long seed){
        TETile[][] world =new TETile[WIDTH][HEIGHT];
        filedNothing(world);
        R.setSeed(seed);
        return addTile(world);

    }

    private TETile[][] addTile(TETile[][] world) {
        List<int[]> rooms=addRooms(world);
        addCorridor(rooms,world);
        for (int i=0;i<WIDTH;i++){
            for (int j=0;j<HEIGHT;j++){
                if (isWall(i,j,world)){
                    world[i][j]= Tileset.WALL;
                }
            }
        }
        addgate(world);
        return world;
    }

    private TETile[][] MakeWorld(){
        TETile[][] world =new TETile[WIDTH][HEIGHT];
        filedNothing(world);
        return addTile(world);

    }

    private void addgate(TETile[][] world) {
        for (int i=0;i<WIDTH;i++){
            for (int j=0;j<HEIGHT;j++){
                if (world[WIDTH-i-1][HEIGHT-1-j].description().equals(Tileset.WALL.description())){
                    world[WIDTH-i-1][HEIGHT-1-j]=Tileset.LOCKED_DOOR;
                    return;
                }
            }
        }
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
            int xR=R.nextInt(WIDTH-room.length-2)+2;
            int yR=R.nextInt(HEIGHT-room[0].length-2)+2;
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
