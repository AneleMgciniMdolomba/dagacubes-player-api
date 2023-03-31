package co.za.anele.dagacubesplayerapi.repository.custom;

import co.za.anele.dagacubesplayerapi.domain.Player;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface CustomPlayerRepository {

    Optional<Player> findById(final Long id, LockModeType lockMode);
}
