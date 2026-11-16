package org.example;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "pokedex")
public class Pokedex implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nome", length = 50, nullable = false)
    private String nome;

    @Column(name = "peso", precision = 10, scale = 2)
    private BigDecimal peso;

    @Column(name = "misc", columnDefinition = "TEXT")
    private String misc;

    public Pokedex() {}

    public Pokedex(String nome, BigDecimal peso, String misc) {
        this.nome = nome;
        this.peso = peso;
        this.misc = misc;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public BigDecimal getPeso() { return peso; }
    public void setPeso(BigDecimal peso) { this.peso = peso; }
    public String getMisc() { return misc; }
    public void setMisc(String misc) { this.misc = misc; }

    @Override
    public String toString() {
        return "Pokedex{id=" + id + ", nome='" + nome + "', peso=" + peso + ", misc='" + misc + "'}";
    }
}
