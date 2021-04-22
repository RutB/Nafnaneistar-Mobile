package is.hi.hbv501g.nafnaneistar.nafnaneistar.Entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * User class - Each User has his own list of id that contain names they have
 * yet to see, ids of names that they have approved, and names of partners they
 * have linked to
 */
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotEmpty
    private String name;

    @Email
    @NotEmpty
    @Column(unique = true)
    private String email;
    @NotEmpty
    private String password;
    @Lob
    private ArrayList<Integer> availableNames;
    // aprovedNames is kept as a key,value pair to contain the Id of the namecard
    // and the 1-5 rating of the namecard
    @Lob
    private HashMap<Integer, Integer> approvedNames;
    private ArrayList<Long> linkedPartners;

    /**
     * User Constructor without parameters to initialize a user via reflections for
     * the persistance framework, but we initalize the approvedNames and the
     * linkedPartners variables so they are not undefined or null. if using
     * reflections the created User must be given an List of availableNames after
     * initialization or else the User will throw error.
     */
    @Lob
    private HashMap<String,String> notifications;
    public User() {

        this.approvedNames = new HashMap<Integer, Integer>();
        this.linkedPartners = new ArrayList<Long>();
    }

    /**
     * User Constructor with parameters. Initializes User with the given parameters
     * and initializes the approvedNames and linkedPartner List
     * 
     * @param name
     * @param email
     * @param password
     * @param availableNames
     */
    public User(String name, String email, String password, ArrayList<Integer> availableNames) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.availableNames = availableNames;
        this.approvedNames = new HashMap<Integer, Integer>();
        this.linkedPartners = new ArrayList<Long>();
        this.notifications = new HashMap<String,String>();
    }

    // Custom Functions and Metods

    /**
     * Function to approve a name. if the id is not a negative number an Id is
     * removed from the available names list and added to the approved names, with
     * the default rating of 0. an updated list of availableNams is returned.
     * 
     * @param id id of the Name chosen
     * @return List that has the selected name removed
     */
    public ArrayList<Integer> approveName(Integer id) {
        int index = this.availableNames.indexOf(id);
        if (index < 0)
            return this.availableNames;
        this.approvedNames.put(this.availableNames.remove(index), 0);
        return this.availableNames;
    }

    /**
     * Disapproves name by removing it from the availablenames list and returns an
     * updated list of available names
     * 
     * @param id id of the name that was disliked
     * @return an updated list
     */
    public ArrayList<Integer> disapproveName(Integer id) {
        int index = this.availableNames.indexOf(id);
        if (index < 0)
            return this.availableNames;
        this.availableNames.remove(index);
        return this.availableNames;
    }
    

    /**
     * Removes the id from the approvedNames
     * 
     * @param id - id of the name to remove
     */
    public void removeApprovedName(Integer id) {
        this.approvedNames.remove(id);
    }

    /**
     * Adds another User with the given id to the linkedpartner List
     * 
     * @param id of the User to link
     */
    public void addLinkedPartner(Long id) {
        this.linkedPartners.add(id);
    }

    /**
     * Removes the Id from the linked partner list
     * 
     * @param id
     * @return if the removing was a success or not
     */
    public boolean removeLinkedPartner(Long id) {
        return this.linkedPartners.remove(id);
    }

    /**
     * Maybe this function will be removed, returns size of availableNames list
     * 
     * @return size of the availableNamesList
     */
    public int getAvailableNamesSize() {
        return this.availableNames.size();
    }

    public int getApprovedNamesSize() {
        return this.approvedNames.keySet().size();
    }

    /**
     * Gets a random Id from the availableNames
     * 
     * @return an Id that is from the availableNames list
     */
    public Integer getRandomNameId() {
        Random r = new Random();
        int size = this.availableNames.size();
        if (size == 0)
            return -1;
        Integer newID = this.availableNames.get(r.nextInt(size));
        return newID;
    }

    /**
     * Gets a random Id from the ids in the given list
     * 
     * @return an Id that is from the given list
     */
    public Integer getRandomNameId(ArrayList<Integer> genderList) {
        Random r = new Random();
        int size = genderList.size();
        if (size == 0)
            return -1;
        Integer newID = genderList.get(r.nextInt(size));
        return newID;
    }

    /**
     * Updates the rating of the id that represents the Name
     * 
     * @param id     id of the name
     * @param rating rating to associate with the name
     */
    public void updateRatingById(Integer id, Integer rating) {
        this.approvedNames.put(id, rating);
    }

    /*
     * Getters and Setters
     */

    public long getId() {
        return this.id;
    }

    public String getName() {
        return StringUtils.capitalize(this.name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Integer> getAvailableNames() {
        return this.availableNames;
    }

    public void setAvailableNames(ArrayList<Integer> ids) {
        this.availableNames = ids;
    }

    public HashMap<Integer, Integer> getApprovedNames() {
        return this.approvedNames;
    }

    public ArrayList<Long> getLinkedPartners() {
        return this.linkedPartners;
    }

    public String getPassword() {
        return password;
    }

    public void setApprovedNames(HashMap<Integer, Integer> names) {
        this.approvedNames = names;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toJsonString() {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            jsonString = "{}";
        }
        Gson g = new Gson();
        JsonObject filteredUser =  g.fromJson(jsonString, JsonObject.class);
        filteredUser.remove("password");
        return filteredUser.toString();

        
    }
	@Override
	public String toString() {
        return "User: " + this.name + " listleft: " + this.getAvailableNamesSize();
    }
    public void addNotification(String id, String message){
        if(this.notifications != null)
            this.notifications.put(id,message);

    }
    public void removeNotification(){
        this.notifications = new HashMap<String,String>();
    }
    public HashMap<String,String> getNotifications(){
        return this.notifications;
    }
}
