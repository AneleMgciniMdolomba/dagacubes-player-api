package co.za.anele.dagacubesplayerapi.features.data;

import co.za.anele.dagacubesplayerapi.domain.TransactionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor(force = true)
@ToString
@EqualsAndHashCode
@Data
@Slf4j
public class PlayerTransactionInputDTO {

    private final BigDecimal amount;
    private final TransactionType transactionType;

    @JsonIgnore
    public boolean isNotValid() {
        return (amount != null && amount.signum() < 0);
    }
}
