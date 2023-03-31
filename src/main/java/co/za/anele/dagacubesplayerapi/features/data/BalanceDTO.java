package co.za.anele.dagacubesplayerapi.features.data;

import lombok.*;

import java.math.BigDecimal;
import java.util.Currency;

@AllArgsConstructor
@NoArgsConstructor(force = true)
@ToString
@EqualsAndHashCode
@Data
public class BalanceDTO {

    private int playerId;
    private BigDecimal amount;
}
