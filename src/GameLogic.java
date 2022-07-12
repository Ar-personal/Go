import java.util.ArrayList;
import java.util.List;

public class GameLogic {

    private Go game;
    private Tile[][] tiles;
    private int dim;
    private int moveNo;
    private int whitePlaced = 0;
    private int blackPlaced = 0;
    private int whiteCaptured = 0;
    private int blackCaptured = 0;
    private int[][] surroundingTiles = new int[][]{{0, -1}, {-1, 0}, {+1, 0}, {0, + 1}};
    private List<List<Tile>> strings = new ArrayList<>();


    public GameLogic(Go game, Tile[][] tiles, int dim) {
        this.game = game;
        this.tiles = tiles;
        this.dim = dim;
    }


    public void tick(){
        checkCaptures();
    }

    public void checkCaptures(){
        for(int row = 0; row < tiles.length; row++){
            for(int col = 0; col < tiles[row].length; col++) {
                //early exit
                if(tiles[row][col].isLabel() || tiles[row][col].getSide() == -1)
                    continue;
                ArrayList<Tile> tilesToCheck = new ArrayList<Tile>();
                for(int[] direction: surroundingTiles){
                    int dx = col + direction[0];
                    int dy = row + direction[1];
                    if (dy >= 1 && dy <= dim) {
                        if (dx >= 1 && dx <= dim) {
                            tilesToCheck.add(tiles[dy][dx]);
                        }
                    }
                }

                //check if immediate tiles are same colour or not, if all are opposite colour then it is captured
                int opp = 0;
                for(Tile t : tilesToCheck){
                    if(!t.isLabel() && t.isPlaced() && t.getSide() >= 0){
                        //check surrounding tiles for their colour or if unplaced
                        if(tiles[row][col].getSide() != t.getSide()){
                            opp++;
                        }

                        if(opp == tilesToCheck.size() && opp > 1) {
                            System.out.println("removing tile");
                            tiles[row][col].setPlaced(false);
                            //add to score
                            if(tiles[row][col].getSide() == 1){
                                blackCaptured++;
                                System.out.println("black_score: " + blackCaptured);
                            }else if(tiles[row][col].getSide() == 0){
                                whiteCaptured++;
                                System.out.println("white_score: " + whiteCaptured);
                            }

                            tiles[row][col].setSide(-1);
                            return;
                        }
                    }
                }


            }
        }
    }

    public void checkStrings(int row, int col){
        //early exit
        if(tiles[row][col].isLabel() || tiles[row][col].getSide() == -1)
            return;

        ArrayList<Tile> string = new ArrayList<>();
        for(int[] direction: surroundingTiles){
            int dx = col + direction[0];
            int dy = row + direction[1];
            if (dy >= 1 && dy <= dim) {
                if (dx >= 1 && dx <= dim) {
                    string.add(tiles[dy][dx]);
                }
            }
        }
    }

    public int getMoveNo() {
        return moveNo;
    }

    public void incerementMoveNo(){
        moveNo++;
        System.out.println(moveNo);
    }

    public void setMoveNo(int moveNo) {
        this.moveNo = moveNo;
    }

    public int getWhitePlaced() {
        return whitePlaced;
    }

    public void setWhitePlaced(int whitePlaced) {
        this.whitePlaced = whitePlaced;
    }

    public int getBlackPlaced() {
        return blackPlaced;
    }

    public void setBlackPlaced(int blackPlaced) {
        this.blackPlaced = blackPlaced;
    }
}
