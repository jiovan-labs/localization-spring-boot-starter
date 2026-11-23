[English](README.md) | [簡體中文](README.zh-CN.md) | [繁體中文](README.zh-TW.md)

# 本地化 Spring Boot Starter

為 Spring Boot 應用提供多語言切換能力。

## 快速開始

- 匯入相依性

```xml
    <dependency>
        <groupId>com.jiovan.spring.starter</groupId>
        <artifactId>localization-spring-boot-starter</artifactId>
        <version>LATEST</version>
    </dependency>
```

> 預設情況下，此 starter 會自動生效，你可以透過 `jiovan.localization.enabled = false` 關閉它。

- 使用字首 `jiovan.localization` 設定 Spring Boot 的 `application.yml`

```yml
jiovan:
    localization:
        model-and-view:
            view-attribute: 'localeChangeTagNames'
            tag-multilingual: false
            tag-names:
                'en-US': 'English'
                'zh-CN': '簡體中文'
                'zh-TW': '繁體中文'
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

- **可選功能**：如果希望 `tag-names` 的值以多語言方式顯示（而非 `application.properties` 中的固定值），例如在當前請求使用 `zh` 區域設定時將 `zh-CN` 顯示為 `簡體中文`，在使用 `en` 區域設定時顯示為 `Simplified Chinese`，可按如下方式實現：
  1. 將 `tag-multilingual` 屬性設為 `true`
  2. 將 `zh-CN` 的值設為 `localization.dropdown-switch`
  3. 將 `localization.dropdown-switch` 作為鍵寫入可由主 `MessageSource` Bean 載入的資源包屬性檔案中（正如我們的另一個 Spring starter `message-source-spring-boot-starter` 所做的那樣）

- 按如下方式編寫模板程式碼（以 `Thymeleaf` 為例）

在 `head` 區域內（可選）：

```html
<meta name="language-raw" th:content="${#locale.toLanguageTag()}" th:if="${not #localeChange.contains(#locale)}"/>
<meta name="language-tag" th:content="${#localeChange.contains(#locale) ? #locale.toLanguageTag() : 'en-US'}"/>
```

在 `body` 區域內：

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

## 區域設定解析：本 Starter 與 Spring Boot 預設行為的差異

Spring Boot 原生只透過 `spring.web.locale` / `spring.web.locale-resolver` 解析區域設定，且只有兩種模式：

- `ACCEPT_HEADER`（預設）：`AcceptHeaderLocaleResolver` 讀取瀏覽器的 `Accept-Language` 請求標頭。無法透過頁面連結或 URL 參數切換語言。
- `FIXED`：`FixedLocaleResolver` 始終使用設定的 `spring.web.locale`。

這就是為什麼點擊語言連結預設不起作用。本 starter 透過 **URL 參數**機制（預設 `?lang=zh-CN`）彌補了這項不足，並將選擇持久化到 Cookie 或 Session 中：

|                | Spring Boot 預設（`spring.web.*`）                   | 本 starter（`jiovan.localization.*`）               |
|----------------|------------------------------------------------------|-----------------------------------------------------|
| LocaleResolver | `AcceptHeaderLocaleResolver` / `FixedLocaleResolver` | `CookieLocaleContextResolver`（或 Session / Fixed） |
| 切換方式       | `Accept-Language` 請求標頭或固定區域設定             | `?lang=xx` URL 參數                                 |
| 是否記住選擇   | 否，每次請求都重新讀取標頭                           | 是，持久化在 Cookie 或 Session 中                   |
| 支援 URL 參數  | 不支援                                               | 支援                                                |

實作原理：

1. `LocalizationResolverConfiguration` 透過 `@AutoConfigureBefore(WebMvcAutoConfiguration.class)` 排在 Spring Boot 之前，並註冊自己的 `localeResolver` Bean。Boot 的 `localeResolver()` 帶有 `@ConditionalOnMissingBean(name = "localeResolver")`，因此會自動讓位。
2. `CookieLocaleContextResolver` 每次呼叫時都讀取 `lang` 參數，優先順序為 **URL 參數 > Cookie > 預設區域設定**。
3. `LocaleChangeInterceptor` 把選中的區域設定寫入 Cookie，因此後續不帶參數的請求也能保持語言。
4. `CookieLocaleRequestContextFilter` 在過濾器鏈早期透過 `LocaleContextHolder` 暴露區域設定，供 `DispatcherServlet` 之外的程式碼使用。

實際影響：

- 本 starter 啟用期間，`spring.web.locale` / `spring.web.locale-resolver` 不再影響 MVC 的區域設定解析，因為 `localeResolver` Bean 已被取代。應改用 `jiovan.localization.cookie-locale-resolver.default-locale`（或 `session-locale-resolver.default-locale`）設定預設值。
- 設定 `jiovan.localization.enabled=false` 會完全關閉本 starter；此時 Spring Boot 回退到 `AcceptHeaderLocaleResolver`，`?lang=xx` 不再生效。
- `locale-resolver-type=cookie`（預設）和 `session` 都支援 URL 切換；`fixed` 會忽略 URL 參數，區域設定固定不變。
- `src/test` 中提供了可執行的測試，分別示範 URL 參數切換行為與 Spring Boot 預設行為。

## 文件

- GitHub：https://github.com/jiovan-labs/localization-spring-boot-starter

## 環境要求

- jdk 17+

## 許可證

本專案採用 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 許可證。

請參閱 `NOTICE.txt` 檔案瞭解所需的宣告與歸屬資訊。

## 捐贈

喜歡這個工具包嗎？歡迎[向我們捐贈](https://jiovan.com/donation)以支援開發。

## 版權

北京玖漫科技有限公司

## 網站

- 玖漫：https://jiovan.com
