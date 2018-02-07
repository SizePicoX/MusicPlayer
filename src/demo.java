import com.Music.Music;
import com.List.MusicNode;

import javax.swing.*;

/**
 * 单元测试
 */
public class demo {
    private JButton 选择文件Button;
    private JButton 播放Button;
    private JPanel demo;


    MusicNode node;
    Music music;

    public static void main(String[] args) {
        //测试，由于MusicList对象未初始化，故在添加乐曲进入列表之前先初始化


        JFrame frame = new JFrame("demo");
        frame.setContentPane(new demo().demo);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


    /* TODO:
    TODO    2.务必考虑，newNode的生存周期在哪里？ */
    private demo() {
        选择文件Button.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.showOpenDialog(demo);
            String mp3FilePath = fileChooser.getSelectedFile().getAbsolutePath();






        });
        播放Button.addActionListener(e -> {

        });
    }
}
