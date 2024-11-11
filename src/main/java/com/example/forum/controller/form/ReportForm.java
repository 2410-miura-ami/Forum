package com.example.forum.controller.form;

import lombok.Getter;
import lombok.Setter;

//Viewへの入出力時に使用するJavaBeansのような入れ物だと思ってもらってOK

//「@Getter」と「@Setter」はその名の通り、getterとsetterを自動生成するアノテーションです。
@Getter
@Setter
public class ReportForm {

    private int id;
    private String content;


}
