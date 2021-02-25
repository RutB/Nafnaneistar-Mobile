package xyz.nafnaneistar.model;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
    private long id;
    private String name;
    private String email;
    private String password;
    private ArrayList<Integer> availableNames;
    private HashMap<Integer, Integer> approvedNames;
    private ArrayList<Long> linkedPartners;

    public User(){};

    public User(long id, String name, String email, String password, ArrayList<Integer> availableNames, HashMap<Integer, Integer> approvedNames, ArrayList<Long> linkedPartners) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.availableNames = availableNames;
        this.approvedNames = approvedNames;
        this.linkedPartners = linkedPartners;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Integer> getAvailableNames() {
        return availableNames;
    }

    public void setAvailableNames(ArrayList<Integer> availableNames) {
        this.availableNames = availableNames;
    }

    public HashMap<Integer, Integer> getApprovedNames() {
        return approvedNames;
    }

    public void setApprovedNames(HashMap<Integer, Integer> approvedNames) {
        this.approvedNames = approvedNames;
    }

    public ArrayList<Long> getLinkedPartners() {
        return linkedPartners;
    }

    public void setLinkedPartners(ArrayList<Long> linkedPartners) {
        this.linkedPartners = linkedPartners;
    }
}
