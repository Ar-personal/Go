import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class GameLogic {

    private Go game;
    private PointLogic pointLogic;
    private Tile[][] tiles;
    private int dim;
    private int moveNo;
    private int whitePlaced = 0;
    private int blackPlaced = 0;
    private int whiteCaptured = 0;
    private int blackCaptured = 0;
    private int[][] surroundingTiles = new int[][]{{+1, 0}, {0, -1}, {-1, 0}, {0, + 1}};
    private List<GoString> strings = new ArrayList<>();

    public GameLogic(Go game, Tile[][] tiles, int dim) {
        this.game = game;
        this.tiles = tiles;
        this.dim = dim;
        pointLogic = new PointLogic(strings);
    }


    public void tick(){
//        checkSingleCaptures();
        pointLogic.updatePoints(strings);
        checkStringCaptures();
    }

    public boolean canPlace(int row, int col, int side){
        List<Point> tryPlace = new ArrayList<>();
        Point p = new Point(col, row);
        tryPlace.add(p);

        int stringLiberties = 0;
        int enemyOccupied = 0;
        //check entire strings liberty count
        stringLiberties = countLiberties(tryPlace);
        enemyOccupied = countEnemyOccupied(tryPlace);


        if(stringLiberties == enemyOccupied && enemyOccupied > 1){
            //if would result in string capture
            if(StringCaptureOnPlace(p, side)){
                incerementMoveNo();
                tiles[row][col].setInternalCapture(true);
                return true;
            }else{
                System.out.println("Cannot place without Ko");
                return false;
            }
        }
        //cant be placed if would be surrounded
        //can be placed if Ko rule
        //if placment results in a string capture return true
        //if no capture and stone would be surrounded reutn false
        //ko?
        incerementMoveNo();
        return true;
    }

    public boolean StringCaptureOnPlace(Point p, int side){
        //for all strings if new stone causes string removal return true
        int killCount = 0;
        List<Point> others = new ArrayList();
        for (int[] direction : surroundingTiles) {
            int dx = p.x + direction[0];
            int dy = p.y + direction[1];
            Point newPoint = new Point(dx, dy);
            if (side != tiles[p.y][p.x].getSide()) {
                others.add(newPoint);
                if (dy >= 1 && dy <= dim) {
                    if (dx >= 1 && dx <= dim) {
                        int cl = countLiberties(others);
                        int ce = countEnemyOccupied(others);
                        if (cl == ce)
                            killCount++;
                    }
                }
                others.clear();
            }
        }
        if(killCount == 4){
            return true;
        }
        return false;
    }

    public void findTerritories(){

    }


    //when a group of stones is surrounded
    public void checkStringCaptures(){
        if(strings.isEmpty())
            return;
        Point toRemove = null;
        for(GoString string : strings){
            if(tiles[string.getRoot().y][string.getRoot().x].isInternalCapture()){
                tiles[string.getRoot().y][string.getRoot().x].setInternalCapture(false);
                continue;
            }
            List<Point> toCheck = new ArrayList<>();
            toCheck.add(string.getRoot());
            if(string.getNodes().size() > 0) {
                toCheck.addAll(string.getNodes());
            }

            int stringLiberties = 0;
            int enemyOccupied = 0;
            //check entire strings liberty count
            stringLiberties = countLiberties(toCheck);
            enemyOccupied = countEnemyOccupied(toCheck);

            if(stringLiberties > 0 && enemyOccupied > 0  && stringLiberties == enemyOccupied){
                //entire string is surrounded
                toRemove = string.getRoot();
                break;
            }
        }
        if(toRemove != null)
            removeString(toRemove);
    }

    public int countLiberties(List<Point> stones){
        int count = 0;
        for(Point p : stones) {
            count += 4;
            for (int[] direction : surroundingTiles) {
                int dx = p.x + direction[0];
                int dy = p.y + direction[1];

                if (dy < 1 || dy > dim) {
                    count--;
                }
                if (dx < 1 || dx > dim) {
                    count--;
                }

                if (dy >= 1 && dy <= dim) {
                    if (dx >= 1 && dx <= dim) {
                        Point check = new Point(dx, dy);
                        //check that the other tile is not a friendly stone and is definitely placed
                        if (tiles[check.y][check.x].getSide() == tiles[p.y][p.x].getSide()) {
                            if (tiles[check.y][check.x].getSide() >= 0) {
                                count--;
                            }
                        }
                    }
                }
            }
        }
        return count;
    }

    public int countEnemyOccupied(List<Point> occu){
        int count = 0;
        for(Point p : occu){
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

    //currently called by tile on tick
    public void checkStrings(int row, int col, int side){
        Tile tile = tiles[row][col];
        Point coords = new Point(col, row);
        //early exit
        if (tile.isLabel() || tile.getSide() == -1)
            return;

        if(strings.size() == 0){
            strings.add(new GoString(coords, new ArrayList<>(), side));
            return;
        }

        if(!pointLogic.PointIsRoot(coords)){
            //point is neither root nor node
            if(!pointLogic.PointIsNode(coords)){
                addToStrings(coords, side);
            }
        }
    }

    public void addToStrings(Point coords, int side){
        int emptyCount = 0;
        int adjacentCount = 0;
            for (int[] direction : surroundingTiles) {
                int dx = coords.x + direction[0];
                int dy = coords.y + direction[1];
                Point newCoords = new Point(dx, dy);
                //check bounds
                if (dy >= 1 && dy <= dim) {
                    if (dx >= 1 && dx <= dim) {
                        adjacentCount++;
                        Tile o = tiles[dy][dx];
                        //check if surrounding tiles are a root, if so add to it
                        if (o.getSide() == side) {
                            boolean root = pointLogic.PointIsRoot(newCoords);
                            //add to root if same colour
                            if (root && o.getSide() == side) {
                                //root exists so add point as node
                                pointLogic.getGoStringFromPoint(newCoords).addToNode(coords);
                                pointLogic.updatePoints(strings);
                                continue;
                            }
                            //if not key it might be a node, if so add to node
                            if(pointLogic.PointIsNode(newCoords)) {
                                //add if node found
                                if (o.getSide() == side) {
                                    //other tile is node so add as node to existing root
                                    pointLogic.getGoStringFromPoint(newCoords).addToNode(coords);
                                    pointLogic.updatePoints(strings);
                                    continue;
                                }
                            }
                        }else{
                            emptyCount++;
                        }
                    }
                }
        }

            //tile surrounded by empty tiles so new root
        if(emptyCount == adjacentCount) {
            strings.add(new GoString(coords, new ArrayList<>(), side));
            pointLogic.updatePoints(strings);
        }


        //merge strings
        checkForDuplicates(coords, side);
    }

    //check if this coordinate is a part of more than one string, if so a merge is needed
    public void checkForDuplicates(Point coords, int side){
        //find all string that contain a dupe
        List<GoString> duplicateStrings = new ArrayList<>();
        for (GoString string : strings){
            if(string.PointIsNode(coords) || string.PointIsRoot(coords)){
                if(string.getSide() == side)
                    duplicateStrings.add(string);
            }
        }

        //merge strings then delete duplicates
        if(duplicateStrings.size() > 1) {
            Point newKey = coords;
            List<Point> newValue = new ArrayList<>();
            for (int i = 0; i < duplicateStrings.size(); i++) {
                //node
                duplicateStrings.get(i).removeNode(coords);
                newValue.addAll(duplicateStrings.get(i).getNodes());
                Point r = duplicateStrings.get(i).getRoot();
                newValue.add(r);
                //remove duplicate strings as merger is replacing them
            }
            strings.removeAll(duplicateStrings);
            strings.add(new GoString(newKey, newValue, side));
        }
    }




    public void removeString(Point root){
        Tile remove = tiles[root.y][root.x];
        int count = 0;
        if(remove.getSide() == -1) {
            System.out.println("error removing string: cannot remove nothing at " + root.toString());
            return;
        }

        for(GoString g :strings){
            if(g.getRoot().equals(root)){
                if(g.getNodes().size() > 0){
                    for(Point p : g.getNodes()){
                        count += 1;
                        tiles[p.y][p.x].setSide(-1);
                        tiles[p.y][p.x].setPlaced(false);
                    }
                }
                tiles[g.getRoot().y][g.getRoot().x].setSide(-1);
                tiles[g.getRoot().y][g.getRoot().x].setPlaced(false);
                strings.remove(g);
                break;
            }
        }
        count++;

        if(remove.getSide() == 0){
            adjustScore(1, count);
        }else{
            adjustScore(0, count);
        }
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
