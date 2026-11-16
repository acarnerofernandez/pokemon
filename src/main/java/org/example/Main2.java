package org.example;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Main2 {
    public static void main(String[] args) {
        try (SessionFactory factory = new Configuration().configure().buildSessionFactory()) {

            PokedexDAO pokedexDAO = new PokedexDAO(factory);
            AdestradorDAO adestradorDAO = new AdestradorDAO(factory);

            System.out.println("============================================================");
            System.out.println("PARTE 2: HQL E EXPORTACIÓN XML COMPLETA");
            System.out.println("============================================================");

            // 1. Inserir 10 pokemons e 2 adestradores
            for (int i = 1; i <= 10; i++) {
                pokedexDAO.crearObjeto(new Pokedex("PokeP2_" + i, new BigDecimal(5 + i), "Desc P2 " + i));
            }
            Adestrador a1 = new Adestrador("Ash P2", LocalDate.of(1997, 4, 1));
            Adestrador a2 = new Adestrador("Misty P2", LocalDate.of(1998, 7, 15));
            adestradorDAO.crearObjeto(a1);
            adestradorDAO.crearObjeto(a2);

            // 2. Listar inicial
            System.out.println("\n--- Listaxe inicial ---");
            List<Pokedex> dexList = pokedexDAO.listarTodosHQL();
            dexList.forEach(System.out::println);
            adestradorDAO.listarTodosHQL().forEach(System.out::println);

            // 3. Modificar 2 adestradores e 2 entradas pokedex
            Adestrador aMod = adestradorDAO.lerPorIdObjeto(a1.getId());
            aMod.setNome("Ash P2 Modificado");
            adestradorDAO.modificarObjeto(aMod);
            adestradorDAO.modificarHQL(a2.getId(), "Misty P2 HQL", LocalDate.of(1998, 7, 20));

            Pokedex pMod = pokedexDAO.lerPorIdObjeto(dexList.get(0).getId());
            pMod.setNome("PokeP2_1_Mod");
            pokedexDAO.modificarObjeto(pMod);
            pokedexDAO.modificarHQL(dexList.get(1).getId(), "PokeP2_2_HQL", new BigDecimal("12.50"), "Mod HQL");

            // 4. Listar modificado
            System.out.println("\n--- Listaxe tras modificacións ---");
            pokedexDAO.listarTodosHQL().forEach(System.out::println);
            adestradorDAO.listarTodosHQL().forEach(System.out::println);

            // 5. Exportar a XML (entre Listar e Eliminar)
            System.out.println("\n--- Exportando a XML ---");
            pokedexDAO.toXML("parte2_pokedex.xml");
            adestradorDAO.toXML("parte2_adestradores.xml");

            // 6. Eliminar tódolos datos
            pokedexDAO.eliminarTodosHQL();
            adestradorDAO.eliminarTodosHQL();
            System.out.println("\nParte 2 rematada e táboas baleiras.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
