package com.arquisoft.shared.web.identidad;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class IdentidadActualWebConfig implements WebMvcConfigurer {

    private final ProveedorIdentidadActual proveedorIdentidadActual;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new IdentidadActualArgumentResolver(proveedorIdentidadActual));
    }
}
