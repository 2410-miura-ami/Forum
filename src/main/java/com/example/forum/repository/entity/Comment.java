package com.example.forum.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

//データアクセス時に使用するJavaBeansのような入れ物だと思ってもらってOK

//「@Getter」と「@Setter」はその名の通り、getterとsetterを自動生成するアノテーションです。

@Entity
@Table(name = "comment")
@Getter
@Setter
public class Comment {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String content;

    @Column
    private int reportId;

    @Column(name = "created_date", insertable = true, updatable = false)
    private Date createdDate;

    @Column
    private Date updatedDate;

}
