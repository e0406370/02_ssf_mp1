package vttp.ssf.mp1.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import vttp.ssf.mp1.models.User;
import vttp.ssf.mp1.repositories.UserRepository;
import vttp.ssf.mp1.utility.PasswordHasher;

@Configuration
public class UserService {

  @Autowired
  private UserRepository userRepo;

  public boolean usernameExists(String username) {

    return userRepo.usernameExists(username);
  }

  public boolean emailExists(String email) {

    return userRepo.emailExists(email);
  }

  public boolean isCorrectMatch(String username, String password) {

    return userRepo.isCorrectMatch(username, password);
  }

  public String generateUserID() {

    UUID userIDInUUID = UUID.randomUUID();
    String userIDInString = userIDInUUID.toString().substring(0, 8);

    return userIDInString;
  }

  public void saveUser(User user) {

    String plainPassword = user.getPassword();
    String hashedPassword = PasswordHasher.hashPassword(plainPassword);

    user.setPassword(hashedPassword);

    userRepo.saveUser(user);
  }

  public User loadUser(String username) {

    User loggedUser = userRepo.loadUser(username);

    return loggedUser;
  }

  public boolean hasUserIDKey(String userID) {

    return userRepo.hasUserIDKey(userID);
  }
}
