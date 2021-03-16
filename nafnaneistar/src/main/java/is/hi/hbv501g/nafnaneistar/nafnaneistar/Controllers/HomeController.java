package is.hi.hbv501g.nafnaneistar.nafnaneistar.Controllers;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import is.hi.hbv501g.nafnaneistar.nafnaneistar.Entities.User;
import is.hi.hbv501g.nafnaneistar.nafnaneistar.Services.NameService;
import is.hi.hbv501g.nafnaneistar.nafnaneistar.Services.UserService;
import is.hi.hbv501g.nafnaneistar.utils.UserUtils;

@Controller
public class HomeController {

    @Autowired
    public HomeController(UserService userService, NameService nameService) {
    }

    @RequestMapping("/")
    public ModelAndView Home() {
        return new ModelAndView("redirect:http://www.nafnaneistar.xyz/");
    }

    @RequestMapping("/settings")
    public String Settings(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if(!UserUtils.isLoggedIn(currentUser)) return "redirect:/login";
        model.addAttribute("user", currentUser);

        return "Settings";
    }








   
}