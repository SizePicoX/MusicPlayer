package GUI;

import com.EnjoyYourMusic;
import com.List.MusicNode;
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
    private JButton play;
    private JButton nextMusic;
    private JButton close;
    private JButton enlarge;
    /* 其他组件 */
    private JTextArea musicInfo;
    private JSlider currentPlayTime;

    /**
     * 拖动MiniCloudMusic相关字段
     * 记录下拖动组件时，鼠标相对于Frame的位置
     */
    private static Point pre_point;
    /**
     * 拖动MiniCloudMusic相关字段
     * 拖动组件动作结束后，Frame的左上角顶点坐标
     */
    private static Point end_point = null;

    /**
     * MiniCloudMusic所关联的系统托盘
     */
    private CloudMusicTray tray;
    /**
     * 唯一的MiniCloudMusic对象.使用范例:
     * MiniCloudMusic.miniCloudMusic = MiniCloudMusic.getMiniCloudMusic(tray);
     */
    public static MiniCloudMusic miniCloudMusic;
    /**
     * 初始化一个MiniCloudMusic.设定其所有的界面,监听器,关联的系统托盘以及位置.
     */
    private MiniCloudMusic(CloudMusicTray tray) {

        /* 为MiniCloudMusic设定UI */
        setUI();

        /* 设定MiniCloudMusic所关联的系统托盘 */
        this.tray = tray;

        /* 为Frame设定拖拽 */
        setDraggable(this);
        /* JButton的数组 */
        /* 为所有的按钮添加监听器，使之可以拖拽 */
        JButton[] buttons = {priorMusic,play,nextMusic,close,enlarge};
        for (JButton button : buttons){
            setDraggable(button);
        }
        /* 为其他组件添加监听器，使之可以拖拽 */
        setDraggable(musicInfo);
        /* 以下是playModule的监听器 */
        /* 当currentMusicNode不存在时，不允许点击playModule中的组件 */
//        MusicNode node = MusicPlayer.currentMusicNode;
//        if (node != null){
//            setPlayModuleListener(node);
//        }
        /* 以下是titleBar和musicInfoBar的监听器 */
        setBarListener();
        /* 添加组件 */
        add(myPanel);
        /* 关闭边框 */
        setUndecorated(true);

        /* 重定位Frame的位置 */
        setLocation();
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
    }
    /**
     * 设定Frame的位置.具有记忆性.能够记录下上一次关闭MiniCloudMusic时窗体的位置
     */
    private void setLocation() {
        /* 以上一次结束MiniCloudMusic的位置来重定位MiniCloudMusic */
        if (end_point != null){
            setLocation(end_point);
        }
        /* 当第一次启动MiniCloudMusic时，以屏幕最上方的中点为重定位初始值 */
        else {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            setLocation((int)screenSize.getWidth() / 2,0);
        }
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
                /* 将光标置为手的形状 */
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                /* 将光标置为手的形状 */
                setCursor(new Cursor(Cursor.HAND_CURSOR));
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
     * @param node 当前播放的MusicNode
     */
    private void setPlayModuleListener(MusicNode node){
        /* priorMusic */
        priorMusic.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }
        });

        /* play */
        play.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                /* 播放乐曲 */
                if (!play.isSelected()){
                    play.setSelected(true);
                    EnjoyYourMusic.play(node);
                }
                /* 暂停播放 */
                else {
                    play.setSelected(false);
                    MusicPlayer.stop();
                }
            }
        });

        /* nextMusic */
        nextMusic.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

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
                tray.setCurrentFrame(miniCloudMusic);
            }
        });

        /* enlarge */
        enlarge.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }
        });

        /* currentPlayTime */
        currentPlayTime.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // TODO: 2018/2/15 这里添加调节播放进度的代码
            }
        });
    }
    /**
     * 使用该法官法以初始化MiniCloudMusic对象
     * @param tray MiniCloudMusic所绑定的系统托盘
     * @return MiniCloudMusic
     */
    public static MiniCloudMusic getMiniCloudMusic(CloudMusicTray tray){
        return new MiniCloudMusic(tray);
    }
    /**
     * 测试
     */
    public static void main(String[] args){
        long a = System.currentTimeMillis();
        CloudMusicTray tray = new CloudMusicTray();
        long b = System.currentTimeMillis();
        System.out.println("创建系统托盘的时间 :" + ( b - a));
        a = System.currentTimeMillis();
        MiniCloudMusic.miniCloudMusic = MiniCloudMusic.getMiniCloudMusic(tray);
        miniCloudMusic.pack();
        b = System.currentTimeMillis();
        System.out.println("创建MiniCloudMusic的时间 :" + ( b - a));
        miniCloudMusic.setVisible(true);
    }
}
