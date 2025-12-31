package co.kremnev.mymarket.model;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private List<Item> items;

    private Date createdAt;
    private Date updatedAt;


    public Long getId() { return id;}

}
