package com.example.forum.controller;

import com.example.forum.controller.form.CommentForm;
import com.example.forum.controller.form.ReportForm;
import com.example.forum.repository.entity.Report;
import com.example.forum.service.CommentService;
import com.example.forum.service.ReportService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class ForumController {
    @Autowired
    ReportService reportService;

    @Autowired
    CommentService commentService;

    @Autowired
    HttpSession session;
    /*
     * 投稿内容表示処理
     */
    @GetMapping
    public ModelAndView top(@RequestParam(name="startDate", required=false) String startDate, @RequestParam(name="endDate", required=false) String endDate)throws ParseException {
        ModelAndView mav = new ModelAndView();
        //編集時にsessionに格納したエラーメッセージを取得
        List<String> errorMessages = (List<String>) session.getAttribute("errorMessages");
        Integer reportID = (Integer) session.getAttribute("reportID");
        //「エラーメッセージが空じゃなければ、エラーメッセージをセットする。」mav.addObjectすることで画面表示の準備できる
        if((errorMessages != null) && (!errorMessages.isEmpty())) {
            mav.addObject("errorMessages", errorMessages);
            mav.addObject("reportID", reportID);
        }
        //エラーメッセージが常に出てきてしまうので、格納後にセッションを破棄する
        session.invalidate();

        // 投稿を全件取得
        //この後、Service → Repository へと処理が続いていきます。
        //その結果、各値がReportForm型のリストである「contentData」へ格納されます
        List<ReportForm> contentData = reportService.findAllReport(startDate, endDate);

        // form用の空のentityを準備
        CommentForm commentForm = new CommentForm();
        // 準備した空のFormを保管
        //これをtop.htmlのth:object="${formMedel}"に指定してる
        mav.addObject("formModel", commentForm);

        //コメントの追加機能
        List<CommentForm> commentContentData = commentService.findAllComment();
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

        //sessionに入れたエラーメッセージを取得
        //List<String> errorMessages = new ArrayList<String>();
        //String errorMessageString = (String) session.getAttribute("errorMessages");
        //errorMessages.add(errorMessageString);
        List<String> errorMessages = (List<String>) session.getAttribute("errorMessages");
        //「エラーメッセージが空じゃなければ、エラーメッセージをセットする。」mav.addObjectすることで画面表示の準備できる
        if((errorMessages != null) && (!errorMessages.isEmpty())) {
            mav.addObject("errorMessages", errorMessages);
        }
        //エラーメッセージが常に出てきてしまうので、格納後にセッションを破棄する
        session.invalidate();
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
    public ModelAndView addContent(@Valid @ModelAttribute("formModel") ReportForm reportForm, BindingResult bindingResult)throws ParseException {
        //バリデーション
        List<String> errorMessages = new ArrayList<String>();

        if (bindingResult.hasErrors()) {
            //エラーがあったら、エラーメッセージを格納する
            //BindingResultのgetFieldErrors()により、(フィールド名と)エラーメッセージを取得できます
            for (FieldError error : bindingResult.getFieldErrors()){
                String message = error.getDefaultMessage();
                //取得したエラーメッセージをエラーメッセージのリストに格納
                errorMessages.add(message);
            }

            // セッションに保存（リダイレクトしても値を保存できるようにするため）
            session.setAttribute("errorMessages", errorMessages);

            //新規投稿画面に遷移
            return new ModelAndView("redirect:/new");
        }
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
        //編集時にsessionに格納したエラーメッセージを取得
        List<String> errorMessages = (List<String>) session.getAttribute("errorMessages");
        //「エラーメッセージが空じゃなければ、エラーメッセージをセットする。」mav.addObjectすることで画面表示の準備できる
        if((errorMessages != null) && (!errorMessages.isEmpty())) {
            mav.addObject("errorMessages", errorMessages);
        }
        //エラーメッセージが常に出てきてしまうので、格納後にセッションを破棄する
        session.invalidate();

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
    public ModelAndView updateContent(@PathVariable Integer id, @Valid @ModelAttribute("formModel") ReportForm reportForm, BindingResult bindingResult)throws ParseException {
        //バリデーション
        List<String> errorMessages = new ArrayList<String>();

        if (bindingResult.hasErrors()) {
            //エラーがあったら、エラーメッセージを格納する
            //BindingResultのgetFieldErrors()により、(フィールド名と)エラーメッセージを取得できます
            for (FieldError error : bindingResult.getFieldErrors()){
                String message = error.getDefaultMessage();
                //取得したエラーメッセージをエラーメッセージのリストに格納
                errorMessages.add(message);
            }

            // セッションに保存（リダイレクトしても値を保存できるようにするため）
            session.setAttribute("errorMessages", errorMessages);

            //編集画面に遷移
            return new ModelAndView("redirect:/edit/{id}");
        }
        // 投稿をテーブルに格納
        //Report型の変数reportを引数として、ReportServiceのsaveReportを実行します。
        reportForm.setId(id);
        //reportForm.setUpdatedDate(updateDate);
        reportService.saveReport(reportForm);
        // その後、rootディレクトリ、つまり、⑤サーバー側：投稿内容表示機能の処理へリダイレクト
        //投稿をテーブルに格納した後、その投稿を表示させてトップ画面へ戻るという仕様
        return new ModelAndView("redirect:/");
    }

    /*
     * コメント機能追加
     */
    @PostMapping("/comment/{reportId}")
    public ModelAndView commentContent(@PathVariable Integer reportId, @Valid @ModelAttribute("formModel") CommentForm commentForm, BindingResult bindingResult)throws ParseException {
        //バリデーション
        List<String> errorMessages = new ArrayList<String>();

        if (bindingResult.hasErrors()) {
            //エラーがあったら、エラーメッセージを格納する
            //BindingResultのgetFieldErrors()により、(フィールド名と)エラーメッセージを取得できます
            for (FieldError error : bindingResult.getFieldErrors()){
                String message = error.getDefaultMessage();
                //取得したエラーメッセージをエラーメッセージのリストに格納
                errorMessages.add(message);
            }

            // セッションに保存（リダイレクトしても値を保存できるようにするため）
            session.setAttribute("errorMessages", errorMessages);
            session.setAttribute("reportID", reportId);

            //top画面に遷移
            return new ModelAndView("redirect:/");
        }

        // 投稿をテーブルに格納
        //Comment型の変数commentを引数として、ReportServiceのsaveReportを実行します。
        commentForm.setReportId(reportId);
        commentForm.setContent(commentForm.getContent());
        commentService.saveComment(commentForm);

        //コメントの追加とともに投稿の更新日も更新
        ReportForm reportForm = reportService.editReport(commentForm.getReportId());

        //このIDの投稿を参照してもってきてから更新？
        reportForm.setUpdatedDate(commentForm.getUpdatedDate());
        reportService.saveReport(reportForm);

        // その後、rootディレクトリ、つまり、⑤サーバー側：投稿内容表示機能の処理へリダイレクト
        //投稿をテーブルに格納した後、その投稿を表示させてトップ画面へ戻るという仕様
        return new ModelAndView("redirect:/");
    }

    /*
     * コメント編集画面表示
     * 画面遷移先を指定
     */
    @GetMapping("/commentEdit/{id}")
    public ModelAndView commentEditContent(@PathVariable Integer id) {
        ModelAndView mav = new ModelAndView();
        //編集する投稿を取得
        CommentForm commentForm = commentService.editComment(id);

        //セッションに格納したエラーメッセージを取得
        List<String> errorMessages = (List<String>)session.getAttribute("errorMessages");
        //最初の編集画面表示の際はエラーメッセージがnull(バリデーションまだ行ってないから)なので、
        // nullではない時とエラーメッセージが空ではない時で条件設定する
        if((errorMessages != null) && (!errorMessages.isEmpty())){
            //画面表示できるようにmav.addObjectで格納
            mav.addObject("errorMessages", errorMessages);
        }
        //エラーメッセージが常に出てしまうので、格納後にセッションを破棄
        session.invalidate();

        //編集する投稿をセット
        mav.addObject("formModel", commentForm);
        //画面遷移先を指定(commentEdit.html)
        mav.setViewName("/commentEdit");

        return mav;
    }

    /*
     * コメント編集処理
     */
    @PutMapping("/commentUpdate/{id}")
    public ModelAndView commentUpdateContent(@PathVariable Integer id, @Valid @ModelAttribute("formModel") CommentForm commentForm, BindingResult bindingResult)throws ParseException {
        //バリデーション
        List<String> errorMessages = new ArrayList<>();

        if(bindingResult.hasErrors()){
            //バリデーションでエラーがあったらbindingResultにエラーが入るようになっていて、そのbindingResultにエラーがあったら
            //そのエラーがどういうものか、内容を取得して、その中でエラーメッセージを取得する
            for (FieldError error : bindingResult.getFieldErrors()){
                String message = error.getDefaultMessage();
                //取得したエラーメッセージをリストに格納
                errorMessages.add(message);
            }

            //エラーメッセージのリストをセッションに格納（のちにリダイレクトするから値の保持ができるようにセッション使う）
            session.setAttribute("errorMessages", errorMessages);

            //top画面に遷移
            return new ModelAndView("redirect:/commentEdit/{id}");
        }


        // 投稿をテーブルに格納
        //Report型の変数reportを引数として、CommentServiceのsaveReportを実行します。
        commentForm.setId(id);
        commentService.saveComment(commentForm);
        // その後、rootディレクトリ、つまり、⑤サーバー側：投稿内容表示機能の処理へリダイレクト
        //投稿をテーブルに格納した後、その投稿を表示させてトップ画面へ戻るという仕様
        return new ModelAndView("redirect:/");
    }

    /*
     * コメント削除処理
     */
    @DeleteMapping("/commentDelete/{id}")
    public ModelAndView commentDeleteContent(@PathVariable Integer id) {
        // 投稿をテーブルから削除
        ////commentServiceのdeleteCommentを実行
        commentService.deleteComment(id);
        // rootへリダイレクト
        // その後、rootディレクトリ、つまり、⑤サーバー側：投稿内容表示機能の処理へリダイレクト
        //投稿をテーブルから削除した後、投稿内容表示処理を経てトップ画面へ戻るという仕様
        return new ModelAndView("redirect:/");
    }
}
