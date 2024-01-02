package vttp.ssf.mp1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import vttp.ssf.mp1.models.User;
import vttp.ssf.mp1.services.RecipeService;
import vttp.ssf.mp1.services.UserService;

@Controller
@RequestMapping("/")
public class DashboardController {

  @Autowired
  private UserService userSvc;

  @Autowired
  private RecipeService recipeSvc;

  @GetMapping("/dashboard")
  public ModelAndView getDashboardPage(HttpSession sess) {

    ModelAndView mav = new ModelAndView();

    if (sess.getAttribute("loggedUser") != null) {

      User loggedUser = (User) sess.getAttribute("loggedUser");

      mav.addObject("loggedUser", loggedUser);
      mav.setViewName("dashboard");
    }

    else {

      mav.setViewName("homepage");
    }

    return mav;
  }

  @GetMapping("/logout")
  public ModelAndView logoutUser(HttpSession sess) {

    ModelAndView mav = new ModelAndView();

    sess.invalidate();

    mav.setViewName("homepage");
    return mav;
  }

  @GetMapping("/profile/{userID}")
  public ModelAndView getUserProfile(
      @PathVariable String userID,
      HttpSession sess) {

    ModelAndView mav = new ModelAndView();

    User loggedUser = (User) sess.getAttribute("loggedUser");

    // checks if the user is not logged in
    if (null == loggedUser) {

      mav.addObject("errorMessage", "You must be logged in to view user profiles.");

      mav.setViewName("accesserror1");
      mav.setStatus(HttpStatus.UNAUTHORIZED); // 401 UNAUTHORIZED
    }

    // checks if the requested user ID does not exist in the database
    else if (!userSvc.hasUserIDKey(userID)) {

      mav.addObject("errorMessage", "User with userID %s is not found.".formatted(userID));

      mav.setViewName("accesserror2");
      mav.setStatus(HttpStatus.NOT_FOUND); // 404 NOT FOUND
    }

    // checks if the requested user ID is the same as the logged user ID (prevent user from viewing other profiles)
    else if (!userID.equals(loggedUser.getUserID())) {

      mav.addObject("errorMessage", "You cannot view profiles of other users.");

      mav.setViewName("accesserror3");
      mav.setStatus(HttpStatus.UNAUTHORIZED); // 401 UNAUTHORIZED
    }

    // user is logged in and the requested user ID exists
    else {

      mav.addObject("loggedUser", loggedUser);
      mav.addObject("bookmarks", recipeSvc.getBookmarksQuantity(userID));

      mav.setViewName("userprofile");
      mav.setStatus(HttpStatus.OK); // 200 OK
    }

    return mav;
  }

}
