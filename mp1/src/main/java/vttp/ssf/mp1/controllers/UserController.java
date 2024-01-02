package vttp.ssf.mp1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import vttp.ssf.mp1.models.User;
import vttp.ssf.mp1.services.UserService;

@Controller
@RequestMapping("/")
public class UserController {

  @Autowired
  private UserService userSvc;

  @GetMapping({ "/", "/homepage", "/index.html" })
  public ModelAndView getHomePage(HttpSession sess) {

    ModelAndView mav = new ModelAndView();

    if (sess.getAttribute("loggedUser") == null) {

      mav.setViewName("homepage");
    }

    else {
      
      User loggedUser = (User) sess.getAttribute("loggedUser");

      mav.addObject("loggedUser", loggedUser);
      mav.setViewName("dashboard");
    }

    return mav;
  }

  @GetMapping("/register")
  public ModelAndView getRegisterUser() {

    ModelAndView mav = new ModelAndView();

    mav.addObject("user", new User());

    mav.setViewName("registeruser");
    return mav;
  }

  @PostMapping("/register/post")
  public ModelAndView postRegisterUser(
      @Valid @ModelAttribute User user,
      BindingResult results) {

    ModelAndView mav = new ModelAndView();

    // syntactic validation errors
    if (results.hasErrors()) {

      mav.setViewName("registeruser");
      mav.setStatus(HttpStatus.BAD_REQUEST);

      return mav;
    }

    // semantic validation error 1 : username already exists in database
    if (userSvc.usernameExists(user.getUsername())) {

      mav.addObject("usernameExists", true);

      mav.setViewName("registeruser");
      mav.setStatus(HttpStatus.BAD_REQUEST); // 400 BAD REQUEST

      return mav;
    }

    // semantic validation error 2 : email already exists in database
    if (userSvc.emailExists(user.getEmail())) {

      mav.addObject("emailExists", true);

      mav.setViewName("registeruser");
      mav.setStatus(HttpStatus.BAD_REQUEST); // 400 BAD REQUEST

      return mav;
    }

    String userID = userSvc.generateUserID();
    user.setUserID(userID);

    userSvc.saveUser(user);

    mav.addObject("successfulRegistration", true);

    mav.setViewName("loginuser");
    mav.setStatus(HttpStatus.CREATED); // 201 CREATED

    return mav;
  }

  @GetMapping("/login")
  public ModelAndView getLoginUser() {

    ModelAndView mav = new ModelAndView();

    mav.addObject("user", new User());

    mav.setViewName("loginuser");
    return mav;
  }

  @PostMapping("/login/post")
  public ModelAndView postLoginUser(
      @ModelAttribute User user,
      HttpSession sess) {

    ModelAndView mav = new ModelAndView();

    // semantic validation error 1 : username does not exist in DB
    if (!userSvc.usernameExists(user.getUsername())) {

      mav.addObject("usernameNotFound", true);

      mav.setViewName("loginuser");
      mav.setStatus(HttpStatusCode.valueOf(400));

      return mav;
    }

    // semantic validation error 2 : password does not match corr. username
    if (!userSvc.isCorrectMatch(user.getUsername(), user.getPassword())) {

      mav.addObject("incorrectPassword", true);

      mav.setViewName("loginuser");
      mav.setStatus(HttpStatusCode.valueOf(400));

      return mav;

    }

    User loggedUser = userSvc.loadUser(user.getUsername());
    sess.setAttribute("loggedUser", loggedUser);

    mav.addObject("loggedUser", loggedUser);

    mav.setViewName("dashboard");
    mav.setStatus(HttpStatusCode.valueOf(200));

    return mav;
  }
}
