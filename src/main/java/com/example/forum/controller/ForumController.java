package com.example.forum.controller;

import com.example.forum.controller.form.CommentForm;
import com.example.forum.controller.form.ReportForm;
import com.example.forum.service.CommentService;
import com.example.forum.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class ForumController {
    @Autowired
    ReportService reportService;

    @Autowired
    CommentService commentService;

    /*
     * 投稿内容表示処理
     */
    @GetMapping
    public ModelAndView top(@PathVariable Integer id) {
        ModelAndView mav = new ModelAndView();
        // 投稿を全件取得
        //この後、Service → Repository へと処理が続いていきます。
        //その結果、各値がReportForm型のリストである「contentData」へ格納されます
        List<ReportForm> contentData = reportService.findAllReport();
        //コメントの追加機能
        List<CommentForm> commentContentData = commentService.findByIdComment(id);
        // 画面遷移先を指定(「現在のURL」/top へ画面遷移することを指定)
        mav.setViewName("/top");
        // 投稿データオブジェクトを保管(先ほどのcontentDataをModelAndView型の変数mavへ格納)
        mav.addObject("contents", contentData);
        mav.addObject("commentContents", commentContentData);
        //変数mavを戻り値として返します
        return mav;
    }

    /*
     * 新規投稿画面表示
     * 投稿内容表示機能 とほとんど変わりませんね
     * 画面遷移先を指定して、空のFormを保管。戻り値mavを返す、という流れです
     */
    @GetMapping("/new")
    public ModelAndView newContent() {
        ModelAndView mav = new ModelAndView();
        // form用の空のentityを準備
        ReportForm reportForm = new ReportForm();
        // 画面遷移先を指定
        mav.setViewName("/new");
        // 準備した空のFormを保管
        mav.addObject("formModel", reportForm);
        return mav;
    }

    /*
     * 新規投稿処理
     */
    @PostMapping("/add")
    public ModelAndView addContent(@ModelAttribute("formModel") ReportForm reportForm) {
        // 投稿をテーブルに格納
        //Report型の変数reportを引数として、ReportServiceのsaveReportを実行します。
        reportService.saveReport(reportForm);
        // その後、rootディレクトリ、つまり、⑤サーバー側：投稿内容表示機能の処理へリダイレクト
        //投稿をテーブルに格納した後、その投稿を表示させてトップ画面へ戻るという仕様
        return new ModelAndView("redirect:/");
    }

    /*
     * 投稿削除処理
     */
    @DeleteMapping("/delete/{id}")
    public ModelAndView deleteContent(@PathVariable Integer id) {
        // 投稿をテーブルから削除
        ////ReportServiceのdeleteReportを実行
        reportService.deleteReport(id);
        // rootへリダイレクト
        // その後、rootディレクトリ、つまり、⑤サーバー側：投稿内容表示機能の処理へリダイレクト
        //投稿をテーブルから削除した後、トップ画面へ戻るという仕様
        return new ModelAndView("redirect:/");
    }

    /*
     * 編集画面表示
     * 画面遷移先を指定
     */
    @GetMapping("/edit/{id}")
    public ModelAndView editContent(@PathVariable Integer id) {
        ModelAndView mav = new ModelAndView();
        //編集する投稿を取得
        ReportForm reportForm = reportService.editReport(id);
        //編集する投稿をセット
        mav.addObject("formModel", reportForm);
        //画面遷移先を指定(edit.html)
        mav.setViewName("/edit");

        return mav;
    }

    /*
     * 投稿編集処理
     */
    @PutMapping("/update/{id}")
    public ModelAndView updateContent(@PathVariable Integer id, @ModelAttribute("formModel") ReportForm reportForm) {
        // 投稿をテーブルに格納
        //Report型の変数reportを引数として、ReportServiceのsaveReportを実行します。
        reportForm.setId(id);
        reportService.saveReport(reportForm);
        // その後、rootディレクトリ、つまり、⑤サーバー側：投稿内容表示機能の処理へリダイレクト
        //投稿をテーブルに格納した後、その投稿を表示させてトップ画面へ戻るという仕様
        return new ModelAndView("redirect:/");
    }

    /*
     * コメント機能追加
     */
    @PostMapping("/comment/{id}")
    public ModelAndView commentContent(@PathVariable Integer id, @ModelAttribute("formModel") CommentForm commentForm){
        // 投稿をテーブルに格納
        //Comment型の変数commentを引数として、ReportServiceのsaveReportを実行します。
        commentForm.setReportId(id);
        commentForm.setContent(commentForm.getContent());
        commentService.saveComment(commentForm);
        // その後、rootディレクトリ、つまり、⑤サーバー側：投稿内容表示機能の処理へリダイレクト
        //投稿をテーブルに格納した後、その投稿を表示させてトップ画面へ戻るという仕様
        return new ModelAndView("redirect:/");
    }

}
