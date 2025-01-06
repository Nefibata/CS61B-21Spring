package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final Random  R= new Random();
    private static final int size = 2;
    private static final int wH=10*size;
    private static final int wW=11*size-6;
    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(wW, wH);

        TETile[][] HexTiles = new TETile[wH][wW];

         filedNothing(HexTiles);

        drawWorld(HexTiles);

        TETile[][] HexTiles2 = new TETile[wW][wH];
        for (int i=0;i<wH;i++){
            for (int j=0;j<wW;j++){
                HexTiles2[j][i] = HexTiles[i][j];
            }
        }

       ter.renderFrame(HexTiles2);
    }
    private static void filedNothing(TETile[][] world){
        for (int i=0;i<wH;i++){
            for (int j=0;j<wW;j++){
                world[i][j]=Tileset.NOTHING;
            }
        }
    }

    private static void drawWorld(TETile[][] world){
        for (int i=0;i<wH;i=2*size+i){
            addHexagon(i,4*size-2,size,world);
        }
        for (int i=size;i<wH-size;i=2*size+i){
            addHexagon(i,2*size-1,size,world);
        }
        for (int i=size;i<wH-size;i=2*size+i){
            addHexagon(i,6*size-3,size,world);
        }
        for (int i=2*size;i<wH-2*size;i=2*size+i){
            addHexagon(i,0,size,world);
        }
        for (int i=2*size;i<wH-2*size;i=2*size+i){
            addHexagon(i,8*size-4,size,world);
        }
    }

    private static void addHexagon(int x ,int y , int size,TETile[][] world ){
        int high = size*2;
        int weight = size+(size-1)*2;
        TETile[][] Hexagon=drawComTile(high,weight,size);
        for (int i =0 ; i<high;i++){
            for (int j=0 ; j<weight;j++){
                if (world[x+i][y+j].description().equals(Tileset.NOTHING.description()) ){
                    world[x+i][y+j]=Hexagon[i][j];
                }
            }
        }

    }

    private static  TETile[][] drawComTile(int high , int weight ,int size){
        TETile[][] reTile=new TETile[high][weight];
        TETile filed =randomTile();
        int air=size-1;
        for (int i =0;i<size;i++){
            for (int j=0 ; j<weight;j++){
                if (j<air-i||weight-j-1<air-i){
                    reTile[i][j] = Tileset.NOTHING;
                }else {
                    reTile[i][j] = filed;
                }
            }
        }
        for (int i =0;i<size;i++){
            for (int j=0 ; j<weight;j++){
                if (j<air-i||weight-j-1<air-i){
                    reTile[high-i-1][j] = Tileset.NOTHING;
                }else {
                    reTile[high-i-1][j] = filed;
                }
            }
        }
        return reTile;
    }

    private static TETile randomTile() {
        int tileNum = R.nextInt(6);
        switch (tileNum) {
            case 0:return Tileset.WALL;
            case 1:return Tileset.FLOWER;
            case 2:return Tileset.FLOOR;
            case 3:return Tileset.GRASS;
            case 4:return Tileset.SAND;
            case 5:return Tileset.TREE;
            case 6:return Tileset.MOUNTAIN;
            default: return Tileset.NOTHING;
        }
    }
}
