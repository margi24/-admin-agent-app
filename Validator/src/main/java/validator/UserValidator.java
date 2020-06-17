package validator;
import model.User;
public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        if(entity.getName()==""){
            throw new ValidationException("Denumirea trebuie sa contina stringuri");
        }
        if(entity.getTip()=="" || entity.getTip()=="Selecteaza"){
            throw new ValidationException("Pretul incorect");
        }
        if(entity.getPassword()==""){
            throw new ValidationException("Cantitatea nu poate fi negativa");
        }


    }
}
