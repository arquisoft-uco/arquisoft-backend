package com.arquisoft.artefactos.application.revisionasesor.command.primaryport;

import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.interactor.impl.RegistrarRevisionAsesorInteractorImpl;
import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.model.RegistrarRevisionAsesorCommand;
import com.arquisoft.artefactos.application.revisionasesor.command.usecase.RegistrarRevisionAsesorUseCase;
import com.arquisoft.artefactos.domain.revisionasesor.RegistroRevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarRevisionAsesorPrimaryPortTest {

    @Mock
    private RegistrarRevisionAsesorUseCase useCase;

    @InjectMocks
    private RegistrarRevisionAsesorInteractorImpl interactor;

    @Test
    void debeMapearYDelegar_cuandoSeEjecutaElInteractor() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var idCreado = UtilUUID.generarNuevoUUID();
        var command = RegistrarRevisionAsesorCommand.crear(version, asesor);
        when(useCase.ejecutar(any(RegistroRevisionAsesorDomain.class)))
                .thenReturn(idCreado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(RegistroRevisionAsesorDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(resultado).isSameAs(idCreado);
        assertThat(captor.getValue().getAsesor()).isEqualTo(asesor);
        assertThat(captor.getValue().getRevision().getVersionArtefacto()).isEqualTo(version);
        assertThat(captor.getValue().getRevision().getEstado()).isEqualTo(EstadoRevisionAsesor.PENDIENTE);
    }

    @Test
    void debeLanzarErrorDeEntrada_cuandoVersionYAsesorNulos() {
        // Act & Assert
        assertThatThrownBy(() -> RegistrarRevisionAsesorCommand.crear(null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting("codigoError").containsExactlyInAnyOrder(
                            ArtefactosCodes.RevisionAsesor.VERSION_ARTEFACTO_REQUERIDA,
                            ArtefactosCodes.RevisionAsesor.ASESOR_REQUERIDO);
                });
    }
}
