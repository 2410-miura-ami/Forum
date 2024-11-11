package com.example.forum.service;

import com.example.forum.controller.form.CommentForm;
import com.example.forum.controller.form.ReportForm;
import com.example.forum.repository.CommentRepository;
import com.example.forum.repository.ReportRepository;
import com.example.forum.repository.entity.Comment;
import com.example.forum.repository.entity.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    CommentRepository commentRepository;

    /*
     * レコード取得処理
     * findAllReportメソッドで、commentRepositoryのfindAllを実行します
     */
    public List<CommentForm> findByIdComment(Integer id) {
        //findByIdメソッドは JpaRepository で定義されているため、CommentRepositoryでメソッドの定義をすることなく、使用できます
        List<Comment> results = new ArrayList<>();
        //results.add((Report)reportRepository.findById(id).orElse(null));
        results.add((Comment)commentRepository.findById(id).orElse(null));
        List<CommentForm> comments = setCommentForm(results);
        return comments;
    }

    /*
     * DBから取得したデータをFormに設定
     * （取得した値をsetCommentFormメソッドでEntity→Formに詰め直して、Controllerに戻しています。
     * これはEntityはデータアクセス時の入れ物、FormはViewへの入出力時に使用する入れ物と役割を分けているためです
     */
    private List<CommentForm> setCommentForm(List<Comment> results) {
        List<CommentForm> comments = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            CommentForm comment = new CommentForm();
            Comment result = results.get(i);
            comment.setId(result.getId());
            comment.setContent(result.getContent());
            comment.setReportId(result.getReportId());
            comment.setCreatedDate(result.getCreatedDate());
            comments.add(comment);
        }
        return comments;
    }

    /*
     * レコード追加
     */
    public void saveComment(CommentForm reqComment) {
        Comment saveComment = setCommentEntity(reqComment);
        //saveメソッドは JpaRepository で定義されているため、ReportRepositoryでメソッドの定義をすることなく、使用できます
        commentRepository.save(saveComment);
    }

    /*
     * リクエストから取得した情報をEntityに設定
     * setReportEntityメソッドでFormからEntityに詰め直してRepositoryに渡しています
     * これはEntityはデータアクセス時の入れ物、FormはViewへの入出力時に使用する入れ物と役割を分けているためです
     */
    private Comment setCommentEntity(CommentForm reqComment) {
        Comment comment = new Comment();
        comment.setId(reqComment.getId());
        comment.setContent(reqComment.getContent());
        comment.setReportId(reqComment.getReportId());
        comment.setCreatedDate(reqComment.getCreatedDate());
        comment.setUpdatedDate(reqComment.getUpdatedDate());
        return comment;
    }
}