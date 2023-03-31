package co.za.anele.dagacubesplayerapi.features.service;

import co.za.anele.dagacubesplayerapi.domain.Transaction;
import co.za.anele.dagacubesplayerapi.domain.TransactionType;
import co.za.anele.dagacubesplayerapi.features.DagaCubesAbstractSpringBootApplicationServiceTests;
import co.za.anele.dagacubesplayerapi.features.DagaCubesApplicationService;
import co.za.anele.dagacubesplayerapi.features.data.BalanceDTO;
import co.za.anele.dagacubesplayerapi.features.data.PlayerTransactionBalanceDTO;
import co.za.anele.dagacubesplayerapi.features.data.PlayerTransactionInputDTO;
import co.za.anele.dagacubesplayerapi.features.exceptions.InvalidPlayerIdException;
import co.za.anele.dagacubesplayerapi.features.exceptions.InvalidTransactionAmount;
import co.za.anele.dagacubesplayerapi.repository.PlayerJpaRepository;
import co.za.anele.dagacubesplayerapi.repository.TransactionsJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DagaCubesApplicationServiceServiceTests extends DagaCubesAbstractSpringBootApplicationServiceTests {

    @Autowired
    private DagaCubesApplicationService dagaCubesApplicationService;

    @Autowired
    private PlayerJpaRepository playerJpaRepository;
    @Autowired
    private TransactionsJpaRepository transactionsJpaRepository;


    @Test
    @Transactional
    void should_return_player_balance() {
        BalanceDTO loadedBalance = this.dagaCubesApplicationService.getPlayerBalance(playerId);

        assertThat(loadedBalance).isNotNull();
        assertThat(loadedBalance.getPlayerId()).isEqualTo(playerId.intValue());
    }

    @Test
    @Transactional
    void should_fail_to_return_player_balance_when_not_found() {
        InvalidPlayerIdException exception = assertThrows(InvalidPlayerIdException.class, () -> {
            BalanceDTO loadedBalance = this.dagaCubesApplicationService.getPlayerBalance(101L);

            assertThat(loadedBalance).isNull();
        });

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).contains("Invalid Player Id.");
    }

    @Test
    @Transactional
    void should_increase_player_balance_on_win() throws InvalidTransactionAmount {
        double win = 352.11;
        BigDecimal amount = new BigDecimal(win).setScale(2, MATH_CONTEXT.getRoundingMode());
        PlayerTransactionInputDTO inputTransaction = new PlayerTransactionInputDTO(/*currencyDTO, */amount, TransactionType.WIN);

        PlayerTransactionBalanceDTO playerTransactionBalanceDTO = this.dagaCubesApplicationService.performTransaction(playerId, inputTransaction);

        // Check response is not null
        assertThat(playerTransactionBalanceDTO).isNotNull();
        assertThat(playerTransactionBalanceDTO.getBalance()).isGreaterThan(openingPlayerBalance);
        Optional<Transaction> transaction = this.transactionsJpaRepository.findById(playerTransactionBalanceDTO.getTransactionId().longValue());
        transaction.ifPresent(transaction1 -> playerJpaRepository.findById(playerId).map(player1 -> {
            assertThat(player1.getTransactions()).contains(transaction1);
            return transaction1;
        }).orElseThrow());
    }

    @Test
    void should_fail_to_increase_when_player_is_not_found() throws InvalidTransactionAmount {
        try {

            InvalidPlayerIdException exception = assertThrows(InvalidPlayerIdException.class, () -> {
                double win = 352.11;
                BigDecimal amount = new BigDecimal(win).setScale(2, MATH_CONTEXT.getRoundingMode());
                PlayerTransactionInputDTO inputTransaction = new PlayerTransactionInputDTO(/*currencyDTO, */amount, TransactionType.WIN);

                PlayerTransactionBalanceDTO playerTransactionBalanceDTO = this.dagaCubesApplicationService.performTransaction(101L, inputTransaction);

                fail("Expectation was the test should fail but didn't and got the response: " + playerTransactionBalanceDTO.toString());
            });

            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).contains("Invalid Player Id.");
        } catch (InvalidTransactionAmount exception) {
            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).contains("Wager amount cannot be greater than current player balance.");
        }
    }

    @Test
    @Transactional
    void decrease_player_balance_on_wager() throws InvalidTransactionAmount {
        double wager = 2.11;
        BigDecimal amount = new BigDecimal(wager).setScale(2, MATH_CONTEXT.getRoundingMode());
        PlayerTransactionInputDTO inputTransaction = new PlayerTransactionInputDTO(/*currencyDTO, */amount, TransactionType.WAGER);

        PlayerTransactionBalanceDTO playerTransactionBalanceDTO = this.dagaCubesApplicationService.performTransaction(playerId, inputTransaction);

        // Check response is not null
        assertThat(playerTransactionBalanceDTO).isNotNull();
        // Check if current balance minus winnings = initial balance
        Optional<Transaction> transaction = this.transactionsJpaRepository.findById(playerTransactionBalanceDTO.getTransactionId().longValue());
        transaction.ifPresent(transaction1 -> playerJpaRepository.findById(playerId).map(player1 -> {
            assertThat(player1.getTransactions()).contains(transaction1);
            return transaction1;
        }).orElseThrow());
    }
}
