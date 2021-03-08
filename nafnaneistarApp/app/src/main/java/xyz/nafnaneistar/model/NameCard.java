package xyz.nafnaneistar.model;
/**
 * Namecard contains the Id, Name, Description and Gender information for each Name
 */

public class NameCard {

    private Integer id;
    private String name;
    private String description;
    private Integer gender;

    /**
     * Empty Constructor to create a new instance via reflection
     */
    public NameCard(){
    }

    /**
     * Constructor for namecard, creates a Namecard with given parameters
     * @param id id of the namecard
     * @param name name associated with the namecard
     * @param description description associated with the name
     * @param gender true means female, false means male
     */
    public NameCard(Integer id, String name, String description,int gender) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.gender = gender;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * modified getter, it suited us better to have the gender represented in 1 or 0
     * @return returns 1 if true (female) and 0 if false (male)
     */
    public int getGender() {
        return  this.gender;
    }

}
