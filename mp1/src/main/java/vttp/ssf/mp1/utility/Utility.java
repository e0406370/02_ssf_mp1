package vttp.ssf.mp1.utility;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

@Component
public class Utility {

    // for password validation
    public static final String PASSWORD_SPECIAL_CHARS = "@#$%^`<>&+=\"!ºª·#~%&'¿¡€,:;*/+-.=_\\[\\]\\(\\)\\|\\_\\?\\\\";

    public static final int PASSWORD_MIN_SIZE = 10;

    public static final String PASSWORD_VALIDATION_MESSAGE = new StringBuilder("Password Requirements:\n")
        .append("- At least 10 characters long.\n")
        .append("- Contains at least one digit.\n")
        .append("- Contains at least one lower case letter.\n")
        .append("- Contains at least one upper case letter.\n")
        .append("- Contains at least one special character.\n")
        .append("- Does not contain any whitespaces, tabs, etc.\n")
        .toString();

    // for creating bean for redis template
    public static final String BEAN_REDIS = "userRedis";

    // for filtering recipes
    public static final String[] MEAL_TYPE = {
        "breakfast",
        "brunch",
        "lunch",
        "dinner",
        "snack",
        "teatime"
    };

    public static final String[] DISH_TYPE = {
        "alcohol cocktail",
        "biscuits and cookies",
        "bread",
        "cereals",
        "condiments and sauces",
        "desserts",
        "drinks",
        "egg",
        "ice cream and custard",
        "main course",
        "pancake",
        "pasta",
        "pastry",
        "pies and tarts",
        "pizza",
        "preps",
        "preserve",
        "salad",
        "sandwiches",
        "seafood",
        "side dish",
        "soup",
        "special occasions",
        "starter",
        "sweets"
    };

    public static final String[] CUISINE_TYPE = {
        "american",
        "asian",
        "british",
        "caribbean",
        "central europe",
        "chinese",
        "eastern europe",
        "french",
        "greek",
        "indian",
        "italian",
        "japanese",
        "korean",
        "kosher",
        "mediterranean",
        "mexican",
        "middle eastern",
        "nordic",
        "south american",
        "south east asian",
        "world"
    };

    public static final String getPascalCase(final String words) {
        return Stream.of(words.trim().split("\\s"))
                .filter(word -> !word.isEmpty())
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" "))
                .trim();
    }
}
