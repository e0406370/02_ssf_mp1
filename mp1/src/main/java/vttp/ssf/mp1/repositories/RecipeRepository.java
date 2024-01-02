package vttp.ssf.mp1.repositories;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import vttp.ssf.mp1.models.Recipe;
import vttp.ssf.mp1.utility.Utility;

@Repository
public class RecipeRepository {

  @Autowired
  @Qualifier(Utility.BEAN_REDIS)
  private RedisTemplate<String, String> template;

  public boolean hasSavedRecipesIDKey(String savedRecipesID) {

    return template.hasKey(savedRecipesID);
  }

  public boolean hasRecipeIDKey(String savedRecipesID, String recipeID) {

    // list -> redis key: savedRecipesID, value: recipeID
    ListOperations<String, String> listOps = template.opsForList();

    List<String> recipeIDList = listOps.range(savedRecipesID, 0, -1);

    return recipeIDList.contains(recipeID);
  }

  public void saveRecipe(String savedRecipesID, Recipe savedRecipe) {

    // map -> redis key: recipeID, hash key: variable name, hash value: variable value
    HashOperations<String, String, Object> hashOps = template.opsForHash();

    // recipeKey (redis key) - prefix: DrecipeID-
    String recipeKey = String.format("DrecipeID-%s", savedRecipe.getRecipeID());

    hashOps.put(recipeKey, "recipeID", savedRecipe.getRecipeID());

    hashOps.put(recipeKey, "recipeName", savedRecipe.getRecipeName());
    hashOps.put(recipeKey, "imageLink", savedRecipe.getImageLink());
    hashOps.put(recipeKey, "sourceName", savedRecipe.getSourceName());
    hashOps.put(recipeKey, "sourceLink", savedRecipe.getSourceLink());

    hashOps.put(recipeKey, "ingredientList", String.join(",", savedRecipe.getIngredientList()));
    hashOps.put(recipeKey, "preparationTime", String.valueOf(savedRecipe.getPreparationTime()));
    hashOps.put(recipeKey, "calories", String.valueOf(savedRecipe.getCalories()));

    hashOps.put(recipeKey, "mealTypeList", String.join(",", savedRecipe.getMealTypeList()));
    hashOps.put(recipeKey, "dishTypeList", String.join(",", savedRecipe.getDishTypeList()));
    hashOps.put(recipeKey, "cuisineTypeList", String.join(",", savedRecipe.getCuisineTypeList()));
    hashOps.put(recipeKey, "tags", String.join(",", savedRecipe.getTags()));

    // list -> redis key: savedRecipesID, value: recipeID
    ListOperations<String, String> listOps = template.opsForList();

    listOps.rightPush(savedRecipesID, savedRecipe.getRecipeID());
  }

  public List<Recipe> loadRecipes(String savedRecipesID) {

    // list -> redis key: savedRecipesID, value: recipeID
    ListOperations<String, String> listOps = template.opsForList();

    List<String> recipeIDList = listOps.range(savedRecipesID, 0, -1);

    return recipeIDList.stream()
        .map(this::loadRecipe)
        .collect(Collectors.toList());
  }

  private Recipe loadRecipe(String recipeID) {

    // redis key: recipeID, hash key: variable name, hash value: variable value
    HashOperations<String, String, Object> hashOps = template.opsForHash();

    // recipeKey (redis key) - prefix: DrecipeID-
    String recipeKey = String.format("DrecipeID-%s", recipeID);

    Recipe loadedRecipe = new Recipe();
    loadedRecipe.setRecipeID(hashOps.get(recipeKey, "recipeID").toString());

    loadedRecipe.setRecipeName(hashOps.get(recipeKey, "recipeName").toString());
    loadedRecipe.setImageLink(hashOps.get(recipeKey, "imageLink").toString());
    loadedRecipe.setSourceName(hashOps.get(recipeKey, "sourceName").toString());
    loadedRecipe.setSourceLink(hashOps.get(recipeKey, "sourceLink").toString());

    loadedRecipe.setIngredientList(getListFromCSV(hashOps.get(recipeKey, "ingredientList").toString()));
    loadedRecipe.setPreparationTime(Double.valueOf(hashOps.get(recipeKey, "preparationTime").toString()));
    loadedRecipe.setCalories(Double.valueOf(hashOps.get(recipeKey, "calories").toString()));

    loadedRecipe.setMealTypeList(getListFromCSV(hashOps.get(recipeKey, "mealTypeList").toString()));
    loadedRecipe.setDishTypeList(getListFromCSV(hashOps.get(recipeKey, "dishTypeList").toString()));
    loadedRecipe.setCuisineTypeList(getListFromCSV(hashOps.get(recipeKey, "cuisineTypeList").toString()));
    loadedRecipe.setTags(getListFromCSV(hashOps.get(recipeKey, "tags").toString()));

    return loadedRecipe;
  }

  // CSV - Comma Separated Value string
  private List<String> getListFromCSV(String csv) {

    return Arrays.asList(csv.split(","));
  }
}
