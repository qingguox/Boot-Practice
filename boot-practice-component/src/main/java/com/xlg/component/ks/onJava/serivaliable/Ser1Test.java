package com.xlg.component.ks.onJava.serivaliable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author wangqingwei
 * Created on 2021-12-08
 */
public class Ser1Test {

    public static void main(String[] args) {
        Ser1 s = new Ser1("adf", 2);
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("saa.txt"));
            outputStream.writeObject(s);

            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("saa.txt"));
            s = (Ser1) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(s.toString());
    }
}
