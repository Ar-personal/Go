import java.awt.*;
import java.util.*;
import java.util.List;

public class GameLogic {

    private Go game;
    private PointLogic pointLogic;
    private Tile[][] tiles;
    private int dim;
    private int moveNo, currentTerritorySide = -1;
    private int whitePlaced = 0;
    private int blackPlaced = 0;
    private int whiteCaptured = 0;
    private int blackCaptured = 0;
    private int[][] surroundingTiles = new int[][]{{+1, 0}, {0, -1}, {-1, 0}, {0, + 1}};
    private List<GoString> strings = new ArrayList<>();
    private List<List<Point>> territories = new ArrayList<>();

    public GameLogic(Go game, Tile[][] tiles, int dim) {
        this.game = game;
        this.tiles = tiles;
        this.dim = dim;
        pointLogic = new PointLogic(strings);
    }


    public void tick(){
        pointLogic.updatePoints(strings);
    }

    public boolean canPlace(int row, int col, int side){
        List<Point> tryPlace = new ArrayList<>();
        Point p = new Point(col, row);
        tryPlace.add(p);

        Tile t = tiles[row][col];
        //cant place on an existing tile
        if(t == null || t.isPlaced()){
            tiles[row][col].setSide(t.getSide());
            tiles[row][col].setPlaced(t.isPlaced());
            return false;
        }

        int stringLiberties = 0;
        int enemyOccupied = 0;
        //check entire strings liberty count
        stringLiberties = countLiberties(tryPlace, side);
        enemyOccupied = countEnemyOccupied(tryPlace);

        //Ko rule
        if(tiles[row][col].isKo()){
            System.out.println("cant place with Ko");
            tiles[row][col].setPlaced(false);
            tiles[row][col].setSide(-1);
            return false;
        }


        if(stringLiberties == enemyOccupied && enemyOccupied > 1){
            //if would result in string capture
            if(StringCaptureOnPlace(p, side)){
                incerementMoveNo();
                return true;
            }else{
                return false;
            }
        }
        //cant be placed if would be surrounded
        //can be placed if Ko rule
        //if placment results in a string capture return true
        //if no capture and stone would be surrounded reutn false
        //ko?
        resetKo();
        incerementMoveNo();
        return true;
    }

    public void resetKo(){
        for(int row = 1; row < dim; row++){
            for(int col = 1; col < dim; col++){
                tiles[row][col].setKo(false);
            }
        }
    }

    public boolean StringCaptureOnPlace(Point p, int side){
        //for all strings if new stone causes string removal return true
        int killCount = 0;
        Point koTile = new Point();
        List<Point> others = new ArrayList();
        for (int[] direction : surroundingTiles) {
            int dx = p.x + direction[0];
            int dy = p.y + direction[1];
            Point newPoint = new Point(dx, dy);
                others.add(newPoint);
                if (dy >= 1 && dy <= dim)
                    if (dx >= 1 && dx <= dim)
                        if (side != tiles[newPoint.y][newPoint.x].getSide()) {
                            int cl = countLiberties(others, side);
                            int ce = countEnemyOccupied(others);
                            if (cl == ce) {
                                killCount++;
                                koTile = new Point(dx, dy);
                            }
                    }
                others.clear();
        }
        if(killCount == 4)
            return true;
        if(killCount == 1) {
            tiles[koTile.y][koTile.x].setKo(true);
            return true;
        }
        return false;
    }

    public void findTerritories() {
        //dibt bother unless a few stones have been placed
        if(getMoveNo() < 3)
            return;

        for (int row = 1; row < dim; row++) {
            outerloop:
            for (int col = 1; col < dim; col++) {

                List<Point> territory = new ArrayList<>();
                Point newPoint = new Point(col, row);
                if(tiles[row][col].getSide() >= 0)
                    continue;

                for(List<Point> p : territories){
                    if(p.contains(newPoint))
                        continue outerloop;
                }

                if(!territory.contains(newPoint))
                    territory.add(newPoint);

                int side = tiles[newPoint.y][newPoint.x].getSide();

                int size = findAdjacentEmpties(territory).size();
                while (true){
                    if(size + findAdjacentEmpties(territory).size() == size)
                        break;
                    territory.addAll(findAdjacentEmpties(territory));
                    size = findAdjacentEmpties(territory).size();
                }

                //if the liberties of this territory is surrounded by equal amount of same stone its a terri
                if(checkIsTerritory(territory)) {
                    int libs = countLiberties(territory, -1);
                    int enemy = countEnemyOccupied(territory);

                    if(libs > 0 && enemy > 0  && libs == enemy){
                        territories.add(new ArrayList<>(territory));
                        if(currentTerritorySide == 0)
                            adjustScore(1, territory.size());
                        else
                            adjustScore(0, territory.size());
                        territory.clear();
                    }
                }
            }
        }
    }

    private List<Point> findAdjacentEmpties(List<Point> list){
        List<Point> toAdd = new ArrayList<>();
        for(Point p : list){
            for (int[] direction : surroundingTiles) {
                int dx = p.x + direction[0];
                int dy = p.y + direction[1];
                Point newPoint = new Point(dx, dy);
                    if (dy >= 1 && dy <= dim)
                        if (dx >= 1 && dx <= dim)
                            if(tiles[dy][dx].getSide() == -1)
                                if(!list.contains(newPoint) && !toAdd.contains(newPoint))
                                    toAdd.add(newPoint);

            }
        }
        return toAdd;
    }

    //when a group of stones is surrounded
    public void checkStringCaptures(){
        if(strings.isEmpty())
            return;
        Point toRemove = null;
        for(GoString string : strings){
            List<Point> toCheck = new ArrayList<>();
            toCheck.add(string.getRoot());
            if(string.getNodes().size() > 0) {
                toCheck.addAll(string.getNodes());
            }

            int stringLiberties = 0;
            int enemyOccupied = 0;
            //check entire strings liberty count
            stringLiberties = countLiberties(toCheck, string.getSide());
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


    public boolean checkIsTerritory(List<Point> empties){
        boolean isWhite = false;
        boolean isBlack = false;
        for(Point p : empties){
            for(int[] direction : surroundingTiles) {
                int dx = p.x + direction[0];
                int dy = p.y + direction[1];

                if (dy >= 1 && dy <= dim) {
                    if (dx >= 1 && dx <= dim) {
                        Point check = new Point(dx, dy);
                        //check if all the surrounding tiles are the same, if not it cant be a territory
                        if (tiles[check.y][check.x].getSide() == 0) {
                            if(isBlack)
                                return false;
                            isWhite = true;
                        }

                        if (tiles[check.y][check.x].getSide() == 1) {
                            if(isWhite)
                                return false;
                            isBlack = true;
                        }
                        currentTerritorySide = tiles[check.y][check.x].getSide();
                    }
                }
            }
        }
        return true;
    }


    public int countLiberties(List<Point> stones, int side){
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
                        //check that the other tile is not a friendly stone
                        if (tiles[check.y][check.x].getSide() == side) {
                                count--;
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

        checkStringCaptures();
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
        int side = remove.getSide();
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

        if(side == 0){
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


    public int getWhiteCaptured() {
        return whiteCaptured;
    }

    public int getBlackCaptured() {
        return blackCaptured;
    }
}
