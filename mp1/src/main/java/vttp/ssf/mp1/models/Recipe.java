package vttp.ssf.mp1.models;

import java.util.List;

public class Recipe {

  private String recipeID;

  private String recipeName;
  private String imageLink;
  private String sourceName;
  private String sourceLink;

  private List<String> ingredientList;
  private Double preparationTime;
  private Double calories;

  private List<String> mealTypeList;
  private List<String> dishTypeList;
  private List<String> cuisineTypeList;
  private List<String> tags;

  // Constructor Methods
  public Recipe() {};

  public Recipe(String recipeID, String recipeName, String imageLink, String sourceName, String sourceLink,
      List<String> ingredientList, Double preparationTime, Double calories, List<String> mealTypeList,
      List<String> dishTypeList, List<String> cuisineTypeList, List<String> tags) {
        
    this.recipeID = recipeID;
    this.recipeName = recipeName;
    this.imageLink = imageLink;
    this.sourceName = sourceName;
    this.sourceLink = sourceLink;
    this.ingredientList = ingredientList;
    this.preparationTime = preparationTime;
    this.calories = calories;
    this.mealTypeList = mealTypeList;
    this.dishTypeList = dishTypeList;
    this.cuisineTypeList = cuisineTypeList;
    this.tags = tags;
  }

  // Getter Methods

  public String getRecipeID() {
    return this.recipeID;
  }

  public String getRecipeName() {
    return this.recipeName;
  }

  public String getImageLink() {
    return this.imageLink;
  }

  public String getSourceName() {
    return this.sourceName;
  }

  public String getSourceLink() {
    return this.sourceLink;
  }

  public List<String> getIngredientList() {
    return this.ingredientList;
  }

  public Double getPreparationTime() {
    return this.preparationTime;
  }

  public Double getCalories() {
    return this.calories;
  }

  public List<String> getMealTypeList() {
    return this.mealTypeList;
  }

  public List<String> getDishTypeList() {
    return this.dishTypeList;
  }

  public List<String> getCuisineTypeList() {
    return this.cuisineTypeList;
  }

  public List<String> getTags() {
    return this.tags;
  }

  // Setter Methods

  public void setRecipeID(String recipeID) {
    this.recipeID = recipeID;
  }

  public void setRecipeName(String recipeName) {
    this.recipeName = recipeName;
  }

  public void setImageLink(String imageLink) {
    this.imageLink = imageLink;
  }

  public void setSourceName(String sourceName) {
    this.sourceName = sourceName;
  }

  public void setSourceLink(String sourceLink) {
    this.sourceLink = sourceLink;
  }

  public void setIngredientList(List<String> ingredientList) {
    this.ingredientList = ingredientList;
  }

  public void setPreparationTime(Double preparationTime) {
    this.preparationTime = preparationTime;
  }

  public void setCalories(Double calories) {
    this.calories = calories;
  }

  public void setMealTypeList(List<String> mealTypeList) {
    this.mealTypeList = mealTypeList;
  }

  public void setDishTypeList(List<String> dishTypeList) {
    this.dishTypeList = dishTypeList;
  }

  public void setCuisineTypeList(List<String> cuisineTypeList) {
    this.cuisineTypeList = cuisineTypeList;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  @Override
  public String toString() {
    return "Recipe [recipeID=" + recipeID + ", recipeName=" + recipeName + ", imageLink=" + imageLink + ", sourceName="
        + sourceName + ", sourceLink=" + sourceLink + ", ingredientList=" + ingredientList + ", preparationTime="
        + preparationTime + ", calories=" + calories + ", mealTypeList=" + mealTypeList + ", dishTypeList="
        + dishTypeList + ", cuisineTypeList=" + cuisineTypeList + ", tags=" + tags + "]";
  }
}
