package com.arquisoft.fichas.application.revisionitem.query.usecase.impl;

import com.arquisoft.fichas.application.revisionitem.query.criteria.RevisionItemCriteria;
import com.arquisoft.fichas.application.revisionitem.query.readmodel.RevisionItemReadModel;
import com.arquisoft.fichas.application.revisionitem.query.secondaryport.RevisionItemQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.RevisionItemKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarRevisionesItemAsesorUseCaseImplTest {

    @Mock
    private RevisionItemQueryOutputPort revisionItemQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarRevisionesItemAsesorUseCaseImpl useCase;

    @Test
    void debeConsultarPuertoYRetornarResultado_cuandoHayCoincidencias() {
        // Arrange
        var criteria = RevisionItemCriteria.builder().pagina(0).tamanio(10).build();
        var revision = new RevisionItemReadModel(
                UUID.randomUUID(), UUID.randomUUID(), "EN_PROGRESO", "En Progreso", Instant.now());
        var resultadoEsperado = PaginatedResult.of(List.of(revision), 0, 10, 1L);

        when(revisionItemQueryOutputPort.consultarTodas(criteria)).thenReturn(resultadoEsperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        assertThat(resultado.getContent()).hasSize(1);
        verify(revisionItemQueryOutputPort, times(1)).consultarTodas(criteria);
        verify(logger).debug(eq(RevisionItemKey.LOG_CONSULTANDO_ELABORADAS),
                eq(criteria.tieneFiltros()), eq(criteria.tieneOrden()));
        verify(logger).debug(eq(RevisionItemKey.LOG_CONSULTA_ELABORADAS_COMPLETADA),
                eq(resultadoEsperado.getTotalElements()));
    }

    @Test
    void debeRetornarPaginaVacia_cuandoElPuertoNoEncuentraRevisiones() {
        // Arrange
        var criteria = RevisionItemCriteria.builder().pagina(0).tamanio(10).build();
        var resultadoVacio = PaginatedResult.of(List.<RevisionItemReadModel>of(), 0, 10, 0L);
        when(revisionItemQueryOutputPort.consultarTodas(any(RevisionItemCriteria.class)))
                .thenReturn(resultadoVacio);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }
}
