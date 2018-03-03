package com;


import Implements.Implements;
import com.List.MusicList;
import com.List.MusicNode;
import com.List.PlayMode;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 音乐播放器所有功能的实现
 * 包括当前正在播放的歌曲以及当前播放模式
 * P.S.乐曲链表保存在 D:\JAVA CODE\MusicPlayer\乐曲列表
 * 包含有不同播放模式的实现，这些都是基于MusicList的,同时实现各种播放模式不需要额外的构造一个新的MusicList
 */
public class MusicPlayer implements Serializable,PlayMode {

    /*--------------------------------------------------------------------------------------------------------------
            以下是播放器的各种控件对应的值，每次修改都务必序列化存储
    --------------------------------------------------------------------------------------------------------------*/
    /**
     * 当前正在播放的乐曲,退出播放器后再次启动，仍然播放这首歌
     * P.S.(该字段并不保存到ini文件里)
     */
    public static  MusicNode currentMusicNode = null;

    /**
     * 当前播放列表在数组中的位置.这是真正被保存到ini文件里的字段
     */
    private static int indexOfCurrentMusicList ;

    /**
     * 记录下当前播放的乐曲是链表中第几个,以便下次初始化播放器时初始化currentMusicNode
     * P.S.当且仅当退出播放器时才需要该变量
     */
    private static int indexOfCurrentMusicNode;

    /**
     * 播放模式,退出播放器后再次启动，仍然以该模式播放
     * 仅允许0，1，2这三个值中的一个
     * 默认为顺序播放
     */
    public static int currentPlayMode;

    /**
     * 播放器的音量
     */
    private static int volume;

    /**
     * 当前进度条的位置
     */
    public static int currentPlayTime;


    /**
     * 播放器正在播放的当前播放列表(该字段并不保存到ini文件里)
     */
    public static MusicList currentMusicList = null;

    /**
     * 播放器管理的所有播放列表(该字段并不保存到ini文件)
     * P.S.第一个元素为播放器默认播放列表，其他为用户自建的播放列表.
     */
    public static ArrayList<MusicList> TotalMusicList = new ArrayList<>();

    /**
     * 所有MusicList对应的链表的序列化文件的相对路径,这是真正保存到ini文件里的字段
     * P.S.播放器开始时，使用该数组以初始化TotalMusicList.
     */
    public static ArrayList<String> TotalMusicListFileName = new ArrayList<>();

    /**
     * 乐曲列表总数
     */
    public static int sum = 1;
/*--------------------------------------------------------------------------------------------------------------
        //以下是用于实现随机播放的字段
--------------------------------------------------------------------------------------------------------------*/

    /**
     * 记录下本次播放器启动后的随机播放产生的访问序列
       以播放器启动时的currentMusicNode为原点，随机访问模式下调用getNextMusic方法得到的访问序列
     */
    private  static ArrayList<MusicNode> nextMusic;

    /**
     * 用来访问 nextMusic 的指针,指向栈顶(即最近添加进去的元素)，值为-1的时候表示没有元素
     */
    private static int indexOfNext;

    /**
     * 记录下本次播放器启动后的随机播放产生的访问序列
     以播放器启动时的currentMusicNode为原点，随机访问模式下调用getPriorMusic方法得到的访问序列
     */
    private  static ArrayList<MusicNode> priorMusic;

    /**
     * 用来访问 priorMusic 的指针,指向栈顶(即最近添加进去的元素)，值为-1的时候表示没有元素
     */
    private static int indexOfPrior;


    /**
     * 记录下 getNextMusic 或者 getPriorMusic 方法中的随机播放中的向
     * nextMusic 或者 priorMusic 写入不同的 MusicNode 的次数
     * P.S.与 maxCall 协调使用
     */
    private static int callCount;


      /**
     * 保证前10次产生随机播放时播放不同的乐曲（这要求乐曲列表里至少有11首歌）
     * 如果乐曲列表里没有11首歌，那么就保证前 sum - 1 次随机播放时播放不同的歌
     * E.G.当乐曲总共只有9首时，随机播放的前8首歌将互不相同，且都与根节点乐曲不同
     */
    private static int maxCall;


