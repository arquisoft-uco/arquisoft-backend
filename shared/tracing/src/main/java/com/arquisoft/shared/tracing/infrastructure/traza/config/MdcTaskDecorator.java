package com.arquisoft.shared.tracing.infrastructure.traza.config;

import com.arquisoft.shared.tracing.application.traza.secondaryport.ContextoDiagnosticoOutputPort;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;

public class MdcTaskDecorator implements TaskDecorator {

    private final ContextoDiagnosticoOutputPort contexto;

    public MdcTaskDecorator(final ContextoDiagnosticoOutputPort contexto) {
        this.contexto = contexto;
    }

    @Override
    public Runnable decorate(final Runnable runnable) {
        Map<String, String> contextoHiloOrigen = contexto.capturar();
        return () -> {
            Map<String, String> contextoHiloExecutor = contexto.capturar();
            contexto.restaurar(contextoHiloOrigen);
            try {
                runnable.run();
            } finally {
                contexto.restaurar(contextoHiloExecutor);
            }
        };
    }
}
