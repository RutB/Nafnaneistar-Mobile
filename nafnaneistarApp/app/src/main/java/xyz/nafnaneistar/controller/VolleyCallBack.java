package xyz.nafnaneistar.controller;

import java.util.ArrayList;

import xyz.nafnaneistar.activities.items.NameCardItem;
import xyz.nafnaneistar.activities.items.UserItem;

public interface VolleyCallBack<T> {
    ArrayList<NameCardItem> onSuccess();
    void onResponse(T response);
    void onError(String error);

   // void onResponse(ArrayList<NameCardItem> list);
  //  void onUserResponse(ArrayList<UserItem> list);
}
