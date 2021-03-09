package is.hi.hbv501g.nafnaneistar.nafnaneistar.Controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import is.hi.hbv501g.nafnaneistar.nafnaneistar.Entities.NameCard;
import is.hi.hbv501g.nafnaneistar.nafnaneistar.Entities.User;
import is.hi.hbv501g.nafnaneistar.nafnaneistar.Services.NameService;
import is.hi.hbv501g.nafnaneistar.nafnaneistar.Services.UserService;
import is.hi.hbv501g.nafnaneistar.utils.UserUtils;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * NameController
 * Listens to most of the paths connected to activities that have
 * primary focus the name aspect of the application
 */
@Controller
public class NameController {
    private UserService userService;
    private NameService nameService;


    /**
     * Constructor for NameController, it needs a UserService and a NameService to function
     * @param userService
     * @param nameService
     */
    @Autowired
    public NameController(UserService userService, NameService nameService) {
        this.userService = userService;
        this.nameService = nameService;
    }


    /**
     * swipeNames is activated when the user accesses /swipe on the domain.
     * to access /swipe the user have logged in and maintain an active session.
     * if there is no session of currentUser the user is redirected to /login 
     * @param model manages the data for the viewing template
     * @param session manages the session of the user
     * @return on a valid session, the user is rendered the Swipe template
     */
    @RequestMapping(value = "/swipe", method = RequestMethod.GET)
    public String swipeNames(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if(!UserUtils.isLoggedIn(currentUser))
            return "redirect:/login";
        int mSize = UserUtils.getGenderList(currentUser,nameService,0).size();
        int fSize = UserUtils.getGenderList(currentUser,nameService,1).size();
        
        model.addAttribute("maleleft","("+mSize+")" );
        model.addAttribute("femaleleft","("+fSize+")" );

        model.addAttribute("user", currentUser);
        Optional<NameCard> nc = nameService.findById(currentUser.getRandomNameId());
        model.addAttribute("name",nc);
        return "Swipe";
    }

    /**
     * On accessing /viewliked from the domain, if the user is logged in and has a
     * valid session the model is populated with data and the User is rendered the viewliked 
     * template
     * @param model manages the data for the viewing template
     * @param session manages the session of the user
     * @return viewliked viewing template on succssfull authentication
     */
    @RequestMapping(value = "/viewlikedOld", method = RequestMethod.GET)
    public String viewLiked_old(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if(!UserUtils.isLoggedIn(currentUser)) return "redirect:/login";
                
        int fnames = UserUtils.getGenderList(currentUser.getApprovedNames().keySet(), nameService, 1).size();
        int mnames = UserUtils.getGenderList(currentUser.getApprovedNames().keySet(), nameService, 0).size();

        int totalfnames = nameService.countByGender(true);
        int totalmnames = nameService.countByGender(false);

        int totalmnamesleft = UserUtils.getGenderList(currentUser,nameService,0).size() ;
        int totalfnamesleft = UserUtils.getGenderList(currentUser,nameService,1).size() ;

        
        int femaledisliked = Math.abs(totalfnames - (totalfnamesleft) - fnames) ;
        int maledisliked = Math.abs(totalmnames - (totalmnamesleft)  - mnames) ;
        
        Integer[] femalestats = new Integer[] {fnames,femaledisliked,totalfnamesleft};
        Integer[] malestats = new Integer[] {mnames,maledisliked,totalmnamesleft};

        String meaning = nameService.findDescriptionByName(currentUser.getName().split(" ")[0]);
        ArrayList<User> partners = new ArrayList<User>();
        for(Long id : currentUser.getLinkedPartners()) {
            User partner = userService.findById(id).get();
            if(!partners.contains(partner))
                 partners.add(partner);
        }
            
        HashMap<NameCard,Integer> ncs = new HashMap<>();
        currentUser.getApprovedNames().forEach((key,value) -> ncs.put((nameService.findById(key).orElse(null)),value)); 

        model.addAttribute("ncs", ncs);
        model.addAttribute("femalestats", femalestats);
        model.addAttribute("malestats", malestats);
        model.addAttribute("partners", partners);
        model.addAttribute("meaning", meaning);
        model.addAttribute("user", currentUser);
        return "viewliked";
    }

    /**
     * On accessing /searchname from the domain, if the user is logged in and has a
     * valid session the User is rendered the searchname template
     * @param model manages the data for the viewing template
     * @param session manages the session of the user
     * @return searchname template rendered
     */
    @RequestMapping(value = "/searchname", method = RequestMethod.GET)
    public String searchName(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if(!UserUtils.isLoggedIn(currentUser))
            return "redirect:/login";
        model.addAttribute("user", currentUser);

        return "searchname";
    }

    /**
     * Takes input from user and populates the model with names that are like the users input 
     * @param searchedName String which is used for a search of available names
     * @param model Model populated with related data
     * @param session Current users session
     * @param gender A string that signifies gender: 0 is male, 1 is female, 3 are all.
     * @return searchname template
     */
    @RequestMapping(value="/searchname", method = RequestMethod.POST)
    public String searchName(
            @RequestParam(required = true) String searchedName,
            @RequestParam(required = true) String gender,
            Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if(!UserUtils.isLoggedIn(currentUser)){
            return "redirect:login";
        }
        searchedName = searchedName.toLowerCase();
        ArrayList<NameCard> searchedList;
        if ((Integer.parseInt(gender) == 3)) {
            searchedList = (ArrayList<NameCard>) nameService.findAllByNameLike(StringUtils.capitalize(searchedName.concat("%")));
        }
        else{
            Boolean kyn = (Integer.parseInt(gender) == 1) ? true : false;
            searchedList = (ArrayList<NameCard>) nameService.findAllByNameLikeAndGender((StringUtils.capitalize(searchedName.concat("%"))), kyn);
        }

        HashMap<NameCard,Integer> ncs = new HashMap<>();
        currentUser.getApprovedNames().forEach((key,value) -> ncs.put((nameService.findById(key).orElse(null)),value));
        ArrayList<NameCard> approvedList = new ArrayList<NameCard>(ncs.keySet());
        ArrayList<Integer> approvedId = new ArrayList<>();

        for (NameCard item : approvedList){
            approvedId.add(item.getId());
        }
        
        model.addAttribute("user", currentUser);
        model.addAttribute("approvedList",approvedId);
        model.addAttribute("names", searchedList);
        return "searchname";
    }
}
