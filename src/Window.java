import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Arrays;
import java.util.List;

public class Window extends JPanel{

    private Go game;
    private JFrame frame;
    private String title;
    private int  x = 0, y = 0, dims, width, height, scale = 40;
    private Tile[][] tiles;
    int size;

    private boolean gameOverWindowOpen = false;

    private MouseListener mouseListener;
    private BorderLayout borderLayout = new BorderLayout();

    public Window(Go game, int dims){
        this.game = game;
        this.dims = dims;
        width = dims * scale;
        height = dims * scale;
        size = width / (dims + 1);
        createDisplay();
        createLayout();
    }

    public void createDisplay(){
        frame = new JFrame();
        frame.setTitle(title);
        frame.setSize(new Dimension(width, height));
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setPreferredSize(new Dimension(width, height));
        this.setMaximumSize(new Dimension(width, height));
        this.setMinimumSize(new Dimension(width, height));

        mouseListener = new MouseListener();

        frame.addMouseListener(mouseListener);
        frame.addMouseMotionListener(mouseListener);

        this.addMouseListener(mouseListener);
        this.addMouseMotionListener(mouseListener);
        this.setLayout(new GridBagLayout());

        frame.add(this);
        frame.pack();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        render(g2d);
    }


    public void createLayout(){
        List<String> yLabel = Arrays.asList("9", "8", "7", "6", "5", "4", "3", "2", "1", " ");
        tiles = new Tile[dims + 1][dims + 1];

        int i = 0;
        for (int row = 0; row < tiles.length; row++) {
            x = 0;
            for (int column = 0; column < tiles[row].length; column++) {
                tiles[row][column] = new Tile(game, x, y, false, false, "", size, column, row, true);

                //the fringe tiles
                if(row == 1 & column == 0 || column == 1 && row == 0){
                    tiles[row][column].setLabel(true);
                }


                //check for out label tiles
                if(column == 0 || column >= dims)
                    tiles[row][column].setRender(false);
                if(row == 0 || row >= dims)
                    tiles[row][column].setRender(false);

                if(column == 0 || column == dims + 1)
                    tiles[row][column].setLabel(true);
                if(row == 0 || row == dims + 1)
                    tiles[row][column].setLabel(true);

                if(row == 0 && column > 0 && column < dims + 1)
                        tiles[row][column].setLabel(getCharForNumber(column));

                if(column == 0 && row >= 1)
                    tiles[row][column].setLabel(String.valueOf(dims - row + 1));

                tiles[row][column].setTileID(i);
                i++;
                x += size;
            }
            y += size;
        }
    }

    public void tick(){
        for (int row = 0; row < tiles.length; row++) {
            for (int column = 0; column < tiles[row].length; column++) {
                tiles[row][column].tick();
            }
        }
    }

    public void render(Graphics2D graphics2D){
        if(tiles == null)
            return;
        for (int row = 0; row < tiles.length; row++) {
            for (int column = 0; column < tiles[row].length; column++) {
                tiles[row][column].render(graphics2D);
            }
        }
    }

    //https://stackoverflow.com/questions/10813154/how-do-i-convert-a-number-to-a-letter-in-java
    private String getCharForNumber(int i) {
        return i > 0 && i < 27 ? String.valueOf((char)(i + 64)) : null;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public MouseListener getMouseListener() {
        return mouseListener;
    }

    public void setMouseListener(MouseListener mouseListener) {
        this.mouseListener = mouseListener;
    }

    public JFrame getFrame() {
        return frame;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    public Tile[][] getTiles() {
        return tiles;
    }
}
