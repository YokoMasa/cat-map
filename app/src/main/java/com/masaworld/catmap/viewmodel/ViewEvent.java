package com.masaworld.catmap.viewmodel;

public class ViewEvent<T> {

    private T payload;
    private boolean handled;

    public boolean isHandled() {
        return handled;
    }

    public void handled() {
        handled = true;
    }

    public T getPayload() {
        return payload;
    }

    public ViewEvent(T payload) {
        this.payload = payload;
    }

}
