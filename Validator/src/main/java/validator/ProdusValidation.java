package validator;

import model.Produs;

public class ProdusValidation implements Validator<Produs> {
    @Override
    public void validate(Produs entity) throws ValidationException {
        if(entity.getDenumire()==""){
            throw new ValidationException("Denumirea produsului e incorecta");
        }
        if(entity.getPret()<0){
            throw new ValidationException("Pretul incorect");
        }
        if(entity.getCantitate()<0){
            throw new ValidationException("Cantitatea incorecta");
        }


    }
}
