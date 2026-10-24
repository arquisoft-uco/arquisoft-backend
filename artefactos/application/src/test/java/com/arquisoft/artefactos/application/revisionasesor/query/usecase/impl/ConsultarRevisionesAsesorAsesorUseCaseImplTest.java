package com.arquisoft.artefactos.application.revisionasesor.query.usecase.impl;

import com.arquisoft.artefactos.application.revisionasesor.query.criteria.RevisionAsesorAsesorCriteria;
import com.arquisoft.artefactos.application.revisionasesor.query.readmodel.RevisionAsesorReadModel;
import com.arquisoft.artefactos.application.revisionasesor.query.secondaryport.RevisionAsesorQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.util.UtilUUID;
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
class ConsultarRevisionesAsesorAsesorUseCaseImplTest {

    @Mock
    private RevisionAsesorQueryOutputPort revisionAsesorQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarRevisionesAsesorAsesorUseCaseImpl useCase;

    @Test
    void debeConsultarPuertoUnaVezYRetornarResultado_cuandoHayCoincidencias() {
        // Arrange
        var criteria = RevisionAsesorAsesorCriteria.builder().pagina(0).tamanio(10).build();
        var revision = new RevisionAsesorReadModel(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                2, "PENDIENTE", "Pendiente");
        var resultadoEsperado = PaginatedResult.of(List.of(revision), 0, 10, 1L);
        when(revisionAsesorQueryOutputPort.consultarTodasAsesor(criteria)).thenReturn(resultadoEsperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        verify(revisionAsesorQueryOutputPort, times(1)).consultarTodasAsesor(criteria);
        verify(logger).debug(eq(RevisionAsesorKey.LOG_CONSULTANDO_ASESOR),
                eq(criteria.tieneFiltros()), eq(criteria.tieneOrden()));
        verify(logger).debug(eq(RevisionAsesorKey.LOG_CONSULTA_ASESOR_COMPLETADA),
                eq(resultadoEsperado.getTotalElements()));
    }
}
