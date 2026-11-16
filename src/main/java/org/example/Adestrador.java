package org.example;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "adestrador")
public class Adestrador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nome", length = 50, nullable = false)
    private String nome;

    @Column(name = "nacemento")
    private LocalDate nacemento;

    public Adestrador() {}

    public Adestrador(String nome, LocalDate nacemento) {
        this.nome = nome;
        this.nacemento = nacemento;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDate getNacemento() { return nacemento; }
    public void setNacemento(LocalDate nacemento) { this.nacemento = nacemento; }
}
