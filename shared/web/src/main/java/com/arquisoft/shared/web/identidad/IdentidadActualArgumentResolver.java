package com.arquisoft.shared.web.identidad;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class IdentidadActualArgumentResolver implements HandlerMethodArgumentResolver {

    private final ProveedorIdentidadActual proveedorIdentidadActual;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return IdentidadActual.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        return proveedorIdentidadActual.obtener();
    }
}
