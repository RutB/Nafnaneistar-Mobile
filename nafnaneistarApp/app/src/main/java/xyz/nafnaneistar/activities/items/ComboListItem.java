package xyz.nafnaneistar.activities.items;

public class ComboListItem {
    public final String name;
    public final int rating;
    public final String operations;
    public final int gender;

    public ComboListItem(String name, int rating, String operations, int gender) {
        this.name = name;
        this.rating = rating;
        this.operations = operations;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }

    public String getOperations() {
        return operations;
    }

    public int getGender() {
        return gender;
    }

    @Override
    public String toString() {
        return name;
    }
}

