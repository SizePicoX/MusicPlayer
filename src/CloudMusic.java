import javax.swing.*;

public class CloudMusic {
    public static void main(String[] args) {
        JFrame frame = new JFrame("CloudMusic");
        frame.setContentPane(new CloudMusic().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private JButton 上一首Button;
    private JPanel panel1;
    private JButton 暂停开始Button;
    private JButton 下一首Button;
    private JTextArea textArea1;
    private JProgressBar progressBar1;
    private JSlider slider1;
    private JComboBox comboBox1;
    private JTextArea textArea2;
    private JTextField textField1;
    private JButton 添加Button;
    private JButton 删除Button;

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
