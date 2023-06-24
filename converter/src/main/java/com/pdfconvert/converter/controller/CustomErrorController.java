package com.pdfconvert.converter.controller;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        // Retrieve the error status code
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");

        // Handle different status codes accordingly
        if (statusCode != null) {
            if (statusCode == 400) {
                // Bad Request
                return "error-400";
            } else if (statusCode == 404) {
                // Not Found
                return "error-404";
            } else if (statusCode == 500) {
                // Internal Server Error
                return "error-500";
            }
        }

        // Default error page
        return "error";
    }

//    @Override
//    public String getErrorPath() {
//        return "/error";
//    }
}
