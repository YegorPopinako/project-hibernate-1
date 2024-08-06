package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {

    private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        Properties properties = new Properties();
        properties.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
        properties.put(Environment.URL, "jdbc:mysql://localhost:3306/rpg");
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "root");
        properties.put(Environment.HBM2DDL_AUTO, "update");
        sessionFactory = new Configuration()
                .setProperties(properties)
                .buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        Session session = sessionFactory.openSession();
        Query<Player> query = session.createNativeQuery("select * from player offset :pageNumber limit :pageSize", Player.class)
                .setParameter("pageNumber", pageNumber)
                .setParameter("pageSize", pageSize);
        return query.list();
    }

    @Override
    public int getAllCount() {
        Session session = sessionFactory.openSession();
        Query<Integer> query = session.createNamedQuery("Player_getAllCount", Integer.class);
        return query.getSingleResult();
    }

    @Override
    public Player save(Player player) {
        Session session = sessionFactory.openSession();
        Transaction tr = session.beginTransaction();
        session.persist(player);
        tr.commit();
        session.close();
        return player;
    }

    @Override
    public Player update(Player player) {
        Session session = sessionFactory.openSession();
        Transaction tr = session.beginTransaction();
        session.merge(player);
        tr.commit();
        session.close();
        return player;
    }

    @Override
    public Optional<Player> findById(long id) {
        Session session = sessionFactory.openSession();
        Query<Player> query = session.createQuery("from Player where id = :id", Player.class)
                .setParameter("id", id);
        return query.uniqueResultOptional();
    }

    @Override
    public void delete(Player player) {
        Session session = sessionFactory.openSession();
        Transaction tr = session.beginTransaction();
        session.delete(player);
        tr.commit();
        session.close();
    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }
}