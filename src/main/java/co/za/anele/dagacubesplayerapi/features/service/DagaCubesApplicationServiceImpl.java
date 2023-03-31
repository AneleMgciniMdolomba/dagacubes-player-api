package co.za.anele.dagacubesplayerapi.features.service;

import co.za.anele.dagacubesplayerapi.domain.Balance;
import co.za.anele.dagacubesplayerapi.domain.Player;
import co.za.anele.dagacubesplayerapi.domain.Transaction;
import co.za.anele.dagacubesplayerapi.domain.TransactionType;
import co.za.anele.dagacubesplayerapi.features.DagaCubesApplicationService;
import co.za.anele.dagacubesplayerapi.features.data.*;
import co.za.anele.dagacubesplayerapi.features.exceptions.InvalidPlayerIdException;
import co.za.anele.dagacubesplayerapi.features.exceptions.InvalidTransactionAmount;
import co.za.anele.dagacubesplayerapi.features.exceptions.WagerGreaterThanBalanceException;
import co.za.anele.dagacubesplayerapi.repository.PlayerJpaRepository;
import co.za.anele.dagacubesplayerapi.repository.TransactionsJpaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DagaCubesApplicationServiceImpl implements DagaCubesApplicationService {

    private final PlayerJpaRepository playerJpaRepository;
    private final TransactionsJpaRepository transactionsJpaRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BalanceDTO getPlayerBalance(final Long playerId) throws InvalidPlayerIdException {
        return this.playerJpaRepository.findById(playerId).map(player -> {
            BalanceDTO balance = new BalanceDTO();
            balance.setPlayerId(player.getId().intValue());
            balance.setAmount(player.getBalance().getAmount());

            return balance;
        }).orElseThrow(() -> new InvalidPlayerIdException("Invalid Player Id."));
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, noRollbackFor = {WagerGreaterThanBalanceException.class, InvalidTransactionAmount.class})
    public synchronized PlayerTransactionBalanceDTO performTransaction(final Long playerId, PlayerTransactionInputDTO transactionInputDTO) throws InvalidTransactionAmount, WagerGreaterThanBalanceException {
        // Input validation if missed at rest entrypoint level
        if (transactionInputDTO.isNotValid()) {
            throw new InvalidTransactionAmount("Cannot update player balance with a negative amount value.");
        }

        LocalDateTime transactionTime = LocalDateTime.now();
        BigDecimal amount = transactionInputDTO.getAmount();
        TransactionType transactionType = transactionInputDTO.getTransactionType();

        Player player = playerJpaRepository.findById(playerId, LockModeType.PESSIMISTIC_WRITE).orElseThrow(() -> new InvalidPlayerIdException("Invalid Player Id."));
        Balance balance = player.getBalance();

        // Validate input amount for wager against latest DB player balance information
        if (transactionType == TransactionType.WAGER) {
            if (amount.compareTo(balance.getAmount()) > 0) {
                throw new WagerGreaterThanBalanceException("Wager amount cannot be greater than current player balance.");
            }
        }

        // Create Transaction
        Transaction transaction = new Transaction();
        transaction.setTransactionDateTime(transactionTime);
        transaction.setAmount(amount);
        transaction.setTransactionType(transactionType);
        transaction.setPlayer(player);

        // Save Transaction, set to player and update balances
        transaction = transactionsJpaRepository.save(transaction);
        player.transactionPerformed(transaction);
        playerJpaRepository.save(player);

        return new PlayerTransactionBalanceDTO(BigInteger.valueOf(transaction.getId()), player.getBalance().getAmount());
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, noRollbackFor = {WagerGreaterThanBalanceException.class, InvalidTransactionAmount.class})
    public TransactionsDTO loadPlayerTransactions(PlayerTransactionsDTO playerTransactionsDTO) throws InvalidTransactionAmount {
        List<TransactionDTO> transactionDTOS = this.playerJpaRepository.findByUsername(playerTransactionsDTO.getUsername()).map(player -> {
            // Not going to be fast performance wise, limiting rows should be at least at DB level to instead of return whole player collection and limit by 10
            List<Transaction> collect = player.getTransactions().stream()
                    .sorted((o1, o2) -> o2.getId().compareTo(o1.getId()))
                    .limit(10).collect(Collectors.toList());

            List<TransactionDTO> results;
            results = collect.stream().map(x -> new TransactionDTO(BigInteger.valueOf(x.getId()), x.getTransactionType(), x.getAmount())).collect(Collectors.toList());

            return results;
        }).orElseThrow(() -> new InvalidPlayerIdException("Invalid username."));
        return new TransactionsDTO(transactionDTOS);
    }

}
