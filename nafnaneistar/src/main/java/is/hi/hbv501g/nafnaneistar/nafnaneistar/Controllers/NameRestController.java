package is.hi.hbv501g.nafnaneistar.nafnaneistar.Controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
import net.minidev.json.JSONObject;

/**
 * NameRestController contains methonds and function to process fetch calls from
 * the viewing template
 */
@RestController
public class NameRestController {

    NameService nameService;
    UserService userService;

    /**
     * The NameRestController requires a NameService and UserService
     * 
     * @param nameService
     * @param userService
     */
    @Autowired
    public NameRestController(NameService nameService, UserService userService) {
        this.nameService = nameService;
        this.userService = userService;
    }

    /**
     * Adds the name to the approvedNames list if the user has an active session and
     * returns a new namecard from the availableList of the logged in User
     * 
     * @param id      id of the name
     * @param male    getparameter that implies if viewing only male names
     * @param female  getparameter that implies if viewing only female names
     * @param session to get the User session
     * @return a new NameCard
     */
    @GetMapping(path = "/swipe/approve/{id}", produces = "application/json")
    public Optional<NameCard> approveName__old(@PathVariable String id, @RequestParam(required = false) String male,
            @RequestParam(required = false) String female, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        currentUser.approveName(Integer.parseInt(id));
        userService.save(currentUser);
        int gender = 3;
        if (male != null && female == null) {
            gender = 0;
        }
        if (male == null && female != null) {
            gender = 1;
        }
        return getNewNameCard(currentUser, nameService, gender);
    }

    /**
     * Adds the name to the approvedNames list if the user has an active session and
     * returns a new namecard from the availableList of the logged in User
     * 
     * @param id      id of the name
     * @param male    getparameter that implies if viewing only male names
     * @param female  getparameter that implies if viewing only female names
     * @param session to get the User session
     * @return a new NameCard
     */
    @GetMapping(path = "/swipe/approve", produces = "application/json")
    public String approveName(@RequestParam(required = true) String id, @RequestParam(required = true) String email,
            @RequestParam(required = true) String pass, @RequestParam(required = false) String male,
            @RequestParam(required = false) String female) {
        System.out.println("approve");
        int gender = 3;
        if (male != null && female == null) {
            gender = 0;
        }
        if (male == null && female != null) {
            gender = 1;
        }
        if (UserUtils.isAuthenticated(userService, email, pass)) {
            User user = userService.findByEmail(email);
            user.approveName(Integer.parseInt(id));
            userService.save(user);
            NameCard nc = getNewNameCard(user, nameService, gender).get();

            return nc.toJsonString();
        }
        return "{}";
    }

    /**
     * Disapproves the name with the id and removes it from the availableNames list
     * and then returns a new name
     * 
     * @param id      id of the name
     * @param male    getparameter that implies if viewing only male names
     * @param female  getparameter that implies if viewing only female names
     * @param session to get the User session
     * @return a new NameCard
     */
    @GetMapping(path = "/swipe/disapprove", produces = "application/json")
    public String disapproveName(@RequestParam(required = false) String id,
            @RequestParam(required = true) String email, @RequestParam(required = true) String pass,
            @RequestParam(required = false) String male, @RequestParam(required = false) String female) {
        System.out.println("disapprove");
        int gender = 3;
        if (male != null && female == null) {
            gender = 0;
        }
        if (male == null && female != null) {
            gender = 1;
        }
        if (UserUtils.isAuthenticated(userService, email, pass)) {
            User user = userService.findByEmail(email);
            user.disapproveName(Integer.parseInt(id));
            userService.save(user);
            NameCard nc = getNewNameCard(user, nameService, gender).get();

            return nc.toJsonString();
        }
        return "{}";
    }

