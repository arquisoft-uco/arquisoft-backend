package com.arquisoft.biblioteca.application.estudianteproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.primaryport.model.AgregarEstudiantesProyectoGradoCommand;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.primaryport.model.RemoverEstudianteProyectoGradoCommand;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.result.AgregacionEstudiantesProyectoGradoResult;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.result.RemocionEstudianteProyectoGradoResult;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.usecase.AgregarEstudiantesProyectoGradoUseCase;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.usecase.RemoverEstudianteProyectoGradoUseCase;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudianteProyectoGradoInteractorsImplTest {

    @Mock
    private AgregarEstudiantesProyectoGradoUseCase agregarUseCase;
    @Mock
    private RemoverEstudianteProyectoGradoUseCase removerUseCase;

    @Test
    void debeDelegarAgregarConElCompuestoMapeado_cuandoRecibeElCommand() {
        // Arrange
        var interactor = new AgregarEstudiantesProyectoGradoInteractorImpl(agregarUseCase);
        var proyecto = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-15T10:00:00Z");
        var command = AgregarEstudiantesProyectoGradoCommand.crear(
                proyecto.toString(), "Titulo", List.of(estudiante.toString()), ocurridoEn);
        var esperado = new AgregacionEstudiantesProyectoGradoResult(proyecto, 1, 0, 0, 0);
        when(agregarUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(AgregacionEstudiantesProyectoGradoDomain.class);
        verify(agregarUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getProyecto().getTitulo()).isEqualTo("Titulo");
        assertThat(captor.getValue().getVinculos()).singleElement()
                .satisfies(vinculo -> assertThat(vinculo.getAutor()).isEqualTo(estudiante));
    }

    @Test
    void debeDelegarRemoverConElVinculoMapeado_cuandoRecibeElCommand() {
        // Arrange
        var interactor = new RemoverEstudianteProyectoGradoInteractorImpl(removerUseCase);
        var proyecto = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var command = RemoverEstudianteProyectoGradoCommand.crear(
                proyecto.toString(), estudiante.toString(), Instant.parse("2026-11-15T10:00:00Z"));
        var esperado = new RemocionEstudianteProyectoGradoResult.Removida(proyecto, estudiante);
        when(removerUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(EstudianteProyectoGradoDomain.class);
        verify(removerUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getAutor()).isEqualTo(estudiante);
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(proyecto);
    }
}
