package co.za.anele.dagacubesplayerapi.features.data;

import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;

@AllArgsConstructor
@NoArgsConstructor(force = true)
@ToString
@EqualsAndHashCode
@Data
public class PlayerTransactionBalanceDTO {

    private final BigInteger transactionId;
    private final BigDecimal balance;
}