    @GetMapping(value = "/viewliked", produces = "application/json")
    public String viewLiked(@RequestParam(required = false) String email, @RequestParam(required = false) String pass) {
        System.out.println(email);
        if (UserUtils.isAuthenticated(userService, email, pass)) {
            User currentUser = userService.findByEmail(email);
            int fnames = UserUtils.getGenderList(currentUser.getApprovedNames().keySet(), nameService, 1).size();
            int mnames = UserUtils.getGenderList(currentUser.getApprovedNames().keySet(), nameService, 0).size();

            int totalfnames = nameService.countByGender(true);
            int totalmnames = nameService.countByGender(false);

            int totalmnamesleft = UserUtils.getGenderList(currentUser, nameService, 0).size();
            int totalfnamesleft = UserUtils.getGenderList(currentUser, nameService, 1).size();

            int femaledisliked = Math.abs(totalfnames - (totalfnamesleft) - fnames);
            int maledisliked = Math.abs(totalmnames - (totalmnamesleft) - mnames);

            Integer[] femalestats = new Integer[] { fnames, femaledisliked, totalfnamesleft };
            Integer[] malestats = new Integer[] { mnames, maledisliked, totalmnamesleft };

            String meaning = nameService.findDescriptionByName(currentUser.getName().split(" ")[0]);
            ArrayList<User> partners = new ArrayList<User>();
            for (Long id : currentUser.getLinkedPartners()) {
                User partner = userService.findById(id).get();
                if (!partners.contains(partner))
                    partners.add(partner);
            }

            HashMap<String, Integer> ncs = new HashMap<>();
            currentUser.getApprovedNames()
                    .forEach((key, value) -> {
                        String nc = nameService.findById(key).get().toJsonString();
                        ncs.put(nc, value);
                    });
            Gson gson = new Gson();
            JsonArray malestatsJSON = gson.toJsonTree(malestats).getAsJsonArray();
            JsonArray femalestatsJSON = gson.toJsonTree(femalestats).getAsJsonArray();
            JsonArray partnersJSON = gson.toJsonTree(partners).getAsJsonArray();


            JsonObject userInfo = new JsonObject();

            userInfo.addProperty("name", currentUser.getName());
            userInfo.addProperty("meaning", meaning);
            userInfo.add("malestats", malestatsJSON);
            userInfo.add("femalestats", femalestatsJSON);
            userInfo.add("partners", partnersJSON);
            userInfo.addProperty("approvedNames", gson.toJson(ncs));

            return userInfo.toString();
        }
        return "{'message':'Villa í auðkenningu'}";
    }

    /**
     * Disapproves the name with the id and removes it from the availableNames list
     * and then returns a new name
     * 
     * @param id      id of the name
     * @param male    getparameter that implies if viewing only male names
     * @param female  getparameter that implies if viewing only female names
     * @param session to get the User session
     * @return a new NameCard
     */
    @GetMapping(path = "/swipe/disapprove/{id}", produces = "application/json")
    public Optional<NameCard> disapproveName__old(@PathVariable String id, @RequestParam(required = false) String male,
            @RequestParam(required = false) String female, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        currentUser.disapproveName(Integer.parseInt(id));
        userService.save(currentUser);
        int gender = 3;
        if (male != null && female == null) {
            gender = 0;
        }
        if (male == null && female != null) {
            gender = 1;
        }
        return getNewNameCard(currentUser, nameService, gender);
    }

    /**
     * requests a new name without adding or removing from a list
     * 
     * @param male    getparameter that implies if viewing only male names
     * @param female  getparameter that implies if viewing only female names
     * @param session to get the User session
     * @return a new namecard
     */
    @GetMapping(path = "/swipe/newname_old", produces = "application/json")
    public Optional<NameCard> getNewName_old(@RequestParam(required = false) String male,
            @RequestParam(required = false) String female, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        int gender = 3;
        if (male != null && female == null) {
            gender = 0;
        }
        if (male == null && female != null) {
            gender = 1;
        }
        return getNewNameCard(currentUser, nameService, gender);

    }

