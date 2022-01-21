import java.util.List;

/**
 * @author wangqingwei
 * Created on 2021-05-31
 */
public class Test {

    public static void main(String[] args) {
//        Node head = new Node(1, 0, true, Lists.newArrayList());
//        List<Node> next = Lists.newArrayList();
//        next.add(new Node(1, 0, false, null));
//        next.add(new Node(2, 0, false, null));
//        head.setNext(next);
//        process(head);
        String heap1 = new String("ab");
        System.out.println(heap1);
        String normal = "ab";
        System.out.println(normal);
        System.out.println(heap1 == normal);
        System.out.println(heap1.intern() == normal);
    }


    public static void process(Node head) {
        if (head == null) {
            return;
        }

        List<Node> childNodeList = head.getNext();   // 子节点
        if (childNodeList == null || childNodeList.size() == 0) {
            return;
        }

        for (Node curNode : childNodeList) {
            process(curNode);
        }
    }


    public static class Node {
        public int inherit;   // 继承
        public int finalInt;   // 终止
        public boolean defined;   // 定义
        public List<Node> next; // 子树

        public Node() {
        }

        public Node(int inherit, int finalInt, boolean defined, List<Node> next) {
            this.inherit = inherit;
            this.finalInt = finalInt;
            this.defined = defined;
            this.next = next;
        }

        public int getInherit() {
            return inherit;
        }

        public void setInherit(int inherit) {
            this.inherit = inherit;
        }

        public int getFinalInt() {
            return finalInt;
        }

        public void setFinalInt(int finalInt) {
            this.finalInt = finalInt;
        }

        public boolean isDefined() {
            return defined;
        }

        public void setDefined(boolean defined) {
            this.defined = defined;
        }

        public List<Node> getNext() {
            return next;
        }

        public void setNext(List<Node> next) {
            this.next = next;
        }
    }

}
