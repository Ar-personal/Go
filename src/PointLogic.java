import java.awt.*;
import java.util.List;

public class PointLogic {

    private List<GoString> strings;
    public PointLogic(List<GoString> strings) {
        this.strings = strings;
    }

    public void updatePoints(List<GoString> strings){
        this.strings = strings;
    }

    public boolean PointIsRoot(Point root){
        for(GoString g : strings){
            if(g.getRoot().equals(root))
                return true;
        }
        return false;
    }

    public boolean PointIsNode(Point p){
        for(GoString g : strings){
            if(g.getNodes().size() > 0)
                for(Point n : g.getNodes()){
                    if(n.equals(p))
                        return true;
                }
        }
        return false;
    }


    public int getRootIndex(Point root){
        for(GoString g : strings){
            if(g.getRoot().equals(root));
                return strings.indexOf(g);
        }
        return -1;
    }

    public int RootIndexFromNode(Point p){
        for(GoString g : strings){
            if(g.getNodes().size() > 0)
                for(Point n : g.getNodes()){
                    if(n.equals(p));
                        return strings.indexOf(g);
                }
        }
        return -1;
    }

    public GoString getGoStringFromPoint(Point p){
        for(GoString g : strings){
            if(p.equals(g.getRoot()))
                return g;
            if(g.getNodes().size() > 0)
                if(g.getNodes().contains(p)){
                        return g;
                }
        }
        return null;
    }

}
