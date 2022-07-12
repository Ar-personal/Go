import java.awt.*;
import java.net.URL;

public class Tile {
    private Go game;
    private float mX, mY;
    private int x, y, id = 0, tileID, posX, posY, side = -1;
    private boolean isPlaced = false, isLabel, isEdge, render = true;
    private int size;
    private String label;
    private Color color;

    public Tile(Go game, int x, int y, boolean isLabel, boolean isEdge, String label, int size, int posX, int posY, boolean render){
        this.game = game;
        this.x = x;
        this.y = y;
        this.isLabel = isLabel;
        this.label = label;
        this.size = size;
        this.posX = posX;
        this.posY = posX;
    }

    public void tick(){
        mX = game.getWindow().getMouseListener().getX();
        mY = game.getWindow().getMouseListener().getY();

        if (game.getWindow().getMouseListener().isMouseClicked()) {
            if (contains(mX, mY) && !isLabel) {
                if(!isPlaced) {
                    isPlaced = true;
                    game.getGameLogic().incerementMoveNo();
                    if (game.getGameLogic().getMoveNo() % 2 != 0) {
                        color = Color.black;
                        side = 0;
                    }else{
                        color = Color.white;
                        side = 1;
                    }
                    game.getWindow().getMouseListener().setMouseClicked(false);
                }
            }
            return;
        }
    }

    public void render(Graphics2D g2d){
        g2d.setColor(Color.red);
        g2d.fillRect(x, y, size, size);
        g2d.setColor(Color.black);

        if(!isLabel && render) {
            g2d.drawRect(x, y, size -1, size -1);
        }

        if(isLabel){
            g2d.drawString(label, x, y + 10);
        }

//
        if(isPlaced){
            g2d.setColor(color);
            g2d.fillOval(x - (size / 2), y - (size / 2), size , size);
        }


    }


    public int getSide() {
        return side;
    }

    public void setSide(int side) {
        this.side = side;
    }

    public boolean isPlaced() {
        return isPlaced;
    }

    public void setPlaced(boolean placed) {
        isPlaced = placed;
    }

    public boolean isRender() {
        return render;
    }

    public void setRender(boolean render) {
        this.render = render;
    }

    public int getTileID() {
        return tileID;
    }

    public void setTileID(int tileID) {
        this.tileID = tileID;
    }

    public boolean isLabel() {
        return isLabel;
    }

    public void setLabel(boolean label) {
        isLabel = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isEdge() {
        return isEdge;
    }

    public void setEdge(boolean edge) {
        isEdge = edge;
    }

    public boolean contains(float mX, float mY) {
        if(mX >= x - size && mY >= y - size && mX <= x + size / 2 && mY <= y + size / 2){
            return true;
        }
        return false;
    }
}

