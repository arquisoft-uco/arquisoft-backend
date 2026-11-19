package com.arquisoft.shared.catalogoparametros.infrastructure.config;

import com.arquisoft.shared.catalogoparametros.CatalogoParametrosEncendido;
import com.arquisoft.shared.catalogoparametros.infrastructure.exception.ConfiguracionCatalogoParametrosInvalidaException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

@Component
@CatalogoParametrosEncendido
@DependsOn("catalogoMensajesRedis")
@RequiredArgsConstructor
public class ValidadorConfiguracionCatalogoParametros {

    private static final String PERFIL_PRODUCCION = "prod";

    private final CatalogoParametrosProperties propiedades;
    private final Environment entorno;

    @PostConstruct
    public void validar() {
        var problemas = propiedades.problemas(entorno.acceptsProfiles(Profiles.of(PERFIL_PRODUCCION)));
        if (!problemas.isEmpty()) {
            throw new ConfiguracionCatalogoParametrosInvalidaException(String.join(", ", problemas));
        }
    }
}
