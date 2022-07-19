import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuScreen {

    private Go game;
    private JFrame frame;
    private JPanel panel1;
    private JButton b1;

    public MenuScreen(Go game){
        this.game = game;
        createDisplay();
    }

    public void createDisplay(){
        frame = new JFrame();
        frame.setSize(new Dimension(400, 300));
        frame.setLocationRelativeTo(null);
        frame.setTitle("Go");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        panel1 = new JPanel();


        JLabel label = new JLabel("Select game size:");

        String[] options = {"3", "5", "7", "9", "11", "13", "15", "17", "19", "21"};
        JComboBox<String> jComboBox = new JComboBox<>(options);
        jComboBox.setSelectedIndex(4);
        ((JLabel)jComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        b1 = new JButton("Play");

        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dimensions = Integer.parseInt(jComboBox.getItemAt(jComboBox.getSelectedIndex()));
                game.createScreen(dimensions);
                frame.setVisible(false);
            }
        });

        panel1.add(label);
        panel1.add(jComboBox);
        panel1.add(b1);


        frame.add(panel1);
        frame.setVisible(true);
    }

    public JFrame getFrame() {
        return frame;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }
}
