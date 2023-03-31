package co.za.anele.dagacubesplayerapi.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "player_transaction")
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode
@Data
@Table
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(scale = 2)
    @EqualsAndHashCode.Exclude
    private BigDecimal amount;

    @Column(updatable = false)
    private LocalDateTime transactionDateTime;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false, updatable = false)
    private Player player;
}
