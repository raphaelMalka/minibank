package com.minibank.account;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {

    // Requête déduite du nom de la méthode :
    // SELECT ... WHERE account_id = ? ORDER BY booking_date DESC
    List<TransactionEntity> findByAccountIdOrderByBookingDateDesc(String accountId);
}
