package com.example.findmybus;

/**
 * Created by Jeet on 29-Apr-17.
 */

public class EmptyItem extends AbstractItem {
    public EmptyItem(String label) {
        super(label);
    }

    @Override
    public int getType() {
        return TYPE_EMPTY;
    }
}
