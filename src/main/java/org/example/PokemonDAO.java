package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.List;

public class PokemonDAO {

    private final SessionFactory factory;

    public PokemonDAO(SessionFactory factory) {
        this.factory = factory;
    }

    // --- MÉTODOS CON OBXECTOS HIBERNATE ---
    public void crearObjeto(Pokemon p) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(p);
            tx.commit();
        }
    }

    public Pokemon lerPorIdObjeto(int id) {
        try (Session session = factory.openSession()) {
            return session.get(Pokemon.class, id);
        }
    }

    public void modificarObjeto(Pokemon p) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(p);
            tx.commit();
        }
    }

    public void eliminarTodosObjetos() {
        List<Pokemon> todos = listarTodosSQL();
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            for (Pokemon p : todos) {
                Pokemon managed = session.get(Pokemon.class, p.getId());
                if (managed != null) session.remove(managed);
            }
            tx.commit();
        }
    }

    // --- MÉTODOS CON QUERIES SQL ESPECÍFICAS ---
    public void crearSQL(String nome, LocalDate nacemento, int pokedexId, int adestradorId) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createNativeMutationQuery(
                            "INSERT INTO pokemon (nome, nacemento, pokedexentry, adestrador) VALUES (:nome, :nac, :dexId, :adId)")
                    .setParameter("nome", nome)
                    .setParameter("nac", nacemento)
                    .setParameter("dexId", pokedexId)
                    .setParameter("adId", adestradorId)
                    .executeUpdate();
            tx.commit();
        }
    }

    public List<Pokemon> listarTodosSQL() {
        try (Session session = factory.openSession()) {
            return session.createNativeQuery("SELECT * FROM pokemon ORDER BY id", Pokemon.class).list();
        }
    }

    public void modificarSQL(int id, String nome, LocalDate nacemento) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createNativeMutationQuery("UPDATE pokemon SET nome = :nome, nacemento = :nac WHERE id = :id")
                    .setParameter("nome", nome)
                    .setParameter("nac", nacemento)
                    .setParameter("id", id)
                    .executeUpdate();
            tx.commit();
        }
    }

    public void eliminarTodosSQL() {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createNativeMutationQuery("DELETE FROM pokemon").executeUpdate();
            tx.commit();
        }
    }
}
