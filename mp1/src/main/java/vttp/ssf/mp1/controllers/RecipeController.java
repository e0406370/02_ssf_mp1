package vttp.ssf.mp1.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import vttp.ssf.mp1.models.Recipe;
import vttp.ssf.mp1.models.User;
import vttp.ssf.mp1.services.RecipeService;
import vttp.ssf.mp1.utility.Utility;

@Controller
@RequestMapping("/")
public class RecipeController {

  @Autowired
  private Utility utility;

  @Autowired
  private RecipeService recipeSvc;

  @GetMapping("/searchrecipes")
  public ModelAndView getSearchRecipesPage() {

    ModelAndView mav = new ModelAndView();
    mav.addObject("utility", utility);

    String queryString = "";
    mav.addObject("queryString", queryString);
    mav.addObject("mealTypes", Utility.MEAL_TYPE);
    mav.addObject("dishTypes", Utility.DISH_TYPE);
    mav.addObject("cuisineTypes", Utility.CUISINE_TYPE);

    mav.setViewName("recipesearch");
    return mav;
  }

  @GetMapping("/searchrecipes/list")
  public ModelAndView getViewRecipesPage(
      @RequestParam MultiValueMap<String, String> params,
      HttpSession sess) {

    ModelAndView mav = new ModelAndView();

    String query = params.getFirst("queryString");
    String mealType = params.getFirst("mealType");
    String dishType = params.getFirst("dishType");
    String cuisineType = params.getFirst("cuisineType");

    List<Recipe> recipes = recipeSvc.getRecipes(query, mealType, dishType, cuisineType);
    mav.addObject("recipes", recipes);

    sess.setAttribute("recipes", recipes);
    sess.setAttribute("listedRecipes", recipes);

    mav.setViewName("recipelist");
    return mav;
  }

  @GetMapping("/saverecipe/{recipeID}")
  public ModelAndView getSaveRecipe(
      @PathVariable String recipeID,
      HttpSession sess) {

    ModelAndView mav = new ModelAndView();

    User loggedUser = (User) sess.getAttribute("loggedUser");
    if (null == loggedUser) {

      mav.addObject("errorMessage", "You must be logged in to save recipes.");

      mav.setViewName("accesserror1");
      mav.setStatus(HttpStatus.UNAUTHORIZED); // 401 UNAUTHORIZED
    }

    else {

      String userID = loggedUser.getUserID();
      String savedRecipesID = recipeSvc.createSavedRecipesID(userID);

      List<Recipe> recipes = (List<Recipe>) sess.getAttribute("recipes");
      Recipe savedRecipe = recipeSvc.extractRecipe(recipes, recipeID).get();
      recipeSvc.saveRecipe(savedRecipesID, savedRecipe);

      List<Recipe> savedRecipes = recipeSvc.loadRecipes(savedRecipesID);
      sess.setAttribute("listedRecipes", savedRecipes);

      mav.addObject("recipes", savedRecipes);
      mav.addObject("loggedUser", loggedUser);

      mav.setViewName("savedrecipes");
      mav.setStatus(HttpStatus.CREATED); // 201 CREATED
    }

    return mav;
  }

  @GetMapping("/savedrecipes")
  public ModelAndView getBookmarks(HttpSession sess) {

    ModelAndView mav = new ModelAndView();

    User loggedUser = (User) sess.getAttribute("loggedUser");
    if (null == loggedUser) {

      mav.addObject("errorMessage", "You must be logged in to view bookmarked recipes.");

      mav.setViewName("accesserror1");
      mav.setStatus(HttpStatus.UNAUTHORIZED); // 401 UNAUTHORIZED
    }

    else {

      String userID = loggedUser.getUserID();
      String savedRecipesID = recipeSvc.createSavedRecipesID(userID);

      List<Recipe> savedRecipes = recipeSvc.loadRecipes(savedRecipesID);
      sess.setAttribute("listedRecipes", savedRecipes);

      mav.addObject("recipes", savedRecipes);
      mav.addObject("loggedUser", loggedUser);

      mav.setViewName("savedrecipes");
      mav.setStatus(HttpStatus.OK); // 200 OK
    }

    return mav;
  }
}
