package com.arquisoft.biblioteca.application.documentacion.command.primaryport.interactor.impl;

import com.arquisoft.biblioteca.application.documentacion.command.primaryport.model.RegistrarDocumentacionCommand;
import com.arquisoft.biblioteca.application.documentacion.command.usecase.RegistrarDocumentacionUseCase;
import com.arquisoft.biblioteca.domain.documentacion.RegistroDocumentacionDomain;
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
class RegistrarDocumentacionInteractorImplTest {

    @Mock
    private RegistrarDocumentacionUseCase registrarDocumentacionUseCase;

    @InjectMocks
    private RegistrarDocumentacionInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseYDevolverElId_cuandoRecibeElCommand() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var bibliotecario = UtilUUID.generarNuevoUUID();
        var idEsperado = UtilUUID.generarNuevoUUID();
        var command = RegistrarDocumentacionCommand.crear(entregable.toString(), bibliotecario);
        when(registrarDocumentacionUseCase.ejecutar(any())).thenReturn(idEsperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isEqualTo(idEsperado);
        var captor = ArgumentCaptor.forClass(RegistroDocumentacionDomain.class);
        verify(registrarDocumentacionUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(captor.getValue().getBibliotecario()).isEqualTo(bibliotecario);
    }
}
