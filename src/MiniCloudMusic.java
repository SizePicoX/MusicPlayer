import javax.swing.*;

public class MiniCloudMusic {
    private JPanel panel;
    private JButton button1;
    private JButton button2;

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        JFrame frame = new JFrame("MiniCloudMusic");




        frame.setContentPane(new MiniCloudMusic().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
