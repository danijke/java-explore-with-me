package ru.practicum.ewm.main.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.lang.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.*;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.practicum.ewm.main.util.OffsetBasedPageRequest;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new FromSizePageableResolver());
    }

    static class FromSizePageableResolver implements HandlerMethodArgumentResolver {

        @Override
        public boolean supportsParameter(@NonNull MethodParameter parameter) {
            return parameter.getParameterType().equals(OffsetBasedPageRequest.class);
        }

        @Override
        @Nullable
        public Object resolveArgument(@NonNull MethodParameter parameter,
                                      @Nullable ModelAndViewContainer mavContainer,
                                      @NonNull NativeWebRequest webRequest,
                                      @Nullable org.springframework.web.bind.support.WebDataBinderFactory binderFactory) {
            String fromRaw = webRequest.getParameter("from");
            String sizeRaw = webRequest.getParameter("size");

            int from = 0;
            int size = 10;

            if (fromRaw != null && !fromRaw.isEmpty()) {
                from = Integer.parseInt(fromRaw);
            }
            if (sizeRaw != null && !sizeRaw.isEmpty()) {
                size = Integer.parseInt(sizeRaw);
            }

            return new OffsetBasedPageRequest(from, size);
        }
    }
}
