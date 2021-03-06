package CloudMusicGUI;

import Implements.Implements;
import com.EnjoyYourMusic;
import com.List.MusicList;
import com.List.MusicNode;
import com.List.PlayMode;
import com.Music.Music;
import com.MusicPlayer;
import java.awt.Insets;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class CloudMusic extends JFrame implements Runnable{
    /* CloudMusic根面板 */
    private JPanel myPanel;
    /* list组件 */
    private JPanel list;//list主面板
    public  JTextArea musicInfo;//显示乐曲基本信息的文本框.格式为 "乐曲名-歌手"
    private JPanel labelAndAddSongPanel;//"我的歌单"标签和"添加歌单"按钮
    private JLabel songList;//"我的歌单"标签
    private JButton addSongList;//添加歌单按钮
    private JScrollPane musicListScrollPane;
    public JList<String> musicList;//当前所有乐曲列表
    /* music组件 */
    private JPanel music;//music主面板
    private JPanel musicListOperationAndLabelPanel;//对musicList的相关操作及label的面板
    public JLabel musicListName;//此时选定的musicList名字
    private JPanel labelPanel;//music的相关label的面板
    private JLabel songName;//音乐标题
    private JLabel artist;//歌手
    private JLabel album;//专辑
    private JLabel songTime;//时长
    private JPanel musicListOperation;//musicList的相关操作面板
    private JButton addSongs;//添加乐曲
    private JTextField searchSongs;//搜索乐曲
    private JButton cleanSearchField;
    private JPanel currentMusicListPanel;//当前显示的乐曲列表的面板
    private JScrollPane currentMusicListScrollPane;
    private JList<String> currentMusicList;//当前显示的乐曲列表
    /* playModule组件 */
    private JPanel playModule;//playModule主面板
    private JButton priorMusic;//上一首
    private JButton nextMusic;//下一首
    public JButton play_pause;//开始和暂停
    public JComboBox playMode;//播放模式
    public JSlider currentPlayTime;
    public JLabel currentPlayTimeLabel;
    public JLabel songTimeLabel;
    /* titleBar组件 */
    private JPanel titleBar;//titleBar主面板
    private JLabel CloudMusic;//"CloudMusic"标签
    private JButton miniMode;//mini模式
    private JButton iconic;//图标化模式
    private JButton close;//关闭
    private JButton maxiMize;//最大化

    //GUI主面板所管理的JList的数据模式
    public static DefaultListModel<String> musicListModel;
    public static DefaultListModel<String> currentMusicListModel;

    /* 拖动CloudMusic相关 */
    private static Point pre_point;
    private static Point end_point;

    /**
     * 记录每首乐曲开始播放时对应的系统时刻
     * 每次使用当前系统播放时间减去starTime则得到当前播放时刻
     */
    public static long starTime;

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
     * 标记currentOperationList是否被更新
     */
    private static boolean isMusicListUpdate = false;

    /**
     * 标记进度条是否被拖拽
     */
    public static boolean iSliderDragged = false;

    /**
     * 标记是否替换了当前播放的歌单
     */
    public static boolean isReplaceCurrentMusicList = false;

    /**
     * 标记是否播放了与当前乐曲不同的乐曲
     */
    public static boolean isPlayOtherMusic = false;

    /**
     * 用于实现断点播放
     */
    public static boolean flag;

    /**
     * 当前正在操作的音乐播放列表.一定是所有播放列表中的一个.
     */
    public static MusicList currentOperationList;

    /**
     * 由搜索方法的得到的搜索值
     */
    private static ArrayList<MusicNode> scanMusicNodes;

    /**
     * 标记检索指定乐曲是否成功
     */
    private boolean isSearchSuccess = false;

    /**
     * 标记播放线程是采用自动连播还是根据给定的MusicNode来播放
     * 为true则代表采用自动连播
     */
    public static boolean isAutomaticPlay = true;

    /**
     * 备选方案.用于实现暂停和播放乐曲
     * 为true代表正在播放
     */
    public static boolean IS_PLAY_OR_PAUSE = false;

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
        add(CurrentMusicListRightButtonMenu.currentMusicListRightButtonMenu);
        add(MusicListRightButtonMenu.musicListRightButtonMenu);
        /* 设定组件的监听器 */
        /* 初始化各个沐足元素的值 */
        initForList();
        initForMusic();
        initForPlayModule();
        initForTitleBar();
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

        /* titleBar的组件的数组 */
        JComponent[] titleBarComponents = {titleBar,miniMode,iconic, maxiMize,close,CloudMusic};
        /* 使得CloudMusic能够被拖拽 */
        for (JComponent component : titleBarComponents){
            setCursorAndDraggableForTitleBar(component);
        }
        /* 其他需要设定鼠标经过光标的组件的数组 */
        JComponent[] components = {priorMusic,nextMusic,play_pause,playMode,addSongList, addSongs,cleanSearchField};
        /* 为playModule组件组件设定UI */
        for (JComponent component : components){
            setCursorForComponent(component);
        }
        /* 去掉标题栏,以添加自己的标题栏 */
        setUndecorated(true);
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
    private void initForList(){
        //初始化musicList各个组件的值
        currentOperationList = MusicPlayer.currentMusicList;
        musicInfo.setText(MusicPlayer.getCurrentMusicInfo());

        //初始化musicList
        musicListModel = new DefaultListModel<>();
        for (int i = 0; i < MusicPlayer.TotalMusicListFileName.size() ; ++i){
            musicListModel.addElement(MusicPlayer.TotalMusicListFileName.get(i));
        }
        //导入用户数据
        musicList.setModel(musicListModel);
        //将JList添加到ScrollPane
        musicListScrollPane.setViewportView(musicList);
        //创建歌单
        addSongList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point point = new Point(end_point.x + cloudMusic.getWidth() / 2 - 150,
                        end_point.y + cloudMusic.getHeight() / 2 - 100);
                AddList addList = new AddList(point);
                addList.pack();
                addList.setVisible(true);
                String str = addList.songListName;
                if (str != null){
                    //创建MusicList对象
                    MusicList newList = new MusicList();
                    newList.setInitFileName(str);
                    //刷新GUI
                    musicListModel.addElement(str);
                }
            }
        });
        //musicList的监听器.双击以转到选定歌单
        musicList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JList list = (JList) e.getSource();
                //双击则显示所选择列表的歌单
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2){
                    int index = list.getSelectedIndex();
                    //更新当前正在操作的链表
                    currentOperationList = MusicPlayer.TotalMusicList.get(index);
                    //更新GUI
                    musicListName.setText(currentOperationList.toString());
                    SwingUtilities.invokeLater(() -> {
                        currentMusicListModel.clear();
                        int cnt = currentOperationList.sum;
                        if (cnt != 0){
                            MusicNode node = currentOperationList.getFirstMusic();
                            while (cnt > 0){
                                currentMusicListModel.addElement(Implements.renderer(node.toString()));
                                node = node.next;
                                --cnt;
                            }
                        }
                    });
                }
                //弹出右键菜单
                else if (e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() == 1){
                    int index = list.getSelectedIndex();
                    //必须选择一个歌单点击才弹出右键菜单
                    if (MusicPlayer.TotalMusicList.size() != 0 && index > 0){
                        //获取当前所选择的歌单
                        MusicListRightButtonMenu.musicListRightButtonMenu.selectedMusicList = MusicPlayer.TotalMusicList.get(index);
                        //显示菜单
                        Point point = MouseInfo.getPointerInfo().getLocation();
                        MusicListRightButtonMenu.musicListRightButtonMenu.show(cloudMusic,point.x,point.y);
                    }
                }
            }
        });
    }

    /**
     * 为music设置监听器
     */
    private void initForMusic(){
        //初始化musicListName标签
        musicListName.setText(MusicPlayer.currentMusicList.toString());
        //初始化currentMusicList
        currentMusicListModel = new DefaultListModel<>();
        currentMusicList.setModel(currentMusicListModel);
        //将上一次的播放列表显示出来
        int cnt = MusicPlayer.currentMusicList.sum;
        if (cnt != 0){
            MusicNode node = MusicPlayer.currentMusicList.getFirstMusic();
            while (cnt > 0){
                currentMusicListModel.addElement(Implements.renderer(node.toString()));
                node = node.next;
                --cnt;
            }
        }
        //将currentMusicList添加到ScrollPane
        currentMusicListScrollPane.setViewportView(currentMusicList);
        //添加乐曲
        addSongs.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("选择本地音乐文件或文件夹");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);//允许选择文件和文件夹
                fileChooser.setMultiSelectionEnabled(true);//允许选择多个文件
                //仅允许选择MP3，WMA，WAV文件
                fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
                FileNameExtensionFilter filterMp3 = new FileNameExtensionFilter("mp3 文件", "mp3");
