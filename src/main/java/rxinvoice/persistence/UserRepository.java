package rxinvoice.persistence;

import com.google.common.base.Optional;
import rxinvoice.domain.User;

/**
 * Created with IntelliJ IDEA.
 * User: xavierhanin
 * Date: 4/10/13
 * Time: 22:18
 * To change this template use File | Settings | File Templates.
 */
public interface UserRepository {
    User createUser(User user);

    User updateUser(User user);

    Iterable<User> findUsers();

    Optional<User> findUserByKey(String key);

    Optional<User> findUserByName(String name);

    void deleteUser(String key);

    void setCredentials(String userKey, String passwordHash);

    Optional<User> findAndCheckCredentials(String name, String passwordHash);
}
