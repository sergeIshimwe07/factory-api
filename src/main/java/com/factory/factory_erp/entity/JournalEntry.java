package com.factory.factory_erp.entity;

import com.factory.factory_erp.entity.enums.JournalStatus;
import com.factory.factory_erp.entity.enums.JournalType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "journal_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalEntry extends BaseEntity {
    
    @Column(name = "journal_id", unique = true, nullable = false, length = 50)
    private String journalId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JournalType type;
    
    @Column(length = 100)
    private String reference;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private JournalStatus status = JournalStatus.draft;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @OneToMany(mappedBy = "journalEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<JournalEntryLine> lines = new ArrayList<>();
    
    @PrePersist
    public void prePersist() {
        if (journalId == null) {
            journalId = "jour_" + String.format("%03d", System.currentTimeMillis() % 1000);
        }
        if (entryDate == null) {
            entryDate = LocalDate.now();
        }
    }
    
    public void addLine(JournalEntryLine line) {
        lines.add(line);
        line.setJournalEntry(this);
    }
    
    public void removeLine(JournalEntryLine line) {
        lines.remove(line);
        line.setJournalEntry(null);
    }
}