//                FileNameExtensionFilter filterWMA = new FileNameExtensionFilter("wma 文件","wma");
//                FileNameExtensionFilter filterWAV = new FileNameExtensionFilter("wav 文件","wav");
                fileChooser.addChoosableFileFilter(filterMp3);
//                fileChooser.addChoosableFileFilter(filterWMA);
//                fileChooser.addChoosableFileFilter(filterWAV);
                int returnVal = fileChooser.showOpenDialog(cloudMusic);
                //当用户点击确定按钮时才添加到链表
                if (JFileChooser.APPROVE_OPTION == returnVal){
                    //根据用户选择的目录开始扫描mp3文件
                    File[] file = fileChooser.getSelectedFiles();
                    if (file != null) {
                        for (File aFile : file) {
                            //保证不会添加已存在的元素
                            cloudMusic.addSongs(aFile, currentOperationList);
                        }
                        //将添加到链表里的数据显示到GUI
                        //更新GUI的操作必须在SwingUtilities线程里面进行
                        //链表被更新时，才更新GUI
                        if (isMusicListUpdate) {
                            currentMusicListModel.clear();
                            SwingUtilities.invokeLater(() -> {
                                int cnt = currentOperationList.sum;
                                if (cnt != 0) {
                                    MusicNode node = currentOperationList.getFirstMusic();
                                    while (cnt > 0) {
                                        currentMusicListModel.addElement(Implements.renderer(node.toString()));
                                        node = node.next;
                                        --cnt;
                                    }
                                }
                            });
                            //保证链表被更新时才更新GUI
                            isMusicListUpdate = false;
                            /* 更新随机播放字段 */
                            MusicPlayer.setRandomPlay(currentOperationList.sum);
                        }
                    }
                }
            }
        });
        //搜索乐曲
        searchSongs.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                //每次键入有效字符时则搜索乐曲
                String str = e.paramString().split("[,]")[3];
                String detectedText = searchSongs.getText();
                //当为有效字符时
                if ((!str.contains(" "))  && (!str.contains("Enter")) && (!str.contains("Delete"))){
                    isValueEffective = true;
                    if (currentOperationList.sum != 0){
                        scanMusicNodes = new ArrayList<>(15);
                        currentMusicListModel.clear();
                        //开始搜索并更新GUI
                        //检索到了对应的MusicNode且搜索框非空则更新GUI
                        //当搜索框为空时，由下面的检测搜索框为空的方法更新GUI
                        if (!searchSongs.getText().equals("")){
                            SwingUtilities.invokeLater(() -> {
                                int cnt = currentOperationList.sum;
                                MusicNode node = currentOperationList.getFirstMusic();
                                String[] musicScanInfo = new String[3];
                                while (cnt > 0){
                                    musicScanInfo[0] = node.music.getSongName();
                                    musicScanInfo[1] = node.music.getArtist();
                                    musicScanInfo[2] = node.music.getAlbum();
                                    for (int i = 0; i < 2 ; ++i){
                                        if (Implements.KMP(musicScanInfo[i],detectedText) != -1){
                                            currentMusicListModel.addElement(Implements.renderer(node.toString()));
                                            scanMusicNodes.add(node);
                                            //标记当前检索成功
                                            isSearchSuccess = true;
                                            break;
                                        }
                                    }
                                    node = node.next;
                                    --cnt;
                                }
                                if (!isSearchSuccess) {
                                    //检索失败时，动态数组置空
                                    scanMusicNodes = null;
                                }
                            });
                        }
                    }
                }
                else if (!isValueEffective){
                    searchSongs.setText("");
                }
                //每次键入数据之后，检测文本框是否被清空
                if (searchSongs.getText().equals("")){
                    isValueEffective = false;
                    //here
                    //当搜索框里面没有用户输入数据时，退出搜索模式.同时将占用的资源释放
                    isSearchSuccess = false;
                    scanMusicNodes = null;
                    //重绘GUI
                    if (currentOperationList.sum != 0){
                        SwingUtilities.invokeLater(() -> {
                            currentMusicListModel.clear();
                            MusicNode node = currentOperationList.getFirstMusic();
                            int cnt = currentOperationList.sum;
                            while (cnt > 0){
                                currentMusicListModel.addElement(Implements.renderer(node.toString()));
                                node = node.next;
                                --cnt;
                            }
                        });
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
                isValueEffective = false;
                //here
                //当搜索框里面没有用户输入数据时，退出搜索模式.同时将占用的资源释放
                isSearchSuccess = false;
                scanMusicNodes = null;
                //重绘GUI
                if (currentOperationList.sum != 0){
                    SwingUtilities.invokeLater(() -> {
                        currentMusicListModel.clear();
                        MusicNode node = currentOperationList.getFirstMusic();
                        int cnt = currentOperationList.sum;
                        while (cnt > 0){
                            currentMusicListModel.addElement(Implements.renderer(node.toString()));
                            node = node.next;
                            --cnt;
                        }
                    });
                }
            }
        });
        //currentMusicList的监听器.双击以播放乐曲
        currentMusicList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JList list = (JList) e.getSource();
                //使用鼠标左键双击时
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2){
                    //当双击另外一个播放列表的音乐时，替换当前播放列表,并且更新随机播放的值
                    if (!MusicPlayer.currentMusicList.toString().equals(currentOperationList.toString())){
                        MusicPlayer.currentMusicList = currentOperationList;
                        isReplaceCurrentMusicList = true;
                        MusicPlayer.setRandomPlay(MusicPlayer.currentMusicList.sum);
                        flag = false;
                    }
                    //当处于检索模式下，使用数组scanMusicNodes里面的数据来播放
                    if (isSearchSuccess){
                        //设定当前正在播放的乐曲
                        MusicPlayer.currentMusicNode = scanMusicNodes.get(list.getSelectedIndex());

                        //暂停状态下播放选择的乐曲，则先唤醒再播放
                        if (!IS_PLAY_OR_PAUSE && CloudMusicThreadManager.playMusic.isAlive()){
                            CloudMusicThreadManager.playMusic.resume();
                            CloudMusicThreadManager.musicProgressBar.resume();
                            flag = false;
                        }
                        //播放线程尚未开始,则启动
                        if (!CloudMusicThreadManager.playMusic.isAlive()){
                            CloudMusicThreadManager.playMusic.start();
                            CloudMusicThreadManager.musicProgressBar.start();
                        }
                        else {
                            try {
                                EnjoyYourMusic.buffer.close();
                                EnjoyYourMusic.buffer = null;
                                isAutomaticPlay = false;
                                flag = false;
                            } catch (IOException ex) {
                                //do nothing
                                //只是为了引发播放线程终止播放当前乐曲并播放选中的歌
                            }
                        }
                    }
                    //当双击与当前播放乐曲不同的乐曲时才播放.当然，切换了播放列表也行
                    else if (isReplaceCurrentMusicList || MusicPlayer.getIndexOfCurrentMusicNode() != list.getSelectedIndex()){
                        isPlayOtherMusic = true;
                        //设定当前正在播放的乐曲
                        MusicPlayer.setCurrentMusicNode(list.getSelectedIndex());
                        //暂停状态下播放选择的乐曲，则先唤醒再播放
                        if (!IS_PLAY_OR_PAUSE && CloudMusicThreadManager.playMusic.isAlive()){
                            CloudMusicThreadManager.playMusic.resume();
                            CloudMusicThreadManager.musicProgressBar.resume();
                            flag = false;
                        }

                        if (!CloudMusicThreadManager.playMusic.isAlive()){
                            CloudMusicThreadManager.playMusic.start();
                            CloudMusicThreadManager.musicProgressBar.start();

                        }
                        else {
                            try {
                                EnjoyYourMusic.buffer.close();
                                EnjoyYourMusic.buffer = null;
                                isAutomaticPlay = false;
                                flag = false;
                            } catch (IOException ex) {
                                //do nothing
                                //只是为了引发播放线程终止播放当前乐曲并播放选中的歌
                            }
                        }
                    }
                }
                //弹出右键菜单
                else if (e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() == 1){
                    int index = list.getSelectedIndex();
                    //必须选中一个表项才弹出右键菜单
                    if (currentOperationList.sum != 0 && index != -1){
                        MusicNode node;
                        //非检索模式下直接从链表中获取数据
                        if (!isSearchSuccess){
                            node = currentOperationList.getFirstMusic();
                            while (index > 0){
                                node = node.next;
                                --index;
                            }
                        }
                        //检索模式下从检索结果的数组中获取数据
                        else {
                            node = scanMusicNodes.get(list.getSelectedIndex());
                        }
                        CurrentMusicListRightButtonMenu.currentMusicListRightButtonMenu.selectedMusicNode = node;
                        Point point = MouseInfo.getPointerInfo().getLocation();
                        CurrentMusicListRightButtonMenu.currentMusicListRightButtonMenu.show(cloudMusic,point.x,point.y);
                    }
                }
            }
        });
    }

    /**
     * 为playModule设置监听器
     */
    private void initForPlayModule(){
        try{
            ImageIcon priorMusic_icon = new ImageIcon("src\\icon\\priorMusic.png");
            priorMusic.setIcon(priorMusic_icon);
            ImageIcon play_icon = new ImageIcon("src\\icon\\play.png");
            play_pause.setIcon(play_icon);
            ImageIcon pause_icon = new ImageIcon("src\\icon\\pause.png");
            play_pause.setSelectedIcon(pause_icon);
            ImageIcon nextMusic_icon = new ImageIcon("src\\icon\\nextMusic.png");
            nextMusic.setIcon(nextMusic_icon);
        }catch (NullPointerException ex){
            //do nothing
        }

        //初始化播放模组
        playMode.setSelectedIndex(MusicPlayer.currentPlayMode);

        priorMusic.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1){
                    playPriorMusic();
                }
            }
        });
        play_pause.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1){
                    SET_PLAY_OR_PAUSE();
                }
            }
        });
        nextMusic.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1){
                    playNextMusic();
                }
            }
        });
        playMode.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED){
                /* 选择顺序播放时 */
                if (playMode.getSelectedIndex() == 0 && MusicPlayer.currentPlayMode != 0){
                    MusicPlayer.currentPlayMode = 0;
                    CloudMusicTray.tray.playMode.setSelectedIndex(0);
                }
                /* 选择随机播放时 */
                else if (playMode.getSelectedIndex() == 1 && MusicPlayer.currentPlayMode != 1){
                    MusicPlayer.currentPlayMode = 1;
                    CloudMusicTray.tray.playMode.setSelectedIndex(1);
                }
                /* 选择单曲循环时 */
                else if (playMode.getSelectedIndex() == 2 && MusicPlayer.currentPlayMode != 2){
                    MusicPlayer.currentPlayMode = 2;
                    CloudMusicTray.tray.playMode.setSelectedIndex(2);
                }
            }
        });
        currentPlayTime.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                //标记拖拽了进度条
                iSliderDragged = true;
                try {
                    EnjoyYourMusic.buffer.close();
                } catch (IOException e1) {
                    // do nothing
                }
            }
        });
    }

    /**
     * 为titleBar设定监听器
     */
    private void initForTitleBar(){
        ImageIcon CloudMusic_icon = new ImageIcon("src\\icon\\titleIcon.png");
        CloudMusic.setIcon(CloudMusic_icon);
        ImageIcon miniMode_icon = new ImageIcon("src\\icon\\miniMode.png");
        miniMode.setIcon(miniMode_icon);
        ImageIcon iconic_icon = new ImageIcon("src\\icon\\iconic.png");
        iconic.setIcon(iconic_icon);
        ImageIcon maxMize_icon = new ImageIcon("src\\icon\\maxiMize.png");
        maxiMize.setIcon(maxMize_icon);
        ImageIcon icon = new ImageIcon("src\\icon\\normalSize.png");
        maxiMize.setSelectedIcon(icon);
        ImageIcon close_icon = new ImageIcon("src\\icon\\close.png");
        close.setIcon(close_icon);

        /* 启动mini模式 */
        miniMode.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1){
                    //隐藏CloudMusic
                    setVisible(false);
                    //更改系统托盘对应的显示值
                    CloudMusicTray.tray.frameMode.setSelectedIndex(1);
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

    /**
     * 给定的是一个目录，则递归的将目录下的所有MP3文件添加到指定链表;
     * 给定的是单独的文件，则仅将这一个文件添加到指定链表
     * @param file 给定的文件
     * @param currentOperationList 乐曲将被添加到的链表
     *
     */
    private void addSongs(File file, MusicList currentOperationList){
        //为普通文件时
        if (!file.isDirectory()){
            //解码成功且链表中不存在这首乐曲则添加到链表
            String[] musicInfo = Music.setMusicInfo(file.getAbsolutePath());
            if (musicInfo != null) {
                Music newMusic = new Music(musicInfo);
                MusicNode newNode = new MusicNode(newMusic);
                if (!currentOperationList.contains(newNode)){
                    //当且仅当是全新的元素时才添加到链表
                    currentOperationList.addSong(newNode);
                    isMusicListUpdate = true;
                }
            }
        }
        //当为目录时
        else {
            File[] files = file.listFiles(pathname -> {
                //当为MP3文件或者是目录则允许
                return pathname.isDirectory() || pathname.getAbsolutePath().endsWith(".mp3") ||pathname.getAbsolutePath().endsWith(".MP3");
            });

            if (files != null){
                for (File file1 : files) {
                    addSongs(file1, currentOperationList);
                }
            }
        }

    }


    /**
     * 播放当前乐曲的上一首歌
     */
    public void playPriorMusic(){
        //将开始按钮设定为暂停
        if (MusicPlayer.currentMusicList.sum != 0){
            //获取当前乐曲的上一首
            MusicNode node = MusicPlayer.getPriorMusic();

            MusicPlayer.currentMusicNode = Implements.ensureNotNull(node);

            //暂停状态下播放下一首，则先唤醒再播放上一首
            if (!IS_PLAY_OR_PAUSE){
                CloudMusicThreadManager.playMusic.resume();
                CloudMusicThreadManager.musicProgressBar.resume();
                flag = false;
            }

            if (!CloudMusicThreadManager.playMusic.isAlive()){
                CloudMusicThreadManager.playMusic.start();
                CloudMusicThreadManager.musicProgressBar.start();
            }
            else {
                try {
                    EnjoyYourMusic.buffer.close();
                    EnjoyYourMusic.buffer = null;
                    isAutomaticPlay = false;
                    flag = false;
                } catch (IOException ex) {
                    //do nothing
                    //只是为了引发播放线程终止播放当前乐曲并播放选中的歌
                }
            }
        }
    }


    /**
     * 播放当前乐曲的下一首歌
     */
    public void playNextMusic(){
        if (MusicPlayer.currentMusicList.sum != 0){
            //获取当前乐曲的下一首
            MusicNode node = MusicPlayer.getNextMusic();

            MusicPlayer.currentMusicNode = Implements.ensureNotNull(node);

            //暂停状态下播放下一首，则先唤醒再播放下一首
            if (!IS_PLAY_OR_PAUSE){
                CloudMusicThreadManager.playMusic.resume();
                CloudMusicThreadManager.musicProgressBar.resume();
                flag = false;
            }

            if (!CloudMusicThreadManager.playMusic.isAlive()){
                CloudMusicThreadManager.playMusic.start();
                CloudMusicThreadManager.musicProgressBar.start();
            }
            else {
                try {
                    EnjoyYourMusic.buffer.close();
                    EnjoyYourMusic.buffer = null;
                    isAutomaticPlay = false;
                    flag = false;
                } catch (IOException ex) {
                    //do nothing
                    //只是为了引发播放线程终止播放当前乐曲并播放选中的歌
                }
            }
        }
    }


    /**
     * 设定播放或者暂停.
     * 采用备选方案
     */
    public void SET_PLAY_OR_PAUSE(){
        if (MusicPlayer.currentMusicList.sum != 0){
            //备选方案.
            //暂停
            if (IS_PLAY_OR_PAUSE){
                CloudMusicThreadManager.playMusic.suspend();
                CloudMusicThreadManager.musicProgressBar.suspend();

                IS_PLAY_OR_PAUSE = false;
                //修改CloudMusic相关图标及文字
                play_pause.setSelected(false);
                //修改MiniCloudMusic相关图标及文字
                MiniCloudMusic.miniCloudMusic.play_pause.setSelected(false);
                //修改CloudMusicTray相关图标及文字
                CloudMusicTray.tray.play_pause.setIcon(new ImageIcon("src\\icon\\playTrayIcon.png"));
                CloudMusicTray.tray.play_pause.setText("播放");
                CloudMusicTray.tray.play_pause.setSelected(false);
                flag = false;
            }
            //播放
            else {
                //当最开始没有启动播放器线程时，则启动播放器线程以播放当前选定的乐曲
                if (!CloudMusicThreadManager.playMusic.isAlive()){
                    //获取上一次播放的结束时的MusicNode
                    MusicNode node = MusicPlayer.currentMusicNode;

                    MusicPlayer.currentMusicNode = Implements.ensureNotNull(node);
                    //启动播放线程以及进度条控制线程
                    CloudMusicThreadManager.playMusic.start();
                    CloudMusicThreadManager.musicProgressBar.start();
                }
                //而启动了播放器线程，那么当然唤醒他们
                else {
                    CloudMusicThreadManager.playMusic.resume();

                    //获取恢复播放时时的系统时间播放进度
                    //currentPlayTimeLabel.setText(currentPlayTimeLabel.getText());
                    String[] str = currentPlayTimeLabel.getText().split(":");
                    starTime = System.currentTimeMillis() - Integer.toUnsignedLong(Integer.parseInt(str[0]) * 60 * 1000 + Integer.parseInt(str[1]) * 1000);

                    CloudMusicThreadManager.musicProgressBar.resume();
                    IS_PLAY_OR_PAUSE = true;
                    flag = false;
                }
                //修改CloudMusic相关图标及文字
                play_pause.setSelected(true);
                //修改MiniCloudMusic相关图标及文字
                MiniCloudMusic.miniCloudMusic.play_pause.setSelected(true);
                //修改CloudMusicTray相关图标及文字
                CloudMusicTray.tray.play_pause.setIcon(new ImageIcon("src\\icon\\pauseTrayIcon.png"));
                CloudMusicTray.tray.play_pause.setText("暂停");
                CloudMusicTray.tray.play_pause.setSelected(true);
            }
        }
    }


    @Override
    public void run() {
        //显示播放器主界面
        cloudMusic.setVisible(true);
    }
}

