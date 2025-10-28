import jakarta.persistence.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class StudentCRUDApp {

    // --- Student Entity ---
    @Entity
    @Table(name = "student")
    public static class Student {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;

        private String name;
        private String email;

        public Student() {}

        public Student(String name, String email) {
            this.name = name;
            this.email = email;
        }

        // Getters and setters
        public int getId() { return id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    // --- Hibernate Utility ---
    public static class HibernateUtil {
        private static final SessionFactory sessionFactory = buildSessionFactory();

        private static SessionFactory buildSessionFactory() {
            try {
                return new Configuration()
                        .configure("hibernate.cfg.xml")  // Make sure this file exists
                        .addAnnotatedClass(Student.class)
                        .buildSessionFactory();
            } catch (Throwable ex) {
                throw new ExceptionInInitializerError(ex);
            }
        }

        public static SessionFactory getSessionFactory() {
            return sessionFactory;
        }
    }

    // --- Main Method: CRUD Operations ---
    public static void main(String[] args) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        // --- Create ---
        Student student = new Student("Nitika", "nitika@example.com");
        session.save(student);
        System.out.println("Student Created: " + student.getName());

        // --- Read ---
        Student s = session.get(Student.class, student.getId());
        System.out.println("Student Retrieved: " + s.getName() + ", " + s.getEmail());

        // --- Update ---
        s.setEmail("nitika123@example.com");
        session.update(s);
        System.out.println("Student Updated: " + s.getName() + ", " + s.getEmail());

        // --- Delete ---
        session.delete(s);
        System.out.println("Student Deleted: " + s.getName());

        tx.commit();
        session.close();
        HibernateUtil.getSessionFactory().close();
    }
}
