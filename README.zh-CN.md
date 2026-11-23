[English](README.md) | [简体中文](README.zh-CN.md) | [繁體中文](README.zh-TW.md)

# 本地化 Spring Boot Starter

为 Spring Boot 应用提供多语言切换能力。

## 快速开始

- 导入依赖

```xml
    <dependency>
        <groupId>com.jiovan.spring.starter</groupId>
        <artifactId>localization-spring-boot-starter</artifactId>
        <version>LATEST</version>
    </dependency>
```

> 默认情况下，此 starter 会自动生效，你可以通过 `jiovan.localization.enabled = false` 关闭它。

- 使用前缀 `jiovan.localization` 配置 Spring Boot 的 `application.yml`

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

- **可选功能**：如果希望 `tag-names` 的值以多语言方式显示（而非 `application.properties` 中的固定值），例如在当前请求使用 `zh` 区域设置时将 `zh-CN` 显示为 `简体中文`，在使用 `en` 区域设置时显示为 `Simplified Chinese`，可按如下方式实现：
  1. 将 `tag-multilingual` 属性设为 `true`
  2. 将 `zh-CN` 的值设为 `localization.dropdown-switch`
  3. 将 `localization.dropdown-switch` 作为键写入可由主 `MessageSource` Bean 加载的资源包属性文件中（正如我们的另一个 Spring starter `message-source-spring-boot-starter` 所做的那样）

- 按如下方式编写模板代码（以 `Thymeleaf` 为例）

在 `head` 区域内（可选）：

```html
<meta name="language-raw" th:content="${#locale.toLanguageTag()}" th:if="${not #localeChange.contains(#locale)}"/>
<meta name="language-tag" th:content="${#localeChange.contains(#locale) ? #locale.toLanguageTag() : 'en-US'}"/>
```

在 `body` 区域内：

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

## 区域设置解析：本 Starter 与 Spring Boot 默认行为的差异

Spring Boot 原生只通过 `spring.web.locale` / `spring.web.locale-resolver` 解析区域设置，且只有两种模式：

- `ACCEPT_HEADER`（默认）：`AcceptHeaderLocaleResolver` 读取浏览器的 `Accept-Language` 请求头。无法通过页面链接或 URL 参数切换语言。
- `FIXED`：`FixedLocaleResolver` 始终使用配置的 `spring.web.locale`。

这就是为什么点击语言链接默认不起作用。本 starter 通过 **URL 参数**机制（默认 `?lang=zh-CN`）弥补了这一空白，并将选择持久化到 Cookie 或 Session 中：

|                | Spring Boot 默认（`spring.web.*`）                   | 本 starter（`jiovan.localization.*`）               |
|----------------|------------------------------------------------------|-----------------------------------------------------|
| LocaleResolver | `AcceptHeaderLocaleResolver` / `FixedLocaleResolver` | `CookieLocaleContextResolver`（或 Session / Fixed） |
| 切换方式       | `Accept-Language` 请求头或固定区域设置               | `?lang=xx` URL 参数                                 |
| 是否记住选择   | 否，每次请求都重新读请求头                           | 是，持久化在 Cookie 或 Session 中                   |
| 支持 URL 参数  | 不支持                                               | 支持                                                |

实现原理：

1. `LocalizationResolverConfiguration` 通过 `@AutoConfigureBefore(WebMvcAutoConfiguration.class)` 排在 Spring Boot 之前，并注册自己的 `localeResolver` Bean。Boot 的 `localeResolver()` 带有 `@ConditionalOnMissingBean(name = "localeResolver")`，因此会自动让位。
2. `CookieLocaleContextResolver` 每次调用时都读取 `lang` 参数，优先级为 **URL 参数 > Cookie > 默认区域设置**。
3. `LocaleChangeInterceptor` 把选中的区域设置写入 Cookie，因此后续不带参数的请求也能保持语言。
4. `CookieLocaleRequestContextFilter` 在过滤器链早期通过 `LocaleContextHolder` 暴露区域设置，供 `DispatcherServlet` 之外的代码使用。

实际影响：

- 本 starter 启用期间，`spring.web.locale` / `spring.web.locale-resolver` 不再影响 MVC 的区域设置解析，因为 `localeResolver` Bean 已被替换。应改用 `jiovan.localization.cookie-locale-resolver.default-locale`（或 `session-locale-resolver.default-locale`）配置默认值。
- 设置 `jiovan.localization.enabled=false` 会完全关闭本 starter；此时 Spring Boot 回退到 `AcceptHeaderLocaleResolver`，`?lang=xx` 不再生效。
- `locale-resolver-type=cookie`（默认）和 `session` 都支持 URL 切换；`fixed` 会忽略 URL 参数，区域设置固定不变。
- `src/test` 中提供了可运行的测试，分别演示 URL 参数切换行为和 Spring Boot 默认行为。

## 文档

- GitHub：https://github.com/jiovan-labs/localization-spring-boot-starter

## 环境要求

- jdk 17+

## 许可证

本项目采用 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 许可证。

请参阅 `NOTICE.txt` 文件了解所需的声明与归属信息。

## 捐赠

喜欢这个工具包吗？欢迎[向我们捐赠](https://jiovan.com/donation)以支持开发。

## 版权

北京玖漫科技有限公司

## 网站

- 玖漫：https://jiovan.com
