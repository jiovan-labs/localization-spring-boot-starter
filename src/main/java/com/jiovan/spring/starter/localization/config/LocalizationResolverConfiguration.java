/*
 * Copyright (c) 2016 Jiovan Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jiovan.spring.starter.localization.config;


import java.util.Optional;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.webmvc.autoconfigure.DispatcherServletAutoConfiguration;
import org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleContextResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import com.jiovan.plexus.common.toolkit.util.CollectionPlainWraps;
import com.jiovan.plexus.common.toolkit.util.MapPlainWraps;
import com.jiovan.plexus.common.toolkit.util.StringUtilsWraps;
import com.jiovan.plexus.spring.toolkit.constant.SpringBeanConst;
import com.jiovan.spring.starter.localization.filter.CookieLocaleRequestContextFilter;
import com.jiovan.spring.starter.localization.property.LocalizationProperties;
import com.jiovan.spring.starter.localization.resolver.CookieLocaleContextResolver;


/**
 * Configuration of {@link org.springframework.web.servlet.LocaleContextResolver} for localization
 *
 * @author David Hsing
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnBooleanProperty(prefix = LocalizationViewConfiguration.PROPERTIES_PREFIX, name = "enabled", matchIfMissing = true)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@AutoConfigureAfter(value = DispatcherServletAutoConfiguration.class)
@AutoConfigureBefore(value = WebMvcAutoConfiguration.class)
@AutoConfigureOrder(value = Ordered.HIGHEST_PRECEDENCE + 8)
@Import(value = {LocalizationResolverConfiguration.Entry.class, LocalizationResolverConfiguration.Cookie.class, LocalizationResolverConfiguration.Session.class, LocalizationResolverConfiguration.Fixed.class})
public class LocalizationResolverConfiguration {
    @Order(value = 0)
    @EnableConfigurationProperties(value = LocalizationProperties.class)
    static class Entry {
    }


    @Order(value = 1)
    @ConditionalOnProperty(prefix = LocalizationViewConfiguration.PROPERTIES_PREFIX, name = "locale-resolver-type", havingValue = "cookie", matchIfMissing = true)
    static class Cookie {
        /**
         * Returns a {@link org.springframework.boot.web.servlet.FilterRegistrationBean} that registers a cookie-aware request context filter
         *
         * @param properties the locale change properties providing filter order, URL patterns and init parameters
         *
         * @return the configured request context filter registration
         *
         * @see org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter#requestContextFilter()
         */
        @Bean
        @ConditionalOnMissingBean(name = SpringBeanConst.REQUEST_CONTEXT_FILTER)
        @SuppressWarnings("JavadocReference")
        public FilterRegistrationBean<RequestContextFilter> requestContextFilterRegistration(@Nonnull LocalizationProperties properties) {
            CookieLocaleRequestContextFilter filter = new CookieLocaleRequestContextFilter(properties);
            LocalizationProperties.CookieLocaleFilter filterProps = properties.getCookieLocaleFilter();
            LocalizationProperties.CookieLocaleResolver resolverProps = properties.getCookieLocaleResolver();
            filter.setThreadLocaleInheritable(BooleanUtils.isTrue(filterProps.getThreadInheritable()));
            StringUtilsWraps.ifNotBlank(resolverProps.getCookieName(), filter::setCookieName);
            FilterRegistrationBean<RequestContextFilter> result = new FilterRegistrationBean<>(filter);
            result.setName(SpringBeanConst.REQUEST_CONTEXT_FILTER);
            Optional.ofNullable(filterProps.getFilerOrder()).ifPresent(result::setOrder);
            CollectionPlainWraps.ifNotEmpty(filterProps.getFilterPaths(), result::setUrlPatterns);
            MapPlainWraps.ifNotEmpty(filterProps.getFilterParams(), result::setInitParameters);
            return result;
        }

        /**
         * Returns a cookie-based {@link org.springframework.web.servlet.LocaleContextResolver} configured from the given properties
         *
         * @param properties the locale change properties providing cookie name, path, domain and default locale settings
         *
         * @return the configured cookie locale resolver
         *
         * @see org.springframework.web.servlet.i18n.CookieLocaleResolver
         */
        @Bean(name = DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME)
        @ConditionalOnProperty(prefix = LocalizationViewConfiguration.PROPERTIES_PREFIX + ".cookie-locale-resolver", name = "cookie-name")
        @ConditionalOnMissingBean(name = DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME)
        public LocaleContextResolver localeContextResolver(@Nonnull LocalizationProperties properties) {
            CookieLocaleContextResolver resolver = new CookieLocaleContextResolver(properties.getCookieLocaleResolver().getCookieName());
            resolver.setParamName(properties.getLocaleInterceptor().getParamName());
            LocalizationProperties.CookieLocaleResolver props = properties.getCookieLocaleResolver();
            StringUtilsWraps.ifNotBlank(props.getCookiePath(), resolver::setCookiePath);
            StringUtilsWraps.ifNotBlank(props.getCookieDomain(), resolver::setCookieDomain);
            Optional.ofNullable(props.getMaxAge()).ifPresent(resolver::setCookieMaxAge);
            resolver.setCookieHttpOnly(BooleanUtils.isTrue(props.getHttpOnly()));
            resolver.setCookieSecure(BooleanUtils.isTrue(props.getSecureProtocol()));
            resolver.setRejectInvalidCookies(BooleanUtils.isTrue(props.getRejectInvalid()));
            Optional.ofNullable(props.getDefaultLocale()).ifPresent(resolver::setDefaultLocale);
            Optional.ofNullable(props.getDefaultTimeZone()).ifPresent(resolver::setDefaultTimeZone);
            return resolver;
        }
    }


    @Order(value = 2)
    @ConditionalOnProperty(prefix = LocalizationViewConfiguration.PROPERTIES_PREFIX, name = "locale-resolver-type", havingValue = "session")
    static class Session {
        /**
         * Returns a session-based {@link org.springframework.web.servlet.LocaleContextResolver} configured from the given properties
         *
         * @param properties the locale change properties providing session attribute names and default locale settings
         *
         * @return the configured session locale resolver
         *
         * @see org.springframework.web.servlet.i18n.SessionLocaleResolver
         */
        @Bean(name = DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME)
        @ConditionalOnMissingBean(name = DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME)
        public LocaleContextResolver localeContextResolver(@Nonnull LocalizationProperties properties) {
            SessionLocaleResolver resolver = new SessionLocaleResolver();
            LocalizationProperties.SessionLocaleResolver props = properties.getSessionLocaleResolver();
            StringUtilsWraps.ifNotBlank(props.getSessionName(), resolver::setLocaleAttributeName);
            StringUtilsWraps.ifNotBlank(props.getTimezoneName(), resolver::setTimeZoneAttributeName);
            Optional.ofNullable(props.getDefaultLocale()).ifPresent(resolver::setDefaultLocale);
            Optional.ofNullable(props.getDefaultTimeZone()).ifPresent(resolver::setDefaultTimeZone);
            return resolver;
        }
    }


    @Order(value = 3)
    @ConditionalOnProperty(prefix = LocalizationViewConfiguration.PROPERTIES_PREFIX, name = "locale-resolver-type", havingValue = "fixed")
    static class Fixed {
        /**
         * Returns a fixed {@link org.springframework.web.servlet.LocaleContextResolver} configured from the given properties
         *
         * @param properties the locale change properties providing the default locale and time zone
         *
         * @return the configured fixed locale resolver
         *
         * @see org.springframework.web.servlet.i18n.FixedLocaleResolver
         */
        @Bean(name = DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME)
        @ConditionalOnMissingBean(name = DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME)
        public LocaleContextResolver localeContextResolver(@Nonnull LocalizationProperties properties) {
            FixedLocaleResolver resolver = new FixedLocaleResolver();
            LocalizationProperties.SessionLocaleResolver props = properties.getSessionLocaleResolver();
            Optional.ofNullable(props.getDefaultLocale()).ifPresent(resolver::setDefaultLocale);
            Optional.ofNullable(props.getDefaultTimeZone()).ifPresent(resolver::setDefaultTimeZone);
            return resolver;
        }
    }
}
