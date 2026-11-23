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

package com.jiovan.spring.starter.localization.dialect;


import jakarta.annotation.Nonnull;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;
import com.jiovan.spring.starter.localization.factory.LocalizationExpressionFactory;
import lombok.Getter;
import lombok.Setter;


/**
 * {@link org.thymeleaf.dialect.IExpressionObjectDialect} for localization
 *
 * @author David Hsing
 */
@Getter
@Setter
@SuppressWarnings("unused")
public class LocalizationThymeleafDialect extends AbstractDialect implements IExpressionObjectDialect {
    private static final String DIALECT_NAME = "Localization";    // $NON-NLS-1$
    private LocalizationExpressionFactory expressionFactory;

    public LocalizationThymeleafDialect() {
        super(DIALECT_NAME);
    }

    public LocalizationThymeleafDialect(@Nonnull LocalizationExpressionFactory factory) {
        super(DIALECT_NAME);
        expressionFactory = factory;
    }

    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        return expressionFactory;
    }
}
