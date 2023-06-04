package com.xlg.cms.api.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author wangqingwei
 * Created on 2022-08-17
 */
public class TEst {
    public static void main(String[] args) throws IOException {
        copyFile("/Users/easy/Desktop/a.txt");

    }

    public static void copyFile(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        List<String> list = Files.readAllLines(path);
        String[] strings = list.get(0).split(",");
        for (String s : strings) {
            System.out.println(s.trim());
        }
    }
}
