package xyz.nafnaneistar.controller;

import java.util.ArrayList;

import xyz.nafnaneistar.activities.items.NameCardItem;

public interface VolleyCallBack<T> {
    ArrayList<NameCardItem> onSuccess();
    void onResponse(T response);
    void onError(String error);
}
