package com.example.forum.controller.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


//Viewへの入出力時に使用するJavaBeansのような入れ物だと思ってもらってOK

//「@Getter」と「@Setter」はその名の通り、getterとsetterを自動生成するアノテーションです。
@Getter
@Setter
public class ReportForm {

    private int id;
    @NotEmpty(message="投稿内容を入力してください")
    private String content;
    private Date createdDate;
    private Date updatedDate;

}
