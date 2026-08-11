package com.arquisoft.fichas.application.fichaperfil.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estadofichaperfil.command.usecase.AsignarEstadoInicialFichaPerfilUseCase;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.usecase.AsignarEstudiantesFichaPerfilUseCase;
import com.arquisoft.fichas.application.fichaperfil.command.primaryport.model.RegistrarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.command.usecase.RegistrarFichaPerfilUseCase;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarFichaPerfilInteractorImplTest {

    @Mock
    private RegistrarFichaPerfilUseCase registrarFichaPerfilUseCase;

    @Mock
    private AsignarEstadoInicialFichaPerfilUseCase asignarEstadoInicialFichaPerfilUseCase;

    @Mock
    private AsignarEstudiantesFichaPerfilUseCase asignarEstudiantesFichaPerfilUseCase;

    @InjectMocks
    private RegistrarFichaPerfilInteractorImpl registrarFichaPerfilInteractor;

    @Test
    void debeRegistrarLaFicha_cuandoEjecuta() {
        // Arrange
        UUID asesor = UUID.randomUUID();
        var command = new RegistrarFichaPerfilCommand("Título de prueba", asesor, List.of(UUID.randomUUID()));
        UUID fichaId = UUID.randomUUID();
        when(registrarFichaPerfilUseCase.ejecutar(any(FichaPerfilDomain.class))).thenReturn(fichaId);

        // Act
        UUID resultado = registrarFichaPerfilInteractor.ejecutar(command);

        // Assert
        assertThat(resultado).isEqualTo(fichaId);
    }

    @Test
    void debeConstruirLaFichaConLosDatosDelCommand_cuandoEjecuta() {
        // Arrange
        UUID asesor = UUID.randomUUID();
        var command = new RegistrarFichaPerfilCommand("Título de prueba", asesor, List.of(UUID.randomUUID()));
        when(registrarFichaPerfilUseCase.ejecutar(any(FichaPerfilDomain.class))).thenReturn(UUID.randomUUID());

        // Act
        registrarFichaPerfilInteractor.ejecutar(command);

        // Assert
        ArgumentCaptor<FichaPerfilDomain> fichaCaptor = ArgumentCaptor.forClass(FichaPerfilDomain.class);
        verify(registrarFichaPerfilUseCase).ejecutar(fichaCaptor.capture());
        assertThat(fichaCaptor.getValue().getTituloProyecto()).isEqualTo("Título de prueba");
        assertThat(fichaCaptor.getValue().getAsesorFicha()).isEqualTo(asesor);
    }

    @Test
    void debeAsignarElEstadoInicial_cuandoRegistraLaFicha() {
        // Arrange
        var command = new RegistrarFichaPerfilCommand("Título de prueba", UUID.randomUUID(), List.of(UUID.randomUUID()));
        UUID fichaId = UUID.randomUUID();
        when(registrarFichaPerfilUseCase.ejecutar(any(FichaPerfilDomain.class))).thenReturn(fichaId);

        // Act
        registrarFichaPerfilInteractor.ejecutar(command);

        // Assert
        ArgumentCaptor<EstadoFichaPerfilDomain> estadoCaptor = ArgumentCaptor.forClass(EstadoFichaPerfilDomain.class);
        verify(asignarEstadoInicialFichaPerfilUseCase).ejecutar(estadoCaptor.capture());
        assertThat(estadoCaptor.getValue().getFichaPerfil()).isEqualTo(fichaId);

        InOrder inOrder = inOrder(registrarFichaPerfilUseCase, asignarEstadoInicialFichaPerfilUseCase);
        inOrder.verify(registrarFichaPerfilUseCase).ejecutar(any());
        inOrder.verify(asignarEstadoInicialFichaPerfilUseCase).ejecutar(any());
    }

    @Test
    void debeAsignarLosEstudiantes_cuandoElCommandLosTrae() {
        // Arrange
        UUID asesor = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        var command = new RegistrarFichaPerfilCommand("Título de prueba", asesor, List.of(estudiante));
        UUID fichaId = UUID.randomUUID();
        when(registrarFichaPerfilUseCase.ejecutar(any(FichaPerfilDomain.class))).thenReturn(fichaId);

        // Act
        registrarFichaPerfilInteractor.ejecutar(command);

        // Assert — la ficha ya persistida es la que se usa para asignar, no la de entrada
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<EstudianteFichaPerfilDomain>> asignarCaptor = ArgumentCaptor.forClass(List.class);
        verify(asignarEstudiantesFichaPerfilUseCase).ejecutar(asignarCaptor.capture());
        assertThat(asignarCaptor.getValue())
                .hasSize(1)
                .allMatch(relacion -> relacion.getFichaPerfilId().equals(fichaId));
        assertThat(asignarCaptor.getValue().get(0).getEstudianteId()).isEqualTo(estudiante);
    }
}
