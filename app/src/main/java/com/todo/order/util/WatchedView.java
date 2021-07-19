package com.todo.order.util;

import android.view.View;

public class WatchedView {

    static class Listener {
        void onVisibilityChanged(int visibility) {

        }
    }

    private View v;
    private Listener listener;

    WatchedView(View v) {
        this.v = v;
    }

    void setListener(Listener l) {
        this.listener = l;
    }

    public void setVisibility(int visibility) {
        v.setVisibility(visibility);
        if(listener != null) {
            listener.onVisibilityChanged(visibility);
        }
    }
}
