package com.arquisoft.seguridad.application.autorizacion.command.primaryport.interactor.impl;

import com.arquisoft.seguridad.application.autorizacion.command.primaryport.model.AutorizarAccesoCommand;
import com.arquisoft.seguridad.application.autorizacion.command.result.DecisionAccesoResult;
import com.arquisoft.seguridad.application.autorizacion.command.usecase.AutorizarAccesoUseCase;
import com.arquisoft.seguridad.domain.autorizacion.AccesoDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutorizarAccesoInteractorImplTest {

    @Mock
    private AutorizarAccesoUseCase useCase;

    @Test
    void debeMapearYDelegarEnElUseCaseSinTransaccion_cuandoEjecuta() {
        // Arrange
        var esperado = new DecisionAccesoResult.Permitida("d");
        var captor = ArgumentCaptor.forClass(AccesoDomain.class);
        when(useCase.ejecutar(any(AccesoDomain.class))).thenReturn(esperado);
        var interactor = new AutorizarAccesoInteractorImpl(useCase);

        // Act
        var resultado = interactor.ejecutar(AutorizarAccesoCommand.crear("GET", "/api/x", "t", "principal", null));

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getRecurso()).isEqualTo("/api/x");
        var ejecutar = Arrays.stream(AutorizarAccesoInteractorImpl.class.getDeclaredMethods())
                .filter(m -> "ejecutar".equals(m.getName()) && !m.isBridge())
                .findFirst().orElseThrow();
        assertThat(ejecutar.getAnnotation(Transactional.class)).isNull();
    }
}
