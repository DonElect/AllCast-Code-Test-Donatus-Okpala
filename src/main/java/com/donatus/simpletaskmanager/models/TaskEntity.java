package com.donatus.simpletaskmanager.models;

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
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer periodInDays;

    @Column(nullable = false)
    private Timestamp startDate;

    @Column(nullable = false, length = 25)
    private TaskStatus status;

    @Column(nullable = false)
    private String taskTitle;

    @Column(length = 1000)
    private String taskDetails;

    @ManyToOne(cascade = {CascadeType.DETACH,CascadeType.MERGE
            ,CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
