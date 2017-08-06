package com.example.findmybus;

/**
 * Created by Jeet on 29-Apr-17.
 */

public class CenterItem extends AbstractItem {
    public CenterItem(String label) {
        super(label);
    }

    @Override
    public int getType() {
        return TYPE_CENTER;
    }
}
