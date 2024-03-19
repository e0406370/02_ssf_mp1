package vttp.ssf.mp1.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.servlet.http.HttpSession;

import vttp.ssf.mp1.models.Recipe;
import vttp.ssf.mp1.services.RecipeService;

@RestController
@RequestMapping("/")
public class RestRecipeController {

  @Autowired
  private RecipeService recipeSvc;

  @GetMapping("/recipe/{recipeID}")
  public ResponseEntity<String> getRecipeInJson(
      @PathVariable String recipeID,
      HttpSession sess) {

    List<Recipe> recipes = (List<Recipe>) sess.getAttribute("listedRecipes");
    if (null == recipes) {

      String notFoundMessage = "You must be logged in to view recipes in JSON.";

      JsonObject notFound = Json.createObjectBuilder()
          .add("message", notFoundMessage)
          .build();

      ResponseEntity<String> result1 = ResponseEntity
          .status(HttpStatus.UNAUTHORIZED) // 401 UNAUTHORIZED
          .body(notFound.toString());

      return result1;
    }

    Optional<Recipe> savedRecipeOptional = recipeSvc.extractRecipe(recipes, recipeID);
    if (!savedRecipeOptional.isPresent()) {

      String notFoundMessage = String.format("Recipe with recipeID %s not found", recipeID);

      JsonObject notFound = Json.createObjectBuilder()
          .add("message", notFoundMessage)
          .build();

      ResponseEntity<String> result2 = ResponseEntity
          .status(HttpStatus.NOT_FOUND) // 404 NOT FOUND
          .body(notFound.toString());

      return result2;
    }

    else {

      ResponseEntity<String> result3 = ResponseEntity
          .status(HttpStatus.OK) // 200 OK
          .body(recipeSvc.convertRecipeToJson(savedRecipeOptional.get()));

      return result3;
    }
  }
}
