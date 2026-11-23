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

package com.jiovan.spring.starter.localization.config;


import java.util.Optional;
import jakarta.annotation.Nonnull;
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
import com.jiovan.plexus.common.toolkit.constant.AssertMessageConst;
import com.jiovan.plexus.common.toolkit.util.CollectionPlainWraps;
import com.jiovan.spring.starter.localization.interceptor.LocalizationViewInterceptor;
import com.jiovan.spring.starter.localization.property.LocalizationProperties;
import lombok.RequiredArgsConstructor;


/**
 * Configuration of view interceptor for localization
 *
 * @author David Hsing
 */
@Configuration
@ConditionalOnBooleanProperty(prefix = LocalizationViewConfiguration.PROPERTIES_PREFIX, name = "enabled", matchIfMissing = true)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(value = LocalizationProperties.class)
@RequiredArgsConstructor
public class LocalizationViewConfiguration implements WebMvcConfigurer {
    public static final String PROPERTIES_PREFIX = "jiovan.localization";    // $NON-NLS-1$
    public static final String VIEW_INTERCEPTOR = "localizationViewInterceptor";    // $NON-NLS-1$
    private final LocalizationProperties properties;

    /**
     * Returns the {@link com.jiovan.spring.starter.localization.interceptor.LocalizationViewInterceptor} that applies the locale to the view
     *
     * @return a new {@link com.jiovan.spring.starter.localization.interceptor.LocalizationViewInterceptor} configured with the localization properties
     */
    @Bean(name = VIEW_INTERCEPTOR)
    @ConditionalOnMissingBean
    public LocalizationViewInterceptor viewInterceptor() {
        return new LocalizationViewInterceptor(properties);
    }

    @Override
    public void addInterceptors(@Nonnull InterceptorRegistry registry) {
        LocalizationProperties.ModelAndView props = properties.getModelAndView();
        Assert.notEmpty(props.getInterceptPaths(), AssertMessageConst.NOT_EMPTY);
        InterceptorRegistration registration = registry.addInterceptor(viewInterceptor()).addPathPatterns(props.getInterceptPaths());
        Optional.ofNullable(props.getInterceptorOrder()).ifPresent(registration::order);
        CollectionPlainWraps.ifNotEmpty(props.getExcludePaths(), element -> registration.excludePathPatterns(element));
    }
}
