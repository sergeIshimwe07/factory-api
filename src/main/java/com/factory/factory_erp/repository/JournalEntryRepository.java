package com.factory.factory_erp.repository;

import com.factory.factory_erp.entity.JournalEntry;
import com.factory.factory_erp.entity.enums.JournalStatus;
import com.factory.factory_erp.entity.enums.JournalType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
    
    Optional<JournalEntry> findByJournalId(String journalId);
    
    Page<JournalEntry> findByType(JournalType type, Pageable pageable);
    
    Page<JournalEntry> findByStatus(JournalStatus status, Pageable pageable);
    
    @Query("SELECT je FROM JournalEntry je WHERE je.entryDate >= :startDate AND je.entryDate <= :endDate")
    Page<JournalEntry> findByDateRange(@Param("startDate") LocalDate startDate, 
                                       @Param("endDate") LocalDate endDate, 
                                       Pageable pageable);
}
