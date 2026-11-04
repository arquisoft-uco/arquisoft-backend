package com.arquisoft.artefactos.application.observacion.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.observacion.command.primaryport.model.RegistrarObservacionCommand;
import com.arquisoft.artefactos.application.observacion.command.usecase.RegistrarObservacionUseCase;
import com.arquisoft.artefactos.domain.observacion.RegistroObservacionDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class RegistrarObservacionInteractorImplTest {

    @Mock
    private RegistrarObservacionUseCase useCase;

    @InjectMocks
    private RegistrarObservacionInteractorImpl interactor;

    @Test
    void debeMapearYDelegar_cuandoSeEjecuta() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var id = UtilUUID.generarNuevoUUID();
        var command = RegistrarObservacionCommand.crear(revision, "Detalle", asesor);
        when(useCase.ejecutar(any(RegistroObservacionDomain.class))).thenReturn(id);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(RegistroObservacionDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getAsesor()).isEqualTo(asesor);
        assertThat(captor.getValue().getObservacion().getRevisionAsesor()).isEqualTo(revision);
        assertThat(resultado).isSameAs(id);
    }
}
