package com.zf1976.ant.common.security.config;

import com.zf1976.ant.common.security.safe.evaluator.SecurityPermissionEvaluator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

/**
 * @author ant
 * Create by Ant on 2020/10/14 8:10 下午
 */
//@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
//public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
//
//    @Override
//    @Bean(name = "SecurityMethodSecurityExpressionHandler")
//    protected MethodSecurityExpressionHandler createExpressionHandler() {
//        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
//        expressionHandler.setPermissionEvaluator(new SecurityPermissionEvaluator());
//        return expressionHandler;
//    }
//
//}
