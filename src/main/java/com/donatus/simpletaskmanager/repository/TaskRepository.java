package com.donatus.simpletaskmanager.repository;

import com.donatus.simpletaskmanager.models.TaskEntity;
import com.donatus.simpletaskmanager.models.TaskStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    @Query(value = "SELECT t FROM TaskEntity t ORDER BY t.dateModified")
    Slice<TaskEntity> pageAllTask(Pageable pageable);
    Slice<TaskEntity> findByUserId(Long user_id, Pageable pageable);
    Slice<TaskEntity> findByStatusAndUserId(TaskStatus status, Long user_id, Pageable pageable);
    Slice<TaskEntity> findByStatus(TaskStatus status, Pageable pageable);

    Slice<TaskEntity> findByStartDateAndPeriodInDays(Timestamp startDate, Integer periodInDays);
}