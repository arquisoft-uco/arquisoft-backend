package com.arquisoft.entregables.application.artefacto.command.primaryport.interactor.impl;

import com.arquisoft.entregables.application.artefacto.command.primaryport.model.OmitirArtefactoCommand;
import com.arquisoft.entregables.application.artefacto.command.primaryport.model.PurgarArtefactosProyectoGradoCommand;
import com.arquisoft.entregables.application.artefacto.command.primaryport.model.ReactivarArtefactoCommand;
import com.arquisoft.entregables.application.artefacto.command.primaryport.model.RegistrarVersionCerradaArtefactoCommand;
import com.arquisoft.entregables.application.artefacto.command.result.OmisionArtefactoResult;
import com.arquisoft.entregables.application.artefacto.command.result.PurgaArtefactosProyectoGradoResult;
import com.arquisoft.entregables.application.artefacto.command.result.ReactivacionArtefactoResult;
import com.arquisoft.entregables.application.artefacto.command.result.RegistroVersionCerradaArtefactoResult;
import com.arquisoft.entregables.application.artefacto.command.usecase.OmitirArtefactoUseCase;
import com.arquisoft.entregables.application.artefacto.command.usecase.PurgarArtefactosProyectoGradoUseCase;
import com.arquisoft.entregables.application.artefacto.command.usecase.ReactivarArtefactoUseCase;
import com.arquisoft.entregables.application.artefacto.command.usecase.RegistrarVersionCerradaArtefactoUseCase;
import com.arquisoft.entregables.domain.artefacto.OmisionArtefactoDomain;
import com.arquisoft.entregables.domain.artefacto.PurgaArtefactosProyectoGradoDomain;
import com.arquisoft.entregables.domain.artefacto.ReactivacionArtefactoDomain;
import com.arquisoft.entregables.domain.artefacto.RegistroVersionCerradaArtefactoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InteractoresArtefactoImplTest {

    @Mock
    private RegistrarVersionCerradaArtefactoUseCase registrarVersionCerradaArtefactoUseCase;

    @Mock
    private OmitirArtefactoUseCase omitirArtefactoUseCase;

    @Mock
    private ReactivarArtefactoUseCase reactivarArtefactoUseCase;

    @Mock
    private PurgarArtefactosProyectoGradoUseCase purgarArtefactosProyectoGradoUseCase;

    @Test
    void debeDelegarEnElUseCaseYDevolverSuResultado_cuandoSeRegistraUnaVersionCerrada() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var command = RegistrarVersionCerradaArtefactoCommand.crear(
                artefacto.toString(), UtilUUID.generarNuevoUUID().toString(), "Documento",
                new RegistrarVersionCerradaArtefactoCommand.DatosVersion(
                        UtilUUID.generarNuevoUUID().toString(), 1, "pendientes/a.pdf", Instant.now()),
                new RegistrarVersionCerradaArtefactoCommand.DatosRepositorio(
                        UtilUUID.generarNuevoUUID().toString(), UtilUUID.generarNuevoUUID().toString(), "Repo"));
        var esperado = new RegistroVersionCerradaArtefactoResult.Registrada(artefacto);
        when(registrarVersionCerradaArtefactoUseCase.ejecutar(any(RegistroVersionCerradaArtefactoDomain.class)))
                .thenReturn(esperado);
        var interactor = new RegistrarVersionCerradaArtefactoInteractorImpl(registrarVersionCerradaArtefactoUseCase);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        verify(registrarVersionCerradaArtefactoUseCase).ejecutar(any(RegistroVersionCerradaArtefactoDomain.class));
        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    void debeDelegarEnElUseCaseYDevolverSuResultado_cuandoSeOmiteUnArtefacto() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var command = OmitirArtefactoCommand.crear(artefacto.toString(), Instant.now());
        var esperado = new OmisionArtefactoResult.Omitida(artefacto);
        when(omitirArtefactoUseCase.ejecutar(any(OmisionArtefactoDomain.class))).thenReturn(esperado);
        var interactor = new OmitirArtefactoInteractorImpl(omitirArtefactoUseCase);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        verify(omitirArtefactoUseCase).ejecutar(any(OmisionArtefactoDomain.class));
        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    void debeDelegarEnElUseCaseYDevolverSuResultado_cuandoSeReactivaUnArtefacto() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var command = ReactivarArtefactoCommand.crear(artefacto.toString(), Instant.now());
        var esperado = new ReactivacionArtefactoResult.Reactivada(artefacto);
        when(reactivarArtefactoUseCase.ejecutar(any(ReactivacionArtefactoDomain.class))).thenReturn(esperado);
        var interactor = new ReactivarArtefactoInteractorImpl(reactivarArtefactoUseCase);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        verify(reactivarArtefactoUseCase).ejecutar(any(ReactivacionArtefactoDomain.class));
        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    void debeDelegarEnElUseCaseYDevolverSuResultado_cuandoSePurgaUnProyecto() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var command = PurgarArtefactosProyectoGradoCommand.crear(proyecto.toString());
        var esperado = new PurgaArtefactosProyectoGradoResult(proyecto, 2);
        when(purgarArtefactosProyectoGradoUseCase.ejecutar(any(PurgaArtefactosProyectoGradoDomain.class)))
                .thenReturn(esperado);
        var interactor = new PurgarArtefactosProyectoGradoInteractorImpl(purgarArtefactosProyectoGradoUseCase);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        verify(purgarArtefactosProyectoGradoUseCase).ejecutar(any(PurgaArtefactosProyectoGradoDomain.class));
        assertThat(resultado).isSameAs(esperado);
    }
}
