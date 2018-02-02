package com.list;


import Implements.Implements;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 当前正在播放的歌曲以及当前播放模式
 * 包含有不同播放模式的实现，这些都是基于MusicList的,同时实现各种播放模式不需要额外的构造一个新的MusicList
 * 任何时候内存中只允许有一个CurrentMusicList对象
 * 序列化地存储在文件里面，方便下次重新打开播放器时载入内存
 */
public class CurrentMusicList implements Serializable,PlayMode {


    /**
     * 当前正在播放的乐曲,退出播放器后再次启动，仍然播放这首歌(只不过是从头开始)
     */
    private  MusicNode currentMusicNode;

    /**
     * 播放模式,退出播放器后再次启动，仍然以该模式播放
     * 仅允许1.2.3这三个值中的一个
     */
    private  int currentPlayMode;

/*--------------------------------------------------------------------------------------------------------------
        //以下是用于实现随机播放的字段
--------------------------------------------------------------------------------------------------------------*/

    /**
     * 记录下本次播放器启动后的随机播放产生的访问序列
       以播放器启动时的currentMusicNode为原点，随机访问模式下调用getNextMusic方法得到的访问序列
     */
    private  transient ArrayList<MusicNode> nextMusic = new ArrayList<>(45);

    /**
     * 用来访问 nextMusic 的指针,指向栈顶(即最近添加进去的元素)，值为-1的时候表示没有元素
     */
    private transient int indexOfNext = -1;

    /**
     * 记录下本次播放器启动后的随机播放产生的访问序列
     以播放器启动时的currentMusicNode为原点，随机访问模式下调用getPriorMusic方法得到的访问序列
     */
    private  transient ArrayList<MusicNode> priorMusic = new ArrayList<>(45);

    /**
     * 用来访问 priorMusic 的指针,指向栈顶(即最近添加进去的元素)，值为-1的时候表示没有元素
     */
    private transient int indexOfPrior = -1;


    /**
     * 记录下 getNextMusic 或者 getPriorMusic 方法中的随机播放中的向
     * nextMusic 或者 priorMusic 写入不同的 MusicNode 的次数
     * P.S.与 maxCall 协调使用
     */
    private transient int callCount = 1;


      /**
     * 保证前20次产生随机播放时播放不同的乐曲（这要求乐曲列表里至少有21首歌）
     * 如果乐曲列表里没有21首歌，那么就保证前 sum - 1 次随机播放时播放不同的歌
     * E.G.当乐曲总共只有9首时，随机播放的前8首歌将互不相同，且都与根节点乐曲不同
     */
    private transient int maxCall = MusicList.getMusicList().getSum() < 21 ? MusicList.getMusicList().getSum() - 1 : 20;


    /*--------------------------------------------------------------------------------------------------------------
          以上是用于实现随机播放的字段
    --------------------------------------------------------------------------------------------------------------*/


    /**
     * 内存中唯一的CurrentMusicList
     */
    private static transient CurrentMusicList currentMusicList;


    /**
     * 不允许自建CurrentMusicList对象
     */
    private CurrentMusicList(){

    }


    /**
     * @return 返回当前播放列表信息，
     * 序列化时需要调用
     */
    public static CurrentMusicList getCurrentMusicList() {
        return currentMusicList;
    }

    /**
     * @param currentMusicList 解序列化得到的上一次启动播放器时的播放列表信息
     *                         解序列化时候调用
     */
    public static void setCurrentMusicList(CurrentMusicList currentMusicList) {
        CurrentMusicList.currentMusicList = currentMusicList;
    }


    /**
     * 当列表中没有任何歌曲时将会调用
     */
    public void init(){
        currentMusicNode = null;
        currentPlayMode = 1;
    }


    /**
     * 设定播放器现在播放的乐曲,并立刻被序列化保存
     * 当使用随机播放时，必须将用户设定的MusicNode入栈
     * 然后GUI中的事件处理模块将播放currentMusicNode指向的音乐
     */
    public  void setCurrentMusicNode(MusicNode currentMusicNode){
        //当使用随机播放时，必须将用户设定的MusicNode入栈
        if (currentPlayMode == Mode_Random){
            //当priorMusic中没有数据时，向nextMusic中写入数据
            if (indexOfPrior == -1){
                nextMusic.add(currentMusicNode);
                ++indexOfNext;
            }
            //反之，当nextMusic中没有数据时，向priorMusic中写入数据
            else {
                priorMusic.add(currentMusicNode);
                ++indexOfPrior;
            }
        }
        //如果并非随机播放，则只需要修改指针即可
        this.currentMusicNode = currentMusicNode;
        //保存
        Implements.Serialize(currentMusicList);
    }

    /**
     * @return 获取当前正在播放的音乐
     */
    public  MusicNode getCurrentMusicNode() {
        return currentMusicNode;
    }



