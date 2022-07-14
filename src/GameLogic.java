import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
    private int[][] surroundingTiles = new int[][]{{+1, 0}, {0, -1}, {-1, 0}, {0, + 1}};
    private HashMap<Point, List<Point>> strings = new HashMap<>();

    public GameLogic(Go game, Tile[][] tiles, int dim) {
        this.game = game;
        this.tiles = tiles;
        this.dim = dim;
    }


    public void tick(){
//        checkSingleCaptures();

        checkStringCaptures();
    }

    public boolean canPlace(int row, int col, int side){
        //cant be placed if would be surrounded
        //can be placed if Ko rule
        int stringLiberties = 0;
        int enemyOccupied = 0;
        Point pos = new Point(col, row);
        List<Point> libs = new ArrayList<>();
        libs.add(pos);
        List<Point> occu = new ArrayList<>();
        occu.add(pos);
        stringLiberties = countLiberties(libs);
        enemyOccupied = countEnemyOccupied(occu);
        if(stringLiberties == enemyOccupied){
            return false;
        }
        return true;
    }

    public void checkSingleCaptures(){
        for(int row = 0; row < tiles.length; row++){
            for(int col = 0; col < tiles[row].length; col++) {
                Tile tile = tiles[row][col];
                //early exit
                if(tile.isLabel() || tile.getSide() == -1)
                    continue;

                ArrayList<Tile> tilesToCheck = new ArrayList<Tile>();
                for(int[] direction: surroundingTiles){
                    int dx = col + direction[0];
                    int dy = row + direction[1];
                    Tile o = tiles[dy][dx];
                    if (dy >= 1 && dy <= dim) {
                        if (dx >= 1 && dx <= dim) {
                            tilesToCheck.add(o);
                        }
                    }
                }

                //check if immediate tiles are same colour or not, if all are opposite colour then it is captured
                int opp = 0;
                for(Tile t : tilesToCheck){
                    if(!t.isLabel() && t.isPlaced() && t.getSide() >= 0){
                        //check surrounding tiles for their colour or if unplaced
                        if(tile.getSide() != t.getSide())
                            opp++;
                        if(opp == tilesToCheck.size() && opp > 1) {
                            System.out.println("removing tile");
                            tile.setPlaced(false);
                            //add to score
                            if(tile.getSide() == 1){
                                blackCaptured++;
                                System.out.println("black_score: " + blackCaptured);
                            }else if(tile.getSide() == 0){
                                whiteCaptured++;
                                System.out.println("white_score: " + whiteCaptured);
                            }

                            t.setSide(-1);
                            return;
                        }
                    }
                }
            }
        }
    }

    //when a group of stones is surrounded
    public void checkStringCaptures(){
        if(strings.isEmpty())
            return;
        Point toRemove = null;
        for(Point root : strings.keySet()){
            List<Point> toCheck = new ArrayList<>();
            toCheck.add(root);
            if(strings.get(root).size() > 0) {
                toCheck.addAll(strings.get(root));
            }
            int stringLiberties = 0;
            int enemyOccupied = 0;
            //check entire strings liberty count
            stringLiberties = countLiberties(toCheck);
            enemyOccupied = countEnemyOccupied(toCheck);

            if(stringLiberties > 0 && enemyOccupied > 0  && stringLiberties == enemyOccupied){
                //entire string is surrounded
                toRemove = root;
                break;
            }
        }
        if(toRemove != null)
            removeString(toRemove);
    }

    public void checkStrings(int row, int col, int side){
        //duplicate array so we cna remove and make traversing faster
        //check every remaining tile in copy
        Tile tile = tiles[row][col];
        Point coords = new Point(col, row);
        //early exit
        if (tile.isLabel() || tile.getSide() == -1)
            return;

        if(strings.keySet().size() == 0){
            strings.put(coords, new ArrayList<>());
            return;
        }
        //we have first tile and we want to check adjacent tiles for the same colour
        //if there is adjaceent with same colour we want to check that one too
        //ading to a list while checking for new adds and then probably remove tiles from pool
        //other method to check adjacent tile that returns list
        //check roots then values for tile
        if(!strings.containsKey(coords)){
            for(List<Point> i : strings.values()){
                if(i.contains(coords)) {
                    return;
                }
            }
            addToStrings(coords, side);
        }
    }

    public void addToStrings(Point coords, int side){
        boolean added = false;
            for (int[] direction : surroundingTiles) {
                if(added){
                    break;
                }
                int dx = coords.x + direction[0];
                int dy = coords.y + direction[1];
                Point newCoords = new Point(dx, dy);
                //check bounds
                if (dy >= 1 && dy <= dim) {
                    if (dx >= 1 && dx <= dim) {
                        Tile o = tiles[dy][dx];
                        //check if surrounding tiles are a root, if so add to it
                        if (o.getSide() != -1) {
                            boolean root = strings.containsKey(newCoords);
                            //add to root if same colour
                            if (root && o.getSide() == side) {
                                strings.get(newCoords).add(coords);
                                added = true;
                                break;
                            }
                            //if not key it might be a node, if so add to node
                            for (List<Point> i : strings.values()) {
                                //add if node found
                                if (i.contains(newCoords) && o.getSide() == side) {
                                    i.add(coords);
                                    added = true;
                                    break;
                                }
                            }
                        }
                    }
                }
        }
        if(!added) {
            strings.put(coords, new ArrayList<>());
        }
        List<Point> duplicateRoots = new ArrayList<>();
        for (Point root : strings.keySet()){
            if(strings.get(root).contains(coords) || root.equals(coords)){
                duplicateRoots.add(root);
            }
        }
        //merge strings
        if(duplicateRoots.size() > 1){
            Point newKey = coords;
            List<Point> newValue = new ArrayList<>();
            for (int i = 0; i < duplicateRoots.size(); i++){
                strings.get(duplicateRoots.get(i)).remove(coords);
                newValue.addAll(strings.get(duplicateRoots.get(i)));
                newValue.add(duplicateRoots.get(i));
                strings.remove(duplicateRoots.get(i));
            }
            strings.put(newKey, newValue);
            return;
        }
    }



    public int countLiberties(List<Point> stones){
        int count = 0;
        for(Point p : stones) {
            for (int[] direction : surroundingTiles) {
                int dx = p.x + direction[0];
                int dy = p.y + direction[1];
                //check bounds
                if (dy >= 1 && dy <= dim) {
                    if (dx >= 1 && dx <= dim) {
                        if(tiles[dy][dx].getSide() != tiles[p.y][p.x].getSide())
                            count++;
                    }
                }
            }
        }
        return count;
    }

    public int countEnemyOccupied(List<Point> occu){
        int count = 0;
        for(Point p : occu) {
            for (int[] direction : surroundingTiles) {
                int dx = p.x + direction[0];
                int dy = p.y + direction[1];
                //check bounds
                if (dy >= 1 && dy <= dim) {
                    if (dx >= 1 && dx <= dim) {
                        Point check = new Point(dx, dy);
                        //check that the other tile is not a friendly stone and is definitely placed
                        if (tiles[check.y][check.x].getSide() != tiles[p.y][p.x].getSide()) {
                            if (tiles[check.y][check.x].getSide() >= 0) {
                                count++;
                            }
                        }
                    }
                }
            }
        }
        return count;
    }

    public void removeString(Point root){
        Tile remove = tiles[root.y][root.x];
        int count = 0;
        if(remove.getSide() == -1) {
            System.out.println("error removing string: cannot remove nothing at " + root.toString());
            return;
        }

        if(strings.get(root).size() > 0){
            for(Point p : strings.get(root)){
                count += 1;
                tiles[p.y][p.x].setSide(-1);
                tiles[p.y][p.x].setPlaced(false);
            }
        }

        remove.setSide(-1);
        remove.setPlaced(false);
        count++;

        if(remove.getSide() == 0){
            adjustScore(1, count);
        }else{
            adjustScore(0, count);
        }
        strings.remove(root);
    }


    public void adjustScore(int side, int amount){
        if(side == -1) {
            System.out.println("cannot change score for no side");
            return;
        }
        if(side == 1){
            blackCaptured += amount;
        }else{
            whiteCaptured += amount;
        }

        printScores();
    }

    public void printScores(){
        System.out.println("White score: " + whiteCaptured);
        System.out.println("Black score: " + blackCaptured);
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