final class CloudMusicThreadManager {
    /**
     * 播放乐曲线程.
     * 调用该方法时，调用者务必保证 MusicPlayer.currentMusicNode 指向可以播放的乐曲.测试方法如下:
     * 先测试该节点包含的路径所指向的文件是否是一个标准文件，然后测试后缀名是否为MP3.
     * 两条都满足则有可以最大的确保该文件可播放
     */
    public final static Thread playMusic = new Thread(() -> {
        while (true){
            //当已经选定下一首播放时
            if (CurrentMusicListRightButtonMenu.isNextPlay && !CloudMusic.iSliderDragged){
                MusicPlayer.currentMusicNode = CurrentMusicListRightButtonMenu.nextPlayMusicNode;
                CurrentMusicListRightButtonMenu.isNextPlay = false;
            }
            //为系统托盘和miniCloudMusic和CloudMusic的乐曲基本信息文本框设定提示文本.显示当前播放的乐曲
            String toolTipText = MusicPlayer.getCurrentMusicInfo();
            CloudMusicTray.tray.trayIcon.setToolTip(toolTipText);
            CloudMusicTray.tray.play_pause.setIcon(new ImageIcon());
            CloudMusicTray.tray.play_pause.setText("暂停");
            CloudMusicTray.tray.play_pause.setSelected(true);
            CloudMusic.cloudMusic.play_pause.setSelected(true);
            CloudMusic.cloudMusic.musicInfo.setText(toolTipText);
            MiniCloudMusic.miniCloudMusic.play_pause.setSelected(true);
            MiniCloudMusic.miniCloudMusic.musicInfo.setText(toolTipText);
            MiniCloudMusic.miniCloudMusic.musicInfo.setToolTipText(toolTipText);
            //播放标志置为rue
            CloudMusic.IS_PLAY_OR_PAUSE = true;
            //设置当前进度条的最大值及label显示值
            CloudMusic.cloudMusic.songTimeLabel.setText(MusicPlayer.currentMusicNode.music.getSongTime());
            CloudMusic.cloudMusic.currentPlayTime.setMaximum(MusicPlayer.currentMusicNode.getSongTime());
            //P.S.每首乐曲开始播放的时间 starTime 已在EnjoyYourMusic.play方法中设定
            //播放
            EnjoyYourMusic.play(MusicPlayer.currentMusicNode);
            //当拖拽进度条使得流崩溃时不会自动调用getNextMusic
            //当强行更换当前播放乐曲时不会自动调用getNextMusic
            //当已选择下一首播放的乐曲时不会自动调用getNextMusic
            if (!CloudMusic.iSliderDragged && !CurrentMusicListRightButtonMenu.isSelectedOtherMusic
                    && !CurrentMusicListRightButtonMenu.isNextPlay){
                //自动连播
                if (CloudMusic.isAutomaticPlay){
                    //非单曲循环时才调用getNext
                    if (MusicPlayer.currentPlayMode != PlayMode.Mode_Loop){
                        MusicPlayer.currentMusicNode = MusicPlayer.getNextMusic();
                    }
                }
                CloudMusic.isAutomaticPlay = true;
            }
            CurrentMusicListRightButtonMenu.isSelectedOtherMusic = false;
        }
    });

    /**
     * 操控进度条的线程
     */
    public final static Thread musicProgressBar = new Thread(() -> {
        //用于屏蔽不晓得哪里来的bug
        boolean flag = true;
        while (true){
            MusicPlayer.currentPlayTime = (int)((System.currentTimeMillis() -  CloudMusic.starTime) / 1000);
            //更新进度条位置
            CloudMusic.cloudMusic.currentPlayTime.setValue(MusicPlayer.currentPlayTime);
            //更新标签显示时间
            if (!flag){
                CloudMusic.cloudMusic.currentPlayTimeLabel.setText(String.valueOf(MusicPlayer.currentPlayTime / 60) + ":" + String.valueOf(MusicPlayer.currentPlayTime % 60));
            }
            else {
                CloudMusic.cloudMusic.currentPlayTimeLabel.setText("0:0");
                flag = false;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //do nothing
            }
        }
    });
}
