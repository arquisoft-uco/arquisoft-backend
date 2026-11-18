package com.arquisoft.biblioteca.application.estadodocumentacion.query.primaryport.interactor.impl;

import com.arquisoft.biblioteca.application.estadodocumentacion.query.readmodel.EstadoDocumentacionReadModel;
import com.arquisoft.biblioteca.application.estadodocumentacion.query.usecase.ConsultarEstadosDocumentacionUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstadosDocumentacionInteractorImplTest {

    @Mock
    private ConsultarEstadosDocumentacionUseCase consultarEstadosDocumentacionUseCase;

    @InjectMocks
    private ConsultarEstadosDocumentacionInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_cuandoSeEjecuta() {
        // Arrange
        var estados = List.of(new EstadoDocumentacionReadModel("PENDIENTE", "Pendiente", "Descripcion"));
        when(consultarEstadosDocumentacionUseCase.ejecutar()).thenReturn(estados);

        // Act
        var resultado = interactor.ejecutar();

        // Assert
        assertThat(resultado).isSameAs(estados);
        verify(consultarEstadosDocumentacionUseCase).ejecutar();
    }
}
