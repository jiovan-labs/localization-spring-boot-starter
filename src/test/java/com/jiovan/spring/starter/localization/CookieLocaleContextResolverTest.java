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

package com.jiovan.spring.starter.localization;


import java.util.Locale;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import com.jiovan.spring.starter.localization.resolver.CookieLocaleContextResolver;


class CookieLocaleContextResolverTest {
    private static CookieLocaleContextResolver resolver() {
        CookieLocaleContextResolver resolver = new CookieLocaleContextResolver("locale");
        resolver.setParamName("lang");
        resolver.setDefaultLocale(Locale.forLanguageTag("en-US"));
        return resolver;
    }

    @Test
    void readsLocaleFromUrlParam() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("lang", "zh-CN");

        Assertions.assertEquals("zh-CN", resolver().resolveLocale(request).toLanguageTag());
    }

    @Test
    void urlParamHasPriorityOverCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("locale", "zh-TW"));
        request.setParameter("lang", "en-US");

        Assertions.assertEquals("en-US", resolver().resolveLocale(request).toLanguageTag());
    }

    @Test
    void fallsBackToCookieWhenParamIsMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("locale", "zh-TW"));

        Assertions.assertEquals("zh-TW", resolver().resolveLocale(request).toLanguageTag());
    }

    @Test
    void blankParamFallsBackToCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("locale", "zh-CN"));
        request.setParameter("lang", "");

        Assertions.assertEquals("zh-CN", resolver().resolveLocale(request).toLanguageTag());
    }

    @Test
    void fallsBackToDefaultWhenNothingIsAvailable() {
        Assertions.assertEquals("en-US", resolver().resolveLocale(new MockHttpServletRequest()).toLanguageTag());
    }
}
