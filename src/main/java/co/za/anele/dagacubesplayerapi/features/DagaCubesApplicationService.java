package co.za.anele.dagacubesplayerapi.features;

import co.za.anele.dagacubesplayerapi.features.data.*;
import co.za.anele.dagacubesplayerapi.features.exceptions.InvalidPlayerIdException;
import co.za.anele.dagacubesplayerapi.features.exceptions.InvalidTransactionAmount;
import co.za.anele.dagacubesplayerapi.features.exceptions.WagerGreaterThanBalanceException;

public interface DagaCubesApplicationService {

    BalanceDTO getPlayerBalance(final Long id) throws InvalidPlayerIdException;

    PlayerTransactionBalanceDTO performTransaction(final Long playerId, final PlayerTransactionInputDTO transactionInputDTO) throws InvalidTransactionAmount, WagerGreaterThanBalanceException;

    TransactionsDTO loadPlayerTransactions(PlayerTransactionsDTO playerTransactionsDTO) throws InvalidTransactionAmount;
}
