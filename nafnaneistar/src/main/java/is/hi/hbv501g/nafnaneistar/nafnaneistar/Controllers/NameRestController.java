package is.hi.hbv501g.nafnaneistar.nafnaneistar.Controllers;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import is.hi.hbv501g.nafnaneistar.nafnaneistar.Entities.NameCard;
import is.hi.hbv501g.nafnaneistar.nafnaneistar.Entities.User;
import is.hi.hbv501g.nafnaneistar.nafnaneistar.Services.NameService;
import is.hi.hbv501g.nafnaneistar.nafnaneistar.Services.UserService;
import is.hi.hbv501g.nafnaneistar.utils.*;

/**
 * NameRestController contains methonds and function to process 
 * fetch calls from the viewing template
 */
@RestController
public class NameRestController {

    NameService nameService;
    UserService userService;

    /**
     * The NameRestController requires a NameService and UserService
     * @param nameService
     * @param userService
     */
    @Autowired
    public NameRestController(NameService nameService, UserService userService) {
        this.nameService = nameService;
        this.userService = userService;
    }

    /**
     * Adds the name to the approvedNames list if the user has an active session
     * and returns a new namecard from the availableList of the logged in User
     * @param id id of the name
     * @param male getparameter that implies if viewing only male names
     * @param female getparameter that implies if viewing only female names
     * @param session to get the User session
     * @return a new NameCard
     */
    @GetMapping(path="/swipe/approve/{id}", produces = "application/json")
    public Optional<NameCard> approveName(@PathVariable String id,
        @RequestParam(required = false) String male,
        @RequestParam(required = false) String female,
        HttpSession session) 
    {   
        User currentUser = (User) session.getAttribute("currentUser");
        currentUser.approveName(Integer.parseInt(id));
        userService.save(currentUser);
        int gender = 3;
        if(male != null && female == null){
            gender = 0;
        }
        if(male == null && female != null){
            gender = 1; 
        }
        return getNewNameCard(currentUser,nameService,gender);
    }
    /**
     * Disapproves the name with the id and removes it from the availableNames list
     * and then returns a new name
     * @param id id of the name
     * @param male getparameter that implies if viewing only male names
     * @param female getparameter that implies if viewing only female names
     * @param session to get the User session
     * @return a new NameCard
     */
    @GetMapping(path="/swipe/disapprove/{id}", produces = "application/json")
    public Optional<NameCard> disapproveName(@PathVariable String id,
        @RequestParam(required = false) String male,
        @RequestParam(required = false) String female,
        HttpSession session) 
    {
        User currentUser = (User) session.getAttribute("currentUser");
        currentUser.disapproveName(Integer.parseInt(id));
        userService.save(currentUser);
        int gender = 3;
        if(male != null && female == null){
            gender = 0;
        }
        if(male == null && female != null){
            gender = 1; 
        }
        return getNewNameCard(currentUser,nameService,gender);
    }
    
    /**
     * requests a new name without adding or removing from a list
     * @param male getparameter that implies if viewing only male names
     * @param female getparameter that implies if viewing only female names
     * @param session to get the User session
     * @return a new namecard
     */
    @GetMapping(path="/swipe/newname_old", produces = "application/json")
    public Optional<NameCard> getNewName_old(
        @RequestParam(required = false) String male,
        @RequestParam(required = false) String female,
        HttpSession session) 
    {
        User currentUser = (User) session.getAttribute("currentUser");
        int gender = 3;
        if(male != null && female == null){
            gender = 0;
        }
        if(male == null && female != null){
            gender = 1; 
        }
        return getNewNameCard(currentUser,nameService,gender);
            
    }
    @GetMapping(path="/swipe/newname", produces = "application/json")
    public String getNewName(
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String pass,
        @RequestParam(required = false) String male,
        @RequestParam(required = false) String female,
        HttpSession session) 
    {
        int gender = 3;
        if(male != null && female == null){
            gender = 0;
        }
        if(male == null && female != null){
            gender = 1; 
        }
        System.out.println(email);

        if(UserUtils.isAuthenticated(userService, email, pass)) {
            User user = userService.findByEmail(email);
            return getNewNameCard(user,nameService,gender).get().toJsonString();
        }

        return "{}";
            
    }

