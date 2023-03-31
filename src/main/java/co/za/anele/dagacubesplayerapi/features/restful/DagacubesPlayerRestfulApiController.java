package co.za.anele.dagacubesplayerapi.features.restful;

import co.za.anele.dagacubesplayerapi.features.DagaCubesApplicationService;
import co.za.anele.dagacubesplayerapi.features.data.*;
import co.za.anele.dagacubesplayerapi.features.exceptions.InvalidTransactionAmount;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;

@Tag(name = "Player Transactions",
        description = "Responsible for loading player balance, performing player transaction and loading player transactions"
)
@AllArgsConstructor
@RestController(value = "casino/")
@Slf4j
public class DagacubesPlayerRestfulApiController {

    private final DagaCubesApplicationService applicationService;

    /**
     * @param playerId            to get / load balance for. Throws @{@link co.za.anele.dagacubesplayerapi.features.exceptions.InvalidPlayerIdException}
     *                            when that player id is not found.
     * @param transactionInputDTO {@link PlayerTransactionInputDTO} request body to perform an update on a player's profile
     *                            either a WIN or WAGER
     * @return {@link PlayerTransactionBalanceDTO} player's balance after the transactions
     */
    @PostMapping(value = "casino/player/{playerId}/balance/update")
    public ResponseEntity<PlayerTransactionBalanceDTO> updatePlayerBalance(@PathVariable Long playerId,
                                                                           @RequestBody final PlayerTransactionInputDTO transactionInputDTO) {
        if (transactionInputDTO.isNotValid()) {
            log.warn("Trying to update player balance with negative amount");

            throw new InvalidTransactionAmount("Cannot update player balance with a negative amount value.");
        }

        return new ResponseEntity<>(this.applicationService.performTransaction(playerId, transactionInputDTO), HttpStatus.OK);
    }

    /**
     * @param playerId to get / load balance for. Throws @{@link co.za.anele.dagacubesplayerapi.features.exceptions.InvalidPlayerIdException}
     *                 when that player id is not found.
     * @return {@link BalanceDTO} with the player id and the balance with default application currency
     */
    @GetMapping(value = "casino/player/{playerId}/balance")
    public ResponseEntity<BalanceDTO> getPlayerBalance(@PathVariable final Long playerId) {
        return new ResponseEntity<>(this.applicationService.getPlayerBalance(playerId), HttpStatus.OK);
    }

    /**
     * @param playerTransactionsDTO player username
     * @return @{@link TransactionsDTO}
     */
    @PostMapping(value = "casino/admin/player/transactions")
    public ResponseEntity<TransactionsDTO> loadPlayerTransactions(@RequestBody PlayerTransactionsDTO playerTransactionsDTO) {
        return new ResponseEntity<>(this.applicationService.loadPlayerTransactions(playerTransactionsDTO), HttpStatus.OK);
    }
}
