import model.Produs;
import model.User;
import repo.RepoProdus;
import repo.RepoUser;
import validator.ProdusValidation;
import validator.UserValidator;
import validator.ValidationException;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements IServer {
    private RepoProdus repoProdus;
    private RepoUser repoUser;
    private UserValidator userValidator;
    private ProdusValidation produsValidation;
    private Map<String, IObserver> loggedClients;

    public Server(RepoProdus repoProdus, RepoUser repoUser,UserValidator userValidator,ProdusValidation produsValidation){
        this.repoProdus = repoProdus;
        this.repoUser = repoUser;
        this.produsValidation=produsValidation;
        this.userValidator=userValidator;
        loggedClients = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized void login(User pers, IObserver client) throws ServerEx {
        User user = repoUser.findUser(pers.getName());
        if (user !=null ){
            if(loggedClients.containsKey(user.getName()))
                throw new ServerEx("model.User este deja logat");
            loggedClients.put(user.getName(), client);
        }
        else{
            throw new ServerEx("Authentication error");
        }
    }

    @Override
    public synchronized void logout(User pers) throws ServerEx {
        loggedClients.remove(pers.getName());
    }

    @Override
    public synchronized void saveUser(User user) throws ValidationException{
        if(repoUser.findUser(user.getName())!=null){
            throw new ValidationException("Produs existent");
        }
        try {
            userValidator.validate(user);
            repoUser.save(user);
        }catch (ValidationException e){
            throw new ValidationException(e.getMessage());
        }
    }

    @Override
    public synchronized User findByUsername(String user){
        return repoUser.findUser(user);
    }


    @Override
    public synchronized Iterable<Produs>getAllProducts(){
        return repoProdus.getAll();
    }

    @Override
    public synchronized void saveProduct(Produs produs){
        if(repoProdus.findOne(produs.getDenumire())!=null){
            throw new ValidationException("Produs existent");
        }
        try {
            produsValidation.validate(produs);
            repoProdus.save(produs);
        }catch (ValidationException e){
            throw new ValidationException(e.getMessage());
        }

    }

    @Override
    public synchronized void updateProdus(Produs produs) throws ServerEx {
        try{
            produsValidation.validate(produs);
            repoProdus.update(produs);
            notify(produs);
        }catch (ValidationException e){
            throw  new ValidationException(e.getMessage());
        }
    }
    @Override
    public synchronized void deleteProduct(Produs produs){
        if(repoProdus.findOne(produs.getDenumire())==null){
            throw new ValidationException("Produs inexistent");
        }
        repoProdus.delete(produs);
    }
    @Override
    public Produs findProdus(String name){
        if(repoProdus.findOne(name)==null){
            throw new ValidationException("Produs inexistent");
        }
        return repoProdus.findOne(name);

    }

    private final int defaultThreadsNo = 5;
    private synchronized void notify(Produs Produs) throws ServerEx {
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        for(String s: loggedClients.keySet()){
            String name = String.valueOf(loggedClients.entrySet());
            IObserver user = loggedClients.get(s);
            executor.execute(() -> {
                try {
                    user.update(Produs);
                    System.out.println("Notify " + name);
                } catch (ServerEx ex) {
                    System.out.println("Error notifying friend " + ex);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });
        }
        executor.shutdown();
    }

}
