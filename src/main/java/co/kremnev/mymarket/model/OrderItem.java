package co.kremnev.mymarket.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    private int  quantity;

    @Column(insertable = false, updatable = false)
    private Date createdAt;
    @Column(insertable = false)
    private Date updatedAt;

    public OrderItem() {}

    public OrderItem(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public Item getItem() { return item; }
    public Long getId() { return id; }
    public Order getOrder() { return order; }
    public int getQuantity() { return quantity; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdateAt() { return updatedAt; }

    public void setOrder(Order order) { this.order = order; }
}