    @GetMapping(path = "/swipe/newname", produces = "application/json")
    public String getNewName(@RequestParam(required = false) String email, @RequestParam(required = false) String pass,
            @RequestParam(required = false) String male, @RequestParam(required = false) String female) {
        int gender = 3;
        if (male != null && female == null) {
            gender = 0;
        }
        if (male == null && female != null) {
            gender = 1;
        }
        if (UserUtils.isAuthenticated(userService, email, pass)) {
            User user = userService.findByEmail(email);
            NameCard nc = getNewNameCard(user, nameService, gender).get();
            return nc.toJsonString();
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
    @GetMapping(path = "/viewliked/namemaker_OLD", produces = "application/json")
    public String[] getRandomNameOLD(@RequestParam(required = false) String middle,
            @RequestParam(required = false) String gender, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser.getApprovedNames().size() <= 2) {
            return new String[] { "Þú þarft að skoða fleiri nöfn", "" };
        }
        Optional<NameCard> nc = nameService.findById(currentUser.getRandomNameId(UserUtils
                .getGenderList(currentUser.getApprovedNames().keySet(), nameService, Integer.parseInt(gender))));
        String name = nc.get().getName();
        String middlename = "";
        if (middle != null) {
            nc = nameService.findById(currentUser.getRandomNameId(UserUtils
                    .getGenderList(currentUser.getApprovedNames().keySet(), nameService, Integer.parseInt(gender))));
            middlename += " " + nc.get().getName();
        }
        return new String[] { name, middlename };

    }

    @GetMapping(path = "/viewliked/namemaker", produces = "application/json")
    public String getRandomName(
        @RequestParam(required = true) String email, @RequestParam(required = true) String pass,
        @RequestParam(required = true) String gender, @RequestParam(required = false) String middle) {
        if (UserUtils.isAuthenticated(userService, email, pass)) {
            User currentUser = userService.findByEmail(email);
            if (currentUser.getApprovedNames().size() <= 2) {
                return "{'message':'Þú þarft að skoða fleiri nöfn'}";
            }
            Optional<NameCard> nc = nameService.findById(currentUser.getRandomNameId(UserUtils
                    .getGenderList(currentUser.getApprovedNames().keySet(), nameService, Integer.parseInt(gender))));
            String name = nc.get().getName();
            String middlename = "";
            if (middle != null) {
                nc = nameService.findById(currentUser.getRandomNameId(UserUtils
                        .getGenderList(currentUser.getApprovedNames().keySet(), nameService, Integer.parseInt(gender))));
                middlename += " " + nc.get().getName();
            }
            return "{'name':'"+name+"','middle':'"+middlename+"'}";
        }
        return "{'message':'Villa í auðkenningu'}";

    }

    /**
     * function to get the updated list size of female and male names from the
     * availablenames list
     * 
     * @param session - to manage who the user requesting is
     * @return information regarding the remaining male names and remaining female
     *         names
     */
    @GetMapping(path = "/swipe/getlistSize", produces = "application/json")
    public Integer[] getRemainingSize(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        int mSize = UserUtils.getGenderList(currentUser, nameService, 0).size();
        int fSize = UserUtils.getGenderList(currentUser, nameService, 1).size();
        Integer[] size = new Integer[] { mSize, fSize };
        return size;

    }

    /**
     * Gets the joint list of approved names from the current user and the selected
     * id of the partner returns information containing the name, id and gender and
     * the avarage grade of combined rating
     * 
     * @param session   - to get the current user session
     * @param partnerid - the id of the linked partner
     * @return a list of information of the joint names and avarage rating
     */
    @GetMapping(path = "/viewliked/combolist__OLD", produces = "application/json")
    public HashMap<String, Integer> getComboList__old(HttpSession session, @RequestParam String partnerid) {
        Long pID = Long.parseLong(partnerid);
        User partner = userService.findById(pID).orElse(null);
        User currentUser = (User) session.getAttribute("currentUser");
        if (partner == null || currentUser == null)
            return null;
        HashMap<String, Integer> ncs = new HashMap<>();
        Set<Integer> pids = partner.getApprovedNames().keySet();
        Set<Integer> ids = currentUser.getApprovedNames().keySet();
        for (Integer id : ids) {
            if (pids.contains(id)) {
                NameCard nc = nameService.findById(id).orElse(null);
                int avg = (currentUser.getApprovedNames().get(id) + partner.getApprovedNames().get(id));
                avg = (avg == 0) ? avg : avg / 2;
                ncs.put(nc.getName() + "-" + nc.getId() + "-" + nc.getGender(), avg);
            }
        }
        return ncs;
    }
        /**
     * Gets the joint list of approved names from the current user and the selected
     * id of the partner returns information containing the name, id and gender and
     * the avarage grade of combined rating
     * 
     * @param session   - to get the current user session
     * @param partnerid - the id of the linked partner
     * @return a list of information of the joint names and avarage rating
     */
    @GetMapping(path = "/viewliked/combolist", produces = "application/json")
    public String getComboList(@RequestParam(required = true) String email, @RequestParam(required = true) String pass,  @RequestParam(required = true) String pid) {
        Long pID = Long.parseLong(pid);
        System.out.println(pid);
        User partner = userService.findById(pID).orElse(null);
        if (partner == null )
            return "{'message':'Notandi finnst ekki'}";
        if (UserUtils.isAuthenticated(userService, email, pass)) {
            User currentUser = userService.findByEmail(email);

      
            HashMap<String, Integer> ncs = new HashMap<>();
            Set<Integer> pids = partner.getApprovedNames().keySet();
            Set<Integer> ids = currentUser.getApprovedNames().keySet();
            JsonArray jsonarr = new JsonArray();
            
            for (Integer id : ids) {
                if (pids.contains(id)) {
                    NameCard nc = nameService.findById(id).orElse(null);
                    JsonObject namecard = new JsonObject();
                    int avg = (currentUser.getApprovedNames().get(id) + partner.getApprovedNames().get(id));
                    avg = (avg == 0) ? avg : avg / 2;
                    ncs.put(nc.getName() + "-" + nc.getId() + "-" + nc.getGender(), avg);
                    namecard.addProperty("name", nc.getName());
                    namecard.addProperty("id", nc.getId());
                    namecard.addProperty("rating", avg);
                    namecard.addProperty("gender", nc.getGender());
                    jsonarr.add(namecard);
                }
            }
            
            JsonObject userInfo = new JsonObject();
            
            //System.out.println(ncsAndrating);
            userInfo.add("namecards",jsonarr);
            return userInfo.toString();
        }
        return "{'message':'Villa í auðkenningu'}";
    }

    /**
     * an Helper function to reduce redundancy, gets a new namecard based on gender
     * or if both gender are selected
     * 
     * @param user        the active user
     * @param nameService the current nameService
     * @param gender      male, female or both
     * @return returns a new NameCard from the id of the randomly selected name
     */
    private Optional<NameCard> getNewNameCard(User user, NameService nameService, int gender) {
        if (gender == 3) {
            Integer newID = user.getRandomNameId();
            return nameService.findById(newID);
        }
        Integer newID = user.getRandomNameId(UserUtils.getGenderList(user, nameService, gender));
        return nameService.findById(newID);
    }

    /**
     * Adds a name from search results into the current users liked list.
     * 
     * @param id      String of the id of the name to be added to the liked list.
     * @param session The users current Http session
     * @return Boolean, returns true if action is a success, othe.rwise it returns
     *         false.
     */
    @GetMapping(path = "/searchname/addtoliked/{id}", produces = "application/json")
    public boolean approveSearchedName(@PathVariable String id, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (!UserUtils.isLoggedIn(currentUser))
            return false;
        try {
            currentUser.approveName(Integer.parseInt(id));
            userService.save(currentUser);
            return true;
        } catch (Error e) {
            return false;
        }

    }

    /**
     * Removes a search result name from the current users liked list, if it is
     * currently on the list.
     * 
     * @param id      String of the id of the name to be removed from the liked
     *                list.
     * @param session The users current Http session.
     * @return Boolean, returns true if action is a success, otherwise it returns
     *         false.
     */
    @GetMapping(path = "/searchname/removefromliked/{id}", produces = "application/json")
    public boolean removeSearchedName(@PathVariable String id, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (!UserUtils.isLoggedIn(currentUser))
            return false;
        try {
            currentUser.removeApprovedName(Integer.parseInt(id));
            userService.save(currentUser);
            return true;
        } catch (Error e) {
            return false;
        }
    }

    /**A
     * 
     * 
     * 
     *  //@RequestParam String email,
     *  //@RequestParam String pass,
     */
    @GetMapping(path="/searchname", produces = "application/json")
    public String searchName(
        @RequestParam String query) {
        //Checkum hvort user sé logged in. 
        //if (!UserUtils.isAuthenticated(userService, email, pass)) return "{}";
        String searchedName = query.toLowerCase();
        ArrayList<JsonObject> searchResultJson = new ArrayList<JsonObject>();
        ArrayList<NameCard> searchResultNamecard = new ArrayList<NameCard>();
        searchResultNamecard = (ArrayList<NameCard>) nameService.findAllByNameLike(StringUtils.capitalize(searchedName.concat("%")));
        for (int i = 0; i < searchResultNamecard.size(); i++) {
            JsonObject nameData = new JsonObject();
            nameData.addProperty("id", searchResultNamecard.get(i).getId());
            nameData.addProperty("name", searchResultNamecard.get(i).getName());
            searchResultJson.add(nameData);
        }

        Gson gson = new Gson();
        JsonArray nameCardJson = gson.toJsonTree(searchResultJson).getAsJsonArray();
        JsonObject nameCardObj = new JsonObject();
        nameCardObj.add("results", nameCardJson);
        System.out.println("Searchname query results: ");
        System.out.println(nameCardObj.toString());
        return nameCardObj.toString();
        }


}
