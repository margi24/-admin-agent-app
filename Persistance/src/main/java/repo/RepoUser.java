package repo;

import model.HibernateUtils;
import model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class RepoUser {
    static SessionFactory sessionFactory = HibernateUtils.getSessionFactory();

    public void save(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.save(user);
                tx.commit();
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
    }

    public User findUser(String id) {
        try (Session session = sessionFactory.openSession()) {
            Query query = session.createQuery("from User  where name = :id").setParameter("id", id);
            try {
                User produs = (User) query.getSingleResult();
                return produs;
            } catch (Exception e) {
                return null;
            }
        }
    }
}
