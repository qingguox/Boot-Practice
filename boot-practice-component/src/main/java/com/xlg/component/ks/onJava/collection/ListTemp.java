package com.xlg.component.ks.onJava.collection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangqingwei
 */
public class ListTemp {

    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();

        Map<String, String> map2 = new Hashtable<>();

        Map<String, String> map3 = new LinkedHashMap<>();

        Map<String, String> map4 = new ConcurrentHashMap<>();
//        List<Integer> list = Lists.newArrayList();
//        boolean b = list.iterator().hasNext();
//        list.iterator().next();
//
//        List<Integer> list2 = Lists.newLinkedList();
//
//        Vector<Integer> list3 =  new Vector<>();
//
//        CopyOnWriteArrayList<Integer> lo = new CopyOnWriteArrayList<>();
//        Stack<Integer> stack = new Stack<>();
//
        Set<Integer> set = new HashSet<>();
//
//        Set<Integer> set1 = new LinkedHashSet<>();
//
//        Set<Integer> set2 = new TreeSet<>();
    }
}
