package com.list;

import Implements.Implements;
import com.Music.Music;

import java.io.Serializable;


/*--------------------------------------------------------------------------------------------------------------
使用循环双链表来存储Music，
1.方便增加删除，可无限存储而不用动态申请内存
2.便于排序
缺点：1.但是没有随机访问的能力，而这种能力在随机播放和搜索的时候是必要的
     2.目前尚不明确，假如说关闭了播放器之后，MusicNode指向的Music是否会消失
我需要的随机访问能力：
无论当前指针指向哪里，重新给定一个新的位置，要求当前指针要能够以O(1)或者接近O(1)的访问速度抵达新的位置
--------------------------------------------------------------------------------------------------------------*/

/**
 * 显示在音乐列表控件里面
 * 任何时候内存中只允许有一个MusicList对象
 * 序列化的存储在文件中，方便下次重新打开播放器时显示在控件里面
 */
 public class MusicList implements Serializable{

    private  int sum;//歌曲总数
    private  MusicNode FirstMusic;//当前列表第一首乐曲,一开始为空
    private  MusicNode LastMusic;//列表最后一首歌,用于使双链表循环化

    //内存中唯一的tMusicList
    private static transient MusicList musicList;


    /**
     * 不允许自建MusicList
     */
    private MusicList(){
    }

    /**
     * @param musicList 解序列化时得到的MusicList对象，
     *                  每次播放器重启时都应当调用
     */
    public static void setMusicList(MusicList musicList){
        MusicList.musicList = musicList;
    }



    /**
     * @return 获取唯一的MusicList对象
     */
    public static MusicList getMusicList(){
        return musicList;
    }


    /**
     * 当列表中没有任何歌曲时将会调用
     */
    public void init(){
        sum = 0;
        FirstMusic = null;
        LastMusic = null;
    }


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
    下面是增添删除查找方法
    --------------------------------------------------------------------------------------------------------------*/

    /*--------------------------------------------------------------------------------------------------------------
    失败的话让addSong方法抛出异常即可
    不断调用addSong方法就能得到一张关于Music的链表
    仅要修改链表即可，显示是GUI做的事情
    --------------------------------------------------------------------------------------------------------------*/
    /**
     * @param music 被添加的添加歌曲节点
     */

    public void addSong(Music music){
        MusicNode newNode = new MusicNode(music);
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
            //保存
            Implements.Serialize(musicList);
        }
        //列表为空时
        else {
            FirstMusic = newNode;
            LastMusic = newNode;
            ++sum;//歌曲总数加1
            //保存
            Implements.Serialize(musicList);
        }
    }



    /*--------------------------------------------------------------------------------------------------------------
    失败的话让deleteSong方法抛出异常即可
    仅要修改链表即可，显示是GUI做的事情
    --------------------------------------------------------------------------------------------------------------*/
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
        //保存
        Implements.Serialize(musicList);
    }



    /**
     * @param substr 待搜索的字符串，可以是歌曲名字或歌手或专辑中的任意连续字符
     *               返回采用KMP算法,遍历链表得到的匹配项
     * P.S.边计算边调用GU的显示程序，在GUI中显示匹配项(其他不匹配的不显示，当用户点击之后，再重新显示)
     */
    public void searchSong(String substr) {

        MusicNode node = FirstMusic;
        int count = 1;//从列表第一首歌开始搜索
        boolean flag = true;//如果没有一首歌匹配，则为true
        String str;//待检索的字符串，包括歌曲名，歌手，专辑
        while (count <= sum){
            str =  node.music.getSongName() + node.music.getArtist() + node.music.getAlbum();
            //P.S.待搜索的子串需要去掉前导空白
            if (Implements.KMP(str,substr.trim())){
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
