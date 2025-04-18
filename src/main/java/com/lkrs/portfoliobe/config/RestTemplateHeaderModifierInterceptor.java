package com.lkrs.portfoliobe.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestTemplateHeaderModifierInterceptor implements ClientHttpRequestInterceptor {
    @Value("${github.access.token}")
    private String GITHUB_TOKEN;
    @Value("${gitlab.access.token}")
    private String GITLAB_TOKEN;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String token = request.getURI().toString().contains("api.github") ? GITHUB_TOKEN : GITLAB_TOKEN;
        request.getHeaders().add("Authorization", "Bearer "+token);
        return execution.execute(request, body);
    }
}
