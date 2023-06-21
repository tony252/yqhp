/*
 *  Copyright https://github.com/yqhp
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.yqhp.agent.androidtools.browser;

import com.android.ddmlib.IDevice;
import com.fasterxml.jackson.core.type.TypeReference;
import com.yqhp.agent.androidtools.AndroidUtils;
import com.yqhp.common.commons.util.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.SocketUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author jiangyitao
 */
@Slf4j
public class ChromeDevtools {

    private static final String LIST_SOCKET_COMMAND;

    static {
        String socketNamePatterns = String.join("|", List.of(
                "@(.*)_devtools_remote(_\\\\d+)?",
                "@com\\\\.opera\\\\.browser(\\\\.beta)?\\\\.devtools"
        ));
        LIST_SOCKET_COMMAND = String.join("|", List.of(
                "cat /proc/net/unix",
                "grep -E \"" + socketNamePatterns + "\"",
                "awk '{print substr($8, 2)}'")
        );
        log.info("LIST_SOCKET_COMMAND: {}", LIST_SOCKET_COMMAND);
    }

    public static Version getVersion(int localPort) throws IOException {
        String responseBody = httpGet("http://localhost:" + localPort + "/json/version");
        return StringUtils.isBlank(responseBody) ? null : JacksonUtils.readValue(responseBody, Version.class);
    }

    public static List<Page> listPage(int localPort) throws IOException {
        String responseBody = httpGet("http://localhost:" + localPort + "/json");
        return StringUtils.isBlank(responseBody) ? new ArrayList<>() : JacksonUtils.readValue(responseBody, new TypeReference<>() {
        });
    }

    public static Browser getBrowser(IDevice iDevice, String socketName) {
        if (StringUtils.isBlank(socketName)) {
            return null;
        }
        try {
            int localPort = SocketUtils.findAvailableTcpPort();
            iDevice.createForward(localPort, socketName, IDevice.DeviceUnixSocketNamespace.ABSTRACT);
            try {
                Browser browser = new Browser();
                browser.setSocketName(socketName);
                browser.setVersion(getVersion(localPort));
                browser.setPages(listPage(localPort));
                return browser;
            } finally {
                iDevice.removeForward(localPort);
            }
        } catch (Throwable cause) {
            log.error("[{}]getBrowser error, socketName={}",
                    iDevice.getSerialNumber(), socketName, cause);
            return null;
        }
    }

    public static List<Browser> listBrowser(IDevice iDevice) {
        // chrome_devtools_remote
        // webview_devtools_remote_27623
        String socketNames = AndroidUtils.executeShellCommand(iDevice, LIST_SOCKET_COMMAND);
        if (StringUtils.isBlank(socketNames)) {
            return new ArrayList<>();
        }
        return Stream.of(socketNames.split("\n"))
                .distinct()
                .map(socketName -> getBrowser(iDevice, socketName))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 由于java.net.http.HttpClient无法关闭请求连接，如果不关闭连接，会导致cat /proc/net/unix出现大量连接
     * 使用HttpURLConnection代替
     */
    private static String httpGet(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");

        InputStream is = conn.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        try (is; isr; BufferedReader br = new BufferedReader(isr)) {
            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                responseBody.append(line);
            }
            return responseBody.toString();
        } finally {
            conn.disconnect();
        }
    }
}
