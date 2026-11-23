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


import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = MockApplicationInitializer.class)
@AutoConfigureMockMvc
class MockApplicationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void urlParamSwitchesLocaleAndWritesCookie() throws Exception {
        MvcResult result = mockMvc.perform(get("/locale").param("lang", "zh-CN"))
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertEquals("zh-CN", result.getResponse().getContentAsString());
        Cookie cookie = result.getResponse().getCookie("locale");
        Assertions.assertNotNull(cookie, "Locale cookie can not be null");
        Assertions.assertEquals("zh-CN", cookie.getValue());
    }

    @Test
    void cookieKeepsLocaleWhenParamIsAbsent() throws Exception {
        MvcResult result = mockMvc.perform(get("/locale").cookie(new Cookie("locale", "zh-TW")))
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertEquals("zh-TW", result.getResponse().getContentAsString());
    }

    @Test
    void urlParamOverridesCookie() throws Exception {
        MvcResult result = mockMvc.perform(get("/locale")
                        .cookie(new Cookie("locale", "zh-TW"))
                        .param("lang", "en-US"))
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertEquals("en-US", result.getResponse().getContentAsString());
    }

    @Test
    void acceptLanguageHeaderIsNotUsed() throws Exception {
        MvcResult result = mockMvc.perform(get("/locale").header("Accept-Language", "fr-FR"))
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertEquals("en-US", result.getResponse().getContentAsString());
    }
}
