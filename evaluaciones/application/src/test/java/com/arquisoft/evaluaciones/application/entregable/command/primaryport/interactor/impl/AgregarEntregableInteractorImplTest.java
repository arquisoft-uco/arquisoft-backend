package com.arquisoft.evaluaciones.application.entregable.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.entregable.command.primaryport.model.AgregarEntregableCommand;
import com.arquisoft.evaluaciones.application.entregable.command.result.AgregacionEntregableResult;
import com.arquisoft.evaluaciones.application.entregable.command.usecase.AgregarEntregableUseCase;
import com.arquisoft.evaluaciones.domain.entregable.EntregableDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarEntregableInteractorImplTest {

    @Mock
    private AgregarEntregableUseCase agregarEntregableUseCase;

    @InjectMocks
    private AgregarEntregableInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoDelCommand() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var command = AgregarEntregableCommand.crear(id.toString(), proyectoGrado.toString(), "Sistema de riego", 2);
        var esperado = new AgregacionEntregableResult.Agregado(id);
        when(agregarEntregableUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(EntregableDomain.class);
        verify(agregarEntregableUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(id);
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(captor.getValue().getProyecto()).isEqualTo("Sistema de riego");
        assertThat(captor.getValue().getVersion()).isEqualTo(2);
    }
}
