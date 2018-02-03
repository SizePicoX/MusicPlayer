//package com.list;


//import Implements.Implements;
import com.Music.Music;
import javazoom.jl.decoder.*;

import java.io.*;


class A implements Serializable{
    int abcd;

    public void  getAbcd() {
        System.out.println(abcd);
    }
}

public class test {



    public static void main(String[] args)throws FileNotFoundException ,JavaLayerException{
//        long a = System.currentTimeMillis();
//        try {
//            BufferedInputStream buuffer = new BufferedInputStream(new FileInputStream("C:\\Users\\www11\\Music\\病名为爱.mp3"));
//            Player player = new Player(buuffer);
//            //player.play();
//        }catch(IOException ex){}

        //F:\CloudMusic\怎么唱情歌.mp3
        //F:\CloudMusic\DAOKO,米津玄師 - 打上花火.mp3
        //C:\Users\www11\Pictures\Saved Pictures\牧濑红莉栖.JPG
        Music music;
        long a = System.currentTimeMillis();
        String[] buf = Music.setMusicInfo("C:\\Users\\www11\\Music\\DAOKO,米津玄師 - 打上花火.mp3");
        if (buf[0].equals("true")){
            music = new Music("C:\\Users\\www11\\Music\\DAOKO,米津玄師 - 打上花火.mp3",buf);
        }
        long b = System.currentTimeMillis();
        System.out.println(b - a);
        //System.out.println(b - a);
        //long b =System.currentTimeMillis();
//        for (int i = 0; i <= 999 ; ++i){
//            System.out.println(Implements.GetRandomNum(1000));
//        }


        //测试
        //System.out.println("是否成功 " +s .getSongName());

//        int[] array = new int[10];
//        Implements.GetRandomNum(10,array);
//        for (int i = 0; i < 10 ; ++i){
//            System.out.println(array[i]);
//        }
        //获取MP3时长的方法
//        File file = new File("F:\\CloudMusic\\绘梨衣 - 最后的旅行——记《龙族》.mp3");
//        FileInputStream fis=new FileInputStream(file);
//        try {
//            int b=fis.available();
//            Bitstream bt=new Bitstream(fis);
//            Header h = bt.readFrame();
//            int time = (int) h.total_ms(b);
//            int i = time/1000;
//            System.out.println(i/60 + ":" + i%60);
//        }catch (IOException ex){}

//        A a = new A();
//        a.abcd = 105;
//
//        //序列化输入
//        try {
//            FileOutputStream fs = new FileOutputStream("test.ser");
//            ObjectOutputStream os = new ObjectOutputStream(fs);
//            os.writeUnshared(a);
//            os.close();
//        }catch (Exception ex){
//
//        }
//        //输出
//        try {
//            FileInputStream fs = new FileInputStream("test.ser");
//            ObjectInputStream os = new ObjectInputStream(fs);
//            A test = (A) os.readUnshared();
//            System.out.println(test.abcd);
//        }catch (Exception ex){
//
//        }

//        String test = "Hello World";
////        System.out.println(test.toCharArray()[]);

//

    }
}

