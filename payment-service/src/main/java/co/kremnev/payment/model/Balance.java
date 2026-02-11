package co.kremnev.payment.model;

import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("balances")
public class Balance implements Persistable<Long> {
    @Id
    @Column("user_id")
    private Long userId;
    private BigDecimal balance;

    @Transient
    private boolean isNew = false;

    public Balance() {}

    public Balance(Long userId, BigDecimal balance) {
        this.userId = userId;
        this.balance = balance;
        this.isNew = true;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public @Nullable Long getId() {
        return userId;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
