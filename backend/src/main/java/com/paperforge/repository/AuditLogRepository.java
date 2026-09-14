package com.paperforge.repository;

import com.paperforge.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long>, JpaSpecificationExecutor<AuditLog> {

    @Modifying
    @Query("DELETE FROM AuditLog a WHERE a.timestamp < :cutoff")
    int deleteByTimestampBefore(@Param("cutoff") LocalDateTime cutoff);
}
