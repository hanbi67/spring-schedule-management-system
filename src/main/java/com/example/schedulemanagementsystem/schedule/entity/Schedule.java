package com.example.schedulemanagementsystem.schedule.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.example.schedulemanagementsystem.user.entity.User;

@Getter
@Entity
@Table(name = "schedules")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;

    //user id Fk
    @ManyToOne(fetch = FetchType.LAZY, optional = false)  //optional은 JPA에서 null의 허용 여부
    @JoinColumn(name = "user_id", nullable = false)  //nullable은 DB에서 null의 허용 여부
    private User user;

    public Schedule(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
