package cs455.scaling.util;

import java.util.LinkedList;

public class HashList {

    private LinkedList<String> list;

    public HashList() {
        this.list = new LinkedList<>();
    }

    public void add(String item) {
        synchronized (this.list) {
            System.out.println("Added: " + item);
            this.list.add(item);
            this.list.notify();
        }
    }

    public String removeIfPresent(String item) {
        String itemRemoved = null;
        synchronized (this.list) {
            if (list.contains(item)) {
                itemRemoved = list.get(list.indexOf(item));
                list.remove(item);
            }
        }
        return itemRemoved;
    }
}
