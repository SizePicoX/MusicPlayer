package com.list;

import Implements.Implements;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/*--------------------------------------------------------------------------------------------------------------
        注意：链表时必须放在内存中的，绝对不该使用指针来遍历文件目录以达到播放的目的，那样会非常慢
--------------------------------------------------------------------------------------------------------------*/

/**
 * 被MusicPlayer所管理的音乐列表
 * 任何时候内存中只允许有一个MusicList对象
 * 序列化的存储在文件中，方便下次重新打开播放器时显示在控件里面
 */
 public class MusicList implements Serializable{

    /**
     * 歌曲总数
     */
    private  int sum;

    /**
     * 当前列表第一首乐曲,一开始为空
     */
    private  MusicNode FirstMusic;

    /**
     * 列表最后一首歌,用于使双链表循环化
     */
    private  MusicNode LastMusic;


    /*--------------------------------------------------------------------------------------------------------------
      构造函数,不允许自建MusicList
    --------------------------------------------------------------------------------------------------------------*/

    /**
     * 不允许自建MusicList
     */
    private MusicList(){
    }

    /*--------------------------------------------------------------------------------------------------------------
      MusicList的get方法
    --------------------------------------------------------------------------------------------------------------*/

    /**
     * @return 返回列表中乐曲总数
     */
    public  int getSum() {
        return sum;
    }


    /**
     * @return 获取当前列表中的第一首歌
     */
    public  MusicNode getFirstMusic(){
        return FirstMusic;
    }

    /**
     * @return 获取列表最后一首歌曲
     */
    public MusicNode getLastMusic() {
        return LastMusic;
    }


    /*--------------------------------------------------------------------------------------------------------------
     初始化MusicList方法
    --------------------------------------------------------------------------------------------------------------*/


    /**
     * 解序列化方法,用于初始化MusicList对象
     * 启动播放器时才需要解序列化
     * 整个程序运行期间只需要调用一次
     * @return MusicList
     */
    public static MusicList Init(){
        /* 取出MusicList */
        try {
            FileInputStream fs = new FileInputStream("InitOfMusicList.ser");
            ObjectInputStream os = new ObjectInputStream(fs);
            os.close();

            /* 采用readUnshared读取不断在改变的对象 */
            return (MusicList)os.readUnshared();
        }catch (ClassNotFoundException ex){
            /* 当文件为空时 */
            return new MusicList();
        }catch (IOException ex){
            ex.printStackTrace();
            return null;
        }
    }


    /*--------------------------------------------------------------------------------------------------------------
    下面是增添删除查找方法
    --------------------------------------------------------------------------------------------------------------*/


    /**
     * 调用该方法前，先创建Music对象再创建MusicNode对象
     * @param newNode 添加进入列表的MusicNode
     */
    public void addSong(MusicNode newNode){
        //如果列表不为空
        if (FirstMusic != null){
            //修改指针
            FirstMusic.prior = newNode;
            newNode.next = FirstMusic;
            newNode.prior = LastMusic;
            LastMusic.next = newNode;
            //修改当前列表第一首歌曲
            FirstMusic = newNode;
            ++sum;//歌曲总数加1
        }
        //列表为空时
        else {
            FirstMusic = newNode;
            LastMusic = newNode;
            //修改指针
            FirstMusic.next = LastMusic;
            FirstMusic.prior = LastMusic;
            ++sum;//歌曲总数加1
        }
    }


    /**
     * @param selectedNode 用于从歌曲链表中删除选定的歌曲
     */
    public void deleteSong(MusicNode selectedNode){
        //当选定的节点不是第一首乐曲也不是最后一首时
        if (selectedNode != FirstMusic && selectedNode != LastMusic){
            //修改指针
            //选定歌曲的前一首歌
            selectedNode.prior.next = selectedNode.next;
            //选定歌曲的后一首歌
            selectedNode.next.prior = selectedNode.prior;
        }
        //是当前列表第一个节点时,需要修改当前列表第一首乐曲
        else if (selectedNode == FirstMusic){
            //当是第一首歌也是最后一首歌，即列表只有一首歌时
            if (sum == 1){
                //修改当前列表第一首歌
                FirstMusic = null;
                //修改当前列表最后一首歌
                LastMusic = null;
            }
            else {
                //修改当前列表第一首歌
                FirstMusic = selectedNode.next;
                //修改指针
                LastMusic.next = FirstMusic;
                FirstMusic.prior = LastMusic;
            }
        }
        //是当前列表最后一个节点时，同样需要修改当前列表最后一首歌曲
        else {
            //修改当前列表最后一首歌
            LastMusic = selectedNode.prior;
            //修改指针
            LastMusic.next = FirstMusic;
            FirstMusic.prior = LastMusic;
        }

        //释放指针
        MusicNode.DeleteSelectedNode(selectedNode);
        //歌曲总数减1
        --sum;
    }


    /**
     * @param subStr 待搜索的字符串，可以是歌曲名字或歌手或专辑中的任意连续字符
     *               返回采用KMP算法,遍历链表得到的匹配项
     * P.S.边计算边调用GU的显示程序，在GUI中显示匹配项(其他不匹配的不显示，当用户点击之后，再重新显示)
     */
    public void searchSong(String subStr) {

        MusicNode node = FirstMusic;
        int count = 1;//从列表第一首歌开始搜索
        boolean flag = true;//如果没有一首歌匹配，则为true
        String str;//待检索的字符串，包括歌曲名，歌手，专辑
        while (count <= sum){
            str =  node.music.getSongName() + node.music.getArtist() + node.music.getAlbum();
            //P.S.待搜索的子串需要去掉前导空白
            if (Implements.KMP(str,subStr.trim())){
                //此时匹配，应当调用GUI将被匹配到的MusicNode显示出来
                if (count == 1) flag = false; //只会对flag访问一次

                /*--------------------------------------------------------------------------------------------------------------
                  这里调用GUI的显示程序
                --------------------------------------------------------------------------------------------------------------*/

            }
            node = node.next;
            ++count;
        }
        if (flag){
            //显示“无结果”

            /*--------------------------------------------------------------------------------------------------------------
             这里调用GUI的显示程序
            --------------------------------------------------------------------------------------------------------------*/

        }
    }
}
