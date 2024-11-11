package com.example.forum.repository;

import com.example.forum.repository.entity.Comment;
import com.example.forum.repository.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//ReportRepository が JpaRepository を継承しており、findAllメソッドを実行しているため、こちらで特に何か記載する必要はありません。
// JpaRepositryにはあらかじめいくつかのメソッドが定義されており、SQL文を打つ必要がありません。
//findAllで実行されている処理はSQL文の「select * from report;」のようにイメージしてもらえれば大丈夫です。
@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

}