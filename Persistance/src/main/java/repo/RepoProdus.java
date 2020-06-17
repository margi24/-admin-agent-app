package repo;

import model.HibernateUtils;
import model.Produs;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class RepoProdus {
    static SessionFactory sessionFactory= HibernateUtils.getSessionFactory();

    public Iterable<Produs> getAll(){
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                List<Produs> produse=  session.createQuery("from Produs").list();
                tx.commit();
                return  produse;
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                if (tx != null)
                    tx.rollback();
            }
        }
        return null;
    }

    public void save(Produs produs) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.save(produs);
                tx.commit();
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
    }
    public void update(Produs produs) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.update(produs);
                tx.commit();
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
    }
    public void delete(Produs produs) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.delete(produs);
                tx.commit();
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
    }

    public Produs findOne(String id) {
        try (Session session = sessionFactory.openSession()) {
            Query query = session.createQuery("from Produs  where denumire = :id").setParameter("id",id);
            try{
                Produs produs = (Produs) query.getSingleResult();
                return produs;
            }catch (Exception e){
                return  null;
            }
        }
    }
}
