package com.factory.factory_erp.repository;

import com.factory.factory_erp.entity.Account;
import com.factory.factory_erp.entity.enums.AccountType;
import com.factory.factory_erp.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    Optional<Account> findByAccountId(String accountId);
    
    Optional<Account> findByCode(String code);
    
    List<Account> findByType(AccountType type);
    
    List<Account> findByStatus(Status status);
    
    List<Account> findByTypeAndStatus(AccountType type, Status status);
}
