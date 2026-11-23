package com.arquisoft.evaluaciones.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "evaluaciones.finalizacion")
@Getter
@Setter
public class EvaluacionesFinalizacionProperties {

    private BigDecimal pesoAsesor = BigDecimal.ONE;
    private BigDecimal pesoJurado = BigDecimal.ONE;
}
