package is.hi.hbv501g.nafnaneistar.nafnaneistar.Controllers;

import java.util.HashMap;

import javax.servlet.http.HttpSession;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import is.hi.hbv501g.nafnaneistar.nafnaneistar.Entities.NameCard;
import is.hi.hbv501g.nafnaneistar.nafnaneistar.Entities.User;
import is.hi.hbv501g.nafnaneistar.nafnaneistar.Services.NameService;
import is.hi.hbv501g.nafnaneistar.nafnaneistar.Services.UserService;
import is.hi.hbv501g.nafnaneistar.utils.UserUtils;

/**
 * UserRestController contains methods and functions to process fetch calls from
 * the viewing template
 */
@RestController
public class UserRestController {

    NameService nameService;
    UserService userService;

    @Autowired
    public UserRestController(NameService nameService, UserService userService) {
        this.nameService = nameService;
        this.userService = userService;
    }

    /**
     * function that validates the information from the User to login, if the User
     * with the given email matches the given password the user is logged in
     * 
     * @param email    - Email of User
     * @param password - Password of the User
     * @param session  - Session to keep information
     * @return true if the credentials were right, false otherwise
     */
    @GetMapping(path = "/login/check", produces = "application/json")
    public String checkLogin(@RequestParam String email, @RequestParam String password) 
    {   
        User user  = userService.findByEmailAndPassword(email, password);
        if(user != null){
            return user.toJsonString();
        }
        return "{}";
    }

    /**
     * A fetch call to process if the entered email is in use or not before a User 
     * tries to use it to signup.
     * @param email - the desired email to signup with
     * @return true or false depending on if the email is in use or not
     */
    @GetMapping(path="/signup/checkemail", produces = "application/json")
    public boolean validateEmail(@RequestParam String email) 
    {   User user = userService.findByEmail(email);
        if(user != null)
            return false;
        return true;
    }
    
    /**
     * A fetch call to process if the entered email has an established user to link to.
     * @param email - the desired email to signup with
     * @return true or false depending on if the email is valid or not
     */
    @GetMapping(path="/linkpartner/checkemail", produces = "application/json")
    public boolean validateEmailPartner(@RequestParam String email, HttpSession session) 
    {   User user = userService.findByEmail(email);
        User curr = (User) session.getAttribute("currentUser");
        if (curr.getId() != user.getId()){
            if(user != null){
                boolean boo = false;   
                for(Long id : curr.getLinkedPartners()){
                    System.out.print(boo);
                    if(user.getId() == id){
                        boo = true;
                    }
                }
                if(!boo)
                    return true;
            }
        }
        return false;
    }

    /**
     * Processes if the User wants to remove a partner from linked partners, and removes the partner from the 
     * linked partners
     * @param id
     * @param session
     * @return
     */
    @GetMapping(path="/linkpartner/remove", produces = "application/json")
    public boolean removeFromLink(@RequestParam String id, HttpSession session) 
    {  User user = (User) session.getAttribute("currentUser");
        try {
            user.removeLinkedPartner(Long.parseLong(id));
            userService.save(user);
            return true;
        } catch(Error e){
            return false;
        }
    }
    
    /**
     * A fetch call to update the rating for a users approvedName
     * @param id id of the name to update
     * @param rating a rating of 1-5 
     * @param session manages the session of the user
     * @return true or false depending if the operation was a success
     */
    @GetMapping(path="/viewliked/updaterating", produces = "application/json")
    public boolean updateNameRating(@RequestParam String id,@RequestParam String rating, HttpSession session) 
    {   User currentUser = (User) session.getAttribute("currentUser");
        if(!UserUtils.isLoggedIn(currentUser)) return false;
        Integer nameId = Integer.parseInt(id);
        Integer nameRating = Integer.parseInt(rating);
        try {
            currentUser.updateRatingById(nameId, nameRating);
            userService.save(currentUser);
            return true;
        }catch(Error e){
            return false;
        }       
    }

    /**
     * Processes if the User wants to remove name from approved Names, and removes the name from the 
     * approved names
     * @param id
     * @param session
     * @return
     */
    @GetMapping(path="/viewliked/remove", produces = "application/json")
    public boolean removeFromApproved(@RequestParam String id, HttpSession session) 
    {  User user = (User) session.getAttribute("currentUser");
        try {
            user.removeApprovedName(Integer.parseInt(id));
            userService.save(user);
            return true;
        } catch(Error e){
            return false;
        }
    }

    /**
     * A fetch call to get the list of names that match the given rank specified in the id of the element
     * @param id - id of the element that represents the rank of the name
     * @param session - to get from current user
     * @return - Returns a list of approved names matching the selected rank
     */
    @GetMapping(path="/viewliked/getrankedList", produces = "application/json")
    public HashMap<String,Integer> getrankedList(@RequestParam String id, HttpSession session) 
    {   HashMap<String,Integer> ncs = new HashMap<>();
        User currentUser = (User) session.getAttribute("currentUser"); 
        Integer rank = Integer.parseInt(id);

        currentUser.getApprovedNames().forEach((key,value) -> {
            if(value.equals(rank)) {
                NameCard nc = nameService.findById(key).orElse(null);
                ncs.put(nc.getName()+"-"+nc.getId()+"-"+nc.getGender(),value); 
            }
        });
        return ncs;

    }




}
