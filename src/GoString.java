import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GoString {
    private Point root;
    private List<Point> nodes = new ArrayList<>();
    private int side;
    boolean surrounded = false;
    boolean hasTerritory;
    private List<Point> pointsInTerritory;

    public GoString(Point root, List<Point> nodes, int side) {
        this.root = root;
        this.nodes = nodes;
        this.side = side;
    }

    public void addToNode(Point point){
        if(!nodes.contains(point)){
            nodes.add(point);
        }
    }


    public boolean PointIsRoot(Point root){
            if(this.root == root)
                return true;
        return false;
    }

    public boolean PointIsNode(Point p){
        if(nodes.size() > 0)
            for(Point n : nodes){
                if(n.equals(p))
                    return true;
        }
        return false;
    }

    public void removeNode(Point p){
        if(nodes.contains(p)){
            nodes.remove(p);
        }
    }

    public int getSide() {
        return side;
    }


    public Point getRoot() {
        return root;
    }

    public void setRoot(Point root) {
        this.root = root;
    }

    public List<Point> getNodes() {
        return nodes;
    }

    public void setNodes(List<Point> nodes) {
        this.nodes = nodes;
    }

    public boolean isSurrounded() {
        return surrounded;
    }

    public void setSurrounded(boolean surrounded) {
        this.surrounded = surrounded;
    }

    public boolean isHasTerritory() {
        return hasTerritory;
    }

    public void setHasTerritory(boolean hasTerritory) {
        this.hasTerritory = hasTerritory;
    }

    public List<Point> getPointsInTerritory() {
        return pointsInTerritory;
    }

    public void setPointsInTerritory(List<Point> pointsInTerritory) {
        this.pointsInTerritory = pointsInTerritory;
    }
}
