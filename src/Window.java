import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
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
    private List<Point> handicaps = new ArrayList();

    public Window(Go game, int dims){
        this.game = game;
        this.dims = dims;
        width = dims * scale;
        height = dims * scale;
        size = width / (dims + 1);
        createDisplay();
        createLayout();

        int center = dims * (size / 2);
        handicaps.add(new Point(center, center));
        handicaps.add(new Point(center, center - (int)(size / 4) - (int) center / 2));
        handicaps.add(new Point(center, center + (int)(size / 4) + (int) center / 2));
        handicaps.add(new Point(center - (int)(size / 4) - (int) center / 2, center - (int)(size / 4) - (int) center / 2));
        handicaps.add(new Point(center + (int)(size / 4) + (int) center / 2, center + (int)(size / 4) + (int) center / 2));
        handicaps.add(new Point(center + (int)(size / 4) + (int) center / 2, center));
        handicaps.add(new Point(center - (int)(size / 4) - (int) center / 2, center));
        handicaps.add(new Point(center + (int)(size / 4) + (int) center / 2, center - (int)(size / 4) - (int) center / 2));
        handicaps.add(new Point(center - (int)(size / 4) - (int) center / 2, center + (int)(size / 4) + (int) center / 2));

    }

    public void createDisplay(){
        frame = new JFrame();
        frame.setTitle(title);
        frame.setSize(new Dimension(width, height));
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel(new GridLayout());

        JButton jButton1 = new JButton();
        jButton1.setText("resign");
        JButton jButton2 = new JButton();
        jButton2.setText("end");
        JButton jButton3 = new JButton();
        jButton3.setText("pass");
        JButton jButton4 = new JButton();
        jButton4.setText("new");

        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(game.getGameLogic().getMoveNo() % 2 != 0)
                    JOptionPane.showMessageDialog(null, "White resigns, black wins");
                else{
                    JOptionPane.showMessageDialog(null, "Black resigns, white wins");
                }
            }
        });

        jButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.getGameLogic().findTerritories();
                String result = "";
                if(game.getGameLogic().getBlackCaptured() == game.getGameLogic().getWhiteCaptured()){
                    result = "The game ends in a draw";
                }

                if(game.getGameLogic().getBlackCaptured() > game.getGameLogic().getWhiteCaptured()){
                    result = "White is victorious";
                }else{
                    result = "Black is victorious";
                }

                JOptionPane.showMessageDialog(null, result);
            }
        });

        jButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.getGameLogic().incerementMoveNo();
            }
        });

        jButton4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.restart();
            }
        });



        this.setPreferredSize(new Dimension(width, height));
        this.setMaximumSize(new Dimension(width, height));
        this.setMinimumSize(new Dimension(width, height));

        mouseListener = new MouseListener();

        frame.addMouseListener(mouseListener);
        frame.addMouseMotionListener(mouseListener);

        this.addMouseListener(mouseListener);
        this.addMouseMotionListener(mouseListener);
        buttonPanel.add(jButton1);
        buttonPanel.add(jButton2);
        buttonPanel.add(jButton3);
        buttonPanel.add(jButton4);
        this.add(buttonPanel, BorderLayout.SOUTH);
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
                tiles[row][column] = new Tile(game, x, y, false, "", size, column, row, dims);

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
        if(game.getGameLogic() != null)
            frame.setTitle("Go     Move: " + game.getGameLogic().getMoveNo() + "     Black points: " + game.getGameLogic().getWhiteCaptured()
                    + "     White points: " + game.getGameLogic().getBlackCaptured());
    }

    public void render(Graphics2D graphics2D){
        if(tiles == null)
            return;
        for (int row = 0; row < tiles.length; row++) {
            for (int column = 0; column < tiles[row].length; column++) {
                tiles[row][column].render(graphics2D);
            }
        }

        for(Point p : handicaps){
            graphics2D.fillOval(p.x + (int)(size / 2.5), p.y + (int)(size / 2.5), size / 6, size / 6);
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
