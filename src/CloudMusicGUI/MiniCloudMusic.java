package CloudMusicGUI;

import com.MusicPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
 * CloudMusic的迷你模式
 */
public class MiniCloudMusic extends JFrame {
    /* MiniCloudMusic中的GUI组件 */
    /* 面板 */
    private  JPanel myPanel;
    private  JPanel playModule;
    private  JPanel titleBar;
    private  JPanel musicInfoBar;
    /* 按钮 */
    private JButton priorMusic;
    public JButton play_pause;
    private JButton nextMusic;
    private JButton close;
    private JButton enlarge;
    /* 其他组件 */
    public JTextArea musicInfo;
    /**
     * 拖动MiniCloudMusic相关字段
     * 记录下拖动组件时，鼠标相对于Frame的位置
     */
    private static Point pre_point;
    /**
     * 拖动MiniCloudMusic相关字段
     * 拖动组件动作结束后，Frame的左上角顶点坐标
     */
    private static Point end_point;

    /**
     * 唯一的MiniCloudMusic对象.使用范例:
     * MiniCloudMusic.miniCloudMusic = MiniCloudMusic.getMiniCloudMusic(tray);
     */
    public static MiniCloudMusic miniCloudMusic;
    /**
     * 初始化一个MiniCloudMusic.设定其所有的界面,监听器,关联的系统托盘以及位置.
     */
    private MiniCloudMusic() {

        /* 为MiniCloudMusic设定UI */
        setUI();

        /* 为Frame设定拖拽 */
        setDraggable(this);
        /* JButton的数组 */
        /* 为所有的按钮添加监听器，使之可以拖拽 */
        JButton[] buttons = {priorMusic, play_pause,nextMusic,close,enlarge};
        for (JButton button : buttons){
            setDraggable(button);
        }
        /* 为其他组件添加监听器，使之可以拖拽 */
        setDraggable(musicInfo);
        /* 以下是playModule的监听器 */
        setPlayModuleListener();
        /* 以下是titleBar和musicInfoBar的监听器 */
        setBarListener();
        /* 添加组件 */
        add(myPanel);
        /* 关闭边框 */
        setUndecorated(true);
        /* 将所有巨剑装入Frame中 */
        pack();
        /* 设定MiniCloudMusic的初始位置 */
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        end_point = new Point(screenSize.width / 2,0);
        setLocation(end_point);
    }
    /**
     * 为MiniCloudMusic设定UI
     */
    private void setUI(){
        /* 设定图标 */
        ImageIcon icon = new ImageIcon("src\\icon\\format.png");
        setIconImage(icon.getImage());
        /* 设定LookAndFell */
        String lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }
        //初始化乐曲基本信息文本
        musicInfo.setText(MusicPlayer.getCurrentMusicInfo());
    }
    /**
     * 在关闭边框的情况下，使得MiniCloudMusic可拖拽
     * P.S.使用泛型，使得不必定义多个完全一样就是参数不同的方法
     */
    private <E extends Component> void setDraggable(E e){
        /* 当鼠标点击和松开时 */
        e.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                /* 记录下鼠标第一次点击时相对于组件的位置 */
                pre_point = new Point(e.getX(),e.getY());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                /* 鼠标离开时将光标复原 */
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                /* 当鼠标经过组件时，将光标置为手的形状 */
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });
        /* 当鼠标拖动时 */
        e.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                end_point = new Point(getLocation().x + e.getX() - pre_point.x,
                        getLocation().y + e.getY() - pre_point.y);
                setLocation(end_point);
            }
        });
    }
    /**
     * 为playModule设置监听器
     * 当currentMusicNode不存在时，则点击将不会出发事件
     */
    private void setPlayModuleListener(){
        /* priorMusic */
        priorMusic.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                CloudMusic.cloudMusic.playPriorMusic();
            }
        });

        /* play */
        play_pause.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                CloudMusic.cloudMusic.SET_PLAY_OR_PAUSE();
            }
        });

        /* nextMusic */
        nextMusic.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                CloudMusic.cloudMusic.playNextMusic();
            }
        });
    }
    /**
     * 为 titleBar 和 musicInfoBar 设定监听器
     */
    private void setBarListener(){
        /* close */
        close.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                /* 此时隐藏MiniCloudMusic.可以在系统托盘区找到它 */
                setVisible(false);
            }
        });

        /* enlarge */
        enlarge.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setVisible(false);
                //更改系统托盘对应的显示值
                CloudMusicTray.tray.frameMode.setSelectedIndex(0);
                CloudMusic.cloudMusic.setVisible(true);
            }
        });
    }
    /**
     * 使用该法官法以初始化MiniCloudMusic对象
     * @return MiniCloudMusic
     */
    public static MiniCloudMusic getMiniCloudMusic(){
        return new MiniCloudMusic();
    }
}
