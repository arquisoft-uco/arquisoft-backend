package com.arquisoft.shared.tracing.infrastructure.traza.config;

import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.tracing.application.traza.primaryport.impl.GestorTrazaImpl;
import com.arquisoft.shared.tracing.application.traza.secondaryport.ContextoDiagnosticoOutputPort;
import com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc.MdcContextoDiagnosticoOutputAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TrazabilidadConfig {

    @Bean
    public ContextoDiagnosticoOutputPort contextoDiagnosticoOutputPort() {
        return new MdcContextoDiagnosticoOutputAdapter();
    }

    @Bean
    public GestorTraza gestorTraza(final ContextoDiagnosticoOutputPort contexto,
                                   @Value("${arquisoft.trazas.anonimizar-ip:false}") final boolean anonimizarIp) {
        return new GestorTrazaImpl(contexto, anonimizarIp);
    }
}
