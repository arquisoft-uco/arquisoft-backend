package com.arquisoft.artefactos.application.revisionasesor.query.usecase.impl;

import com.arquisoft.artefactos.application.revisionasesor.query.criteria.RevisionAsesorEstudianteCriteria;
import com.arquisoft.artefactos.application.revisionasesor.query.readmodel.RevisionAsesorReadModel;
import com.arquisoft.artefactos.application.revisionasesor.query.secondaryport.RevisionAsesorQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarRevisionesAsesorEstudianteUseCaseImplTest {

    @Mock
    private RevisionAsesorQueryOutputPort revisionAsesorQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarRevisionesAsesorEstudianteUseCaseImpl useCase;

    @Test
    void debeConsultarPuertoUnaVezYRetornarResultado_cuandoHayCoincidencias() {
        // Arrange
        var criteria = RevisionAsesorEstudianteCriteria.builder().pagina(0).tamanio(10).build();
        var revision = new RevisionAsesorReadModel(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 2, "PENDIENTE", "Pendiente");
        var resultadoEsperado = PaginatedResult.of(List.of(revision), 0, 10, 1L);
        when(revisionAsesorQueryOutputPort.consultarTodasEstudiante(criteria)).thenReturn(resultadoEsperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        verify(revisionAsesorQueryOutputPort, times(1)).consultarTodasEstudiante(criteria);
        verify(logger).debug(eq(RevisionAsesorKey.LOG_CONSULTANDO_ESTUDIANTE),
                eq(criteria.tieneFiltros()), eq(criteria.tieneOrden()));
        verify(logger).debug(eq(RevisionAsesorKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA),
                eq(resultadoEsperado.getTotalElements()));
    }

    @Test
    void debeRetornarPaginaVacia_cuandoNoHayRevisiones() {
        // Arrange
        var criteria = RevisionAsesorEstudianteCriteria.builder().pagina(0).tamanio(10).build();
        var resultadoVacio = PaginatedResult.of(List.<RevisionAsesorReadModel>of(), 0, 10, 0L);
        when(revisionAsesorQueryOutputPort.consultarTodasEstudiante(criteria)).thenReturn(resultadoVacio);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }
}
