package co.za.anele.dagacubesplayerapi.repository.custom;

import co.za.anele.dagacubesplayerapi.domain.Player;
import co.za.anele.dagacubesplayerapi.features.exceptions.InvalidPlayerIdException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class CustomPlayerRepositoryImpl implements CustomPlayerRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, noRollbackFor = {InvalidPlayerIdException.class})
    public Optional<Player> findById(Long id, LockModeType lockMode) {
        Player player = entityManager.find(Player.class, id, lockMode);

        if (player == null) {
            throw new InvalidPlayerIdException("Invalid Player Id.");
        }

        return Optional.of(player);
    }
}