    /*--------------------------------------------------------------------------------------------------------------
            构造函数，不允许自建MusicPlayer对象
    --------------------------------------------------------------------------------------------------------------*/
    /**
     * 不允许自建MusicPlayer对象
     */
    private MusicPlayer(){
    }
    /*--------------------------------------------------------------------------------------------------------------
            播放器的初始化和保存方法
    --------------------------------------------------------------------------------------------------------------*/
    /**
     * 初始化方法
     * 整个程序运行期间只需要调用一次
     */
    public static void Init(){
        /* 用户数据被保存的路径 */
        String filePath = "D:\\JAVA CODE\\MusicPlayer\\config.ini";

        try {

            /* 用户设置数组 */
            String[] userSetting = new String[6];
            /* 用户设置数组对应的值 */
            String[] userSettingValue = new String[6];

            // 音乐播放器设置.匹配 MusicPlayer 段
            /* 当前播放列表在数组中的位置 */
            userSetting[0] = "indexOfCurrentMusicList";
            /* 当前播放的乐曲是链表中第几个 */
            userSetting[1] = "indexOfCurrentMusicNode";
            /* 播放模式 */
            userSetting[2] = "currentPlayMode";
            /* 播放器的音量 */
            userSetting[3] = "volume";
            /* 当前进度条的位置 */
            userSetting[4] = "currentPlayTime";
            /* 乐曲列表总数 */
            userSetting[5] = "sum";

            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            /* 用于执行匹配 */
            Pattern p;
            Matcher m;

            /* 开始匹配音乐播放器设置段 */
            while ((line = reader.readLine()) != null){
                /* 过滤空行 */
                if (line.equals("")) {
                    continue;
                }
                /* 过滤仅有注释的行 */
                if (line.trim().split(";")[0].equals("")){
                    continue;
                }
                p = Pattern.compile("\\[" + "MusicPlayer" + "]");
                m = p.matcher(line);
                /* 匹配到了指定的段(MusicPlayer)，则开始更新对应的字段 */
                if (m.matches()) {
                    /* 检索所有给定的字段 */
                    for (int k = 0; k < userSetting.length ;++k){
                        while ((line = reader.readLine()) != null){
                            /* 当给定字段被匹配到时 */
                            if (line.trim().split(";")[0].split(" = ")[0].equalsIgnoreCase(userSetting[k])){
                                /* 读取对应字段的值 */
                                userSettingValue[k] = line.trim().split(";")[0].split(" = ")[1];
                                break;
                            }
                        }
                    }
                    /* MusicPlayer段匹配完成就退出本次循环 */
                    break;
                }
            }

            /* 初始化播放器设置 */
            indexOfCurrentMusicList = Integer.valueOf(userSettingValue[0]);
            indexOfCurrentMusicNode = Integer.valueOf(userSettingValue[1]);
            currentPlayMode = Integer.valueOf(userSettingValue[2]);
            volume = Integer.valueOf(userSettingValue[3]);
            currentPlayTime = Integer.valueOf(userSettingValue[4]);
            sum = Integer.valueOf(userSettingValue[5]);

            /* 开始匹配用户播放列表段 */
            while ((line = reader.readLine()) != null){
                /* 过滤空行 */
                if (line.equals("")) {
                    continue;
                }
                /* 过滤仅有注释的行 */
                if (line.trim().split(";")[0].equals("")){
                    continue;
                }
                p = Pattern.compile("\\[" + "MusicListPath" + "]");
                m = p.matcher(line);
                /* 当匹配到了对应段(MusicListPath),则写入 TotalMusicListFileName 数组 */
                if (m.matches()){
                    int i = 0;
                    while (((line = reader.readLine()) != null) && i < sum){
                        /* 过滤空行 */
                        if (line.equals("")) {
                            continue;
                        }
                        /* 过滤仅有注释的行 */
                        if (line.trim().split(";")[0].equals("")){
                            continue;
                        }
                        if (line.contains("TotalMusicListFileName[" + i + "]")){
                            TotalMusicListFileName.add(line.trim().split(";")[0].split(" = ")[1]);
                            ++i;
                        }
                    }
                }
            }
            /* 关闭流 */
            reader.close();
            /* 使用 TotalMusicListFileName 来初始化 TotalMusicList */
            for (int i = 0; i < sum ; ++i){
                TotalMusicList.add(new MusicList(TotalMusicListFileName.get(i)));
            }
            /* 根据上一次的记录以恢复当前播放列表以及当前播放乐曲 */
            initCurrentMusicList(indexOfCurrentMusicList);
            initCurrentMusicNode(indexOfCurrentMusicNode);

            //设定随机播放相关字段
            setRandomPlay(currentMusicList.sum);

        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
    /**
     * 播放器的保存方法
     * 当且仅当关闭播放器时调用
     * 用户数据将被保存到config.ini文件里面
     */
    public static void Save(){
        /* 保存所有MusicList所对应的乐曲链表 */
        int i = 0;
        while (i < TotalMusicList.size()){
            TotalMusicList.get(i).Save();
            ++i;
        }

        /* 找到当前播放列表是第几个 */
        indexOfCurrentMusicList = getIndexOfCurrentMusicList();
        /* 找到当前播放乐曲时当前播放列表中的第几首歌 */
        indexOfCurrentMusicNode = getIndexOfCurrentMusicNode();

        /* 用户数据将被保存的绝对路径 */
        String filePath = "D:\\JAVA CODE\\MusicPlayer\\config.ini";

        /* 保存音乐播放器的设置及用户播放列表 */
        try {
            /* 需要修改的用户设置数组 */
            String[] userSetting = new String[6];
            /* 用户设置数组对应的值 */
            String[] userSettingValue = new String[6];

            // 音乐播放器设置.匹配 MusicPlayer 段
            /* 当前播放列表在数组中的位置 */
            userSetting[0] = "indexOfCurrentMusicList";
            /* 当前播放的乐曲是链表中第几个 */
            userSetting[1] = "indexOfCurrentMusicNode";
            /* 播放模式 */
            userSetting[2] = "currentPlayMode";
            /* 播放器的音量 */
            userSetting[3] = "volume";
            /* 当前进度条的位置 */
            userSetting[4] = "currentPlayTime";
            /* 乐曲列表总数 */
            userSetting[5] = "sum";

            // 音乐播放器设置对应的新值
            /* 当前播放列表在数组中的位置 */
            userSettingValue[0] = Integer.toString(indexOfCurrentMusicList);
            /* 当前播放的乐曲是链表中第几个 */
            userSettingValue[1] = Integer.toString(indexOfCurrentMusicNode);
            /* 播放模式 */
            userSettingValue[2] = Integer.toString(currentPlayMode);
            /* 播放音量 */
            userSettingValue[3] = Integer.toString(volume);
            /* 当前进度条的位置 */
            userSettingValue[4] = Integer.toString(currentPlayTime);
            /* 乐曲列表总数 */
            userSettingValue[5] = Integer.toString(sum);

            /* 开始保存用户数据 */
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            /* 用于执行匹配 */
            Pattern p;
            Matcher m;
            /* 暂存ini文件.每一个元素都是ini文件的一行 */
            ArrayList<String> fileContent = new ArrayList<>(45);

            /* 开始匹配音乐播放器设置段 */
            while ((line = reader.readLine()) != null){
                /* 过滤空行 */
                if (line.equals("")) {
                    fileContent.add(line);
                    continue;
                }
                /* 过滤仅有注释的行 */
                if (line.trim().split(";")[0].equals("")){
                    fileContent.add(line);
                    continue;
                }
                /* 保存上一行 */
                fileContent.add(line);
                p = Pattern.compile("\\[" + "MusicPlayer" + "]");
                m = p.matcher(line);
                /* 匹配到了指定的段(MusicPlayer)，则开始更新对应的字段 */
                if (m.matches()) {
                    /* 检索所有给定的字段 */
                    for (int k = 0; k < userSetting.length ;++k){
                        while ((line = reader.readLine()) != null){
                            /* 当给定字段被匹配到时 */
                            if (line.trim().split(";")[0].split(" = ")[0].equalsIgnoreCase(userSetting[k])){
                                /* 当该字段中不含有分号时 */
                                if (!line.contains(";")){
                                    line = line.split(" = ")[0] + " = " + userSettingValue[k];
                                    fileContent.add(line);
                                }
                                /* 当给定的字段中含有分号时 */
                                else {
                                    /* 修改对应的值 */
                                    line = line.split(";")[0].split(" = ")[0] + " = " + userSettingValue[k] + ";" + line.split(";")[1];
                                    /* 将修改后的值保存起来 */
                                    fileContent.add(line);
                                }
                                break;
                            }
                            else fileContent.add(line);
                        }
                    }
                    /* MusicPlayer段匹配完成就退出本次循环 */
                    break;
                }
            }
            /* 开始匹配用户播放列表段 */
            while ((line = reader.readLine()) != null){
                /* 过滤空行 */
                if (line.equals("")) {
                    fileContent.add(line);
                    continue;
                }
                /* 过滤仅有注释的行 */
                if (line.trim().split(";")[0].equals("")){
                    fileContent.add(line);
                    continue;
                }
                /* 保存上一行 */
                fileContent.add(line);
                p = Pattern.compile("\\[" + "MusicListPath" + "]");
                m = p.matcher(line);
                /* 暴力做法，直接不管用户的注释等 */
                /* 当匹配到了对应段(MusicListPath),则更新对应字段 */
                if (m.matches()){
                    for (int n = 0; n < TotalMusicListFileName.size(); ++n){
                        String str = "TotalMusicListFileName[" + n + "]" + " = " + TotalMusicListFileName.get(n);
                        fileContent.add(str);
                    }
                    break;
                }
            }
            /* 关闭流 */
            reader.close();
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath,false));
            for (String aFileContent : fileContent) {
                writer.write(aFileContent + "\n");
            }
            writer.flush();
            /* 关闭流 */
            writer.close();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
    /*--------------------------------------------------------------------------------------------------------------
            播放器的get方法
    --------------------------------------------------------------------------------------------------------------*/
    /**P.S.用户在使用播放器时并不知道自己所播放的是哪个列表的那首歌，所以需要该方法在退出播放器时找到用户的当前播放列表和乐曲
     * 在save方法中使用，以设定indexOfCurrentMusicNode
     * @return 当返回 -1 时代表没用找到，正常情况下，此时要么是用户没有播放任何一首歌，要么就是音乐列表为空
     */
    public static int getIndexOfCurrentMusicNode() {
        if (currentMusicNode != null && currentMusicList.sum != 0){
            //计数器，以记录currentMusicNode在链表中是第几个元素
            int cnt = 0;
            //从当前列表的第一首乐曲开始检索
            MusicNode node = currentMusicList.getFirstMusic();
            while (!currentMusicNode.equals(node)){
                node = node.next;
                ++cnt;
            }
            return cnt;
        }
        else return -1;
    }

    /** P.S.用户在使用播放器时并不知道自己所播放的是哪个列表的那首歌，所以需要该方法在退出播放器时找到用户的当前播放列表和乐曲
     * 在save方法中调用，以设定indexOfCurrentMusicList
     * @return 当前播放列表在 TotalMusicList 数组中的位置
     */
    public static int getIndexOfCurrentMusicList() {
        return TotalMusicList.indexOf(currentMusicList);
    }
    /**
     * 在GUI的系统托盘控件中调用
     * @return 当前播放乐曲的基本信息.格式为: 乐曲名 - 歌手
     * 当currentMusicNode为空时，返回 "CloudMusic"
     */
    public static String getCurrentMusicInfo(){
        if (currentMusicNode != null){
            return currentMusicNode.music.getSongName() + " - " + currentMusicNode.music.getArtist();
        }
        else return "CloudMusic";
    }
    /*--------------------------------------------------------------------------------------------------------------
            播放器的set方法
    --------------------------------------------------------------------------------------------------------------*/
    /**
     * 初始化播放器时调用，根据上一次的记录以恢复当前播放列表
     * @param indexOfCurrentMusicList  当前播放列表在数组中的位置
     */
    private static void initCurrentMusicList(int indexOfCurrentMusicList){
        currentMusicList = TotalMusicList.get(indexOfCurrentMusicList);
    }
    /**
     * 当用户点击GUI中的某一个音乐列表的乐曲时时调用以设定当前播放乐曲
     * P.S.当使用随机播放时，必须将用户设定的MusicNode入栈
     *
     */
    public  static void setCurrentMusicNode(int indexOfCurrentMusicNode){
        initCurrentMusicNode(indexOfCurrentMusicNode);
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
    }
    /**
     * 初始化播放器时调用，根据上一次的记录以恢复当前播放乐曲
     * @param indexOfCurrentMusicNode  当前播放的乐曲是当前链表中第几个
     */
    private static void initCurrentMusicNode(int indexOfCurrentMusicNode){
        int cnt = 0;
        MusicNode node = currentMusicList.getFirstMusic();
        while (cnt < indexOfCurrentMusicNode){
            node = node.next;
            ++cnt;
        }
        currentMusicNode = node;
    }
    /*--------------------------------------------------------------------------------------------------------------
   下面是各种播放模式的实现
   --------------------------------------------------------------------------------------------------------------*/
    /**
     * 以设定的currentPlayMode获取下一首歌
     * 用户按下“下一首”按钮或者本次播放结束后自动调用
     * 当currentMusicNode为空时，返回null
     * 只要调用了该方法，不管原来播放器是暂停的还是怎样的，都会开始播放指针所指向的Music
     */
    public static MusicNode getNextMusic(){
        //当currentMusicNode不为空时
        if (currentMusicNode != null){


            /* 顺序播放时 */
            /* 由于是循环双链表，故顺序播放等价于“列表循环” */
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
                    这可以保证从当前位置开始的前几次播放不会播放
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
                            offset = Implements.GetRandomNum(currentMusicList.sum);
                            //计数器
                            int cnt = 1;

                            /* 当取正向偏移时(从当前位置出发一路取next) */
                            if (offset > 0){
                                //将指针指向当前指针加上偏移量之后的新的位置
                                while (cnt != offset){
                                    node = node.next;
                                    ++cnt;
                                }
                            }
                            /* 当取反向偏移时(从当前位置出发一路取prior) */
                            else {
                                offset = offset * (-1);
                                //将指针指向当前指针加上偏移量之后的新的位置
                                while (cnt != offset){
                                    node = node.prior;
                                    ++cnt;
                                }
                            }
                        }
                    }
                    //调用次数超过maxCall次时，则不同的调用可能输出相同的MusicNode
                    else {
                        //生成偏移量
                        offset = Implements.GetRandomNum(currentMusicList.sum);
                        //计数器
                        int cnt = 1;

                        /* 当取正向偏移时(从当前位置出发一路取next) */
                        if (offset > 0){
                            //将指针指向当前指针加上偏移量之后的新的位置
                            while (cnt != offset){
                                node = node.next;
                                ++cnt;
                            }
                        }
                        /* 当取反向偏移时(从当前位置出发一路取prior) */
                        else {
                            offset = offset * (-1);
                            //将指针指向当前指针加上偏移量之后的新的位置
                            while (cnt != offset){
                                node = node.prior;
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
            单曲循环时，不会自动调用getNextMusic方法
            单曲循环时不用清空nextMusic和priorMusic数组
            */
            else if (currentPlayMode == Mode_Loop){
                /* 修改指针 */
                currentMusicNode = currentMusicNode.next;
                return currentMusicNode;
            }
        }
        return null;
    }
    /**
     * 以选定的currentPlayMode获取上一首歌
     * 当且仅当用户按下“上一首”按钮才会调用
     * 当currentMusicNode为空时，返回null
     * 只要调用了该方法，不管原来播放器是暂停的还是怎样的，都会开始播放指针所指向的Music
     */
    public static MusicNode getPriorMusic(){
        if (currentMusicNode != null){


            /* 顺序播放时 */
            /* 由于是循环双链表，故顺序播放等价于“列表循环” */
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
                            offset = Implements.GetRandomNum(currentMusicList.sum);
                            //计数器
                            int cnt = 1;

                            /* 当取正向偏移时(从当前位置出发一路取next) */
                            if (offset > 0){
                                //将指针指向当前指针加上偏移量之后的新的位置
                                while (cnt != offset){
                                    node = node.next;
                                    ++cnt;
                                }
                            }
                            /* 当取反向偏移时(从当前位置出发一路取prior) */
                            else {
                                //将指针指向当前指针加上偏移量之后的新的位置
                                offset = offset * (-1);
                                while (cnt != offset){
                                    node = node.prior;
                                    ++cnt;
                                }
                            }
                        }
                    }
                    //调用次数超过maxCall次时，则不同的调用可能输出相同的MusicNode
                    else {
                        //生成偏移量
                        offset = Implements.GetRandomNum(currentMusicList.sum);
                        //计数器
                        int cnt = 1;

                        /* 当取正向偏移时(从当前位置出发一路取next) */
                        if (offset > 0){
                            //将指针指向当前指针加上偏移量之后的新的位置
                            while (cnt != offset){
                                node = node.next;
                                ++cnt;
                            }
                        }
                        /* 当取反向偏移时(从当前位置出发一路取prior) */
                        else {
                            //将指针指向当前指针加上偏移量之后的新的位置
                            offset = offset * (-1);
                            while (cnt != offset){
                                node = node.prior;
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
            单曲循环时,不会自动调用getNextMusic方法
            单曲循环时不需要清空nextMusic和priorMusic数组
            */
            else if (currentPlayMode == Mode_Loop){
                currentMusicNode = currentMusicNode.prior;
                return currentMusicNode;
            }
        }
        return null;
    }

    public static void setRandomPlay(int sum){
        //初始化随机播放时，最大的产生不同随机乐曲的次数
        maxCall = sum < 11 ? sum - 1 : 10;
        nextMusic = new ArrayList<>(45);
        if (nextMusic.size() != 0){
            nextMusic.clear();
        }
        priorMusic = new ArrayList<>(45);
        if (priorMusic.size() != 0){
            priorMusic.clear();
        }
        indexOfNext = -1;
        indexOfPrior= -1;
        callCount = 1;
    }
}
