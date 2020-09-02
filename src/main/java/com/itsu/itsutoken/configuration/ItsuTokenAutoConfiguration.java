package com.itsu.itsutoken.configuration;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.itsu.itsutoken.checker.TokenChecker;
import com.itsu.itsutoken.table.TableSample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cn.hutool.core.collection.CollectionUtil;

@Configuration
// @EnableConfigurationProperties(ItsuTokenProperties.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ConditionalOnClass({ TokenChecker.class })
@ConditionalOnProperty(name = "itsu-token.enable", havingValue = "true", matchIfMissing = true)
public class ItsuTokenAutoConfiguration {

    @Autowired
    private ItsuTokenProperties properties;

    @Bean
    public TokenChecker<? extends TableSample> tokenChecker(DataSource dataSource,
            DataSourceProperties dataSourceProperties) {
        TokenChecker<? extends TableSample> tokenChecker = properties.getType().generateTokenChecher();

        if (properties.getInit().isAutoCreateTable()) {
            if (CollectionUtil.isEmpty(dataSourceProperties.getSchema())) {
                String schemaLocation = properties.getInit().getSchemaLocation();
                dataSourceProperties.setSchema(Arrays.asList(schemaLocation));
            }
            DataSourceInitializer dataSourceInitializer = new DataSourceInitializer(dataSource, dataSourceProperties);
            dataSourceInitializer.createSchema();
        }

        return tokenChecker;
    }

    @Bean
    public WebMvcConfigurer tokenRegisterWebMvcConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new HandlerInterceptor() {

                    @Override
                    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                            throws Exception {
                        String requestURI = request.getRequestURI();
                        if (requestURI.endsWith(properties.getWebRegister().getRegisterUrl())) {
                            if (properties.getWebRegister().isEnable()) {
                                return true;
                            } else {
                                response.getWriter().write("itsu-token.web-register is not set to true");
                                return false;
                            }
                        } else if (requestURI.endsWith("registerToken.html")) {
                            if (properties.getWebRegister().isEnable()) {
                                String login = (String) request.getSession().getAttribute("login");
                                if ("yes".equalsIgnoreCase(login)) {
                                    return true;
                                } else {
                                    response.getWriter().write("Authorization error");
                                    response.setStatus(401);
                                    return false;
                                }
                            } else {
                                response.getWriter().write("itsu-token.web-register is not set to true");
                                return false;
                            }

                        } else {
                            return true;
                        }

                    }

                });
            }

        };
    }

}