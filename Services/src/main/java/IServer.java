import model.Produs;
import model.User;
import validator.ValidationException;

public interface IServer {
    void login(User pers, IObserver client) throws ServerEx;
    void logout(User pers) throws ServerEx;

    Iterable<Produs> getAllProducts() ;
    void saveUser(User user) throws ValidationException;
    void saveProduct(Produs user) throws ValidationException;
    User findByUsername(String user);

    void updateProdus(Produs m) throws ServerEx;

    void deleteProduct(Produs produs);

    Produs findProdus(String name);
}
