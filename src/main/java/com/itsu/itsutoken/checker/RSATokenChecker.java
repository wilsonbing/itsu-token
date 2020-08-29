package com.itsu.itsutoken.checker;

import com.itsu.itsutoken.annotation.Token;
import com.itsu.itsutoken.exception.TokenCheckException;
import com.itsu.itsutoken.table.RSATableSample;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class RSATokenChecker extends TokenChecker<RSATableSample> {
    private static final Logger log = LoggerFactory.getLogger(RSATokenChecker.class);

    public RSATokenChecker(RSATableSample tableSample) {
        this.setTableSample(tableSample);
    }

    @Pointcut("@annotation(com.itsu.itsutoken.annotation.Token)")
    public void rule() {
    }

    @Before("rule()&&@annotation(tokenAnno)")
    public void before(JoinPoint joinPoint, Token tokenAnno) throws TokenCheckException {
        if (tokenAnno.requried()) {
            check(joinPoint);
        } else {
            log.info("token marked required is false, jump token check");
        }
    }

    @Override
    public void check(JoinPoint joinPoint) throws TokenCheckException {
        // TODO Auto-generated method stub

    }

}