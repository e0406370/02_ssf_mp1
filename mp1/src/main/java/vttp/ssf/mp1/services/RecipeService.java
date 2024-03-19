package vttp.ssf.mp1.services;

import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import vttp.ssf.mp1.models.Recipe;
import vttp.ssf.mp1.repositories.RecipeRepository;

@Service
public class RecipeService {

  @Autowired
  private RecipeRepository recipeRepo;

  @Value("${recipeapi.url}")
  private String recipeApiUrl;

  @Value("${recipeapi.type}")
  private String recipeApiType;

  @Value("${recipeapi.id}")
  private String recipeApiID;

  @Value("${recipeapi.key}")
  private String recipeApiKey;

  @Value("${recipeapi.random}")
  private String recipeApiRandom;

  private RestTemplate template = new RestTemplate();

  public List<Recipe> getRecipes(String query, String mealType, String dishType, String cuisineType) {

    if (query == null && mealType == null && dishType == null && cuisineType == null) {

      return Collections.emptyList();
    }

    String requestUrl = buildRequestUrl(query, mealType, dishType, cuisineType);

    RequestEntity<Void> request = RequestEntity
        .get(requestUrl)
        .build();

    ResponseEntity<String> response = template.exchange(request, String.class);

    String jsonPayload = response.getBody();
    JsonReader jsonReader = Json.createReader(new StringReader(jsonPayload));
    JsonObject jsonObject = jsonReader.readObject();
    JsonArray jsonHitsArray = jsonObject.getJsonArray("hits");

    return jsonHitsArray.stream()
        .map(jv -> jv.asJsonObject())
        .map(jo -> jo.getJsonObject("recipe"))
        .map(rjo -> {

          String recipeID = extractRecipeIDFromUri(rjo.getString("uri"));

          String recipeName = rjo.getString("label");
          String imageLink = rjo.getString("image");
          String sourceName = rjo.getString("source");
          String sourceLink = rjo.getString("url");

          List<String> ingredientList = parseJsonArray(rjo.getJsonArray("ingredientLines"));
          Double preparationTime = rjo.getJsonNumber("totalTime").doubleValue();
          Double calories = rjo.getJsonNumber("calories").doubleValue();

          List<String> mealTypeList = parseJsonArray(rjo.getJsonArray("mealType"));
          List<String> dishTypeList = parseJsonArray(rjo.getJsonArray("dishType"));
          List<String> cuisineTypeList = parseJsonArray(rjo.getJsonArray("cuisineType"));
          List<String> tags = parseJsonArray(rjo.getJsonArray("tags"));

          return new Recipe(recipeID, recipeName, imageLink, sourceName, sourceLink, ingredientList, preparationTime,
              calories, mealTypeList, dishTypeList, cuisineTypeList, tags);
        })
        .toList();
  }

  private String buildRequestUrl(String query, String mealType, String dishType, String cuisineType) {

    StringBuilder requestUrlBuilder = new StringBuilder(recipeApiUrl)
        .append("?type=").append(recipeApiType)
        .append("&q=").append(query)
        .append("&app_id=").append(recipeApiID)
        .append("&app_key=").append(recipeApiKey);

    if (!cuisineType.equals("none")) {
      requestUrlBuilder.append("&cuisineType=").append(cuisineType);
    }

    if (!mealType.equals("none")) {
      requestUrlBuilder.append("&mealType=").append(mealType);
    }

    if (!dishType.equals("none")) {
      requestUrlBuilder.append("&dishType=").append(dishType);
    }

    // ensures random permutation of <=20 recipes based on the criteria filled
    requestUrlBuilder.append("&random=").append(recipeApiRandom);

    return requestUrlBuilder.toString();
  }

  private String extractRecipeIDFromUri(String uri) {

    return uri.split("#recipe_")[1];
  }

  private List<String> parseJsonArray(JsonArray jsonArray) {

    if (jsonArray != null) {
      return jsonArray.stream()
          .map(JsonValue::toString)
          .map(s -> s.replaceAll("^\"|\"$", "")) // removes leading and trailing backslashes
          .collect(Collectors.toList());
    }

    else {
      return Collections.emptyList();
    }
  }

  public String createSavedRecipesID(String userID) {

    // savedRecipesKey (redis key) - prefix: DsavedrecipesID-
    return String.format("DsavedrecipesID-%s", userID);
  }

  public Optional<Recipe> extractRecipe(List<Recipe> recipes, String recipeID) {
    return recipes.stream()
        .filter(recipe -> recipe.getRecipeID().equals(recipeID))
        .findFirst();
  }

  public void saveRecipe(String savedRecipesID, Recipe savedRecipe) {

    if (!recipeRepo.hasRecipeIDKey(savedRecipesID, savedRecipe.getRecipeID())) {

      recipeRepo.saveRecipe(savedRecipesID, savedRecipe);
    }
  }

  public List<Recipe> loadRecipes(String savedRecipesID) {

    if (!recipeRepo.hasSavedRecipesIDKey(savedRecipesID)) {

      return Collections.emptyList();
    }

    return recipeRepo.loadRecipes(savedRecipesID);
  }

  public int getBookmarksQuantity(String userID) {

    String savedRecipesID = createSavedRecipesID(userID);

    return loadRecipes(savedRecipesID).size();
  }

  public String convertRecipeToJson(Recipe recipe) {

    return Json.createObjectBuilder()
        .add("recipe_id", recipe.getRecipeID())
        .add("recipe_name", recipe.getRecipeName())
        .add("image_link", recipe.getImageLink())
        .add("source_name", recipe.getSourceName())
        .add("source_link", recipe.getSourceLink())
        .add("ingredient_list", Json.createArrayBuilder(recipe.getIngredientList()).build())
        .add("preparation_time", recipe.getPreparationTime())
        .add("calories", recipe.getCalories())
        .add("meal_type_list", Json.createArrayBuilder(recipe.getMealTypeList()).build())
        .add("dish_type_list", Json.createArrayBuilder(recipe.getDishTypeList()).build())
        .add("cuisine_type_list", Json.createArrayBuilder(recipe.getCuisineTypeList()).build())
        .add("tags", Json.createArrayBuilder(recipe.getTags()).build())
        .build()
        .toString();
  }
}
