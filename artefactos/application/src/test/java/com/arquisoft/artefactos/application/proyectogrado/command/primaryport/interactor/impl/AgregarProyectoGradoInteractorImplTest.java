package com.arquisoft.artefactos.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model.AgregarProyectoGradoCommand;
import com.arquisoft.artefactos.application.proyectogrado.command.result.AgregacionProyectoGradoResult;
import com.arquisoft.artefactos.application.proyectogrado.command.usecase.AgregarProyectoGradoUseCase;
import com.arquisoft.artefactos.domain.proyectogrado.ProyectoGradoDomain;
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
class AgregarProyectoGradoInteractorImplTest {

    @Mock
    private AgregarProyectoGradoUseCase agregarProyectoGradoUseCase;

    @InjectMocks
    private AgregarProyectoGradoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoDelCommand() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var command = new AgregarProyectoGradoCommand(id, "Sistema de gestion");
        var esperado = new AgregacionProyectoGradoResult.Agregado(id);
        when(agregarProyectoGradoUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(ProyectoGradoDomain.class);
        verify(agregarProyectoGradoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(id);
        assertThat(captor.getValue().getNombre()).isEqualTo("Sistema de gestion");
    }
}
