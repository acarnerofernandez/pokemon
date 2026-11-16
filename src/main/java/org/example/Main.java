package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        try (SessionFactory factory = new Configuration().configure().buildSessionFactory();
             Session session = factory.openSession()) {

            session.beginTransaction();

            // 1. Crear entrada en Pokedex
            Pokedex pikachuDex = new Pokedex("Pikachu", new BigDecimal("6.00"), "Ratón eléctrico");
            session.persist(pikachuDex);

            // 2. Crear Adestrador
            Adestrador ash = new Adestrador("Ash Ketchum", LocalDate.of(1997, 4, 1));
            session.persist(ash);

            // 3. Crear Pokemon asociado a ambos
            Pokemon miPikachu = new Pokemon("Pika", LocalDate.now(), pikachuDex, ash);
            session.persist(miPikachu);

            session.getTransaction().commit();
            System.out.println("¡Todo guardado con éxito! ID del Pokémon: " + miPikachu.getId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
