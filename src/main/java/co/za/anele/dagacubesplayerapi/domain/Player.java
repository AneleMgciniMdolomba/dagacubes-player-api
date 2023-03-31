package co.za.anele.dagacubesplayerapi.domain;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor(force = true)
@ToString
@EqualsAndHashCode
@Data
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String username;

    @EqualsAndHashCode.Exclude
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "balance_amount", scale = 2)),
            @AttributeOverride(name = "currency", column = @Column(name = "balance_currency"))
    })
    private Balance balance;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Transaction> transactions = new HashSet<>();

    @Version
    private int version;

    public void transactionPerformed(Transaction transaction) {
        // Updating transactions
        if (this.transactions == null) {
            this.transactions = new HashSet<>();
        }

        // Add transactions to the list
        this.transactions.add(transaction);

        // validations = if wager, subtract, if win add
        if (TransactionType.WIN == transaction.getTransactionType()) {
            this.balance.add(transaction.getAmount());
        } else if (TransactionType.WAGER == transaction.getTransactionType()) {
            this.balance.subtract(transaction.getAmount());
        }
    }
}
