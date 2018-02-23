package com.List;

import Implements.Implements;
import Error.InitException;
import Error.InitError;
import com.MusicPlayer;

import java.io.*;

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
    public  int sum = 0;

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
    private  boolean isModify = false;

    /**
     * 保存有乐曲播放列表的序列化文件的名字。
     */
    private String initFileName = null;
    /**
     * 该方法用于在启动播放器时从用户数据里恢复音乐播放列表
     * P.S.需要保证输入进来的序列化文件是已经存在的
     * @param initFileName 使用输入的已存在的序列化文件的相对路径以初始化MusicList对象.
     */
    public MusicList(String initFileName){

        /* 设定乐曲列表序列化文件名字（相对路径） */
        this.initFileName = initFileName;

        try {
            /* 初始化 */
            Init(initFileName);
        }catch (InitException ex){
            int errorCode = ex.getErrorCode();
            /* 将输出给用户看的错误信息 */
            String errorMessage = ex.getMessage();
            switch (errorCode){
                /* 初始化时，由于某种原因，文件的部分数据丢失，仅成功初始化了一部分,可能文件部分损坏 */
                case InitError.Part_File_Unreadable :{
                    // TODO: 2018/2/8 使用GUI告诉用户文件部分损坏
                    break;
                }
                /* 初始化时，整个文件都无法读取,说明可能文件损坏 */
                case InitError.File_Unreadable :{
                    // TODO: 2018/2/8 使用GUI告诉用户文件全部损坏
                    break;
                }
            }
        }
    }
    /**
     * 用户接口
     * 该方法用于创建一张全新的乐曲列表
     * P.S.使用该方法后必须为列表选定一个序列化文件名以保存
     */
    public MusicList(){

    }
    /*--------------------------------------------------------------------------------------------------------------
     MusicList的set方法
    --------------------------------------------------------------------------------------------------------------*/
    /**
     * 用户接口,用于根据给定的音乐列表名字在序列化文件数组中创建相应的表项
     * 调用该方法后，视为用户创建列表成功
     * P.S.当且仅当创建全新的播放列表之后才调用,且不允许同名
     * @param MusicListName 创建新的播放列表时指定的播放列表名字
     */
    public void setInitFileName(String MusicListName){
        // TODO: 2018/2/8 创建新列表时，应当不允许同名
        this.initFileName = MusicListName + ".ser";
        /* 将用户新建播放列表对应的链表的序列化文件的相对路径添加到数组 */
        MusicPlayer.TotalMusicListFileName.add(MusicPlayer.sum,MusicListName);
        /* 将用户新建的MusicList对象添加到数组,并且列表总数加1 */
        MusicPlayer.TotalMusicList.add(MusicPlayer.sum++,this);
        /* 创建即代表修改 */
        isModify = true;
    }
    /*--------------------------------------------------------------------------------------------------------------
      MusicList的get方法
    --------------------------------------------------------------------------------------------------------------*/
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

    /**
     *
     * @return 该MusicList对应的链表的序列化文件的相对路径
     */
    public String getInitFileName() {
        return initFileName;
    }
    /*--------------------------------------------------------------------------------------------------------------
     初始化和保存MusicList对象方法
    --------------------------------------------------------------------------------------------------------------*/
    /**
     * 初始化方法,使用用户数据来初始化MusicList以构造链表
     * 启动播放器时才需要解序列化
     * 整个程序运行期间只需要调用一次
     * @param initFileName 输入进来的序列化文件名.
     *                     P.S.我们应当保证输入进来的文件名指向已经创建了的的播放列表
     */
    private void Init(String initFileName){
        /* 初始化内存中的链表 */
        try {
            /* 打开流文件 */
            FileInputStream fs = new FileInputStream(initFileName);
            ObjectInputStream os = new ObjectInputStream(fs);
            /* 从流中读取第一歌节点 */
            FirstMusic = (MusicNode)os.readObject();
            /* 乐曲总数加1 */
            ++sum;
            /* 取出链表最后一个节点,并得到乐曲总数 */
            while (fs.available() > 0){
                LastMusic = (MusicNode)os.readObject();
                ++sum;
            }
            /* 关闭流 */
            os.close();
        }
        /* 给定的序列化文件正常读取但里面没有数据时 */
        /* P.S.抛出该错误时，大概率时因为用户没有添加乐曲进入列表,故此时什么都不做 */
        catch (ClassNotFoundException ex){
            /* 此时什么都不用做 */
        }
        /* 此时读取流文件时候出错,那么已经读取了多少个MusicNode对象就往链表里插入多少个，然后报错 */
        catch (IOException ex){
            /* 当输入流抛出异常前已经读取了一部分MusicNode对象出来时 */
            if (sum != 0){
                throw  new InitException("仅有一部分的数据被正常读取，其他的读取失败.",InitError.Part_File_Unreadable);
            }
            /* 此时整个文件都无法读取 */
            else throw new InitException("文件读取失败.",InitError.File_Unreadable);
        }
    }
    /**
     * 保存MusicList对象所管理的乐曲链表
     */
    public void Save(){
        /*如果链表被修改才需要保存*/
        if (isModify){
            int i = 0;
            MusicNode node = FirstMusic;
            try {
                FileOutputStream fs = new FileOutputStream(initFileName);
                ObjectOutputStream os = new ObjectOutputStream(fs);
                /* 先将上一次的序列化文件清空 */
                os.writeObject(null);
                /* 将这张链表全部写入序列化文件 */
                /*
                在序列化存储的时候，必须将整个链表遍历.因为如果不这样，读取的时候就只能读取处一个MusicNode对象
                虽然讲仍然可以通过这个对象的指针访问整个链表，但这样在初始化的时候就没法找到LastMusic节点了
                */
                while (i < sum){
                    os.writeObject(node);
                    node = node.next;
                }
                os.close();
            } catch (IOException ex){
                ex.printStackTrace();
            }
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
        isModify = true;
    }

    /**
     * @param selectedNode 用于从歌曲链表中删除选定的歌曲
     * @param isDeleteOrNot 为true时，表示连同选定的歌曲的源文件也给删除
     * @return 当且仅当删除源文件失败时才返回false.节点的删除不可能失败
     */
    public boolean deleteSong(MusicNode selectedNode,boolean isDeleteOrNot){
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
        boolean result = true;
        //如果用户选择删除源文件时
        if (isDeleteOrNot){
            File file = new File(selectedNode.music.getMp3FilePath());
            result = file.delete();
        }
        //释放指针
        MusicNode.DeleteSelectedNode(selectedNode);
        //歌曲总数减1
        --sum;
        //只要调用了该方法，则视为音乐链表已经被修改
        isModify = true;
        //当删除源文件失败时，才返回false.链表的删除不可能失败
        return result;
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

    @Override
    public String toString() {
        return initFileName;
    }

    /**
     * @param selectedNode 待检测的MusicNode
     * @return 如果在该列表中存在指向的Music与待检测的MusicNode所指向的Music完全一样的结点，则返回true
     */
    public boolean contains(MusicNode selectedNode){
        int cnt = sum;
        MusicNode node = FirstMusic;
        while (cnt > 0){
            if (selectedNode.isSame(node)) return true;
            node = node.next;
            --cnt;
        }
        return false;
    }
}
