package com.si.mindhealth.controllers;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute("jakarta.servlet.error.status_code");
        int statusCode = status != null ? (Integer) status : 500;

        model.addAttribute("status", statusCode);
        if (statusCode == 403) {
            model.addAttribute("message", "Bạn không có quyền truy cập trang này!");
        } else if (statusCode == 404) {
            model.addAttribute("message", "Không tìm thấy trang bạn yêu cầu!");
        } else {
            model.addAttribute("message", "Đã xảy ra lỗi không mong muốn!");
        }

        return "error";
    }
}
