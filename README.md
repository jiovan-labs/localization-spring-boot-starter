[English](README.md) | [简体中文](README.zh-CN.md) | [繁體中文](README.zh-TW.md)

# Localization Spring Boot Starter

Spring Boot application enables an ability of switching multilingualism.

## Quickstart

- Import dependencies

```xml
    <dependency>
        <groupId>com.jiovan.spring.starter</groupId>
        <artifactId>localization-spring-boot-starter</artifactId>
        <version>LATEST</version>
    </dependency>
```

> By default, this starter will auto take effect, you can turn it off by `jiovan.localization.enabled = false`

- Configure Spring Boot `application.yml` with prefix `jiovan.localization`

```yml
jiovan:
    localization:
        model-and-view:
            view-attribute: 'localeChangeTagNames'
            tag-multilingual: false
            tag-names:
                'en-US': 'English'
                'zh-CN': '简体中文'
                'zh-TW': '繁体中文'
        locale-interceptor:
            param-name: 'lang'
            intercept-paths:
                - '/**'
            exclude-paths:
                - '/foo/**'
                - '/bar/**'
        cookie-locale-resolver:
            http-only: true
```

- **Optional feature**: If you want to display the `tag-names` value in multilingual (not the fixed value in `application.properties`), for example, display the value of `zh-CN` to `简体中文` when current request is using `zh` locale, or display `Simplified Chinese` when the request is using `en` locale. You can reach your goal as following:
  1. Configure the `tag-multilingual` attribute to `true`
  2. Set the value of `zh-CN` to `localization.dropdown-switch`
  3. Write the `localization.dropdown-switch` as a key in a resource bundle properties that could be loaded by the primary `MessageSource` bean (Just as our another Spring starter `message-source-spring-boot-starter` does)

- Write your template code as following (take `Thymeleaf` as an example)

Under the `head` segment (optional):

```html
<meta name="language-raw" th:content="${#locale.toLanguageTag()}" th:if="${not #localeChange.contains(#locale)}"/>
<meta name="language-tag" th:content="${#localeChange.contains(#locale) ? #locale.toLanguageTag() : 'en-US'}"/>
```

Under the `body` segment:

```html
<ul>
    <li class="lang-switch dropdown">
        <a href="javascript:">
            <i class="fa fa-globe" aria-hidden="true"></i>
            <span>Language</span>
            <i class="bi bi-chevron-down"></i>
        </a>
        <ul role="menu" th:if="${not #maps.isEmpty(localeChangeTagNames)}">
            <li th:each="tagName: ${localeChangeTagNames}"><a th:href="${'?lang=' + tagName.key}"><span th:text="${tagName.value}">Placeholder</span></a></li>
        </ul>
    </li>
</ul>
```

## Locale Resolution: This Starter vs. Spring Boot Defaults

Spring Boot resolves the locale only through `spring.web.locale` / `spring.web.locale-resolver`, and offers just two modes:

- `ACCEPT_HEADER` (default): `AcceptHeaderLocaleResolver` reads the browser's `Accept-Language` header. There is no way to switch languages from a page link or a URL parameter.
- `FIXED`: `FixedLocaleResolver` always uses the configured `spring.web.locale`.

That is why clicking a language link does not work out of the box. This starter fills the gap with a **URL parameter based** mechanism (`?lang=zh-CN` by default) and persists the choice in a cookie or session:

|                       | Spring Boot defaults (`spring.web.*`)                | This starter (`jiovan.localization.*`)             |
|-----------------------|------------------------------------------------------|----------------------------------------------------|
| LocaleResolver        | `AcceptHeaderLocaleResolver` / `FixedLocaleResolver` | `CookieLocaleContextResolver` (or Session / Fixed) |
| How to switch         | `Accept-Language` header or a fixed locale           | `?lang=xx` URL parameter                           |
| Remembers the choice  | No, re-reads the header every request                | Yes, persisted in a cookie or session              |
| URL parameter support | No                                                   | Yes                                                |

How it works:

1. `LocalizationResolverConfiguration` is ordered **before** `WebMvcAutoConfiguration` and registers its own bean named `localeResolver`. Spring Boot's `localeResolver()` is annotated with `@ConditionalOnMissingBean(name = "localeResolver")`, so it backs off.
2. `CookieLocaleContextResolver` reads the `lang` parameter on every call, with priority **URL parameter > cookie > default locale**.
3. `LocaleChangeInterceptor` writes the selected locale into the cookie, so later requests without the parameter still keep the language.
4. `CookieLocaleRequestContextFilter` exposes the locale through `LocaleContextHolder` early in the filter chain, for code outside the `DispatcherServlet`.

Practical consequences:

- While this starter is enabled, `spring.web.locale` / `spring.web.locale-resolver` no longer affect MVC locale resolution because the `localeResolver` bean has been replaced. Configure the fallback locale with `jiovan.localization.cookie-locale-resolver.default-locale` (or `session-locale-resolver.default-locale`) instead.
- Setting `jiovan.localization.enabled=false` disables the starter entirely; Spring Boot then falls back to `AcceptHeaderLocaleResolver` and `?lang=xx` has no effect.
- URL switching works with `locale-resolver-type=cookie` (default) and `session`. With `fixed` the URL parameter is ignored and the locale is fixed.
- See `src/test` for runnable tests that demonstrate both the URL-parameter behaviour and the Spring Boot default behaviour.

## Document

- Github: https://github.com/jiovan-labs/localization-spring-boot-starter

## Requirement

- jdk 17+

## License

This project is under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)

See the `NOTICE.txt` file for required notices and attributions.

## Donation

You like this package? Then [donate to us](https://jiovan.com/donation) to support the development.

## Copyright

Beijing Jiovan Technology Ltd.

## Website

- Jiovan: https://jiovan.com
