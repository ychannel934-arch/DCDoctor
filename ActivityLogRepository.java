package com.dcdoctor.adminserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dcdoctor.adminserver.model.ActivityLog;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Integer> { // ĐÃ SỬA
}