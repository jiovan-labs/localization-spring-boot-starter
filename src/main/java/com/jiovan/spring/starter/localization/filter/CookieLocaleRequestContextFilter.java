/*
 * Copyright (c) 2022 Jiovan Ltd. All rights reserved.
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

package com.jiovan.spring.starter.localization.filter;


import java.io.IOException;
import java.util.Locale;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.jiovan.plexus.spring.toolkit.util.AntPathWraps;
import com.jiovan.plexus.spring.toolkit.util.UriUtilsWraps;
import com.jiovan.plexus.spring.toolkit.util.WebUtilsWraps;
import com.jiovan.spring.starter.localization.property.LocalizationProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


/**
 * {@link jakarta.servlet.Filter} for detecting locale from cookies, and setting up request context
 *
 * @author David Hsing
 *
 * @see org.springframework.web.filter.RequestContextFilter
 * @see org.springframework.web.filter.OncePerRequestFilter
 * @see org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration
 * @see org.springframework.boot.servlet.filter.OrderedRequestContextFilter
 */
@RequiredArgsConstructor
@Getter
@Setter
@SuppressWarnings("unused")
public class CookieLocaleRequestContextFilter extends OrderedRequestContextFilter {
    private final LocalizationProperties properties;
    private String cookieName;
    private boolean threadLocaleInheritable;

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain chain) throws ServletException, IOException {
        if (StringUtils.isBlank(cookieName)) {
            chain.doFilter(request, response);
            return;
        }
        Locale locale = ObjectUtils.getIfNull(WebUtilsWraps.getLocaleFromCookie(request, cookieName), request.getLocale());
        LocaleContextHolder.setLocale(locale, threadLocaleInheritable);
        ServletRequestAttributes attributes = new ServletRequestAttributes(request, response);
        RequestContextHolder.setRequestAttributes(attributes, threadLocaleInheritable);
        try {
            chain.doFilter(request, response);
        } finally {
            LocaleContextHolder.resetLocaleContext();
            RequestContextHolder.resetRequestAttributes();
            attributes.requestCompleted();
        }
    }

    @Override
    protected boolean shouldNotFilter(@Nonnull HttpServletRequest request) throws ServletException {
        LocalizationProperties.CookieLocaleFilter props = properties.getCookieLocaleFilter();
        if (!CollectionUtils.isEmpty(props.getExcludePaths())) {
            String servletPath = UriUtilsWraps.getServletPath(request);
            return AntPathWraps.matchAnyPatterns(servletPath, props.getExcludePaths());
        }
        return super.shouldNotFilter(request);
    }
}
