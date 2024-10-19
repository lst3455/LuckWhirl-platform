package org.example.types.annotation;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface RateLimitAccessInterceptor {

    String key() default "all";

    double permitPerSecond();

    double blackListCount() default 0;

    String feedbackMethod();
}
