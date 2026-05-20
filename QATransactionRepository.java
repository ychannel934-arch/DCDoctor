package com.dcdoctor.adminserver.repository;

import com.dcdoctor.adminserver.model.QATransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QATransactionRepository extends JpaRepository<QATransaction, Integer> { // ĐÃ SỬA
    List<QATransaction> findAllByOrderByTransactionDateDesc();
    long countByStatus(String status);
}