package co.za.anele.dagacubesplayerapi;

import co.za.anele.dagacubesplayerapi.domain.Player;
import co.za.anele.dagacubesplayerapi.repository.PlayerJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
@Commit
@Transactional
public abstract class DagacubesPlayerApiApplicationTests {

    protected static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;
    protected static String USERNAME = "JamesBond007";
    protected static Long playerId;
    protected static BigDecimal openingPlayerBalance;
    private Player player;

    @BeforeAll
    void setup(@Autowired PlayerJpaRepository playerJpaRepository) {
//        Balance balance = new Balance();
//        balance.setCurrency(Currency.getInstance(Locale.getDefault()));
//        BigDecimal initBalanceAmount = new BigDecimal(100.00);
//        initBalanceAmount = initBalanceAmount.setScale(2, MATH_CONTEXT.getRoundingMode());
//        balance.setAmount(initBalanceAmount);

//        USERNAME = USERNAME + "-Repo";

        Player player = playerJpaRepository.findByUsername(USERNAME)
                .orElseThrow(() -> new RuntimeException("Player not loaded for tests."));
//        player.setUsername(USERNAME);
//        player.setBalance(balance);

//        playerJpaRepository.saveAndFlush(player);
        // Set PlayerId
        playerId = player.getId();
        openingPlayerBalance = player.getBalance().getAmount();
    }

    protected Player initPlayerAndGet(final String username) {
        this.reset();

        this.player = new Player();
        this.player.setBalance(null);
        this.player.setUsername(username);

        return this.player;
    }

    private void reset() {
        this.player = null;
    }
}
