package co.za.anele.dagacubesplayerapi.features.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
public class ProblemDTO {

    private final int status;
    private final String message;
}
