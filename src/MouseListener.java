
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class MouseListener implements java.awt.event.MouseListener, MouseMotionListener {
    private boolean mouseReleased, mousePressed, mouseClicked = false, mouseRightButtonClicked = false , mouseMiddleClicked = false, mouseMiddlePressed, mouseDragged;

    private float x, y, lastUpdatedX, lastUpdatedY;

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1){
            mouseClicked = true;
        }

        if(e.getButton() == MouseEvent.BUTTON2){
            mouseMiddleClicked = true;
            mouseClicked = false;
        }

        if(e.getButton() == MouseEvent.BUTTON3){
            mouseRightButtonClicked = true;
            mouseClicked = false;
        }



    }

    @Override
    public void mousePressed(MouseEvent e) {
        lastUpdatedX = e.getX();
        lastUpdatedY = e.getY();
        if(e.getButton() == MouseEvent.BUTTON2){
            mouseMiddlePressed = true;

        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getX() != lastUpdatedX) {
            x = lastUpdatedX;
        }
        if (e.getX() != lastUpdatedY) {
            y = lastUpdatedY;
        }
        if(e.getButton() == MouseEvent.BUTTON2){
            mouseMiddlePressed = false;
        }
        mouseClicked = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {
        mouseClicked = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        return;

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        x = e.getX();
        y = e.getY();

    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }


    public boolean isMouseClicked() {
        return mouseClicked;
    }

    public void setMouseClicked(boolean mouseClicked) {
        this.mouseClicked = mouseClicked;
    }

    public boolean isMouseRightButtonClicked() {
        return mouseRightButtonClicked;
    }

    public void setMouseRightButtonClicked(boolean mouseRightButtonClicked) {
        this.mouseRightButtonClicked = mouseRightButtonClicked;
    }

    public boolean isMouseMiddleClicked() {
        return mouseMiddleClicked;
    }

    public void setMouseMiddleClicked(boolean mouseMiddleClicked) {
        this.mouseMiddleClicked = mouseMiddleClicked;
    }

    public boolean isMouseMiddlePressed() {
        return mouseMiddlePressed;
    }

    public void setMouseMiddlePressed(boolean mouseMiddlePressed) {
        this.mouseMiddlePressed = mouseMiddlePressed;
    }
}
