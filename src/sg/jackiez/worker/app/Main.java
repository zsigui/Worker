package sg.jackiez.worker.app;

import java.util.LinkedList;

import sg.jackiez.worker.module.ok.Robot;
import sg.jackiez.worker.module.ok.manager.DBManager;
import sg.jackiez.worker.utils.SLogUtil;

public class Main {

    public static void main(String[] args) {
        new Robot().start();
//        SLogUtil.setPrintFile(false);
//        SLogUtil.setDebugLevel(SLogUtil.Level.INFO);
//        PrecursorManager.get().init(OKTypeConfig.SYMBOL_EOS, OKTypeConfig.CONTRACT_TYPE_QUARTER);
//        FutureDataGrabber grabber = new FutureDataGrabber(PrecursorManager.get().getInstrumentId());
//        grabber.startAll();
        DBManager.get().startGrab();
//        Tree root = new Tree();
//        root.key = 11;
//        root.left = new Tree();
//        root.right = new Tree();
//        root.left.key = 133;
//        root.right.key = 135;
//        root.left.left = new Tree();
//        root.left.left.key = 17;
//        root.left.right = new Tree();
//        root.left.right.key = 12;
//        root.left.right.left = new Tree();
//        root.left.right.left.key = 34;
//        root.left.right.right = new Tree();
//        root.left.right.right.key = 37;
//        new Main().depthTravesal2(root);
    }


    public int reorderNumber(int num) {
        if (num > -10 && num < 10) return num;

        int count = num;

        int result = 0;
        while (count != 0) {
            result = result * 10 + count % 10;
            count /= 10;
        }
        return result;
    }

    public int binarySearch(int[] data, int k) {
        if (data == null || data.length == 0) {
            return -1;
        }
        int index = -1;
        int high = data.length - 1, low = 0, mid;
        while (low <= high) {
            mid = low + (high - low) / 2;
            if (k > data[mid]) {
                low = mid + 1;
            } else if (k < data[mid]) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return index;
    }

    static class Tree {
        int key;
        Tree left;
        Tree right;
    }

    public void depthTravesal(Tree t) {
        if (t == null) {
            return;
        }

        SLogUtil.v(t.key);
        depthTravesal(t.left);
        depthTravesal(t.right);
    }

    public void depthTravesal2(Tree t) {
        if (t == null) {
            return;
        }

        LinkedList<Tree> treeList = new LinkedList<>();
        treeList.addFirst(t);
        while (!treeList.isEmpty()) {
            t = treeList.removeFirst();
            SLogUtil.v(t.key);
            if (t.right != null) {
                treeList.addFirst(t.right);
            }
            if (t.left != null) {
                treeList.addFirst(t.left);
            }
        }
    }
}
