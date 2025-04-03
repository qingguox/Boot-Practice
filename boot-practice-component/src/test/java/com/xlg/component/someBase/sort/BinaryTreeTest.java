package com.xlg.component.someBase.sort;

import javax.xml.soap.Node;
import java.util.*;

/**
 * @author qingguox
 * Created on 2025-02-16
 */
public class BinaryTreeTest {

    public static void main(String[] args) {
        TreeNodeOne<Integer> head = new TreeNodeOne<>(2);

        TreeNodeOne<Integer> left = new TreeNodeOne<>(1);
        TreeNodeOne<Integer> right = new TreeNodeOne<>(3);

        head.left = left;
        head.right = right;

        System.out.println("inOrder...");
        inOrder(head);

        System.out.println("middleOrder...");
        middleOrder(head);

        System.out.println("afterOrder...");
        afterOrder(head);

        // 二叉树层序遍历
        List<List<Integer>> lists = printTreeLevel(head);
        for (List<Integer> x1 : lists) {
            System.out.println(Arrays.toString(x1.toArray()));
        }
        System.out.println("222...");
    }

    public static<T> List<List<T>> printTreeLevel(TreeNodeOne<T> head) {
        Queue<TreeNodeOne<T>> queue = new ArrayDeque<>();
        if (head == null) {
            return null;
        }
        queue.add(head);
        List<List<T>> res = new ArrayList<>();
        while (!queue.isEmpty()) {
            int count = queue.size();

            LinkedList<T> list = new LinkedList<>();
            while (count > 0) {
                TreeNodeOne<T> poll = queue.poll();
                if (res.size() % 2 == 0) {
                    // 奇数
                    list.addLast(poll.item);
                } else {
                    list.addFirst(poll.item);
                }
                if (poll.left != null) {
                    queue.add(poll.left);
                }
                if (poll.right != null) {
                    queue.add(poll.right);
                }
                count--;
            }
            res.add(list);

        }
        return res;
    }

    // 二叉树先序遍历: 根左右, 本质上是用栈
    public static void inOrder(TreeNodeOne head) {
        Stack<TreeNodeOne> stack = new Stack<>();
        if (head == null) {
            return;
        }
        stack.push(head);
        while (!stack.isEmpty()) {
            TreeNodeOne pop = stack.pop();
            System.out.println(pop.item);
            if (pop.getRight() != null) {
                stack.push(pop.getRight());
            }
            if (pop.getLeft() != null) {
                stack.push(pop.getLeft());
            }
        }
    }

    // 二叉树中序遍历: 左根右,  先把把左侧节点全部入栈
    public static void middleOrder(TreeNodeOne head) {
        if (head == null) {
            return;
        }
        Stack<TreeNodeOne> stack = new Stack<>();
        while (!stack.isEmpty() || head != null) {
            if (head != null) {
                stack.push(head);
                head = head.left;
            } else {
                head = stack.pop();
                System.out.println(head.item);
                head = head.right;
            }
        }
    }

    // 二叉树后序遍历: 左右根, 采用heapStack 构建栈底 -> 栈顶部 根右左
    public static void afterOrder(TreeNodeOne head) {
        if (head == null) {
            return;
        }
        Stack<TreeNodeOne> stack = new Stack<>();
        Stack<TreeNodeOne> help = new Stack<>();
        stack.push(head);
        while (!stack.isEmpty()) {
            head = stack.pop();
            help.push(head);
            if (head.getLeft() != null) {
                stack.push(head.left);
            }
            if (head.getRight() != null) {
                stack.push(head.right);
            }
        }
        while (!help.isEmpty()) {
            System.out.println(help.pop().item);
        }
    }

    // 在二叉树中找到一个指定节点X的后继节点, 中续遍历 左 根 右
    // X 是 非叶子节点, 后继节点是: X 是右侧最左节点
    // X 是 叶子节点, 后继节点是: X父节点中的一个
    public static TreeNodeOne getSuccessOrNode(TreeNodeOne node) {
        if (node == null) {
            return null;
        }
        if (node.right != null) {
            return getLeftMost(node.right);
        }
        TreeNodeOne parent = node.parent;
        while (parent != null && parent.left != node) {
            node = parent;
            parent = parent.parent;
        }
        return parent;
    }

    private static TreeNodeOne getLeftMost(TreeNodeOne node) {
        if (node == null) {
            return node;
        }
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    // 二叉树的序列化和反序列化  先序遍历 和 层序遍历两种
    public static String serialByPer(TreeNodeOne head) {
        if (head == null) {
            return "#!";
        }
        String res = head.item + "!";
        res += serialByPer(head.left);
        res += serialByPer(head.right);
        return res;
    }

    public static TreeNodeOne recoverByPreString(String preStr) {
        String[] values = preStr.split("!");
        Queue<String> queue = new LinkedList<>();
        for (int i = 0; i < values.length; i++) {
            queue.offer(values[i]);
        }
        return recoverByOrder(queue);
    }

    private static TreeNodeOne recoverByOrder(Queue<String> queue) {
        String value = queue.poll();
        if (value.equals("#")) {
            return null;
        }
        TreeNodeOne head = new TreeNodeOne(Integer.valueOf(value));
        head.left = recoverByOrder(queue);
        head.right = recoverByOrder(queue);
        return head;
    }

    // 层序遍历
    public static String serialByLevel(TreeNodeOne head) {
        if (head == null) {
            return "#!";
        }
        String res = head.item + "!";
        Queue<TreeNodeOne> queue = new LinkedList<>();
        while (!queue.isEmpty()) {
            head = queue.poll();
            if (head.left != null) {
                res += head.left + "!";
                queue.offer(head.left);
            } else {
                res += "#!";
            }
            if (head.right != null) {
                res += head.right + "!";
                queue.offer(head.right);
            } else {
                res += "#!";
            }
        }
        return res;
    }

    public static TreeNodeOne recoverByLevelString(String levelStr) {
        String[] values = levelStr.split("!");
        int index = 0;
        TreeNodeOne head = generateNodeByString(values[index++]);
        Queue<TreeNodeOne> queue = new LinkedList<>();
        if (head != null) {
            queue.offer(head);
        }
        TreeNodeOne node;
        while (!queue.isEmpty()) {
            node = queue.poll();
            node.left = generateNodeByString(values[index++]);
            node.right = generateNodeByString(values[index++]);
            if (node.left != null) {
                queue.offer(node.left);
            }
            if (node.right != null) {
                queue.offer(node.right);
            }
        }
        return head;
    }

    private static TreeNodeOne generateNodeByString(String value) {
        if (value.equals("#")) {
            return null;
        }
        return new TreeNodeOne(value);
    }

    public static class TreeNodeOne<T> {
        private T item;
        private TreeNodeOne left;
        private TreeNodeOne right;
        private TreeNodeOne parent;

        public TreeNodeOne() {
        }

        public TreeNodeOne(T item) {
            this.item = item;
        }

        public TreeNodeOne(T item, TreeNodeOne left, TreeNodeOne right) {
            this.item = item;
            this.left = left;
            this.right = right;
        }

        public T getItem() {
            return item;
        }

        public void setItem(T item) {
            this.item = item;
        }

        public TreeNodeOne getLeft() {
            return left;
        }

        public void setLeft(TreeNodeOne left) {
            this.left = left;
        }

        public TreeNodeOne getRight() {
            return right;
        }

        public void setRight(TreeNodeOne right) {
            this.right = right;
        }

        public TreeNodeOne getParent() {
            return parent;
        }

        public void setParent(TreeNodeOne parent) {
            this.parent = parent;
        }
    }
}
