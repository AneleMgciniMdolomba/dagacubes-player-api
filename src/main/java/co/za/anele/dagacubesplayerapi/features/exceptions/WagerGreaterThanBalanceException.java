package co.za.anele.dagacubesplayerapi.features.exceptions;

import lombok.Getter;

@Getter
public class WagerGreaterThanBalanceException extends RuntimeException {
    private static final long serialVersionUID = -7408355218747794021L;
    private final String message;

    public WagerGreaterThanBalanceException(String message) {
        super(message);
        this.message = message;
    }
}
