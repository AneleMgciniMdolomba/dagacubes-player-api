package co.za.anele.dagacubesplayerapi.repository;

import co.za.anele.dagacubesplayerapi.domain.Player;
import co.za.anele.dagacubesplayerapi.repository.custom.CustomPlayerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PlayerJpaRepository extends JpaRepository<Player, Long>, CustomPlayerRepository {

    @Query(value = "SELECT player FROM Player player" +
            " LEFT JOIN FETCH player.transactions " +
            "WHERE player.username = :username")
    Optional<Player> findByUsername(final String username);

}
