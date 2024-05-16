package com.donatus.simpletaskmanager.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "task_table")
public class TaskEntity extends BaseEntity{
    private Integer periodInDays;

    @Column(nullable = false)
    @JsonFormat(pattern="yyyy-MM-dd")
    private Timestamp startDate;

    @Column(nullable = false, length = 25)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(nullable = false)
    private String taskTitle;

    @Column(length = 1000)
    private String taskDetails;

    @Column(nullable = false)
    private String createdBy;

    private String assignBy;

    @ManyToOne(cascade = {CascadeType.DETACH,CascadeType.MERGE
            ,CascadeType.PERSIST,CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
