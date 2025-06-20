package chapter6.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.Message;
import chapter6.beans.User;
import chapter6.logging.InitApplication;
import chapter6.service.MessageService;

@WebServlet(urlPatterns = { "/edit" })
public class EditServlet extends HttpServlet{

	/**
	* ロガーインスタンスの生成
	*/
    Logger log = Logger.getLogger("twitter");

    /**
    * デフォルトコンストラクタ
    * アプリケーションの初期化を実施する。
    */
    public EditServlet() {
        InitApplication application = InitApplication.getInstance();
        application.init();

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

	  log.info(new Object(){}.getClass().getEnclosingClass().getName() +
        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

	    User user = (User) request.getSession().getAttribute("loginUser");
	    Message message = new Message();
	    List<String> errorMessages = new ArrayList<String>();
	    String messageId = request.getParameter("id");

	    if(!StringUtils.isBlank(messageId) || messageId.matches("^[0-9]+$")) {
	    	//検査okだったらselect
    	    message = new MessageService().select(Integer.parseInt(messageId));
	    }


        	errorMessages.add("不正なパラメータが入力されました");


        if(errorMessages.size() != 0) {
            request.setAttribute("errorMessages", errorMessages);
            request.setAttribute("user", user);
            request.getRequestDispatcher("top.jsp").forward(request, response);
            return;
        }

        request.setAttribute("user", user);
        request.setAttribute("message", message);
        request.getRequestDispatcher("edit.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

	  log.info(new Object(){}.getClass().getEnclosingClass().getName() +
        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

       Message message = new Message();
       message.setId(Integer.parseInt(request.getParameter("id")));
       message.setText(request.getParameter("text"));
       List<String> errorMessages = new ArrayList<String>();

       if(isValid(message.getText(),errorMessages)) {
    	   new MessageService().update(message);
       }
       if (errorMessages.size() != 0) {
           request.setAttribute("errorMessages", errorMessages);
           request.setAttribute("message", message);
           request.getRequestDispatcher("edit.jsp").forward(request, response);
           return;
       }


       response.sendRedirect("./");
    }

    private boolean isValid(String text, List<String> errorMessages) {

	  log.info(new Object(){}.getClass().getEnclosingClass().getName() +
        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

        if (StringUtils.isBlank(text)) {
            errorMessages.add("メッセージを入力してください");
        } else if (140 < text.length()) {
            errorMessages.add("140文字以下で入力してください");
        }

        if (errorMessages.size() != 0) {
            return false;
        }
        return true;
    }


}
