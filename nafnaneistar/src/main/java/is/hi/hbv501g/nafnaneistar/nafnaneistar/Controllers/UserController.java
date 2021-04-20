package is.hi.hbv501g.nafnaneistar.nafnaneistar.Controllers;

import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import is.hi.hbv501g.nafnaneistar.nafnaneistar.Entities.User;
import is.hi.hbv501g.nafnaneistar.nafnaneistar.Services.NameService;
import is.hi.hbv501g.nafnaneistar.nafnaneistar.Services.UserService;
import is.hi.hbv501g.nafnaneistar.utils.UserUtils;

/**
 * UserController Listens to most of the paths connected to activities that have
 * primary focus the user aspect of the application
 */
@Controller
public class UserController {
    private UserService userService;

    /**
     * Constructor for UserController, it needs a UserService and a NameService to
     * function
     *
     * @param userService
     * @param nameService
     */
    @Autowired
    public UserController(UserService userService, NameService nameService) {
        this.userService = userService;
    }

    /**
     * login is activated as the / on the domain. to access /login the user is not
     * logged in. the user is redirect to /login from other sites if there is no
     * session of currentUser
     *
     * @param model   manages the data for the viewing template
     * @param session manages the session of the user
     * @return on a valid session, the user is rendered the Login template
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (UserUtils.isLoggedIn(currentUser))
            return "redirect:/swipe";
        model.addAttribute("user", new User());
        return "Login";
    }

    /**
     * On the domain, if the user is logged in the model is populated with data
     *
     * @param email    String that has to correspond to user
     * @param password String that has to correspond to same user
     * @param model    manages the data for the viewing template
     * @return login template
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String postLogin(@RequestParam(value = "email", required = true) String email,
            @RequestParam(value = "password", required = true) String password, Model model) {
        model.addAttribute("users", userService.findAll());
        return "Login";
    }

    /**
     * signup is activated when the user accesses /signup on the domain. to access
     * /signup the user is not logged in and has not an active session.
     *
     * @param model manages the data for the viewing template
     * @return signup template
     */
    @RequestMapping(value = "/signup_OLD", method = RequestMethod.GET)
    public String signupForm(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("user", new User());
        return "Signup";
    }

    /**
     * logout is activated when the user accesses /logout on the domain. to access
     * /logout the user is logged in and has an active session
     *
     * @param model   manages the data for the viewing template
     * @param session manages the session of the user
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logOut(Model model, HttpSession session) {
        session.removeAttribute("currentUser");
        return "redirect:/login";
    }

    /**
     * linkpartner is activated when the user accesses /linkpartner on the domain.
     * to access /linkpartner the user is not logged in and has not an active
     * session.
     *
     * @param model   manages the data for the viewing template
     * @param session manages the session of the user
     * @return linkpartner template
     */
    @RequestMapping(value = "/linkpartner_OLD", method = RequestMethod.GET)
    public String linkpartnerForm(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null)
            return "redirect:/login";
        model.addAttribute("users", userService.findAll());
        model.addAttribute("user", currentUser);
        System.out.println("current linked partner" + currentUser.getLinkedPartners());

        for (Long id : currentUser.getLinkedPartners()) {
            System.out.println(userService.findById(id));
        }
        ArrayList<User> partners = new ArrayList<User>();
        for (Long id : currentUser.getLinkedPartners())
            partners.add(userService.findById(id).get());
        model.addAttribute("partners", partners);
        return "linkpartner";
    }

    /**
     * populates the model with data and
     *
     * @param user   Object user that is the current user
     * @param result manages the retrieval of validation errors
     * @param model  manages the data for the viewing template
     * @return signup template
     */

     /**
      * Populates the model with data and saves changes of linked partners
      * @param email String
      * @param model manages the data for the viewing template
      * @param session manages the session of the user
      * @return linkpartner template
      */
    @RequestMapping(value = "/linkpartner_OLD", method = RequestMethod.POST)
    public String linkpartner_old(@RequestParam(value = "email", required = true) String email, Model model,
            HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");

        ArrayList<User> partners = new ArrayList<User>();
        for (Long id : currentUser.getLinkedPartners())
            partners.add(userService.findById(id).get());
        model.addAttribute("partners", partners);

        if (!UserUtils.isLoggedIn(currentUser))
            return "redirect:/login";
        else if (userService.findByEmail(email) == null) {
            return "linkpartner.html";
        } else {
            currentUser.addLinkedPartner(userService.findByEmail(email).getId());
            userService.findByEmail(email).addLinkedPartner(currentUser.getId());
            userService.save(currentUser);
            return "redirect:/linkpartner";
        }
    }
}
