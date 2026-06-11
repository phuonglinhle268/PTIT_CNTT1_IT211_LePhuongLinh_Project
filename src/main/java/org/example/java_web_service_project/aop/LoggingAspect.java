package org.example.java_web_service_project.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.example.java_web_service_project.dto.response.SubmissionResponse;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    //@AfterReturning — Tự động ghi log sau khi chấm điểm thành công.
    @AfterReturning(
            pointcut = "execution(* org.example.java_web_service_project.service.SubmissionService.grade(..))",
            returning = "result"
    )
    public void logAfterGrading(JoinPoint joinPoint, Object result) {
        if (result instanceof SubmissionResponse response) {
            log.info("[GRADE] Lecturer ID: {} graded Submission ID: {} with Score: {}",
                    response.getLecturerId(),
                    response.getId(),
                    response.getScore());
        }
    }

    //Ghi log khi chấm điểm ném ngoại lệ
    @AfterThrowing(
            pointcut = "execution(* org.example.java_web_service_project.service.SubmissionService.grade(..))",
            throwing = "ex"
    )
    public void logGradingError(JoinPoint joinPoint, Exception ex) {
        log.error("[GRADE ERROR] Method: {} | Error: {}",
                joinPoint.getSignature().getName(), ex.getMessage());
    }

    //Ghi log sau khi sinh viên nộp bài thành công
    @AfterReturning(
            pointcut = "execution(* org.example.java_web_service_project.service.SubmissionService.submit(..))",
            returning = "result"
    )
    public void logAfterSubmit(JoinPoint joinPoint, Object result) {
        if (result instanceof SubmissionResponse response) {
            log.info("[SUBMIT] Student ID: {} submitted Course ID: {} | Status: {}",
                    response.getStudentId(),
                    response.getCourseId(),
                    response.getStatus());
        }
    }

    //Ghi log sau khi upload file báo cáo
    @AfterReturning(
            pointcut = "execution(* org.example.java_web_service_project.service.SubmissionService.uploadReport(..))",
            returning = "result"
    )
    public void logAfterUpload(JoinPoint joinPoint, Object result) {
        if (result instanceof SubmissionResponse response) {
            log.info("[UPLOAD] Student ID: {} uploaded report for Course ID: {} | URL: {}",
                    response.getStudentId(),
                    response.getCourseId(),
                    response.getReportUrl());
        }
    }

    //Đo thời gian xử lý toàn bộ lop service
    @Around("execution(* org.example.java_web_service_project.service.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long elapsed = System.currentTimeMillis() - start;

        if (elapsed > 500) {
            log.warn("[SLOW] {}.{}() took {}ms",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    elapsed);
        } else {
            log.debug("[PERF] {}.{}() took {}ms",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    elapsed);
        }
        return result;
    }

    //Ghi log mỗi request vào Controller
    @Before("execution(* org.example.java_web_service_project.controller.*.*(..))")
    public void logControllerRequest(JoinPoint joinPoint) {
        log.debug("[REQUEST] {}.{}()",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
    }
}
