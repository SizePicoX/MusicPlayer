package GUI;

import com.List.MusicNode;
import java.awt.Insets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class CloudMusic extends JFrame{
    private JPanel myPanel;
    private JPanel list;
    private JPanel music;
    private JList<MusicNode> musicList;
    private JTextArea musicInfo;
    private JPanel currentMusicListPanel;
    private JPanel musicListOperationAndLabelPanel;
    private JList<MusicNode> currentMusicList;
    private JButton addSongs;
    private JButton addSingleSong;
    private JTextField searchSongs;
    private JLabel musicListName;
    private JPanel musicListOperation;
    private JPanel labelPanel;
    private JLabel songName;
    private JLabel artist;
    private JLabel album;
    private JLabel songTime;
    private JButton addSongList;
    private JLabel songList;
    private JPanel labelAndAddSongPanel;
    private JButton priorMusic;
    private JButton nextMusic;
    private JButton play_pause;
    private JProgressBar currentPlayTime;
    private JComboBox playMode;
    private JPanel playModule;
    /* titleBar组件 */
    private JPanel titleBar;
    private JButton miniMode;
    private JButton iconic;
    private JButton maxiMize;
    private JButton close;
    private JLabel CloudMusic;

    private static Point pre_point;
    private static Point end_point = null;

    /**
     * CloudMusic所绑定的系统托盘
     */
    private CloudMusicTray tray;
    /**
     * 唯一的CloudMusic对象.使用范例:
     * CloudMusic.cloudMusic = CloudMusic.getCloudMusic(tray);
     */
    public static CloudMusic cloudMusic;

    /**
     * 通过该方法以启动播放器的主界面
     * @param tray CloudMusic所绑定的系统托盘
     */
    private CloudMusic(CloudMusicTray tray){
        /* 绑定系统托盘 */
        this.tray = tray;
        /* 为CloudMusic设定UI */
        setUI();
        /* 各个组件的数组 */
        /* titleBar的组件的数组 */
        JComponent[] titleBarComponents = {titleBar,miniMode,iconic, maxiMize,close,CloudMusic};

        /* 使得CloudMusic能够被拖拽 */
        for (JComponent component : titleBarComponents){
            setDraggable(component);
        }

        /* 设定titleBar的监听器 */
        setListenerForTitleBar();

        /* 添加组件 */
        add(myPanel);
        /* 设定CloudMusic的位置 */
        setLocation();
    }

    /**
     * 为CloudMusic设定UI
     */
    private void setUI(){
        /* 设定LookAndFell */
        String lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }
        /* 设定图标 */
        ImageIcon icon = new ImageIcon("src\\icon\\format.png");
        setIconImage(icon.getImage());
        /* 去掉标题栏,以添加自己的标题栏 */
        setUndecorated(true);
    }

    /**
     * 设定CloudMusic的位置
     */
    private void setLocation(){
        if (end_point != null){
            setLocation(end_point);
        }
        else {
            //默认居中
            //获得屏幕边缘,以获取任务栏高度
            Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(this.getGraphicsConfiguration());
            int x = (Toolkit.getDefaultToolkit().getScreenSize().width - 1000) / 2;
            int y = (Toolkit.getDefaultToolkit().getScreenSize().height - screenInsets.bottom - 600) / 2;//减去任务栏高度
            end_point = new Point(x,y);
            setLocation(end_point);
        }
    }

    /**
     * 使得CloudMusic能够被拖拽
     */
    private void setDraggable(JComponent component){
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                /* 获得当前点击事件的鼠标坐标 */
                pre_point = new Point(e.getX(),e.getY());
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                /* 还原鼠标样式 */
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                /* 鼠标样式设为手形 */
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });
        component.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                end_point = new Point(getLocation().x + e.getX() - pre_point.x,
                        getLocation().y + e.getY() - pre_point.y);
                setLocation(end_point);
            }
        });
    }

    /**
     * 为titleBar设定监听器
     */
    private void setListenerForTitleBar(){
        /* 启动mini模式 */
        miniMode.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //隐藏CloudMusic
                setVisible(false);
                //显示MiniCloudMusic
                MiniCloudMusic.miniCloudMusic.setVisible(true);
                //更改系统托盘所绑定的Frame
                tray.setCurrentFrame(MiniCloudMusic.miniCloudMusic);
            }
        });
        /* 图标化 */
        iconic.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setExtendedState(JFrame.ICONIFIED);
            }
        });
        /* 最大化 */
        maxiMize.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!maxiMize.isSelected()){
                    setExtendedState(JFrame.MAXIMIZED_BOTH);
                    maxiMize.setSelected(true);
                }
                else {

                    // TODO: 2018/2/20 从最大化窗口恢复通常大小的窗口
//                    Dimension dimension = new Dimension(1000,600);
//                    setPreferredSize(dimension);
                    maxiMize.setSelected(false);
                }
            }
        });
        /* 隐藏CloudMusic */
        close.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setVisible(false);
            }
        });
    }

    /**
     * 使用该方法以构造CloudMusic对象
     * @param tray 所绑定的系统托盘
     * @return CloudMusic
     */
    public static CloudMusic getCloudMusic(CloudMusicTray tray){
        CloudMusic cloudMusic = new CloudMusic(tray);
        //初始化系统托盘所绑定的frame
        cloudMusic.tray.setCurrentFrame(cloudMusic);
        return cloudMusic;
    }
    /* 整个播放器的启动方法 */
    public static void main(String[] args) {
        //初始化系统托盘
        CloudMusicTray tray = new CloudMusicTray();
        //初始化MiniCloudMusic
        MiniCloudMusic.miniCloudMusic = MiniCloudMusic.getMiniCloudMusic(tray);

        CloudMusic cloudMusic = new CloudMusic(tray);

        tray.setCurrentFrame(cloudMusic);
        cloudMusic.pack();
        cloudMusic.setVisible(true);
    }
}
