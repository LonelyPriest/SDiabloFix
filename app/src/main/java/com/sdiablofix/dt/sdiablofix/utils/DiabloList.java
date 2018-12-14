package com.sdiablofix.dt.sdiablofix.utils;

import android.util.Log;

/**
 * Created by buxianhui on 2018/12/7.
 */

public class DiabloList <T>{
    private DiabloNode head;
    private Integer size;

    public DiabloList() {
        head = null;
        size = 0;
    }

    public Integer getSize() {
        return size;
    }

    public void print() {
        DiabloNode n = head;
        while (null != n) {
            Log.d("diabloList", n.item.toString());
            n = n.next;
        }
    }

    public void addHead(T item) {
        if (null == head) {
            head = new DiabloNode(item);
        } else {
            DiabloNode e = new DiabloNode(item);
            e.next = head;
            head = e;
        }
        size++;
    }

    public T get(int position) {
        int at = 0;
        DiabloNode e = head;
        while (at != position) {
            at++;
            e = e.next;
        }

        return e.item;
    }

    public T reverseTail() {
        DiabloNode pre = head;
        DiabloNode cur = head;
        while (null != cur.next) {
            pre = cur;
            cur = cur.next;
        }

        pre.next = null;
        cur.next = head;
        head = cur;

        return cur.item;
    }

    private class DiabloNode {
        public T item;
        private DiabloNode next;

        private DiabloNode(T item) {
            this.item = item;
            next = null;
        }
    }
}
