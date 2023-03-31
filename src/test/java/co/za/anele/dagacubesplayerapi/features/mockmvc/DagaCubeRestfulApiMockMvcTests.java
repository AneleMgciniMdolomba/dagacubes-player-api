package co.za.anele.dagacubesplayerapi.features.mockmvc;

import co.za.anele.dagacubesplayerapi.domain.Balance;
import co.za.anele.dagacubesplayerapi.domain.Player;
import co.za.anele.dagacubesplayerapi.domain.Transaction;
import co.za.anele.dagacubesplayerapi.domain.TransactionType;
import co.za.anele.dagacubesplayerapi.features.data.*;
import co.za.anele.dagacubesplayerapi.repository.PlayerJpaRepository;
import co.za.anele.dagacubesplayerapi.repository.TransactionsJpaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Currency;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@WebAppConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class DagaCubeRestfulApiMockMvcTests {

    private static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;
    private static final String LOAD_PLAYER_BALANCE_ENDPOINT = "/casino/player/{playerid}/balance";
    private static final String UPDATE_PLAYER_BALANCE_ENDPOINT = "/casino/player/{playerId}/balance/update";
    private static final String LOAD_PLAYER_TRANSACTIONS_ENDPOINT = "/casino/admin/player/transactions";
    private static String USERNAME = "player-one";
    private static Long playerId;
    private static BigDecimal openingPlayerBalance;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PlayerJpaRepository playerJpaRepository;
    @Autowired
    private TransactionsJpaRepository transactionsJpaRepository;
    private MockMvc mockMvc;

    @BeforeAll
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        log.info("Create a player with balance of 101.22 ZAR");
        Balance balance = new Balance();
        balance.setCurrency(Currency.getInstance(Locale.getDefault()));
        BigDecimal initBalanceAmount = new BigDecimal(101.22);
        initBalanceAmount = initBalanceAmount.setScale(2, MATH_CONTEXT.getRoundingMode());
        balance.setAmount(initBalanceAmount);
//        balanceJpaRepository.save(balance);

        Player player = new Player();
        player.setUsername(USERNAME);
        player.setBalance(balance);

        playerJpaRepository.save(player);

        // Create transactions
        createTransactions(player);
        // Set PlayerId & balance
        playerId = player.getId();
        openingPlayerBalance = player.getBalance().getAmount();
    }

    @Test
    void get_balance_successfully() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(LOAD_PLAYER_BALANCE_ENDPOINT, playerId)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(200);
        BalanceDTO balanceDTO = mapToObject(mvcResult.getResponse().getContentAsString(), BalanceDTO.class);
        assertThat(balanceDTO).isNotNull();
        assertThat(playerId).isEqualTo(balanceDTO.getPlayerId());
    }

    @Test
    void fail_to_get_player_balance() throws Exception {
        final String endpoint = "/casino/player/{playerId}/balance";

        MvcResult mvcResult = mockMvc.perform(get(endpoint, randomId())).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(400);

        // Currently do not have body;
        ProblemDTO balanceDTO = mapToObject(mvcResult.getResponse().getContentAsString(), ProblemDTO.class);
        assertThat(balanceDTO).isNotNull();
        assertThat(balanceDTO.getStatus()).isEqualTo(400);
        assertThat(balanceDTO.getMessage()).containsIgnoringCase("Invalid player id");
    }

    @Test
    void update_player_balance_successfully_with_win() throws Exception {
        final String endpoint = "/casino/player/{playerId}/balance/update";
        double win = 255.11;

        BigDecimal amount = new BigDecimal(win).setScale(2, MATH_CONTEXT.getRoundingMode());
        PlayerTransactionInputDTO inputTransaction = new PlayerTransactionInputDTO(/*currencyDTO, */amount, TransactionType.WIN);

        String request = toJson(inputTransaction);

        MvcResult mvcResult = mockMvc.perform(post(endpoint, playerId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(request)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(200);

        PlayerTransactionBalanceDTO balanceDTO = mapToObject(mvcResult.getResponse().getContentAsString(), PlayerTransactionBalanceDTO.class);
        assertThat(balanceDTO).isNotNull();
        assertThat(balanceDTO.getBalance()).isGreaterThan(openingPlayerBalance);
    }

    @Test
    void update_player_balance_successfully_with_wager() throws Exception {
        double wager = 32.52;

        BigDecimal amount = new BigDecimal(wager).setScale(2, MATH_CONTEXT.getRoundingMode());
        PlayerTransactionInputDTO inputTransaction = new PlayerTransactionInputDTO(/*currencyDTO,*/ amount, TransactionType.WAGER);

        String request = toJson(inputTransaction);

        MvcResult mvcResult = mockMvc.perform(post(UPDATE_PLAYER_BALANCE_ENDPOINT, playerId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(request)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(200);

        PlayerTransactionBalanceDTO balanceDTO = mapToObject(mvcResult.getResponse().getContentAsString(), PlayerTransactionBalanceDTO.class);
        assertThat(balanceDTO).isNotNull();
        assertThat(balanceDTO.getTransactionId()).isNotNull();
    }

    @Test
    void fail_to_update_player_balance_successfully_with_wager_more_than_available_balance() throws Exception {
        double wager = 5332.52;

        BigDecimal amount = new BigDecimal(wager).setScale(2, MATH_CONTEXT.getRoundingMode());
        PlayerTransactionInputDTO inputTransaction = new PlayerTransactionInputDTO(/*currencyDTO, */amount, TransactionType.WAGER);

        String request = toJson(inputTransaction);

        MvcResult mvcResult = mockMvc.perform(post(UPDATE_PLAYER_BALANCE_ENDPOINT, playerId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(request)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(418);

        ProblemDTO balanceDTO = mapToObject(mvcResult.getResponse().getContentAsString(), ProblemDTO.class);
        assertThat(balanceDTO).isNotNull();
        assertThat(balanceDTO.getStatus()).isEqualTo(418);
        assertThat(balanceDTO.getMessage()).containsIgnoringCase("Wager amount cannot be greater than current player balance.");
    }

    @Test
    void fail_to_update_player_balance_successfully_with_negative_wager() throws Exception {
        double wager = -120.58;
        BigDecimal amount = new BigDecimal(wager).setScale(2, MATH_CONTEXT.getRoundingMode());
        PlayerTransactionInputDTO inputTransaction = new PlayerTransactionInputDTO(/*currencyDTO,*/ amount, TransactionType.WAGER);

        String request = toJson(inputTransaction);

        MvcResult mvcResult = mockMvc.perform(post(UPDATE_PLAYER_BALANCE_ENDPOINT, playerId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(request)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(400);

        ProblemDTO balanceDTO = mapToObject(mvcResult.getResponse().getContentAsString(), ProblemDTO.class);
        assertThat(balanceDTO).isNotNull();
        assertThat(balanceDTO.getStatus()).isEqualTo(400);
        assertThat(balanceDTO.getMessage()).containsIgnoringCase("Cannot update player balance with a negative amount value");
    }

    @Test
    void load_player_transactions() throws Exception {
        PlayerTransactionsDTO playerTransactionsDTO = new PlayerTransactionsDTO(USERNAME);
        String request = toJson(playerTransactionsDTO);

        MvcResult mvcResult = mockMvc.perform(post(LOAD_PLAYER_TRANSACTIONS_ENDPOINT).
                contentType(MediaType.APPLICATION_JSON).
                content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(200);

        TransactionsDTO transactionsDTO = mapToObject(mvcResult.getResponse().getContentAsString(), TransactionsDTO.class);
        assertThat(transactionsDTO).isNotNull();
        assertThat(transactionsDTO.getTransactions().size()).isEqualTo(10);
        transactionsDTO.getTransactions().forEach(transactionDTO -> log.info("Printing transaction -> {}", transactionDTO.toString()));
    }

    @Test
    void fail_to_load_player_transactions_random_username() throws Exception {
        final String RANDOM_USERNAME = UUID.randomUUID().toString();
        PlayerTransactionsDTO playerTransactionsDTO = new PlayerTransactionsDTO(RANDOM_USERNAME);
        String request = toJson(playerTransactionsDTO);

        MvcResult mvcResult = mockMvc.perform(post(LOAD_PLAYER_TRANSACTIONS_ENDPOINT).
                contentType(MediaType.APPLICATION_JSON).
                content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(400);

        ProblemDTO balanceDTO = mapToObject(mvcResult.getResponse().getContentAsString(), ProblemDTO.class);
        assertThat(balanceDTO).isNotNull();
        assertThat(balanceDTO.getStatus()).isEqualTo(400);
        assertThat("Invalid username.").containsIgnoringCase(balanceDTO.getMessage());
    }

    private Long randomId() {
        long leftLimit = 20L;
        long rightLimit = 30L;

        return leftLimit + (long) (Math.random() * (rightLimit - leftLimit));
    }

    private <T> T mapToObject(String json, Class<T> clazz) throws JsonProcessingException {
        return this.objectMapper.readValue(json, clazz);
    }

    private String toJson(Object tClass) throws JsonProcessingException {
        return this.objectMapper.writeValueAsString(tClass);
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

            transaction.setAmount(new BigDecimal(2.01).setScale(2, MATH_CONTEXT.getRoundingMode())
                    .add(new BigDecimal(index)).setScale(2, MATH_CONTEXT.getRoundingMode()));
            transaction.setTransactionDateTime(LocalDateTime.of(LocalDate.now(), LocalTime.now()).plus(5, ChronoUnit.SECONDS));

            // saveAndFlush transaction
            transaction = transactionsJpaRepository.saveAndFlush(transaction);

            // update balance
            Balance playerBalance = player.getBalance();
            if (transaction.getTransactionType() == TransactionType.WIN) {
                playerBalance.add(transaction.getAmount());
            } else {
                playerBalance.subtract(transaction.getAmount());
            }

//            playerJpaRepository.saveAndFlush(player);
        }
    }
}
