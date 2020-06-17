import model.Produs;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IObserver  extends Remote {
    void update(Produs m) throws ServerEx, RemoteException;

}
