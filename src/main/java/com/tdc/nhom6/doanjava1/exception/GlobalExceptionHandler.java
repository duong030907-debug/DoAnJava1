package com.tdc.nhom6.doanjava1.exception;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Bắt tất cả exception chưa được xử lý trong controller và render
 * trang error.jsp thay vì hiện trang trắng mặc định của Tomcat.
 *
 * Cũng là nơi tập trung log lỗi — mỗi exception chỉ được log 1 lần
 * tại đây thay vì rải rác khắp các controller.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Lỗi nghiệp vụ (sách không tồn tại, tài khoản trùng, v.v.) */
    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleRuntime(RuntimeException ex, HttpServletRequest req) {
        log.warn("Business error at {}: {}", req.getRequestURI(), ex.getMessage());
        return errorView(ex.getMessage(), req.getRequestURI(), HttpStatus.BAD_REQUEST);
    }

    /** File upload vượt quá 50 MB */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ModelAndView handleMaxUpload(MaxUploadSizeExceededException ex, HttpServletRequest req) {
        log.warn("Upload too large at {}", req.getRequestURI());
        return errorView("Tệp tải lên vượt quá dung lượng tối đa cho phép (50 MB).",
                req.getRequestURI(), HttpStatus.PAYLOAD_TOO_LARGE);
    }

    /** URL không tồn tại (404) */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNotFound(NoHandlerFoundException ex, HttpServletRequest req) {
        return errorView("Trang hoặc tài nguyên không tồn tại: " + req.getRequestURI(),
                req.getRequestURI(), HttpStatus.NOT_FOUND);
    }

    /** Mọi exception khác (lỗi hệ thống) */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneral(Exception ex, HttpServletRequest req) {
        log.error("Unexpected error at {}", req.getRequestURI(), ex);
        return errorView("Lỗi hệ thống — vui lòng thử lại sau.\nChi tiết: " + ex.getMessage(),
                req.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ── Helper ────────────────────────────────────────────────────────
    private ModelAndView errorView(String message, String path, HttpStatus status) {
        ModelAndView mav = new ModelAndView("error");
        mav.setStatus(status);
        mav.addObject("pageTitle", "Lỗi " + status.value());
        mav.addObject("errorMessage", message);
        mav.addObject("errorPath",    path);
        mav.addObject("errorStatus",  status.value());
        return mav;
    }
}
