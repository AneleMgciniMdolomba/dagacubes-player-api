package co.za.anele.dagacubesplayerapi.features.exceptions;

import lombok.Getter;

@Getter
public class InvalidPlayerIdException extends RuntimeException {
    private static final long serialVersionUID = -8216329733898793957L;
    private final String message;

    public InvalidPlayerIdException(String message) {
        super(message);
        this.message = message;
    }
}