    /**
     * @param mode 选定的播放模式代码，仅允许1，2，3，
     *             分别对应 顺序，随机，单曲循环
     *             P.S.并立刻被序列化保存
     */
    public  void setCurrentPlayMode(int mode) {
        switch (mode){
            case Mode_Sequential : currentPlayMode = Mode_Sequential;break;
            case Mode_Random : currentPlayMode = Mode_Random;break;
            case Mode_Loop : currentPlayMode = Mode_Loop;break;
            default: //在这里报错
        }
        //保存
        Implements.Serialize(currentMusicList);
        /*--------------------------------------------------------------------------------------------------------------
        如果输入的模式代码不是1，2，3中的一个，则报错
        --------------------------------------------------------------------------------------------------------------*/
    }

    /**
     * @return 返回当前播放模式
     */
    public  int getCurrentPlayMode(){
        return currentPlayMode;
    }






   /*--------------------------------------------------------------------------------------------------------------
   下面是各种播放模式的实现
   --------------------------------------------------------------------------------------------------------------*/




    /**
     * 以设定的currentPlayMode获取下一首歌
     * 当currentMusicNode为空时，返回null
     * 只要调用了该方法，不管原来播放器是暂停的还是怎样的，都会开始播放指针所指向的Music
     */
    public MusicNode getNextMusic(){
        //当currentMusicNode不为空时
        if (currentMusicNode != null){


            /* 顺序播放时 */
            if (currentPlayMode == Mode_Sequential){
                /* 修改指针 */
                currentMusicNode = currentMusicNode.next;
                return currentMusicNode;
            }


            /* 随机播放时 */
            else if (currentPlayMode == Mode_Random){
                //如果priorMusic中没有数据，那么将本次随机序列的数据写入nextMusic中
                //或者当Prior数组指针indexOfPrior指向根节点（即为0）时还调用getNextMusic，
                //则进入到if语句块，往Next数组中写入数据并播放
                if (indexOfPrior  - 1 <= -1){

                    //当indexOfNext不指向栈顶时，一定是调用了getPriorMusic，则indexOfNext加1即可
                    if (indexOfNext +1 != nextMusic.size()){
                        ++indexOfNext;
                        currentMusicNode = nextMusic.get(indexOfNext);
                        return currentMusicNode;
                    }

                    /*
                    记录下本次启动随机播放时的currentMusicNode作为根节点
                    根节点只有本次随机播放时间内第一次调用getNextMusic方法才记录
                    */
                    if (nextMusic.isEmpty()){
                        nextMusic.add(currentMusicNode);
                        ++indexOfNext;
                    }

                    //本次指针偏移量
                    int offset;

                    //保证线程安全以及随机播放的前callOfNext次调用都返回不同的乐曲
                    MusicNode node = currentMusicNode;

                    /*
                    前maxCall次调用随机播放时
                    保证前maxCall次随机播放输出不同的MusicNode
                    */
                    if (callCount <= maxCall){
                        //如果生成的MusicNode在nextMusic里面，则继续循环直到随机到一个不在序列内的
                        while (nextMusic.indexOf(node) != -1){
                            //生成偏移量
                            offset = Implements.GetRandomNum(MusicList.getMusicList().getSum());
                            //计数器
                            int cnt = 1;

                            /* 当取正向偏移时(从当前位置出发一路取next) */
                            if (offset > 0){
                                //将指针指向当前指针加上偏移量之后的新的位置
                                while (cnt != offset){
                                    node = currentMusicNode.next;
                                    ++cnt;
                                }
                            }
                            /* 当取反向偏移时(从当前位置出发一路取prior) */
                            else {
                                offset = offset * (-1);
                                //将指针指向当前指针加上偏移量之后的新的位置
                                while (cnt != offset){
                                    node = currentMusicNode.prior;
                                    ++cnt;
                                }
                            }
                        }
                    }
                    //调用次数超过maxCall次时，则不同的调用可能输出相同的MusicNode
                    else {
                        //生成偏移量
                        offset = Implements.GetRandomNum(MusicList.getMusicList().getSum());
                        //计数器
                        int cnt = 1;

                        /* 当取正向偏移时(从当前位置出发一路取next) */
                        if (offset > 0){
                            //将指针指向当前指针加上偏移量之后的新的位置
                            while (cnt != offset){
                                node = currentMusicNode.next;
                                ++cnt;
                            }
                        }
                        /* 当取反向偏移时(从当前位置出发一路取prior) */
                        else {
                            offset = offset * (-1);
                            //将指针指向当前指针加上偏移量之后的新的位置
                            while (cnt != offset){
                                node = currentMusicNode.prior;
                                ++cnt;
                            }
                        }
                    }
                    //保证线程安全以及随机播放的前callOfNext次调用都返回不同的乐曲
                    currentMusicNode = node;
                    //记录下本次访问
                    nextMusic.add(currentMusicNode);
                    //修改nextMusic的栈指针
                    ++indexOfNext;
                    //随机播放调用次数加1
                    ++callCount;
                }
                //当priorMusic中有数据中，那么从priorMusic中读取数据并播放
                else {
                    currentMusicNode = priorMusic.get(--indexOfPrior);
                }
                return currentMusicNode;
            }


            /*
            单曲循环时
            单曲循环时不用清空nextMusic和priorMusic数组
            */
            else {
                /* 修改指针 */
                currentMusicNode = currentMusicNode.next;
                return currentMusicNode;
            }
        }
        return null;
    }

