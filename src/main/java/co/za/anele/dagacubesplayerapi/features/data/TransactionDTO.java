package co.za.anele.dagacubesplayerapi.features.data;

import co.za.anele.dagacubesplayerapi.domain.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.BigInteger;

@AllArgsConstructor
@NoArgsConstructor(force = true)
@ToString
@Getter
public class TransactionDTO {

    private final BigInteger transactionId;
    private final TransactionType transactionType;
    private final BigDecimal amount;
}
