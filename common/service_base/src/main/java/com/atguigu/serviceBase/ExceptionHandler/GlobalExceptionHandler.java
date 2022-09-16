package com.atguigu.serviceBase.ExceptionHandler;



import com.atguigu.commonutils.ExceptionUtil;
import com.atguigu.commonutils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public R getExceptionMessage(Exception exception) {
        exception.printStackTrace();
        //输出异常到日志文件中
        log.error(ExceptionUtil.getMessage(exception));
        return R.error().message("全局异常处理");
    }

    @ExceptionHandler(ArithmeticException.class)
    public R getExceptionMessage(ArithmeticException exception) {
        exception.printStackTrace();
        return R.error().message("ArithmeticException异常处理");
    }

    @ExceptionHandler(GuliException.class)
    public R getExceptionMessage(GuliException exception) {
        //输出异常到日志文件中
        log.error(ExceptionUtil.getMessage(exception));

        exception.printStackTrace();
        return R.error().code(exception.getCode()).message(exception.getMsg());
    }

}
