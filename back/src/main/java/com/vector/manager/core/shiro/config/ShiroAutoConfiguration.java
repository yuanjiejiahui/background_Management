package com.vector.manager.core.shiro.config;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.ExecutorServiceSessionValidationScheduler;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import com.vector.manager.core.common.Constant;
import com.vector.manager.core.shiro.cache.CacheManagerBuilder;
import com.vector.manager.core.shiro.filter.KickoutSessionControlFilter;
import com.vector.manager.core.shiro.filter.LMFormAuthenticationFilter;
import com.vector.manager.core.shiro.filter.LMPathMatchingFilter;
import com.vector.manager.core.shiro.listener.LongmarchSessionListener;
import com.vector.manager.core.shiro.realm.CustomRealm;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroAutoConfiguration {

    @Bean(name = "LMCacheManager")
    public CacheManager cacheManager(ObjectProvider<org.springframework.cache.CacheManager> springCacheManagerPvd,
                                     RedisConnectionFactory redisConnectionFactory) {
        CacheManagerBuilder cacheManagerBuilder = new CacheManagerBuilder(springCacheManagerPvd, redisConnectionFactory);
        return cacheManagerBuilder.build();
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, CacheManager cacheManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("cross", new LMPathMatchingFilter());
        filterMap.put("authc", new LMFormAuthenticationFilter());
        KickoutSessionControlFilter kickoutSessionControlFilter = new KickoutSessionControlFilter();
        kickoutSessionControlFilter.setCacheManager(cacheManager);
        kickoutSessionControlFilter.setSessionManager(sessionManager(cacheManager));
        filterMap.put("Kickout", kickoutSessionControlFilter);
        shiroFilterFactoryBean.setFilters(filterMap);
        shiroFilterFactoryBean.setLoginUrl("/noLogin");
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // authc:??????url????????????????????????????????????; anon:??????url????????????????????????
        filterChainDefinitionMap.put("/doc.html", "anon");
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/douyin/callback", "anon");
        filterChainDefinitionMap.put("/mavon-editor.js", "anon");
        filterChainDefinitionMap.put("/webjars/**", "anon");
        filterChainDefinitionMap.put("/swagger-resources/**", "anon");
        filterChainDefinitionMap.put("/v2/api-docs", "anon");
        filterChainDefinitionMap.put("/register", "anon");
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/logout", "cross,anon");
        //????????????API??????
        filterChainDefinitionMap.put("/api/**", "anon");
        //????????????????????????????????????????????????????????????????????????????????? url ???????????? ????????????????????????
        filterChainDefinitionMap.put("/**", "cross,Kickout,authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean
    public DefaultWebSecurityManager securityManager(CacheManager cacheManager) {
        DefaultWebSecurityManager defaultSecurityManager = new DefaultWebSecurityManager();
        defaultSecurityManager.setRealm(customRealm());
        defaultSecurityManager.setCacheManager(cacheManager);
        defaultSecurityManager.setSessionManager(sessionManager(cacheManager));
        defaultSecurityManager.setRememberMeManager(rememberMeManager());
        return defaultSecurityManager;
    }

    @Bean
    public DefaultWebSessionManager sessionManager(CacheManager cacheManager) {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        SimpleCookie simpleCookie = new SimpleCookie("long-march");
        simpleCookie.setHttpOnly(true);
        simpleCookie.setMaxAge(-1);
        sessionManager.setSessionIdCookie(simpleCookie);

        sessionManager.setGlobalSessionTimeout(Constant.SESSION_TIMEOUT);
        // Session Listeners ????????????????????????????????????
        sessionManager.setSessionListeners(Arrays.asList(new LongmarchSessionListener()));
        sessionManager.setCacheManager(cacheManager);
        // ??????????????????????????????????????????
        sessionManager.setDeleteInvalidSessions(true);
        // ???????????????????????????
        sessionManager.setSessionValidationSchedulerEnabled(true);
        // ???????????????????????????
        ExecutorServiceSessionValidationScheduler scheduler = new ExecutorServiceSessionValidationScheduler();
        scheduler.setSessionManager(sessionManager);
        scheduler.setInterval(Constant.SESSION_CLEAR_TILE);
        // ???????????????????????????
        scheduler.enableSessionValidation();
        sessionManager.setSessionValidationScheduler(scheduler);
        sessionManager.setSessionDAO(new LMSessionDAO(cacheManager));
        return sessionManager;
    }

    @Bean
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
        SimpleCookie simpleCookie = new SimpleCookie("remember-long-march");
        simpleCookie.setHttpOnly(true);
        simpleCookie.setMaxAge(-1);
        rememberMeManager.setCookie(simpleCookie);
        byte[] decode = Base64.decode("6ZmI6I2j5Y+R5aSn5ZOlAA==");
        rememberMeManager.setCipherKey(decode);
        return rememberMeManager;
    }

    @Bean
    public CustomRealm customRealm() {
        CustomRealm customRealm = new CustomRealm();
        // ??????realm,??????credentialsMatcher??????????????????????????????
        customRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        customRealm.setCachingEnabled(true);
//        customRealm.setCacheManager(ehCacheManager());
        //????????????????????????????????????AuthenticationInfo???????????????false
        customRealm.setAuthenticationCachingEnabled(true);
        //??????AuthenticationInfo????????????????????? ???ehcache-shiro.xml???????????????????????????
        customRealm.setAuthenticationCacheName(Constant.AUTHENTICATION_CACHE);
        //??????????????????????????????AuthorizationInfo???????????????false
        customRealm.setAuthorizationCachingEnabled(true);
        //??????AuthorizationInfo?????????????????????  ???ehcache-shiro.xml???????????????????????????
        customRealm.setAuthorizationCacheName(Constant.AUTHORIZATION_CACHE);
        return customRealm;
    }

    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        // ????????????:????????????MD5??????;
        hashedCredentialsMatcher.setHashAlgorithmName("MD5");
        // ???????????????????????????????????????????????? md5(md5(""));
        hashedCredentialsMatcher.setHashIterations(2);
        // storedCredentialsHexEncoded?????????true???????????????????????????????????????Hex?????????false??????Base64??????
        hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
        return hashedCredentialsMatcher;
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

}
