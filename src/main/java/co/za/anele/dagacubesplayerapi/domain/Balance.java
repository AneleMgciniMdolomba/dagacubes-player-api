package co.za.anele.dagacubesplayerapi.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Currency;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Balance {

    private BigDecimal amount;
    private Currency currency;

    public void add(final BigDecimal amount) {
        this.amount = this.amount.add(amount);
    }

    public void subtract(final BigDecimal amount) {
        this.amount = this.amount.subtract(amount);
    }
}
