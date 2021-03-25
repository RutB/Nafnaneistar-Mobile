package xyz.nafnaneistar.controller;

import java.util.ArrayList;

import xyz.nafnaneistar.activities.items.ComboListItem;
import xyz.nafnaneistar.model.User;

public interface VolleyCallBack<T> {
    ArrayList<ComboListItem> onSuccess();
    void onResponse(T response);
    void onError(String error);
}
