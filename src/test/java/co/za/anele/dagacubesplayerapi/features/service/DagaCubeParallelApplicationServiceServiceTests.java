package co.za.anele.dagacubesplayerapi.features.service;

import co.za.anele.dagacubesplayerapi.domain.Player;
import co.za.anele.dagacubesplayerapi.domain.TransactionType;
import co.za.anele.dagacubesplayerapi.features.DagaCubesAbstractSpringBootApplicationServiceTests;
import co.za.anele.dagacubesplayerapi.features.DagaCubesApplicationService;
import co.za.anele.dagacubesplayerapi.features.data.PlayerTransactionBalanceDTO;
import co.za.anele.dagacubesplayerapi.features.data.PlayerTransactionInputDTO;
import co.za.anele.dagacubesplayerapi.features.exceptions.WagerGreaterThanBalanceException;
import co.za.anele.dagacubesplayerapi.repository.PlayerJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DagaCubeParallelApplicationServiceServiceTests extends DagaCubesAbstractSpringBootApplicationServiceTests {

    @Autowired
    private PlayerJpaRepository playerJpaRepository;

    @Autowired
    private DagaCubesApplicationService dagaCubesApplicationService;

    private Player getLatestPlayerFromRepo() {
        Optional<Player> loadedPlayer = this.playerJpaRepository.findByUsername(USERNAME);

        if (!loadedPlayer.isPresent()) {
            fail("Player should be loaded initial. Please review @BeforeAll init method.");
        }

        return loadedPlayer.get();
    }

    @Test
    @Transactional
    void first_player_transaction() throws WagerGreaterThanBalanceException {
        try {
            System.out.println("DagaCubeParallelApplicationServiceServiceTests first_player_transaction() start => " + Thread.currentThread().getName());
            double wager = 25.11;
            BigDecimal amount = new BigDecimal(wager).setScale(2, MATH_CONTEXT.getRoundingMode());
            PlayerTransactionInputDTO inputTransaction = new PlayerTransactionInputDTO(/*currencyDTO, */amount, TransactionType.WAGER);

            PlayerTransactionBalanceDTO playerTransactionBalanceDTO = this.dagaCubesApplicationService.performTransaction(playerId, inputTransaction);
            log.info("Thread 1 Response -> {}", playerTransactionBalanceDTO.toString());
            log.info("Players Balance @FIRST {}. Transactions {}", getLatestPlayerFromRepo().getBalance().getAmount(), getLatestPlayerFromRepo().getTransactions().size());

            System.out.println("DagaCubeParallelApplicationServiceServiceTests first_player_transaction() end => " + Thread.currentThread().getName());

        } catch (WagerGreaterThanBalanceException exception) {
            log.error("Surrounded all threads as some will fail -> {}", exception.getMessage());
        }
    }

    @Test
    @Transactional
    void second_player_transaction() throws WagerGreaterThanBalanceException {
        try {

            System.out.println("DagaCubeParallelApplicationServiceServiceTests second_player_transaction() start => " + Thread.currentThread().getName());
            double wager = 32.02;
            BigDecimal amount = new BigDecimal(wager).setScale(2, MATH_CONTEXT.getRoundingMode());
            PlayerTransactionInputDTO inputTransaction = new PlayerTransactionInputDTO(/*currencyDTO, */amount, TransactionType.WAGER);

            PlayerTransactionBalanceDTO playerTransactionBalanceDTO = this.dagaCubesApplicationService.performTransaction(playerId, inputTransaction);
            log.info("Thread 2 Response -> {}", playerTransactionBalanceDTO.toString());
            log.info("Players Balance @SECOND {}. Transactions {}", getLatestPlayerFromRepo().getBalance().getAmount(), getLatestPlayerFromRepo().getTransactions().size());

            System.out.println("DagaCubeParallelApplicationServiceServiceTests second_player_transaction() end => " + Thread.currentThread().getName());
        } catch (WagerGreaterThanBalanceException exception) {
            log.error("Surrounded all threads as some will fail -> {}", exception.getMessage());
        }
    }

    @Test
    @Transactional
    void third_player_transaction() throws WagerGreaterThanBalanceException {
        try {

            System.out.println("DagaCubeParallelApplicationServiceServiceTests third_player_transaction() start => " + Thread.currentThread().getName());
            double wager = 44.63;
            BigDecimal amount = new BigDecimal(wager).setScale(2, MATH_CONTEXT.getRoundingMode());
            PlayerTransactionInputDTO inputTransaction = new PlayerTransactionInputDTO(/*currencyDTO, */amount, TransactionType.WAGER);

            PlayerTransactionBalanceDTO playerTransactionBalanceDTO = this.dagaCubesApplicationService.performTransaction(playerId, inputTransaction);
            log.info("Thread 3 Response -> {}", playerTransactionBalanceDTO.toString());
            log.info("Players Balance @THIRD {}. Transactions {}", getLatestPlayerFromRepo().getBalance().getAmount(), getLatestPlayerFromRepo().getTransactions().size());

            System.out.println("DagaCubeParallelApplicationServiceServiceTests third_player_transaction() end => " + Thread.currentThread().getName());
        } catch (WagerGreaterThanBalanceException exception) {
            log.error("Surrounded all threads as some will fail -> {}", exception.getMessage());
        }
    }

    @Test
    @Transactional
    void fourth_player_transaction() throws WagerGreaterThanBalanceException {
        try {

            System.out.println("DagaCubeParallelApplicationServiceServiceTests fourth_player_transaction() start => " + Thread.currentThread().getName());
            double wager = 64.63;
            BigDecimal amount = new BigDecimal(wager).setScale(2, MATH_CONTEXT.getRoundingMode());
            PlayerTransactionInputDTO inputTransaction = new PlayerTransactionInputDTO(/*currencyDTO, */amount, TransactionType.WAGER);

            PlayerTransactionBalanceDTO playerTransactionBalanceDTO = this.dagaCubesApplicationService.performTransaction(playerId, inputTransaction);
            log.info("Thread 4 Response -> {}", playerTransactionBalanceDTO.toString());
            log.info("Players Balance @FOURTH {}. Transactions {}", getLatestPlayerFromRepo().getBalance().getAmount(), getLatestPlayerFromRepo().getTransactions().size());

            System.out.println("DagaCubeParallelApplicationServiceServiceTests fourth_player_transaction() end => " + Thread.currentThread().getName());
        } catch (WagerGreaterThanBalanceException exception) {
            log.error("Surrounded all threads as some will fail -> {}", exception.getMessage());
        }
    }

    @Test
    @Transactional
    void fifth_player_transaction() throws WagerGreaterThanBalanceException {
        try {

            System.out.println("DagaCubeParallelApplicationServiceServiceTests fifth_player_transaction() start => " + Thread.currentThread().getName());
            double wager = 32.58;
            BigDecimal amount = new BigDecimal(wager).setScale(2, MATH_CONTEXT.getRoundingMode());
            PlayerTransactionInputDTO inputTransaction = new PlayerTransactionInputDTO(/*currencyDTO, */amount, TransactionType.WAGER);

            PlayerTransactionBalanceDTO playerTransactionBalanceDTO = this.dagaCubesApplicationService.performTransaction(playerId, inputTransaction);
            log.info("Thread 5 Response -> {}", playerTransactionBalanceDTO.toString());
            log.info("Players Balance @FIFTH {}. Transactions {}", getLatestPlayerFromRepo().getBalance().getAmount(), getLatestPlayerFromRepo().getTransactions().size());

            System.out.println("DagaCubeParallelApplicationServiceServiceTests fifth_player_transaction() end => " + Thread.currentThread().getName());
        } catch (WagerGreaterThanBalanceException exception) {
            log.error("Surrounded all threads as some will fail -> {}", exception.getMessage());
        }
    }

    @Test
    @Transactional
    void sixth_player_transaction() throws WagerGreaterThanBalanceException {
        try {

            System.out.println("DagaCubeParallelApplicationServiceServiceTests sixth_player_transaction() start => " + Thread.currentThread().getName());
            double wager = 25.11;
            BigDecimal amount = new BigDecimal(wager).setScale(2, MATH_CONTEXT.getRoundingMode());
            PlayerTransactionInputDTO inputTransaction = new PlayerTransactionInputDTO(/*currencyDTO, */amount, TransactionType.WAGER);

            PlayerTransactionBalanceDTO playerTransactionBalanceDTO = this.dagaCubesApplicationService.performTransaction(playerId, inputTransaction);
            log.info("Thread 6 Response -> {}", playerTransactionBalanceDTO.toString());
            log.info("Players Balance @SIXTH {}. Transactions {}", getLatestPlayerFromRepo().getBalance().getAmount(), getLatestPlayerFromRepo().getTransactions().size());

            System.out.println("DagaCubeParallelApplicationServiceServiceTests sixth_player_transaction() end => " + Thread.currentThread().getName());
        } catch (WagerGreaterThanBalanceException exception) {
            log.error("Surrounded all threads as some will fail -> {}", exception.getMessage());
        }
    }

}
