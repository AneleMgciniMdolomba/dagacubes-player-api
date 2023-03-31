package co.za.anele.dagacubesplayerapi.features.data;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@ToString
public class PlayerTransactionsDTO {

    @NonNull
    private final String username;
}
