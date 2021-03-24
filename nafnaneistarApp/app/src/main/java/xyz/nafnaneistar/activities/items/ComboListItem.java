package xyz.nafnaneistar.activities.items;

public class ComboListItem {
    public final int id;
    public final String name;
    public final int rating;
    public final int gender;

    public ComboListItem(int id,String name, int rating, int gender) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.gender = gender;
    }
    public int getId(){
        return id;
    }
    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }


    public int getGender() {
        return gender;
    }

    @Override
    public String toString() {
        return name;
    }
}