    /**
     * 
     * @param middle
     * @param gender
     * @param session to get the User session
     * @return
     */
    @GetMapping(path="/viewliked/namemaker", produces = "application/json")
    public String[] getRandomName(
        @RequestParam(required = false) String middle,
        @RequestParam(required = false) String gender,
        HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if(currentUser.getApprovedNames().size() <= 2){
            return new String[] {"Þú þarft að skoða fleiri nöfn",""};
        }
        Optional<NameCard> nc = nameService.findById(currentUser
                .getRandomNameId(UserUtils.getGenderList(currentUser.getApprovedNames().keySet(), nameService, Integer.parseInt(gender))));
        String name = nc.get().getName();
        String middlename = "";
        if(middle != null) {
            nc = nameService.findById(currentUser
                .getRandomNameId(UserUtils.getGenderList(currentUser.getApprovedNames().keySet(), nameService, Integer.parseInt(gender))));
                middlename += " " + nc.get().getName();
        }
        return new String[] {name,middlename};
            
    }

    /**
     * function to get the updated list size of female and male names from the availablenames list
     * @param session - to manage who the user requesting is
     * @return information regarding the remaining male names and remaining female names
     */
    @GetMapping(path="/swipe/getlistSize", produces = "application/json")
    public Integer[] getRemainingSize(HttpSession session)  {
        User currentUser = (User) session.getAttribute("currentUser");
        int mSize = UserUtils.getGenderList(currentUser,nameService,0).size();
        int fSize = UserUtils.getGenderList(currentUser,nameService,1).size();
        Integer[] size = new Integer[] {mSize,fSize};
        return size;
        
    }

    /**
     * Gets the joint list of approved names from the current user and the selected id of the partner
     * returns information containing the name, id and gender and the avarage grade of combined rating
     * @param session - to get the current user session
     * @param partnerid - the id of the linked partner
     * @return a list of information of the joint names and avarage rating
     */
    @GetMapping(path="/viewliked/combolist", produces ="application/json")
    public HashMap<String,Integer> getComboList(HttpSession session, @RequestParam String partnerid){
        Long pID = Long.parseLong(partnerid);
        User partner = userService.findById(pID).orElse(null);
        User currentUser = (User) session.getAttribute("currentUser");
        if(partner == null || currentUser == null) return null;
        HashMap<String,Integer> ncs = new HashMap<>();
        Set<Integer> pids = partner.getApprovedNames().keySet();
        Set<Integer> ids = currentUser.getApprovedNames().keySet();
        for(Integer id : ids){
            if(pids.contains(id)){
                NameCard nc = nameService.findById(id).orElse(null);
                int avg = (currentUser.getApprovedNames().get(id) + partner.getApprovedNames().get(id));
                avg = (avg == 0) ? avg : avg/2;
                ncs.put(nc.getName()+"-"+nc.getId()+"-"+nc.getGender(),avg); 
            }
        }
        return ncs;      
    }

    /**
     * an Helper function to reduce redundancy, gets a new namecard based on gender or if both gender are
     * selected
     * @param user the active user
     * @param nameService the current nameService
     * @param gender male, female or both
     * @return returns a new NameCard from the id of the randomly selected name
     */
    private Optional<NameCard> getNewNameCard(User user, NameService nameService, int gender){
        if(gender == 3){
            Integer newID = user.getRandomNameId();      
            return nameService.findById(newID); 
        }
        Integer newID = user.getRandomNameId(UserUtils.getGenderList(user,nameService,gender));
        return nameService.findById(newID);
    }
    
    /**
     * Adds a name from search results into the current users liked list.
     * @param id String of the id of the name to be added to the liked list.
     * @param session The users current Http session
     * @return Boolean, returns true if action is a success, othe.rwise it returns false. 
     */
    @GetMapping(path="/searchname/addtoliked/{id}", produces = "application/json")
    public boolean approveSearchedName(@PathVariable String id, HttpSession session){
        User currentUser = (User) session.getAttribute("currentUser");
        if(!UserUtils.isLoggedIn(currentUser))
            return false;
        try{
            currentUser.approveName(Integer.parseInt(id));
            userService.save(currentUser);
            return true;
        }
        catch(Error e){
            return false;
        }
    
    }

    /**
     * Removes a search result name from the current users liked list, if it is currently on the list. 
     * @param id String of the id of the name to be removed from the liked list.
     * @param session The users current Http session.
     * @return Boolean, returns true if action is a success, otherwise it returns false.
     */
    @GetMapping(path="/searchname/removefromliked/{id}", produces = "application/json")
    public boolean removeSearchedName(@PathVariable String id, HttpSession session){
        User currentUser = (User) session.getAttribute("currentUser");
        if(!UserUtils.isLoggedIn(currentUser))
        return false;
        try{
            currentUser.removeApprovedName(Integer.parseInt(id));
            userService.save(currentUser);
            return true;
        }
        catch(Error e){
            return false;
        }
    }
}
