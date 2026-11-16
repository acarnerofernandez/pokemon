package org.example;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Main3 {
    public static void main(String[] args) {
        try (SessionFactory factory = new Configuration().configure().buildSessionFactory()) {

            PokedexDAO pokedexDAO = new PokedexDAO(factory);
            AdestradorDAO adestradorDAO = new AdestradorDAO(factory);
            PokemonDAO pokemonDAO = new PokemonDAO(factory);

            executarBloqueObxectos(pokedexDAO, adestradorDAO, pokemonDAO);
            executarBloqueSQL(pokedexDAO, adestradorDAO, pokemonDAO);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void executarBloqueObxectos(PokedexDAO pokedexDAO, AdestradorDAO adestradorDAO, PokemonDAO pokemonDAO) {
        System.out.println("============================================================");
        System.out.println("Secuencia executada con obxectos e métodos hibernate:");
        System.out.println("============================================================");

        // 1. Inserir 10 pokemons na pokedex
        for (int i = 1; i <= 10; i++) {
            pokedexDAO.crearObjeto(new Pokedex("DexObj_" + i, new BigDecimal(5 + i), "Desc " + i));
        }
        List<Pokedex> dexList = pokedexDAO.listarTodosHQL();

        // 2. Inserir 2 adestradores
        Adestrador ad1 = new Adestrador("Ash Ketchum", LocalDate.of(1997, 4, 1));
        Adestrador ad2 = new Adestrador("Misty", LocalDate.of(1998, 7, 15));
        adestradorDAO.crearObjeto(ad1);
        adestradorDAO.crearObjeto(ad2);

        // 3. Inserir 12 pokemons (6 para cada adestrador)
        for (int i = 1; i <= 6; i++) {
            pokemonDAO.crearObjeto(new Pokemon("PokeAsh_" + i, LocalDate.now(), dexList.get(i - 1), ad1));
            pokemonDAO.crearObjeto(new Pokemon("PokeMisty_" + i, LocalDate.now(), dexList.get(i + 2), ad2));
        }

        // 4. Listar as 3 táboas
        System.out.println("\n--- Listaxe inicial (Obxectos) ---");
        dexList.forEach(System.out::println);
        adestradorDAO.listarTodosHQL().forEach(System.out::println);
        List<Pokemon> pokeList = pokemonDAO.listarTodosSQL();
        pokeList.forEach(System.out::println);

        // 5. Exportar 2 entradas da pokedex serializadas e 2 adestradores a XML
        pokedexDAO.exportarSerializado(List.of(dexList.get(0), dexList.get(1)), "parte3_pokedex_obj.dat");
        adestradorDAO.exportarXML(List.of(ad1, ad2), "parte3_adestradores_obj.xml");

        // 6. Modificar 2 entradas na pokedex, 2 adestradores e 4 pokemons
        Pokedex p1 = pokedexDAO.lerPorIdObjeto(dexList.get(0).getId());
        p1.setNome("MOD_OBJ_1");
        pokedexDAO.modificarObjeto(p1);

        Pokedex p2 = pokedexDAO.lerPorIdObjeto(dexList.get(1).getId());
        p2.setNome("MOD_OBJ_2");
        pokedexDAO.modificarObjeto(p2);

        Adestrador a1 = adestradorDAO.lerPorIdObjeto(ad1.getId());
        a1.setNome("Ash Alterado");
        adestradorDAO.modificarObjeto(a1);

        Adestrador a2 = adestradorDAO.lerPorIdObjeto(ad2.getId());
        a2.setNome("Misty Alterada");
        adestradorDAO.modificarObjeto(a2);

        for (int i = 0; i < 4; i++) {
            Pokemon pk = pokemonDAO.lerPorIdObjeto(pokeList.get(i).getId());
            pk.setNome(pk.getNome() + "_EDIT");
            pokemonDAO.modificarObjeto(pk);
        }

        // 7. Listar tras modificacións
        System.out.println("\n--- Listaxe tras modificacións (Obxectos) ---");
        pokedexDAO.listarTodosHQL().forEach(System.out::println);
        adestradorDAO.listarTodosHQL().forEach(System.out::println);
        pokemonDAO.listarTodosSQL().forEach(System.out::println);

        // 8. Importar serializado e XML para restaurar
        pokedexDAO.importarSerializado("parte3_pokedex_obj.dat").forEach(pokedexDAO::modificarObjeto);
        adestradorDAO.importarXML("parte3_adestradores_obj.xml").forEach(adestradorDAO::modificarObjeto);

        // 9. Listar datos restaurados
        System.out.println("\n--- Listaxe final tras restauración (Obxectos) ---");
        pokedexDAO.listarTodosHQL().forEach(System.out::println);
        adestradorDAO.listarTodosHQL().forEach(System.out::println);

        // 10. Eliminar tódolos datos respectando FKs
        pokemonDAO.eliminarTodosObjetos();
        adestradorDAO.eliminarTodosObjetos();
        pokedexDAO.eliminarTodosObjetos();
        System.out.println("\nTáboas baleiras tras bloque Obxectos.\n");
    }

    private static void executarBloqueSQL(PokedexDAO pokedexDAO, AdestradorDAO adestradorDAO, PokemonDAO pokemonDAO) {
        System.out.println("============================================================");
        System.out.println("Secuencia executada coas queries especificadas:");
        System.out.println("============================================================");

        // 1. Inserir 10 pokemons na pokedex
        for (int i = 1; i <= 10; i++) {
            pokedexDAO.crearSQL("DexSQL_" + i, new BigDecimal(10 + i), "SQL " + i);
        }
        List<Pokedex> dexSQL = pokedexDAO.listarTodosSQL();

        // 2. Inserir 2 adestradores
        adestradorDAO.crearSQL("Brock", LocalDate.of(1995, 5, 10));
        adestradorDAO.crearSQL("Gary Oak", LocalDate.of(1997, 4, 2));
        List<Adestrador> adSQL = adestradorDAO.listarTodosSQL();

        // 3. Inserir 12 pokemons (6 para cada adestrador)
        for (int i = 1; i <= 6; i++) {
            pokemonDAO.crearSQL("PokeBrock_" + i, LocalDate.now(), dexSQL.get(i - 1).getId(), adSQL.get(0).getId());
            pokemonDAO.crearSQL("PokeGary_" + i, LocalDate.now(), dexSQL.get(i + 2).getId(), adSQL.get(1).getId());
        }

        // 4. Listar as 3 táboas
        System.out.println("\n--- Listaxe inicial (Queries SQL) ---");
        dexSQL.forEach(System.out::println);
        adSQL.forEach(System.out::println);
        List<Pokemon> pokeSQL = pokemonDAO.listarTodosSQL();
        pokeSQL.forEach(System.out::println);

        // 5. Exportar 2 entradas da pokedex serializadas e 2 adestradores a XML
        pokedexDAO.exportarSerializado(List.of(dexSQL.get(0), dexSQL.get(1)), "parte3_pokedex_sql.dat");
        adestradorDAO.exportarXML(List.of(adSQL.get(0), adSQL.get(1)), "parte3_adestradores_sql.xml");

        // 6. Modificar 2 entradas na pokedex, 2 adestradores e 4 pokemons
        pokedexDAO.modificarSQL(dexSQL.get(0).getId(), "MOD_SQL_1", new BigDecimal("88.88"), "Mod 1");
        pokedexDAO.modificarSQL(dexSQL.get(1).getId(), "MOD_SQL_2", new BigDecimal("77.77"), "Mod 2");

        adestradorDAO.modificarSQL(adSQL.get(0).getId(), "Brock Alterado", LocalDate.of(1990, 1, 1));
        adestradorDAO.modificarSQL(adSQL.get(1).getId(), "Gary Alterado", LocalDate.of(1990, 2, 2));

        for (int i = 0; i < 4; i++) {
            Pokemon pk = pokeSQL.get(i);
            pokemonDAO.modificarSQL(pk.getId(), pk.getNome() + "_SQL_EDIT", LocalDate.of(2020, 1, 1));
        }

        // 7. Listar tras modificacións
        System.out.println("\n--- Listaxe tras modificacións (Queries SQL) ---");
        pokedexDAO.listarTodosSQL().forEach(System.out::println);
        adestradorDAO.listarTodosSQL().forEach(System.out::println);
        pokemonDAO.listarTodosSQL().forEach(System.out::println);

        // 8. Importar serializado e XML para restaurar usando SQL
        for (Pokedex p : pokedexDAO.importarSerializado("parte3_pokedex_sql.dat")) {
            pokedexDAO.modificarSQL(p.getId(), p.getNome(), p.getPeso(), p.getMisc());
        }
        for (Adestrador a : adestradorDAO.importarXML("parte3_adestradores_sql.xml")) {
            adestradorDAO.modificarSQL(a.getId(), a.getNome(), a.getNacemento());
        }

        // 9. Listar datos restaurados
        System.out.println("\n--- Listaxe final tras restauración (Queries SQL) ---");
        pokedexDAO.listarTodosSQL().forEach(System.out::println);
        adestradorDAO.listarTodosSQL().forEach(System.out::println);

        // 10. Eliminar tódolos datos respectando FKs
        pokemonDAO.eliminarTodosSQL();
        adestradorDAO.eliminarTodosSQL();
        pokedexDAO.eliminarTodosSQL();
        System.out.println("\nTáboas baleiras tras bloque SQL.");
    }
}
