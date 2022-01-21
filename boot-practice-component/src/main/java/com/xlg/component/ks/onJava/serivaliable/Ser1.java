package com.xlg.component.ks.onJava.serivaliable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * @author wangqingwei
 * Created on 2021-12-08
 */
public class Ser1 implements Externalizable {

    private String str;
    private int i;

    public Ser1() {
    }

    public Ser1(String str, int i) {
        System.out.println("generator two params");
        this.str = str;
        this.i = i;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(str);
        out.writeInt(i);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        str = (String) in.readObject();
        i = in.readInt();
    }

    @Override
    public String toString() {
        return "Ser1{" +
                "str='" + str + '\'' +
                ", i=" + i +
                '}';
    }
}