    /**
     * 以选定的currentPlayMode获取上一首歌
     * 当currentMusicNode为空时，返回null
     * 只要调用了该方法，不管原来播放器是暂停的还是怎样的，都会开始播放指针所指向的Music
     */
    public MusicNode getPriorMusic(){
        if (currentMusicNode != null){


            /* 顺序播放时 */
            if (currentPlayMode == Mode_Sequential){
                /* 修改指针 */
                currentMusicNode = currentMusicNode.prior;
                return currentMusicNode;
            }


            /* 随机播放时 */
            else if (currentPlayMode == Mode_Random){
                //如果nextMusic数组里面有数据，则读取并播放
                //或者当Next数组指针indexOfNext指向根节点时，还要调用getPriorMusic，
                //则此时需要进入else语句块，即往Prior数组里面写入数据并播放
                if (indexOfNext - 1 > -1){
                    currentMusicNode = nextMusic.get(--indexOfNext);
                }
                //当nextMusic里面没有数据时,向priorMusic中写入数据并播放
                else {

                    //当indexOfPrior不指向栈顶时，则一定时调用了getNextMusic，则将indexOfPrior加1即可
                    if (indexOfPrior + 1 != priorMusic.size()){
                        ++indexOfPrior;
                        currentMusicNode = priorMusic.get(indexOfPrior);
                        return currentMusicNode;
                    }

                     /*
                    记录下本次启动随机播放时的currentMusicNode作为根节点
                    根节点只有本次随机播放时间内第一次调用getPriorMusic方法才记录
                    */
                    if (priorMusic.isEmpty()){
                        priorMusic.add(currentMusicNode);
                        ++indexOfPrior;
                    }

                    //本次指针偏移量
                    int offset;
                    //保证线程安全以及随机播放的前callOfNext次调用都返回不同的乐曲
                    MusicNode node = currentMusicNode;

                    /*
                    前maxCall次调用随机播放时
                    保证前maxCall次随机播放输出不同的MusicNode
                    */
                    if (callCount <= maxCall){
                        //如果生成的MusicNode在priorMusic里面已经存在，则继续循环直到随机到一个不在序列内的
                        while (priorMusic.indexOf(node) != -1){
                            //生成偏移量
                            offset = Implements.GetRandomNum(MusicList.getMusicList().getSum());
                            //计数器
                            int cnt = 1;

                            /* 当取正向偏移时(从当前位置出发一路取next) */
                            if (offset > 0){
                                //将指针指向当前指针加上偏移量之后的新的位置
                                while (cnt != offset){
                                    node = currentMusicNode.next;
                                    ++cnt;
                                }
                            }
                            /* 当取反向偏移时(从当前位置出发一路取prior) */
                            else {
                                //将指针指向当前指针加上偏移量之后的新的位置
                                offset = offset * (-1);
                                while (cnt != offset){
                                    node = currentMusicNode.prior;
                                    ++cnt;
                                }
                            }
                        }
                    }
                    //调用次数超过maxCall次时，则不同的调用可能输出相同的MusicNode
                    else {
                        //生成偏移量
                        offset = Implements.GetRandomNum(MusicList.getMusicList().getSum());
                        //计数器
                        int cnt = 1;

                        /* 当取正向偏移时(从当前位置出发一路取next) */
                        if (offset > 0){
                            //将指针指向当前指针加上偏移量之后的新的位置
                            while (cnt != offset){
                                node = currentMusicNode.next;
                                ++cnt;
                            }
                        }
                        /* 当取反向偏移时(从当前位置出发一路取prior) */
                        else {
                            //将指针指向当前指针加上偏移量之后的新的位置
                            offset = offset * (-1);
                            while (cnt != offset){
                                node = currentMusicNode.prior;
                                ++cnt;
                            }
                        }
                    }
                    //保证线程安全以及随机播放的前callOfNext次调用都返回不同的乐曲
                    currentMusicNode = node;
                    //记录下本次访问
                    priorMusic.add(currentMusicNode);
                    //修改priorMusic的栈顶指针
                    ++indexOfPrior;
                    //调用次数加1
                    ++callCount;
                }
                return currentMusicNode;
            }


            /*
            单曲循环时
            单曲循环时不需要清空nextMusic和priorMusic数组
            */
            else {
                currentMusicNode = currentMusicNode.prior;
                return currentMusicNode;
            }
        }
        return null;
    }

    /**
     * 调用线程中断使得播放暂停
     */
    public void stop(){
        //?
    }
}
