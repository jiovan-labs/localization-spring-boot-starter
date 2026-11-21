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

package com.jiovan.spring.starter.localization.support;


import java.util.Collection;
import java.util.Locale;
import jakarta.annotation.Nullable;
import com.jiovan.plexus.common.toolkit.util.LocalePlainWraps;
import com.jiovan.spring.starter.localization.property.LocaleChangeProperties;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@SuppressWarnings("unused")
public class LocaleChangeTagObserver {
    private final LocaleChangeProperties properties;

    /**
     * Returns whether the configured tag names contain the given language tag
     *
     * @param languageTag the language tag to look up
     *
     * @return {@code true} if the language tag is configured, {@code false} otherwise
     */
    public boolean contains(@Nullable String languageTag) {
        return properties.getModelAndView().getTagNames() != null && properties.getModelAndView().getTagNames().containsKey(languageTag);
    }

    /**
     * Returns whether the configured tag names contain the given locale's language tag
     *
     * @param locale the locale whose language tag is looked up
     *
     * @return {@code true} if the locale's language tag is configured, {@code false} otherwise
     */
    public boolean contains(@Nullable Locale locale) {
        return locale != null && contains(locale.toLanguageTag());
    }

    /**
     * Returns whether the given locale matches any of the comparison locales
     *
     * @param locale the locale to compare
     * @param comparisons the candidate locales compared by language tag
     *
     * @return {@code true} if the locale matches any candidate, {@code false} otherwise
     */
    public boolean equalsAny(@Nullable Locale locale, @Nullable Locale... comparisons) {
        return LocalePlainWraps.equalsAnyLanguageTags(locale, comparisons);
    }

    /**
     * Returns whether the given locale matches any of the comparison language tags
     *
     * @param locale the locale to compare
     * @param languageTags the candidate language tags
     *
     * @return {@code true} if the locale matches any tag, {@code false} otherwise
     */
    public boolean equalsAny(@Nullable Locale locale, @Nullable String... languageTags) {
        return LocalePlainWraps.equalsAnyLanguageTags(locale, languageTags);
    }

    /**
     * Returns whether the given locale matches any locale in the given collection
     *
     * @param locale the locale to compare
     * @param comparisons the collection of candidate locales compared by language tag
     *
     * @return {@code true} if the locale matches any candidate, {@code false} otherwise
     */
    public boolean equalsAny(@Nullable Locale locale, @Nullable Collection<Locale> comparisons) {
        return LocalePlainWraps.equalsAnyLanguageTags(locale, comparisons);
    }

    /**
     * Returns the configured display name for the given language tag
     *
     * @param languageTag the language tag whose display name is looked up
     *
     * @return the configured display name, or {@code null} if none is configured
     */
    public String getLanguageName(@Nullable String languageTag) {
        return (properties.getModelAndView().getTagNames() == null) ? null : properties.getModelAndView().getTagNames().get(languageTag);
    }

    /**
     * Returns the configured display name for the given locale
     *
     * @param locale the locale whose display name is looked up
     *
     * @return the configured display name, or {@code null} if the locale is {@code null} or none is configured
     */
    public String getLanguageName(@Nullable Locale locale) {
        return (locale == null) ? null : getLanguageName(locale.toLanguageTag());
    }
}
