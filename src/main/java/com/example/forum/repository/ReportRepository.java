package com.example.forum.repository;

import com.example.forum.repository.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

//ReportRepository が JpaRepository を継承しており、findAllメソッドを実行しているため、こちらで特に何か記載する必要はありません。
// JpaRepositryにはあらかじめいくつかのメソッドが定義されており、SQL文を打つ必要がありません。
//findAllで実行されている処理はSQL文の「select * from report;」のようにイメージしてもらえれば大丈夫です。
@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    //メソッド名を「find～」から始めることでSELECT文が発行されます。
    //「〜OrderByIdDesc」とすることで「id」というフィールドを「desc(降順)」で並び替えるクエリが発行されます
    //public List<Report> findAllByOrderByIdDesc();
    public List<Report> findByCreatedDateBetween(Date startDate, Date endDate);
}
