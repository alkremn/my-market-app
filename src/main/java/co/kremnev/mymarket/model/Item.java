package co.kremnev.mymarket.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
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
    private BigDecimal price;

    @Column(insertable = false, updatable = false)
    private Date createdAt;
    @Column(insertable = false)
    private Date updatedAt;

    protected Item() {}

    // Getters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImgPath() {
        return imgPath;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static class Builder {
        private final Item item = new Item();

        public Builder id(Long id) {
            item.setId(id);
            return this;
        }

        public Builder title(String title) {
            item.setTitle(title);
            return this;
        }

        public Builder description(String description) {
            item.setDescription(description);
            return this;
        }

        public Builder imgPath(String imgPath) {
            item.setImgPath(imgPath);
            return this;
        }

        public Builder price(BigDecimal price) {
            item.setPrice(price);
            return this;
        }

        public Item build() {
            return item;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
