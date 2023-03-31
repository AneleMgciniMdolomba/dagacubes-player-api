package co.za.anele.dagacubesplayerapi.features;

import co.za.anele.dagacubesplayerapi.domain.Player;
import co.za.anele.dagacubesplayerapi.domain.Transaction;
import co.za.anele.dagacubesplayerapi.repository.PlayerJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Optional;

@SpringBootTest
@Commit
@Slf4j
public abstract class DagaCubesAbstractSpringBootApplicationServiceTests {

    protected static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;
    protected static String USERNAME = "JamesBond007";

    protected static Long playerId;
    protected static BigDecimal openingPlayerBalance;

    @BeforeAll
    static void init(@Autowired PlayerJpaRepository playerJpaRepository) {
        Player player = playerJpaRepository.findByUsername(USERNAME).orElseThrow(() -> new RuntimeException("Player not loaded from DB"));

        playerId = player.getId();
        openingPlayerBalance = player.getBalance().getAmount();
    }

    @AfterAll
    static void finish(@Autowired PlayerJpaRepository playerJpaRepository) {
        Optional<Player> player = playerJpaRepository.findByUsername(USERNAME);

        if (!player.isPresent()) {
            log.warn("Player not found. Database might be close already");
            return;
        }

        BigDecimal transactionAmounts = player.get().getTransactions().stream().map(Transaction::getAmount)
                .reduce(BigDecimal.valueOf(0), BigDecimal::add);

        transactionAmounts = transactionAmounts.setScale(2, MATH_CONTEXT.getRoundingMode());

        log.info("Opening Player Balance: {}. Current Player Balance {}. Total Number of Transactions {}, Amounting TO {}. ",
                openingPlayerBalance, player.get().getBalance().getAmount(), player.get().getTransactions().size(), transactionAmounts);
    }
}
