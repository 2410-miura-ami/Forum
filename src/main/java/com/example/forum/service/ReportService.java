package com.example.forum.service;

import com.example.forum.controller.form.ReportForm;
import com.example.forum.repository.ReportRepository;
import com.example.forum.repository.entity.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Optional;

@Service
public class ReportService {
    @Autowired
    ReportRepository reportRepository;

    /*
     * レコード全件取得処理
     * findAllReportメソッドで、reportRepositoryのfindAllを実行します
     */
    public List<ReportForm> findAllReport(String startDate, String endDate)throws ParseException {
        //投稿の絞り込み
        //絞り込みのデフォルト値の設定
        //開始日時のデフォルト値。startDefault
        String startDefault = "2020-01-01 00:00:00";

        //終了日時のデフォルト値（現在日時の取得）endDate
        Date nowDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String endDefault = sdf.format(nowDate);
        //受け取ったString型のstartDateとendDateをDate型に変換
        if (StringUtils.hasText(startDate)) {
            startDate = startDate + " 00:00:00";
        } else {
            startDate = startDefault;
        }

        if (StringUtils.hasText(endDate)) {
            endDate = endDate + " 23:59:59";
        } else {
            endDate = endDefault;
        }
        Date start = sdf.parse(startDate);
        Date end = sdf.parse(endDate);

        //findAllメソッドは JpaRepository で定義されているため、ReportRepositoryでメソッドの定義をすることなく、使用できます
        List<Report> results = reportRepository.findByCreatedDateBetweenOrderByUpdatedDateDesc(start, end);
        //List<Report> results = reportRepository.findAllByOrderByIdDesc();
        List<ReportForm> reports = setReportForm(results);
        return reports;
    }
    /*
     * DBから取得したデータをFormに設定
     * （取得した値をsetReportFormメソッドでEntity→Formに詰め直して、Controllerに戻しています。
     * これはEntityはデータアクセス時の入れ物、FormはViewへの入出力時に使用する入れ物と役割を分けているためです
     */
    private List<ReportForm> setReportForm(List<Report> results) {
        List<ReportForm> reports = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            ReportForm report = new ReportForm();
            Report result = results.get(i);
            report.setId(result.getId());
            report.setContent(result.getContent());
            report.setCreatedDate(result.getCreatedDate());
            report.setUpdatedDate(result.getUpdatedDate());
            reports.add(report);
        }
        return reports;
    }

    /*
     * レコード追加
     */
    public void saveReport(ReportForm reqReport)throws ParseException {
        Report saveReport = setReportEntity(reqReport);
        //saveメソッドは JpaRepository で定義されているため、ReportRepositoryでメソッドの定義をすることなく、使用できます
        reportRepository.save(saveReport);
    }

    /*
     * リクエストから取得した情報をEntityに設定
     * setReportEntityメソッドでFormからEntityに詰め直してRepositoryに渡しています
     * これはEntityはデータアクセス時の入れ物、FormはViewへの入出力時に使用する入れ物と役割を分けているためです
     */
    private Report setReportEntity(ReportForm reqReport)throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date updateDate = null;
        Date createDate = null;
        String updateStr = sdf.format(new Date());
        String createStr = sdf.format(new Date());
        updateDate = sdf.parse(updateStr);
        createDate = sdf.parse(createStr);

        Report report = new Report();
        report.setId(reqReport.getId());
        report.setContent(reqReport.getContent());
        report.setCreatedDate(createDate);
        report.setUpdatedDate(updateDate);
        return report;
    }

    /*
     * レコード削除
     */
    public void deleteReport(Integer id) {
        //deleteByIdメソッドは JpaRepository で定義されているため、ReportRepositoryでメソッドの定義をすることなく、使用できます
        reportRepository.deleteById(id);
    }


    /*
     * 編集画面表示のため、編集レコード参照
     * findByIdReportメソッドで、reportRepositoryのfindAllを実行します
     */
    public ReportForm editReport(Integer id) {
        //findByIdメソッドは JpaRepository で定義されているため、ReportRepositoryでメソッドの定義をすることなく、使用できます
        List<Report> results = new ArrayList<>();
        results.add((Report)reportRepository.findById(id).orElse(null));
        //DBから取得した値をsetReportFormメソッドでEntity→Formに詰め直して、Controllerに戻す
        List<ReportForm> reports = setReportForm(results);
        return reports.get(0);
    }

}
