package co.za.anele.dagacubesplayerapi.features.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(force = true)
@ToString
@Getter
public class TransactionsDTO {

    private final List<TransactionDTO> transactions;
}
