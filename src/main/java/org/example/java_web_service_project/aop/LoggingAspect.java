package org.example.java_web_service_project.aop;


import org.example.java_web_service_project.dto.response.SubmissionResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
    //toàn bộ Service — đo thời gian thực thi
    //nếu > 1 giây, ghi ERROR nếu ném exception
    @Around("execution(* org.example.java_web_service_project.service.*.*(..))")
    public Object logServiceTime(ProceedingJoinPoint pjp) throws Throwable {
        String cls = pjp.getSignature().getDeclaringType().getSimpleName();
        String method = pjp.getSignature().getName();
        long start = System.currentTimeMillis();

        try {
            Object result = pjp.proceed();
            long elapsed = System.currentTimeMillis() - start;

            if (elapsed > 1000) {
                log.warn("[SERVICE][SLOW] {}.{}() — {}ms (> 1s threshold)", cls, method, elapsed);
            } else {
                log.info("[SERVICE] {}.{}() — {}ms", cls, method, elapsed);
            }
            return result;

        } catch (Throwable ex) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("[SERVICE][ERROR] {}.{}() — {}ms | {}", cls, method, elapsed, ex.getMessage());
            throw ex;
        }
    }

    //toàn bộ Controller — log HTTP method + URI + thời gian
    @Around("execution(* org.example.java_web_service_project.controller.*.*(..))")
    public Object logControllerTime(ProceedingJoinPoint pjp) throws Throwable {
        String cls    = pjp.getSignature().getDeclaringType().getSimpleName();
        String method = pjp.getSignature().getName();
        long   start  = System.currentTimeMillis();

        String httpMethod = "UNKNOWN";
        String uri = "UNKNOWN";
        try {
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attr != null) {
                HttpServletRequest req = attr.getRequest();
                httpMethod = req.getMethod();
                uri = req.getRequestURI();
            }
        } catch (Exception ignored) {}

        log.info("[CONTROLLER][IN ] {} {} → {}.{}()", httpMethod, uri, cls, method);

        try {
            Object result = pjp.proceed();
            long elapsed  = System.currentTimeMillis() - start;
            log.info("[CONTROLLER][OUT] {} {} → {}.{}() — {}ms",
                    httpMethod, uri, cls, method, elapsed);
            return result;

        } catch (Throwable ex) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("[CONTROLLER][ERR] {} {} → {}.{}() — {}ms | {}",
                    httpMethod, uri, cls, method, elapsed, ex.getMessage());
            throw ex;
        }
    }

    //log sau khi chấm điểm thành công
    @AfterReturning(
            pointcut = "execution(* org.example.java_web_service_project.service.SubmissionService.grade(..))",
            returning = "result"
    )
    public void logAfterGrade(JoinPoint jp, Object result) {
        if (result instanceof SubmissionResponse r) {
            log.info("[GRADE] Lecturer ID: {} | Submission ID: {} | Score: {} | Course: {}",
                    r.getLecturerId(), r.getId(), r.getScore(), r.getCourseCode());
        }
    }

    @AfterThrowing(
            pointcut = "execution(* org.example.java_web_service_project.service.SubmissionService.grade(..))",
            throwing = "ex"
    )
    public void logGradeError(JoinPoint jp, Exception ex) {
        log.error("[GRADE][ERROR] {}", ex.getMessage());
    }

    // Log sau khi nộp bài (FR-07)
    @AfterReturning(
            pointcut = "execution(* org.example.java_web_service_project.service.SubmissionService.submit(..))",
            returning = "result"
    )
    public void logAfterSubmit(JoinPoint jp, Object result) {
        if (result instanceof SubmissionResponse r) {
            log.info("[SUBMIT] Student ID: {} | Course: {} | Status: {}",
                    r.getStudentId(), r.getCourseCode(), r.getStatus());
        }
    }

    // Log sau khi upload báo cáo
    @AfterReturning(
            pointcut = "execution(* org.example.java_web_service_project.service.SubmissionService.uploadReport(..))",
            returning = "result"
    )
    public void logAfterUpload(JoinPoint jp, Object result) {
        if (result instanceof SubmissionResponse r) {
            log.info("[UPLOAD] Student ID: {} | Course: {} | File: {}",
                    r.getStudentId(), r.getCourseCode(), r.getReportUrl());
        }
    }
}