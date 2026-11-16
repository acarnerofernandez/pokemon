package org.example;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pokemon")
public class Pokemon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nome", length = 50, nullable = false)
    private String nome;

    @Column(name = "nacemento")
    private LocalDate nacemento;

    @ManyToOne
    @JoinColumn(name = "pokedexentry", referencedColumnName = "id")
    private Pokedex pokedexEntry;

    @ManyToOne
    @JoinColumn(name = "adestrador", referencedColumnName = "id")
    private Adestrador adestrador;

    public Pokemon() {}

    public Pokemon(String nome, LocalDate nacemento, Pokedex pokedexEntry, Adestrador adestrador) {
        this.nome = nome;
        this.nacemento = nacemento;
        this.pokedexEntry = pokedexEntry;
        this.adestrador = adestrador;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDate getNacemento() { return nacemento; }
    public void setNacemento(LocalDate nacemento) { this.nacemento = nacemento; }
    public Pokedex getPokedexEntry() { return pokedexEntry; }
    public void setPokedexEntry(Pokedex pokedexEntry) { this.pokedexEntry = pokedexEntry; }
    public Adestrador getAdestrador() { return adestrador; }
    public void setAdestrador(Adestrador adestrador) { this.adestrador = adestrador; }
}
