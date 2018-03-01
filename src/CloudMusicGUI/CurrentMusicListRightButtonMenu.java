package CloudMusicGUI;

import Implements.Implements;
import com.EnjoyYourMusic;
import com.List.MusicNode;
import com.MusicPlayer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * CurrentMusicListRightButtonMenu鼠标右键的菜单
 */
public class CurrentMusicListRightButtonMenu extends PopupMenu{
    public  MenuItem play;//播放/暂停
    private MenuItem nextPlay;//下一首播放
    private MenuItem openFolder;//打开文件所在目录
    private MenuItem deleteFromList;//从当前歌单里面删除
    private MenuItem deleteFromLocal;//从本地磁盘里删除

    //UI
    private Font font = new Font("楷体",Font.ITALIC + Font.BOLD, 12);
    //组件的数组
    private MenuItem[] items;

    /**
     * 唯一的RightButtonMenu对象
     */
    public static CurrentMusicListRightButtonMenu currentMusicListRightButtonMenu;

    /**
     * 标记是否选择与当前播放乐曲不同的歌曲
     */
    public static boolean isSelectedOtherMusic = false;

    /**
     * 标记是否采用用户所选择的下一首乐曲来播放
     */
    public static boolean isNextPlay = false;

    /**
     * 鼠标右键选定的MusicNode
     */
    public MusicNode selectedMusicNode = null;

    /**
     * 被选定的下一首播放的乐曲
     */
    public static MusicNode nextPlayMusicNode = null;

    private CurrentMusicListRightButtonMenu(){
        initItems();
        setUI();
        setListener();
    }

    private void initItems(){
        //初始化所有表项
        play = new MenuItem("Play");
        nextPlay = new MenuItem("Next Play");
        openFolder = new MenuItem("Open Folder");
        deleteFromList = new MenuItem("Delete From SongList");
        deleteFromLocal = new MenuItem("Delete From Local");

        items = new MenuItem[5];
        items[0] = play;
        items[1] = nextPlay;
        items[2] = openFolder;
        items[3] = deleteFromList;
        items[4] = deleteFromLocal;

        for (MenuItem item : items){
            add(item);
        }
    }

    private void setUI(){
        for (int i = 0; i < 5; ++i){
            items[i].setFont(font);
        }
    }

    private void setListener(){
        //播放所选择乐曲
        play.addActionListener(e -> {
            //播放线程未启动时直接播放
            if (!CloudMusicThreadManager.playMusic.isAlive()){
                //获取上一次播放的结束时的MusicNode
                MusicPlayer.currentMusicNode = selectedMusicNode;
                //启动播放线程以及进度条控制线程
                CloudMusicThreadManager.playMusic.start();
                CloudMusicThreadManager.musicProgressBar.start();
            }
            //选择的节点不为当前播放节点或者当前操作的列表不为当前播放列表才播放
            else if (!selectedMusicNode.isSame(MusicPlayer.currentMusicNode) ||
                    MusicPlayer.TotalMusicList.indexOf(CloudMusic.currentOperationList) != MusicPlayer.TotalMusicList.indexOf(MusicPlayer.currentMusicList)){
                MusicPlayer.currentMusicNode = selectedMusicNode;
                try {
                    //标记强行切歌.选择与当前播放乐曲不同的乐曲
                    isSelectedOtherMusic = true;
                    EnjoyYourMusic.buffer.close();
                } catch (IOException e1) {
                    //do nothing
                }
            }
        });
        //下一首播放
        nextPlay.addActionListener(e -> {
            //设定下一首播放的乐曲
            isNextPlay = true;
            nextPlayMusicNode = selectedMusicNode;
        });
        //打开歌曲所在文件夹
        openFolder.addActionListener(e -> {
            try {
                Runtime.getRuntime().exec("explorer /select," + selectedMusicNode.music.getMp3FilePath());
            } catch (IOException e1) {
                //do nothing
            }
        });
        //从当前歌单中删除
        deleteFromList.addActionListener(e -> {
            CloudMusic.currentOperationList.deleteSong(selectedMusicNode,false);
            //更新GUI
            SwingUtilities.invokeLater(() -> {
                CloudMusic.currentMusicListModel.clear();
                int cnt = CloudMusic.currentOperationList.sum;
                MusicNode node = CloudMusic.currentOperationList.getFirstMusic();
                while (cnt > 0){
                    CloudMusic.currentMusicListModel.addElement(Implements.renderer(node.toString()));
                    node = node.next;
                    --cnt;
                }
            });
        });
        //从本地磁盘中删除
        deleteFromLocal.addActionListener(e -> {
            CloudMusic.currentOperationList.deleteSong(selectedMusicNode,true);
            //更新GUI
            SwingUtilities.invokeLater(() -> {
                CloudMusic.currentMusicListModel.clear();
                int cnt = CloudMusic.currentOperationList.sum;
                MusicNode node = CloudMusic.currentOperationList.getFirstMusic();
                while (cnt > 0){
                    CloudMusic.currentMusicListModel.addElement(Implements.renderer(node.toString()));
                    node = node.next;
                    --cnt;
                }
            });
        });
    }

    public static CurrentMusicListRightButtonMenu getCurrentMusicListRightButtonMenu(){
        return new CurrentMusicListRightButtonMenu();
    }
}
