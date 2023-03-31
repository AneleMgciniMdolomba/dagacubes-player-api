package co.za.anele.dagacubesplayerapi.repository;

import co.za.anele.dagacubesplayerapi.DagacubesPlayerApiApplicationTests;
import co.za.anele.dagacubesplayerapi.domain.Balance;
import co.za.anele.dagacubesplayerapi.domain.Player;
import co.za.anele.dagacubesplayerapi.domain.Transaction;
import co.za.anele.dagacubesplayerapi.domain.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class SimplePlayerRepositoryJpaTests extends DagacubesPlayerApiApplicationTests {

    @Autowired
    private PlayerJpaRepository playerJpaRepository;
    @Autowired
    private TransactionsJpaRepository transactionsJpaRepository;
    private MathContext mathContext = MathContext.DECIMAL128;

    @Test
    @Transactional
    void create_player() {
        // Create a player with username = player1
        Player player = super.initPlayerAndGet("SimplePlayerRepositoryJpaTestsPlayer");
        playerJpaRepository.saveAndFlush(player);

        assertThat(player.getId()).isNotNull();
    }

    @Test
    @Transactional
    @Rollback
    void should_fail_when_creating_player_with_duplicate_username() {
        Player player = initPlayerAndGet(USERNAME);

        DataIntegrityViolationException jdbcException = assertThrows(
                DataIntegrityViolationException.class, () ->
                        playerJpaRepository.saveAndFlush(player)
        );

        assertThat(jdbcException).isNotNull();
        assertThat(jdbcException.getMessage()).contains("ConstraintViolationException");
    }

    @Test
    void load_player_by_username() {
        // Load player by id
        Optional<Player> optionalPlayer = this.playerJpaRepository.findByUsername(USERNAME);
        if (optionalPlayer.isPresent()) {
            Player fromStore = optionalPlayer.get();

            assertThat(fromStore.getUsername()).isEqualTo(USERNAME);
            assertThat(fromStore.getId()).isEqualTo(1L);
        }
    }

    @Test
    void no_player_should_be_loaded_if_username_not_found() {
        // Load player by id random id
        Optional<Player> optionalPlayer = this.playerJpaRepository.findByUsername("someRandomUsername");
        if (optionalPlayer.isPresent()) {
            throw new IllegalStateException("No player was expected but found");
        }

        assertThat(optionalPlayer).isEmpty();
    }

    @Test
    void load_last_ten_player_transactions() {
        Player player = playerJpaRepository.findById(playerId, LockModeType.PESSIMISTIC_WRITE).orElseThrow();
        playerJpaRepository.saveAndFlush(player);
        // Create random transactions
        createTransactions(player);

        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "transactionDateTime");
        List<Transaction> loadedTransactions = this.transactionsJpaRepository.findAllByPlayer(player, pageable);

        loadedTransactions.forEach(loadedTransaction -> log.info("Transaction Type => {}, Amount => {} and Date Time => {}", loadedTransaction.getTransactionType(),
                loadedTransaction.getAmount(), loadedTransaction.getTransactionDateTime()));

        // Player balance cannot be the same original amount, win / wager will trigger balance update
        assertThat(player.getBalance().getAmount()).isNotEqualTo(openingPlayerBalance);
        assertThat(loadedTransactions.size()).isEqualTo(10);
    }

    @Test
    void load_player_balance() {
        final Optional<Player> loadedPlayer = this.playerJpaRepository.findById(playerId);
        if (!loadedPlayer.isPresent()) {
            throw new IllegalStateException("Expected a player to be loaded initially");
        }

        Balance balance = loadedPlayer.get().getBalance();
        assertThat(balance.getAmount()).isEqualTo(openingPlayerBalance);
    }

    @Test
    void update_player_balance_with_wager_transaction_type() {
        Player player = playerJpaRepository.findById(playerId, LockModeType.PESSIMISTIC_READ).orElseThrow();

        // Create Transaction
        final double wagerAmount = 55.37;
        BigDecimal monetaryWager = new BigDecimal(wagerAmount).setScale(2, mathContext.getRoundingMode());

        // Decrease balance since it's a wager
        Balance balance = player.getBalance();
        balance.subtract(monetaryWager);
        player.setBalance(balance);

        BigDecimal expectedBalance = openingPlayerBalance.subtract(monetaryWager).setScale(2, mathContext.getRoundingMode());

        assertThat(player.getBalance().getAmount()).isEqualTo(expectedBalance);
    }

    @Test
    @Transactional
    void update_player_balance_with_win_transaction_type() {
        Player player = playerJpaRepository.findById(playerId, LockModeType.PESSIMISTIC_READ).orElseThrow();

        // Create Transaction
        final double winAmount = 3251.56;
        BigDecimal amountWon = new BigDecimal(winAmount).setScale(2, mathContext.getRoundingMode());

        // Decrease balance since it's a win
        Balance balance = player.getBalance();
        balance.add(amountWon);

        BigDecimal expectedBalance = openingPlayerBalance.add(amountWon).setScale(2, mathContext.getRoundingMode());

        assertThat(expectedBalance).isGreaterThan(openingPlayerBalance);
    }

    @Test
    @Transactional
    @Rollback
    void should_fail_when_creating_transaction_with_null_player() {
        // Create a transaction with a null player,
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal(25));
        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            // surround in UnexpectedRollbackException & DataIntegrityViolationException
            // Cannot create a transaction without a player
            transaction.setPlayer(null);
            transaction.setTransactionType(TransactionType.WIN);
            transactionsJpaRepository.saveAndFlush(transaction);
        });

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).contains("org.hibernate.PropertyValueException: not-null property references a null or transient value");
    }

    private void createTransactions(final Player player) {
        // Create Player With random number of transactions > 10 but less than 25
        int min = 10, max = 25, numberOfTransactions;

        numberOfTransactions = ThreadLocalRandom.current().nextInt(min, max);
        log.info("About to generate {} transactions.", numberOfTransactions);
        for (int index = 0; index < numberOfTransactions; index++) {
            // New Player Transaction
            Transaction transaction = new Transaction();
            transaction.setPlayer(player);

            // Random transaction type
            int transactionType = ThreadLocalRandom.current().nextInt(1, 3);
            log.info("Inside for. transactionType {} ", transactionType);
            transaction.setTransactionType(transactionType == 1 ? TransactionType.WIN : TransactionType.WAGER);

            transaction.setAmount(new BigDecimal(2.01).setScale(2, mathContext.getRoundingMode())
                    .add(new BigDecimal(index)).setScale(2, mathContext.getRoundingMode()));
            transaction.setTransactionDateTime(LocalDateTime.of(LocalDate.now(), LocalTime.now()));

            // saveAndFlush transaction
            transaction = this.transactionsJpaRepository.saveAndFlush(transaction);

            // update balance
            Balance playerBalance = player.getBalance();
            if (transaction.getTransactionType() == TransactionType.WIN) {
                playerBalance.add(transaction.getAmount());
            } else {
                playerBalance.subtract(transaction.getAmount());
            }

        }

    }
}
