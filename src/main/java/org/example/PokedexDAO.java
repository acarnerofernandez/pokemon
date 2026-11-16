package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.math.BigDecimal;
import java.util.List;

public class PokedexDAO {

    private final SessionFactory factory;

    public PokedexDAO(SessionFactory factory) {
        this.factory = factory;
    }

    // --- 1. MÉTODOS CON OBXECTOS HIBERNATE ---
    public void crearObjeto(Pokedex p) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(p);
            tx.commit();
        }
    }

    public Pokedex lerPorIdObjeto(int id) {
        try (Session session = factory.openSession()) {
            return session.get(Pokedex.class, id);
        }
    }

    public void modificarObjeto(Pokedex p) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(p);
            tx.commit();
        }
    }

    public void eliminarTodosObjetos() {
        List<Pokedex> todos = listarTodosHQL();
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            for (Pokedex p : todos) {
                Pokedex managed = session.get(Pokedex.class, p.getId());
                if (managed != null) session.remove(managed);
            }
            tx.commit();
        }
    }

    // --- 2. MÉTODOS CON HQL (Parte 2) ---
    public List<Pokedex> listarTodosHQL() {
        try (Session session = factory.openSession()) {
            return session.createQuery("FROM Pokedex ORDER BY id", Pokedex.class).list();
        }
    }

    public void modificarHQL(int id, String nome, BigDecimal peso, String misc) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createMutationQuery("UPDATE Pokedex SET nome = :nome, peso = :peso, misc = :misc WHERE id = :id")
                    .setParameter("nome", nome)
                    .setParameter("peso", peso)
                    .setParameter("misc", misc)
                    .setParameter("id", id)
                    .executeUpdate();
            tx.commit();
        }
    }

    public void eliminarTodosHQL() {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createMutationQuery("DELETE FROM Pokedex").executeUpdate();
            tx.commit();
        }
    }

    // --- 3. MÉTODOS CON SQL NATIVO (Parte 3) ---
    public void crearSQL(String nome, BigDecimal peso, String misc) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createNativeMutationQuery("INSERT INTO pokedex (nome, peso, misc) VALUES (:nome, :peso, :misc)")
                    .setParameter("nome", nome)
                    .setParameter("peso", peso)
                    .setParameter("misc", misc)
                    .executeUpdate();
            tx.commit();
        }
    }

    public List<Pokedex> listarTodosSQL() {
        try (Session session = factory.openSession()) {
            return session.createNativeQuery("SELECT * FROM pokedex ORDER BY id", Pokedex.class).list();
        }
    }

    public void modificarSQL(int id, String nome, BigDecimal peso, String misc) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createNativeMutationQuery("UPDATE pokedex SET nome = :nome, peso = :peso, misc = :misc WHERE id = :id")
                    .setParameter("nome", nome)
                    .setParameter("peso", peso)
                    .setParameter("misc", misc)
                    .setParameter("id", id)
                    .executeUpdate();
            tx.commit();
        }
    }

    public void eliminarTodosSQL() {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createNativeMutationQuery("DELETE FROM pokedex").executeUpdate();
            tx.commit();
        }
    }

    // --- 4. EXPORTACIÓN XML (Parte 2) ---
    public void toXML(String rutaArquivo) {
        List<Pokedex> lista = listarTodosHQL();
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = doc.createElement("pokedex");
            doc.appendChild(root);

            for (Pokedex p : lista) {
                Element el = doc.createElement("pokemon");
                el.setAttribute("id", String.valueOf(p.getId()));

                Element nome = doc.createElement("nome");
                nome.setTextContent(p.getNome());
                el.appendChild(nome);

                Element peso = doc.createElement("peso");
                peso.setTextContent(p.getPeso() != null ? p.getPeso().toString() : "");
                el.appendChild(peso);

                Element misc = doc.createElement("misc");
                misc.setTextContent(p.getMisc() != null ? p.getMisc() : "");
                el.appendChild(misc);

                root.appendChild(el);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(new File(rutaArquivo)));
            System.out.println("XML de Pokedex xerado en: " + rutaArquivo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- 5. SERIALIZACIÓN E DESERIALIZACIÓN (Parte 3) ---
    public void exportarSerializado(List<Pokedex> lista, String rutaArquivo) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(rutaArquivo))) {
            oos.writeObject(lista);
            System.out.println("Entradas da Pokedex serializadas en: " + rutaArquivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Pokedex> importarSerializado(String rutaArquivo) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(rutaArquivo))) {
            return (List<Pokedex>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
