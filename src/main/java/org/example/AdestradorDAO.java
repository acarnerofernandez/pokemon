package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AdestradorDAO {

    private final SessionFactory factory;

    public AdestradorDAO(SessionFactory factory) {
        this.factory = factory;
    }

    // --- 1. MÉTODOS CON OBXECTOS HIBERNATE ---
    public void crearObjeto(Adestrador a) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(a);
            tx.commit();
        }
    }

    public Adestrador lerPorIdObjeto(int id) {
        try (Session session = factory.openSession()) {
            return session.get(Adestrador.class, id);
        }
    }

    public void modificarObjeto(Adestrador a) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(a);
            tx.commit();
        }
    }

    public void eliminarTodosObjetos() {
        List<Adestrador> todos = listarTodosHQL();
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            for (Adestrador a : todos) {
                Adestrador managed = session.get(Adestrador.class, a.getId());
                if (managed != null) session.remove(managed);
            }
            tx.commit();
        }
    }

    // --- 2. MÉTODOS CON HQL (Parte 2) ---
    public List<Adestrador> listarTodosHQL() {
        try (Session session = factory.openSession()) {
            return session.createQuery("FROM Adestrador ORDER BY id", Adestrador.class).list();
        }
    }

    public void modificarHQL(int id, String nome, LocalDate nacemento) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createMutationQuery("UPDATE Adestrador SET nome = :nome, nacemento = :nac WHERE id = :id")
                    .setParameter("nome", nome)
                    .setParameter("nac", nacemento)
                    .setParameter("id", id)
                    .executeUpdate();
            tx.commit();
        }
    }

    public void eliminarTodosHQL() {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createMutationQuery("DELETE FROM Adestrador").executeUpdate();
            tx.commit();
        }
    }

    // --- 3. MÉTODOS CON SQL NATIVO (Parte 3) ---
    public void crearSQL(String nome, LocalDate nacemento) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createNativeMutationQuery("INSERT INTO adestrador (nome, nacemento) VALUES (:nome, :nac)")
                    .setParameter("nome", nome)
                    .setParameter("nac", nacemento)
                    .executeUpdate();
            tx.commit();
        }
    }

    public List<Adestrador> listarTodosSQL() {
        try (Session session = factory.openSession()) {
            return session.createNativeQuery("SELECT * FROM adestrador ORDER BY id", Adestrador.class).list();
        }
    }

    public void modificarSQL(int id, String nome, LocalDate nacemento) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createNativeMutationQuery("UPDATE adestrador SET nome = :nome, nacemento = :nac WHERE id = :id")
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
            session.createNativeMutationQuery("DELETE FROM adestrador").executeUpdate();
            tx.commit();
        }
    }

    // --- 4. EXPORTAR E IMPORTAR XML (Partes 2 e 3) ---
    public void toXML(String rutaArquivo) {
        exportarXML(listarTodosHQL(), rutaArquivo);
    }

    public void exportarXML(List<Adestrador> lista, String rutaArquivo) {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = doc.createElement("adestradores");
            doc.appendChild(root);

            for (Adestrador a : lista) {
                Element el = doc.createElement("adestrador");
                el.setAttribute("id", String.valueOf(a.getId()));

                Element nome = doc.createElement("nome");
                nome.setTextContent(a.getNome());
                el.appendChild(nome);

                Element nac = doc.createElement("nacemento");
                nac.setTextContent(a.getNacemento().toString());
                el.appendChild(nac);

                root.appendChild(el);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(new File(rutaArquivo)));
            System.out.println("XML de Adestradores xerado en: " + rutaArquivo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Adestrador> importarXML(String rutaArquivo) {
        List<Adestrador> resultado = new ArrayList<>();
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(rutaArquivo));
            doc.getDocumentElement().normalize();

            NodeList nodos = doc.getElementsByTagName("adestrador");
            for (int i = 0; i < nodos.getLength(); i++) {
                Element el = (Element) nodos.item(i);
                int id = Integer.parseInt(el.getAttribute("id"));
                String nome = el.getElementsByTagName("nome").item(0).getTextContent();
                LocalDate nac = LocalDate.parse(el.getElementsByTagName("nacemento").item(0).getTextContent());

                Adestrador a = new Adestrador(nome, nac);
                a.setId(id);
                resultado.add(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultado;
    }
}
