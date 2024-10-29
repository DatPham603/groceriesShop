package com.project.shopapp.Utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@RequiredArgsConstructor
@Component
public class LocalizationUtils {
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;
    public String getLocalizedMessage(String messageKey){
        //resolveLocale(request) phân tích yêu cầu HTTP để xác định locale hiện tại (ngôn ngữ và vùng miền) dựa trên thông tin trong tiêu đề
        HttpServletRequest request = WebUtils.httpServletRequest();
        Locale locale = localeResolver.resolveLocale(request);
        return messageSource.getMessage(messageKey,null,locale);
    }
}
