package com.minibank.account;

import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data génère l'implémentation : findAll(), findById(), save()... sans une ligne de SQL. */
public interface AccountRepository extends JpaRepository<AccountEntity, String> {
}
