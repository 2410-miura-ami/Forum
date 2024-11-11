package com.example.forum.service;

import com.example.forum.controller.form.ReportForm;
import com.example.forum.repository.ReportRepository;
import com.example.forum.repository.entity.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {
    @Autowired
    ReportRepository reportRepository;

    /*
     * レコード全件取得処理
     * findAllReportメソッドで、reportRepositoryのfindAllを実行します
     */
    public List<ReportForm> findAllReport() {
        //findAllメソッドは JpaRepository で定義されているため、ReportRepositoryでメソッドの定義をすることなく、使用できます
        List<Report> results = reportRepository.findAllByOrderByIdDesc();
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
            reports.add(report);
        }
        return reports;
    }

    /*
     * レコード追加
     */
    public void saveReport(ReportForm reqReport) {
        Report saveReport = setReportEntity(reqReport);
        //saveメソッドは JpaRepository で定義されているため、ReportRepositoryでメソッドの定義をすることなく、使用できます
        reportRepository.save(saveReport);
    }

    /*
     * リクエストから取得した情報をEntityに設定
     * setReportEntityメソッドでFormからEntityに詰め直してRepositoryに渡しています
     * これはEntityはデータアクセス時の入れ物、FormはViewへの入出力時に使用する入れ物と役割を分けているためです
     */
    private Report setReportEntity(ReportForm reqReport) {
        Report report = new Report();
        report.setId(reqReport.getId());
        report.setContent(reqReport.getContent());
        return report;
    }

    /*
     * レコード削除
     */
    public void deleteReport(Integer id) {
        //Report deleteReport = setDeleteEntity(reqReport);
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
