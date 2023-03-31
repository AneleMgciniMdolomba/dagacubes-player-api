package co.za.anele.dagacubesplayerapi.repository;

import co.za.anele.dagacubesplayerapi.domain.Player;
import co.za.anele.dagacubesplayerapi.domain.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionsJpaRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByPlayer(final Player player, Pageable pageable);
}
