package com.example.drhello.ui.main;

import android.view.ViewGroup;

public abstract class DrawerItem<T extends DrawerAdapter.ViewHolder> {
    protected  boolean isCheckedV;

    public  abstract T createViewHolder(ViewGroup parent);
    public abstract void bindViewHolder(T holder);

    public DrawerItem<T>setChecked(boolean isCheckedV){
        this.isCheckedV=isCheckedV;
        return this;
    }

    public boolean isChecked() {
        return isCheckedV;
    }
    public boolean isSelectable(){
        return true;
    }
}
