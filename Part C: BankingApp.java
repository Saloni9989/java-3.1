import jakarta.persistence.*;
import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Properties;

public class BankingApp {

    // --- Account Entity ---
    @Entity
    @Table(name = "account")
    public static class Account {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;

        private String name;
        private double balance;

        public Account() {}
        public Account(String name, double balance) {
            this.name = name;
            this.balance = balance;
        }

        // Getters and Setters
        public int getId() { return id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getBalance() { return balance; }
        public void setBalance(double balance) { this.balance = balance; }
    }

    // --- AccountDAO with Transaction Management ---
    @Repository
    public static class AccountDAO {

        @Autowired
        private SessionFactory sessionFactory;

        @Transactional
        public void transferMoney(int fromId, int toId, double amount) {
            Account fromAccount = sessionFactory.getCurrentSession().get(Account.class, fromId);
            Account toAccount = sessionFactory.getCurrentSession().get(Account.class, toId);

            fromAccount.setBalance(fromAccount.getBalance() - amount);
            toAccount.setBalance(toAccount.getBalance() + amount);

            sessionFactory.getCurrentSession().update(fromAccount);
            sessionFactory.getCurrentSession().update(toAccount);

            System.out.println("Transferred " + amount + " from " + fromAccount.getName() +
                    " to " + toAccount.getName());
        }
    }

    // --- Spring + Hibernate Configuration ---
    @Configuration
    @EnableTransactionManagement
    @ComponentScan(basePackageClasses = BankingApp.class)
    public static class SpringHibernateConfig {

        @Bean
        public DataSource dataSource() {
            BasicDataSource ds = new BasicDataSource();
            ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
            ds.setUrl("jdbc:mysql://localhost:3306/spring_hibernate_db");
            ds.setUsername("root");
            ds.setPassword("password");
            return ds;
        }

        @Bean
        public LocalSessionFactoryBean sessionFactory() {
            LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
            sessionFactory.setDataSource(dataSource());
            sessionFactory.setPackagesToScan(BankingApp.class.getPackageName());
            Properties props = new Properties();
            props.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
            props.put("hibernate.show_sql", "true");
            props.put("hibernate.hbm2ddl.auto", "update");
            sessionFactory.setHibernateProperties(props);
            return sessionFactory;
        }

        @Bean
        public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
            return new HibernateTransactionManager(sessionFactory);
        }
    }

    // --- Main Method ---
    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(SpringHibernateConfig.class);
        AccountDAO accountDAO = context.getBean(AccountDAO.class);

        // Perform a money transfer
        accountDAO.transferMoney(1, 2, 500.0);
        System.out.println("Money transfer completed successfully!");
    }
}
