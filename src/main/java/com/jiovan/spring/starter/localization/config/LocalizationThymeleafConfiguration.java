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


import jakarta.annotation.Nonnull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.thymeleaf.autoconfigure.ThymeleafAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.Thymeleaf;
import com.jiovan.plexus.spring.condition.annotation.ConditionalOnAllBooleanProperties;
import com.jiovan.spring.starter.localization.dialect.LocalizationThymeleafDialect;
import com.jiovan.spring.starter.localization.factory.LocalizationExpressionFactory;
import com.jiovan.spring.starter.localization.property.LocalizationProperties;
import com.jiovan.spring.starter.localization.support.LocalizationTagObserver;
import lombok.RequiredArgsConstructor;


/**
 * Configuration of {@link org.thymeleaf.dialect.IDialect} for localization
 *
 * @author David Hsing
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnAllBooleanProperties(value = {
    @ConditionalOnBooleanProperty(prefix = LocalizationViewConfiguration.PROPERTIES_PREFIX, name = "enabled", matchIfMissing = true),
    @ConditionalOnBooleanProperty(prefix = LocalizationViewConfiguration.PROPERTIES_PREFIX, name = "thymeleaf-dialect", matchIfMissing = true)
})
@ConditionalOnClass(value = Thymeleaf.class)
@EnableConfigurationProperties(value = LocalizationProperties.class)
@RequiredArgsConstructor
@AutoConfigureAfter(value = ThymeleafAutoConfiguration.class)
public class LocalizationThymeleafConfiguration implements BeanPostProcessor {
    private final LocalizationProperties properties;

    @Override
    public Object postProcessAfterInitialization(@Nonnull Object bean, @Nonnull String beanName) throws BeansException {
        if (bean instanceof TemplateEngine instance) {
            LocalizationTagObserver observer = new LocalizationTagObserver(properties);
            LocalizationExpressionFactory factory = new LocalizationExpressionFactory(observer);
            LocalizationThymeleafDialect dialect = new LocalizationThymeleafDialect(factory);
            instance.addDialect(dialect);
        }
        return bean;
    }
}
