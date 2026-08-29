package com.arquisoft.fichas.application.fichaperfil.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.fichaperfil.command.primaryport.model.RegistrarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.command.usecase.RegistrarFichaPerfilUseCase;
import com.arquisoft.fichas.domain.fichaperfil.RegistroFichaPerfilDomain;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarFichaPerfilInteractorImplTest {

    @Mock
    private RegistrarFichaPerfilUseCase registrarFichaPerfilUseCase;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RegistrarFichaPerfilInteractorImpl registrarFichaPerfilInteractor;

    @Test
    void debeRegistrarLaFicha_cuandoEjecuta() {
        // Arrange
        var command = new RegistrarFichaPerfilCommand(
                "Título de prueba", UUID.randomUUID(), List.of(UUID.randomUUID()));
        UUID fichaId = UUID.randomUUID();
        when(registrarFichaPerfilUseCase.ejecutar(any(RegistroFichaPerfilDomain.class))).thenReturn(fichaId);

        // Act
        UUID resultado = registrarFichaPerfilInteractor.ejecutar(command);

        // Assert
        assertThat(resultado).isEqualTo(fichaId);
    }

    @Test
    void debeConstruirElRegistroConLosDatosDelCommand_cuandoEjecuta() {
        // Arrange
        UUID asesor = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        var command = new RegistrarFichaPerfilCommand("Título de prueba", asesor, List.of(estudiante));
        when(registrarFichaPerfilUseCase.ejecutar(any(RegistroFichaPerfilDomain.class)))
                .thenReturn(UUID.randomUUID());

        // Act
        registrarFichaPerfilInteractor.ejecutar(command);

        // Assert
        ArgumentCaptor<RegistroFichaPerfilDomain> registroCaptor =
                ArgumentCaptor.forClass(RegistroFichaPerfilDomain.class);
        verify(registrarFichaPerfilUseCase).ejecutar(registroCaptor.capture());
        assertThat(registroCaptor.getValue().getFicha().getTituloProyecto()).isEqualTo("Título de prueba");
        assertThat(registroCaptor.getValue().getFicha().getAsesorFicha()).isEqualTo(asesor);
        assertThat(registroCaptor.getValue().getEstadoInicial().getFichaPerfil())
                .isEqualTo(registroCaptor.getValue().getFichaPerfil());
        assertThat(registroCaptor.getValue().getEstudiantes().getEstudiantes()).containsExactly(estudiante);
    }
}
