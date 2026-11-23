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
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import com.jiovan.plexus.common.toolkit.constant.AssertMessageConst;
import com.jiovan.plexus.common.toolkit.util.CollectionPlainWraps;
import com.jiovan.plexus.common.toolkit.util.StringUtilsWraps;
import com.jiovan.spring.starter.localization.property.LocalizationProperties;
import lombok.RequiredArgsConstructor;


/**
 * Configuration of {@link org.springframework.web.servlet.i18n.LocaleChangeInterceptor} for localization
 *
 * @author David Hsing
 */
@Configuration
@ConditionalOnBooleanProperty(prefix = LocalizationViewConfiguration.PROPERTIES_PREFIX, name = "enabled", matchIfMissing = true)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(value = LocalizationProperties.class)
@RequiredArgsConstructor
public class LocalizationInterceptorConfiguration implements WebMvcConfigurer {
    private final LocalizationProperties properties;

    /**
     * Returns a {@link org.springframework.web.servlet.i18n.LocaleChangeInterceptor} configured from the locale interceptor properties
     *
     * @return the configured locale change interceptor
     *
     * Usage:
     * <pre><code>
     *     &lt;a href="?lang=en-US"&gt;English&lt;/a&gt;
     *     &lt;a href="?lang=en_US"&gt;English&lt;/a&gt;
     * </code></pre>
     *
     * @see "http://guanxi.iteye.com/blog/2304607"
     */
    @Bean
    @ConditionalOnMissingBean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        LocalizationProperties.LocaleInterceptor props = properties.getLocaleInterceptor();
        StringUtilsWraps.ifNotBlank(props.getParamName(), interceptor::setParamName);
        interceptor.setIgnoreInvalidLocale(BooleanUtils.isTrue(props.getIgnoreInvalidLocale()));
        return interceptor;
    }

    @Override
    public void addInterceptors(@Nonnull InterceptorRegistry registry) {
        LocalizationProperties.LocaleInterceptor props = properties.getLocaleInterceptor();
        Assert.notEmpty(props.getInterceptPaths(), AssertMessageConst.NOT_EMPTY);
        InterceptorRegistration registration = registry.addInterceptor(localeChangeInterceptor()).addPathPatterns(props.getInterceptPaths());
        Optional.ofNullable(props.getInterceptorOrder()).ifPresent(registration::order);
        CollectionPlainWraps.ifNotEmpty(props.getExcludePaths(), element -> registration.excludePathPatterns(element));
    }
}
