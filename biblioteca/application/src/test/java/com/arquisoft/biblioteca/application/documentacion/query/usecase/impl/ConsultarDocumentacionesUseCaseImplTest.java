package com.arquisoft.biblioteca.application.documentacion.query.usecase.impl;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.DocumentacionCriteria;
import com.arquisoft.biblioteca.application.documentacion.query.readmodel.DocumentacionReadModel;
import com.arquisoft.biblioteca.application.documentacion.query.secondaryport.DocumentacionQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.DocumentacionKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarDocumentacionesUseCaseImplTest {

    @Mock
    private DocumentacionQueryOutputPort documentacionQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarDocumentacionesUseCaseImpl useCase;

    @Test
    void debeDelegarEnElPuertoUnaVezYRetornarLaPagina_cuandoHayCriteria() {
        // Arrange
        var criteria = DocumentacionCriteria.builder().pagina(0).tamanio(10).build();
        var esperado = PaginatedResult.<DocumentacionReadModel>of(List.of(), 0, 10, 0L);
        when(documentacionQueryOutputPort.consultar(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(documentacionQueryOutputPort, times(1)).consultar(criteria);
        verify(logger).debug(eq(DocumentacionKey.LOG_CONSULTANDO),
                eq(criteria.getPagina()), eq(criteria.getTamanio()),
                eq(criteria.tieneFiltros()), eq(criteria.tieneOrden()));
        verify(logger).debug(eq(DocumentacionKey.LOG_CONSULTA_COMPLETADA),
                eq(esperado.getTotalElements()), eq(criteria.getPagina()), eq(criteria.getTamanio()));
    }
}
