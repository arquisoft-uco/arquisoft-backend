package com.arquisoft.biblioteca.application.autordocumentacion.query.usecase.impl;

import com.arquisoft.biblioteca.application.autordocumentacion.query.criteria.AutorDocumentacionCriteria;
import com.arquisoft.biblioteca.application.autordocumentacion.query.readmodel.AutorDocumentacionReadModel;
import com.arquisoft.biblioteca.application.autordocumentacion.query.secondaryport.AutorDocumentacionQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.AutorDocumentacionKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarAutoresDocumentacionUseCaseImplTest {

    @Mock
    private AutorDocumentacionQueryOutputPort autorDocumentacionQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarAutoresDocumentacionUseCaseImpl useCase;

    @Test
    void debeDevolverLaListaDelPuertoEnUnaSolaLectura_cuandoHayAutores() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var autores = List.of(
                new AutorDocumentacionReadModel(UtilUUID.generarNuevoUUID(), "1", "Ana", "ana@uco.edu.co"),
                new AutorDocumentacionReadModel(UtilUUID.generarNuevoUUID(), "2", "Beto", "beto@uco.edu.co"),
                new AutorDocumentacionReadModel(UtilUUID.generarNuevoUUID(), "3", "Carla", "carla@uco.edu.co"));
        when(autorDocumentacionQueryOutputPort.consultarPorDocumentacion(documentacion)).thenReturn(autores);

        // Act
        var resultado = useCase.ejecutar(new AutorDocumentacionCriteria(documentacion));

        // Assert
        assertThat(resultado).containsExactlyElementsOf(autores);
        verify(autorDocumentacionQueryOutputPort, times(1)).consultarPorDocumentacion(documentacion);
        var orden = inOrder(logger, autorDocumentacionQueryOutputPort);
        orden.verify(logger).debug(AutorDocumentacionKey.LOG_CONSULTANDO, documentacion);
        orden.verify(autorDocumentacionQueryOutputPort).consultarPorDocumentacion(documentacion);
        orden.verify(logger).debug(AutorDocumentacionKey.LOG_CONSULTA_COMPLETADA, 3);
    }

    @Test
    void debeDevolverListaVacia_cuandoNoHayAutores() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        when(autorDocumentacionQueryOutputPort.consultarPorDocumentacion(documentacion)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(new AutorDocumentacionCriteria(documentacion));

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(AutorDocumentacionKey.LOG_CONSULTA_COMPLETADA, 0);
    }
}
