package CloudMusicGUI;

import com.List.MusicNode;

import java.awt.Insets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CloudMusic extends JFrame{
    /* CloudMusic根面板 */
    private JPanel myPanel;
    /* list组件 */
    private JPanel list;//list主面板
    private JTextArea musicInfo;//显示乐曲基本信息的文本框.格式为 "乐曲名-歌手"
    private JPanel labelAndAddSongPanel;//"我的歌单"标签和"添加歌单"按钮
    private JLabel songList;//"我的歌单"标签
    private JButton addSongList;//添加歌单按钮
    private JList<MusicNode> musicList;//当前所有乐曲列表
    /* music组件 */
    private JPanel music;//music主面板
    private JPanel musicListOperationAndLabelPanel;//对musicList的相关操作及label的面板
    private JLabel musicListName;//此时选定的musicList名字
    private JPanel labelPanel;//music的相关label的面板
    private JLabel songName;//音乐标题
    private JLabel artist;//歌手
    private JLabel album;//专辑
    private JLabel songTime;//时长
    private JPanel musicListOperation;//musicList的相关操作面板
    private JButton choseFolder;//选择目录
    private JButton matchSongs;//匹配乐曲
    private JTextField searchSongs;//搜索乐曲
    private JButton cleanSearchField;
    private JPanel currentMusicListPanel;//当前显示的乐曲列表的面板
    private JList<MusicNode> currentMusicList;//当前显示的乐曲列表
    /* playModule组件 */
    private JPanel playModule;//playModule主面板
    private JButton priorMusic;//上一首
    private JButton nextMusic;//下一首
    private JButton play_pause;//开始和暂停
    private JProgressBar currentPlayTime;//进度条
    private JComboBox playMode;//播放模式
    /* titleBar组件 */
    private JPanel titleBar;//titleBar主面板
    private JLabel CloudMusic;//"CloudMusic"标签
    private JButton miniMode;//mini模式
    private JButton iconic;//图标化模式
    private JButton close;//关闭
    private JButton maxiMize;//最大化


    /* 拖动CloudMusic相关 */
    private static Point pre_point;
    private static Point end_point;

    /**
     * 标记是否全屏.
     * 为true则代表出于全屏模式下.
     */
    private static boolean isFullScreen = false;

    /**
     * 标记是否支持系统托盘.当不支持时，CloudMusic的退出按钮将直接退出整个进程
     */
    public static boolean isSupportedSystemTray;

    /**
     * 标记searchSongs里面是否有有效数据
     */
    private static boolean isValueEffective = false;

    /**
     * 唯一的CloudMusic对象.使用范例:
     * CloudMusic.cloudMusic = CloudMusic.getCloudMusic(tray);
     */
    public static CloudMusic cloudMusic;

    /**
     * 通过该方法以启动播放器的主界面
     */
    private CloudMusic(){
        /* 为CloudMusic设定UI */
        setUI();

        /* 设定组件的监听器 */
        setListenerForList();
        setListenerForMusic();
        setListenerForPlayModule();
        setListenerForTitleBar();

        /* 添加组件 */
        add(myPanel);
        /* 将所有组件装入Frame中 */
        pack();
        /*
        设定默认的Frame的启动位置
        默认居中
        */
        //获得屏幕边缘,以获取任务栏高度
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(this.getGraphicsConfiguration());
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - 1000) / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - screenInsets.bottom - 600) / 2;//减去任务栏高度
        end_point = new Point(x,y);
        /* 设定CloudMusic的位置 */
        setLocation(end_point);
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

        /* titleBar的组件的数组 */
        JComponent[] titleBarComponents = {titleBar,miniMode,iconic, maxiMize,close,CloudMusic};
        /* 使得CloudMusic能够被拖拽 */
        for (JComponent component : titleBarComponents){
            setCursorAndDraggableForTitleBar(component);
        }
        /* 其他需要设定鼠标经过光标的组件的数组 */
        JComponent[] components = {priorMusic,nextMusic,play_pause,playMode,addSongList, choseFolder, matchSongs,cleanSearchField};
        /* 为playModule组件组件设定UI */
        for (JComponent component : components){
            setCursorForComponent(component);
        }
    }
    /**
     * 使得CloudMusic能够被拖拽
     */
    private void setCursorAndDraggableForTitleBar(JComponent component){
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                /* 获得当前点击事件的鼠标坐标 */
                pre_point = new Point(e.getX(),e.getY());
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                /* 鼠标离开titleBar时将光标复原 */
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
                if (!isFullScreen){
                    end_point = new Point(getLocation().x + e.getX() - pre_point.x,
                            getLocation().y + e.getY() - pre_point.y);
                    setLocation(end_point);
                }
                //处于全屏模式下拖动则退出全屏
                else {
                    setSize(1000,600);
                    setLocation(end_point);
                    maxiMize.setSelected(false);
                    isFullScreen = false;
                }
            }
        });
    }

    /**
     * 为组件设置监听器,使得鼠标经过组件时光标形状改变
     * @param component CloudMusic的组件
     */
    private void setCursorForComponent(JComponent component){
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    /**
     * 为list添加监听器
     */
    private void setListenerForList(){
        //创建歌单
        addSongList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point point = new Point(end_point.x + cloudMusic.getWidth() / 2 - 150,
                        end_point.y + cloudMusic.getHeight() / 2 - 100);
                AddList addList = new AddList(point);
                addList.pack();
                addList.setVisible(true);
            }
        });
        //musicList的监听器.双击以转到选定歌单
        // TODO: 2018/2/21 musicList的监听器.双击以转到选定歌单
    }

    /**
     * 为music设置监听器
     */
    private void setListenerForMusic(){
        //选择目录
        choseFolder.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point point = new Point(end_point.x + cloudMusic.getWidth() / 2 - 150,
                        end_point.y + cloudMusic.getHeight() / 2 - 100);
                AddSongs choseFolder = new AddSongs(point);
                choseFolder.pack();
                choseFolder.setVisible(true);
            }
        });
        //匹配乐曲
        matchSongs.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // TODO: 2018/2/21 匹配乐曲
            }
        });
        //搜索乐曲
        searchSongs.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                //每次键入有效字符时则搜索乐曲
                String str = e.paramString().split("[,]")[3];
                //当为有效字符时
                if ((!str.contains(" ")) && (!str.contains("Backspace")) && (!str.contains("Enter")) && (!str.contains("Delete"))){
                    isValueEffective = true;
                    // TODO: 2018/2/21 开始检测
                }
                else if (!isValueEffective){
                    searchSongs.setText("");
                }
                //当退格时,检测文本框是否被清空
                else if (str.contains("Backspace")){
                    if (searchSongs.getText().equals("")){
                        isValueEffective = false;
                        searchSongs.setText("搜索乐曲");
                    }
                }
            }
        });
        searchSongs.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                //获取焦点且没有已输入文本则则清空初始提示文本
                if (!isValueEffective) searchSongs.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                //文本框为空时置提示文本
                if (searchSongs.getText().equals("")) searchSongs.setText("搜索乐曲");
            }
        });
        //清空搜索框
        cleanSearchField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                searchSongs.setText("搜索乐曲");
            }
        });
        //currentMusicList的监听器.双击以播放乐曲
        // TODO: 2018/2/21 currentMusicList的监听器.双击以播放乐曲
    }

    /**
     * 为playModule设置监听器
     */
    private void setListenerForPlayModule(){
        priorMusic.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
        play_pause.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
        nextMusic.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
        // TODO: 2018/2/21 进度条的监听器
        playMode.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED){
//                /* 选择顺序播放时 */
//                if (playMode.getSelectedIndex() == 0 && MusicPlayer.currentPlayMode != 0){
//                    MusicPlayer.currentPlayMode = 0;
//                }
//                /* 选择随机播放时 */
//                else if (playMode.getSelectedIndex() == 1 && MusicPlayer.currentPlayMode != 1){
//                    MusicPlayer.currentPlayMode = 1;
//                }
//                /* 选择单曲循环时 */
//                else if (playMode.getSelectedIndex() == 2 && MusicPlayer.currentPlayMode != 2){
//                    MusicPlayer.currentPlayMode = 2;
//                }
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
                if (e.getButton() == MouseEvent.BUTTON1){
                    //隐藏CloudMusic
                    setVisible(false);
                    //显示MiniCloudMusic
                    MiniCloudMusic.miniCloudMusic.setVisible(true);
                }
            }
        });
        /* 图标化 */
        iconic.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1){
                    setExtendedState(JFrame.ICONIFIED);
                }
            }
        });
        /* 最大化 */
        maxiMize.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1){
                    if (!maxiMize.isSelected()){
                        /* 此时全屏.当然,不会覆盖任务栏 */
                        //获得屏幕宽度
                        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
                        //获取除去任务栏后的屏幕宽度
                        int height = Toolkit.getDefaultToolkit().getScreenSize().height -
                                Toolkit.getDefaultToolkit().getScreenInsets(cloudMusic.getGraphicsConfiguration()).bottom;
                        setSize(width,height);
                        setLocation(0,0);
                        maxiMize.setSelected(true);
                        //标记出于全屏模式
                        isFullScreen = true;
                    }
                    else {
                        setSize(1000,600);
                        setLocation(end_point);
                        maxiMize.setSelected(false);
                    }
                }
            }
        });
        /* 隐藏CloudMusic */
        close.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1){
                    if (isSupportedSystemTray)
                        setVisible(false);
                        //不支持系统托盘时，点击退出按钮将直接退出播放器进程
                    else
                        System.exit(0);
                }
            }
        });
    }
    /**
     * 使用该方法以构造CloudMusic对象
     * @return CloudMusic
     */
    public static CloudMusic getCloudMusic(){
        return new CloudMusic();
    }
}
