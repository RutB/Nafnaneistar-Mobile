package xyz.nafnaneistar.helpers;

import java.util.ArrayList;

import xyz.nafnaneistar.model.NameCard;

public class Utils {

    public static Boolean nameCardListContains(ArrayList<NameCard> list, NameCard item) {
        for (int i = 0; i < list.size(); i++ ) {
            if (list.get(i).getId().equals(item.getId())) return true;
        }
        return false;
    }

}
