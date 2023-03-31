package co.za.anele.dagacubesplayerapi.features.exceptions;

import lombok.Getter;

@Getter
public class InvalidTransactionAmount extends RuntimeException {
    private static final long serialVersionUID = 6937283977053467334L;
    private final String message;

    public InvalidTransactionAmount(String message) {
        super(message);
        this.message = message;
    }
}
