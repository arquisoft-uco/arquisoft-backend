package com.arquisoft.biblioteca.application.documentacion.command.primaryport.interactor.impl;

import com.arquisoft.biblioteca.application.documentacion.command.primaryport.model.CambiarEstadoDocumentacionCommand;
import com.arquisoft.biblioteca.application.documentacion.command.usecase.CambiarEstadoDocumentacionUseCase;
import com.arquisoft.biblioteca.domain.documentacion.CambioEstadoDocumentacionDomain;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CambiarEstadoDocumentacionInteractorImplTest {

    @Mock
    private CambiarEstadoDocumentacionUseCase cambiarEstadoDocumentacionUseCase;

    @InjectMocks
    private CambiarEstadoDocumentacionInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDominioMapeadoDelCommand() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var command = CambiarEstadoDocumentacionCommand.crear(documentacion, "PUBLICADO");

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(CambioEstadoDocumentacionDomain.class);
        verify(cambiarEstadoDocumentacionUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getDocumentacion()).isEqualTo(documentacion);
        assertThat(captor.getValue().getEstado()).isEqualTo(EstadoDocumentacion.PUBLICADO);
    }
}
