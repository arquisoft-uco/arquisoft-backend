package com.arquisoft.biblioteca.application.estadodocumentacion.query.usecase.impl;

import com.arquisoft.biblioteca.application.estadodocumentacion.query.readmodel.EstadoDocumentacionReadModel;
import com.arquisoft.biblioteca.application.estadodocumentacion.query.secondaryport.EstadoDocumentacionQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.EstadoDocumentacionKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstadosDocumentacionUseCaseImplTest {

    @Mock
    private EstadoDocumentacionQueryOutputPort estadoDocumentacionQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEstadosDocumentacionUseCaseImpl useCase;

    @Test
    void debeRetornarLosEstadosDelPuertoEnUnaSolaLectura_cuandoExisten() {
        // Arrange
        var estados = List.of(
                new EstadoDocumentacionReadModel("PENDIENTE", "Pendiente", "Pendiente de publicar"),
                new EstadoDocumentacionReadModel("PUBLICADO", "Publicado", "Publicada"),
                new EstadoDocumentacionReadModel("NO_PUBLICADO", "No Publicado", "No publicada"));
        when(estadoDocumentacionQueryOutputPort.findAll()).thenReturn(estados);

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).containsExactlyElementsOf(estados);
        verify(estadoDocumentacionQueryOutputPort, times(1)).findAll();
        verify(logger).debug(EstadoDocumentacionKey.LOG_CONSULTA_COMPLETADA, 3);
    }

    @Test
    void debeRetornarListaVacia_cuandoElPuertoNoTieneEstados() {
        // Arrange
        when(estadoDocumentacionQueryOutputPort.findAll()).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(EstadoDocumentacionKey.LOG_CONSULTA_COMPLETADA, 0);
    }
}
