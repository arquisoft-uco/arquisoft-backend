package com.arquisoft.shared.tracing.infrastructure.traza.config;

import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.tracing.application.traza.primaryport.impl.GestorTrazaImpl;
import com.arquisoft.shared.tracing.application.traza.secondaryport.ContextoDiagnosticoOutputPort;
import com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc.MdcContextoDiagnosticoOutputAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;

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

    // Sin este decorator, el applicationTaskExecutor autoconfigurado por Spring Boot
    // (usado por @Async, incluida la externalizacion de eventos de Spring Modulith hacia
    // RabbitMQ) arranca cada tarea en un hilo sin el MDC del hilo que la origino. El
    // TrazaMessagePostProcessor entonces no encuentra correlacionId/transaccionId y genera
    // unos nuevos, rompiendo la correlacion entre el contexto productor y el consumidor.
    @Bean
    public TaskDecorator taskDecorator(final ContextoDiagnosticoOutputPort contexto) {
        return new MdcTaskDecorator(contexto);
    }
}
