package co.kremnev.mymarket.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String imgPath;
    private long price;
    private Date createdAt;
    private Date updatedAt;

    public Long id() { return id; }
    public String title() { return title; }
    public String description() { return description; }
    public String imgPath() { return imgPath; }
    public long price() { return price; }
    public Date createdAt() { return createdAt; }
    public Date updatedAt() { return updatedAt; }
}
