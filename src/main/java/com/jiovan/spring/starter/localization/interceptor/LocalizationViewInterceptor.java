/*
 * Copyright (c) 2022 Jiovan Ltd. All rights reserved.
 *
 * Jiovan Confidential
 *
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has been
 * deposited with the National Copyright Administration of China.
 */

package com.jiovan.spring.starter.localization.interceptor;


import java.util.LinkedHashMap;
import java.util.Map;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import com.jiovan.plexus.common.toolkit.util.MapPlainWraps;
import com.jiovan.plexus.spring.toolkit.util.MessageSourceWraps;
import com.jiovan.plexus.spring.toolkit.util.WebUtilsWraps;
import com.jiovan.spring.starter.localization.property.LocalizationProperties;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


/**
 * {@link org.springframework.web.servlet.HandlerInterceptor} for localization
 *
 * @author David Hsing
 */
@RequiredArgsConstructor
public class LocalizationViewInterceptor implements HandlerInterceptor, MessageSourceAware {
    private final LocalizationProperties properties;

    @Setter
    protected MessageSource messageSource;

    @Override
    public void postHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler, @Nullable ModelAndView view) {
        LocalizationProperties.ModelAndView props = properties.getModelAndView();
        if (WebUtilsWraps.isCanonicalModelView(view) && StringUtils.isNotBlank(props.getViewAttribute()) && !CollectionUtils.isEmpty(props.getTagNames())) {
            if (BooleanUtils.isNotTrue(props.getTagMultilingual())) {
                view.addObject(props.getViewAttribute(), props.getTagNames());
                return;
            }
            Map<String, String> tagNames = new LinkedHashMap<>(props.getTagNames());
            MapPlainWraps.recompute(tagNames, (key, value) -> MessageSourceWraps.getMessage(messageSource, value, value, LocaleContextHolder.getLocale()));
            view.addObject(props.getViewAttribute(), tagNames);
        }
    }
}
