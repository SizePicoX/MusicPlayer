package com.List;

import Implements.Implements;
import Error.InitException;
import java.io.*;
import java.util.ArrayList;

/*--------------------------------------------------------------------------------------------------------------
        注意：链表时必须放在内存中的，绝对不该使用指针来遍历文件目录以达到播放的目的，那样会非常慢
--------------------------------------------------------------------------------------------------------------*/

/**
 * 被MusicPlayer所管理的音乐列表
 * 可以创建多个乐曲列表，即MusicList可以有多个实例
 * 序列化的存储在文件中，方便下次重新打开播放器时显示在控件里面
 */
 public class MusicList implements Serializable{

    /**
     * 歌曲总数
     */
    private  int sum = 0;

    /**
     * 当前列表第一首乐曲,一开始为空
     * P.S.由于FirstMusic是引用类型，关闭播放器之后它所指向的值就没了，故它不应该序列化
     */
    private  MusicNode FirstMusic = null;

    /**
     * 列表最后一首歌,用于使双链表循环化
     * P.S.由于LastMusic是引用类型，关闭播放器之后它所指向的值就没了，故它不应该序列化
     */
    private  MusicNode LastMusic = null;

    /**
     * 如果链表被修改，则置为true，反之，则为false
     */
    private  boolean flag = false;

    /**
     * 保存了当前乐曲列表的序列化文件。默认为 InitOfMusicList.ser
     */
    private String initFileName;

    /**
     * 引用计数器.播放器只有一个默认播放列表，故使用引用计数来保证默认初始化仅能使用一次
     */
    private static int ReferenceCount = 0;
    /**
     * @param initFileName 使用该序列化文件以初始化MusicList对象.
     *                     为null时即代表创建的列表为默认列表,其使用的序列化文件名为InitOfMusicList.ser
     *                     但默认初始化仅能使用一次
     */
    public MusicList(String initFileName){
        /* 序列化文件默认为 InitOfMusicList.ser */
        if (initFileName == null && ReferenceCount == 0){
            initFileName = "InitOfMusicList.ser";
            /* 引用计数加1 */
            ++ReferenceCount;
        }
        /* 当连续使用两次默认初始化时，抛出错误 */
        else if (initFileName == null && ReferenceCount == 1){
            throw new InitException("不允许连续两次默认初始化！！！");
        }
        /* 否则就以输入的序列化文件名来初始化 */
        else this.initFileName = initFileName;
        /* 初始化 */
        Init(initFileName);
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
     * 初始化方法,用于初始化MusicList
     * 启动播放器时才需要解序列化
     * 整个程序运行期间只需要调用一次
     */
    private void Init(String initFileName){
        /* 初始化内存中的链表 */
        try {
            /* 暂存从流中读取出来的MusicNode */
            ArrayList<MusicNode> buf = new ArrayList<>(100);
            /* 打开流文件 */
            FileInputStream fs = new FileInputStream(initFileName);
            ObjectInputStream os = new ObjectInputStream(fs);
            /*
            从流文件中读取数据，暂存到缓冲区内
            P.S.这是因为，直接读取会导致链表第一首歌变成最后一首，最后一首变成第一首，且指针的指向全乱掉了
            */
            while (fs.available() > 0){
                buf.add((MusicNode) os.readObject());
                /*--------------------------------------------------------------------------------------------------------------
                // TODO: 2018/2/8/008
                1.以及，从流中读取出来的MusicNode对象中的Music对象，实际上并没有值在里面，是否去实现深拷贝？
                --------------------------------------------------------------------------------------------------------------*/
            }
            os.close();
            /* 初始化播放列表 */
            int i = buf.size();
            while (i > 0){
                --i;
                addSong(buf.get(i));
            }
            /* 由于调用addSong方法会将flag置为true,故应当在初始化完成之后，将flag置为false */
            flag = false;
        }catch (IOException | ClassNotFoundException ex){
            ex.printStackTrace();
            flag = false;
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
        //只要调用了该方法，则视为音乐链表已经被修改
        flag = true;
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
        //只要调用了该方法，则视为音乐链表已经被修改
        flag = true;
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
    /**
     * 保存音乐播放列表
     */
    public void save(){
        /*如果链表被修改才需要保存*/
        if (flag){
            /* 如果链表不为空 */
            if (sum != 0){
                int i = 0;
                MusicNode node = FirstMusic;
                try {
                    FileOutputStream fs = new FileOutputStream(initFileName);
                    ObjectOutputStream os = new ObjectOutputStream(fs);
                    /* 先将上一次的序列化文件清空 */
                    os.writeObject(null);
                    /* 再将本次的数据写入序列化文件 */
                    while (i < sum){
                        os.writeObject(node);
                        node = node.next;
                    }
                    os.close();
                } catch (IOException ex){
                    ex.printStackTrace();
                }
            }
            /* 此时链表被修改且为空，故删除对应的序列化文件 */
            else {
                /* 如果是默认序列化文件则不删除仅置空 */
                if (initFileName.equals("InitOfMusicList.ser")){
                    try {
                        FileOutputStream fs = new FileOutputStream(initFileName);
                        ObjectOutputStream os = new ObjectOutputStream(fs);
                        /* 将默认初始化文件清空 */
                        os.writeObject(null);
                    }catch (IOException ex){
                        ex.printStackTrace();
                    }
                }
                /* 不是默认序列化文件时，则删除 */
                else{
                    File selectedFile = new File(initFileName);
                    /* 此时本该由save方法删除的文件已经不存在，则抛出一个已经删除了的提示 */
                    if (!selectedFile.exists()){
                        throw new InitException("文件出于某种原因不存在！！！");
                    }
                    /* 删除文件 */
                    else {
                        boolean flag =  selectedFile.delete();
                        /* 当删除失败时，抛出错误 */
                        if (!flag) throw new InitException("文件无法被删除！！！");
                    }
                }
            }
        }
    }
}
