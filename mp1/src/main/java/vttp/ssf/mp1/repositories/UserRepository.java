package vttp.ssf.mp1.repositories;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import vttp.ssf.mp1.models.User;
import vttp.ssf.mp1.utility.PasswordHasher;
import vttp.ssf.mp1.utility.Utility;

@Repository
public class UserRepository {

  @Autowired
  @Qualifier(Utility.BEAN_REDIS)
  private RedisTemplate<String, String> template;

  public Set<String> getAllUserIDKeys() {

    Set<String> keys = template.keys("*");

    // avoid checking keys that are not userID (filter off all recipeID and savedrecipesID keys)
    Set<String> filteredKeys = keys.stream()
        .filter(key -> !key.startsWith("DrecipeID") && !key.startsWith("DsavedrecipesID"))
        .collect(Collectors.toSet());

    return filteredKeys;
  }

  public boolean usernameExists(String username) {

    for (String userIDKey : getAllUserIDKeys()) {

      Map<Object, Object> entries = template.opsForHash().entries(userIDKey);
      if (entries.get("username").equals(username)) {
        return true;
      }
    }

    return false;
  }

  public boolean emailExists(String email) {

    for (String userIDKey : getAllUserIDKeys()) {

      Map<Object, Object> entries = template.opsForHash().entries(userIDKey);
      if (entries.get("email").equals(email)) {
        return true;
      }
    }

    return false;
  }

  public boolean isCorrectMatch(String username, String password) {

    for (String userIDKey : getAllUserIDKeys()) {

      Map<Object, Object> entries = template.opsForHash().entries(userIDKey);
      if (entries.get("username").equals(username)) {

        // left: password in plaintext, right: password in encryptedtext
        if (PasswordHasher.checkPassword(password, entries.get("password").toString())) {
          return true;
        }
      }
    }

    return false;
  }

  public void saveUser(User user) {

    String userID = user.getUserID();

    template.opsForHash().put(userID, "username", user.getUsername());
    template.opsForHash().put(userID, "password", user.getPassword());
    template.opsForHash().put(userID, "name", user.getName());
    template.opsForHash().put(userID, "email", user.getEmail());
    template.opsForHash().put(userID, "birthDate", user.getBirthDate().toString());
    template.opsForHash().put(userID, "age", user.getAge().toString());
  }

  public User loadUser(String username) {

    User loggedUser = null;

    for (String userIDKey : getAllUserIDKeys()) {

      Map<Object, Object> entries = template.opsForHash().entries(userIDKey);
      if (entries.get("username").equals(username)) {

        String password = template.opsForHash().get(userIDKey, "password").toString();
        String name = template.opsForHash().get(userIDKey, "name").toString();
        String email = template.opsForHash().get(userIDKey, "email").toString();
        LocalDate birthDate = LocalDate.parse(template.opsForHash().get(userIDKey, "birthDate").toString());
        Integer age = Integer.parseInt(template.opsForHash().get(userIDKey, "age").toString());

        loggedUser = new User(username, password, name, email, birthDate, age);
        loggedUser.setUserID(userIDKey);
      }
    }

    return loggedUser;
  }

  public boolean hasUserIDKey(String userID) {

    return template.hasKey(userID);
  }
}
