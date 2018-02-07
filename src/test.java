//package com.list;


//import Implements.Implements;
import com.Music.Music;
import javazoom.jl.decoder.*;

import java.io.*;

class A implements Serializable{
    int a;
    A next;
}
public class test {


    public static void main(String[] args)throws FileNotFoundException ,JavaLayerException{
//        A a1 = new A();  a1.a = 200;
//        A a2 = new A();  a2.a = 0;
//        A a3 = new A();  a3.a = 3;
//        A a4 = new A();  a4.a = 4;
//        A a5 = new A();  a5.a = 5;
//        A a6 = new A();  a6.a = 6;
//        a1.next = a2;
//        a2.next = a3;
//        a3.next = a4;
//        a4.next = a5;
//        a5.next = a6;
//        a6.next = a1;
//
//        A test = a1;
//        FileOutputStream fs = new FileOutputStream("test.ser");
//
//            try{
//                ObjectOutputStream os = new ObjectOutputStream(fs);
//                for (int i = 0 ; i < 6 ; ++i){
//                    os.writeObject(test);
//                    test =  test.next;
//                }
//                os.close();
//            }catch (Exception ex){
//                ex.printStackTrace();
//            }



//               try {
//            FileOutputStream fileOutputStream = new FileOutputStream("test.ser");
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
//            objectOutputStream.writeObject(null);
//            objectOutputStream.close();
//        }catch (IOException ex){
//            ex.printStackTrace();
//        }



//        A test2;
//        FileInputStream fs = new FileInputStream("test.ser");
//        try{
//
//            ObjectInputStream os = new ObjectInputStream(fs);
//            while (fs.available() > 0){
//                test2 = (A)os.readObject();
//                System.out.println(test2.a);
//            }
//            os.close();
//        }catch (Exception ex){
//            ex.printStackTrace();
//            System.out.println("error!!!");
//        }
    }
}

