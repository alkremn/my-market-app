package co.kremnev.mymarket.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sessionId;

    @OneToMany(mappedBy = "cartitem")
    private List<Item> cartItems;
}
