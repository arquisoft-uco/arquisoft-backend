package com.arquisoft.fichas.application.evaluacionfichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilAsesorCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilAsesorReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.secondaryport.EvaluacionFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.representantecomite.query.readmodel.RepresentanteComiteReadModel;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EvaluacionFichaPerfilKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEvaluacionesFichaPerfilAsesorUseCaseImplTest {

    @Mock
    private EvaluacionFichaPerfilQueryOutputPort evaluacionFichaPerfilQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEvaluacionesFichaPerfilAsesorUseCaseImpl useCase;

    @Test
    void debeConsultarElPuertoUnaVez_retornarLaPagina_yLoguearEntradaYCierre() {
        // Arrange
        var criteria = EvaluacionFichaPerfilAsesorCriteria.builder().pagina(0).tamanio(10).build();
        var evaluacion = new EvaluacionFichaPerfilAsesorReadModel(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Proyecto A", Instant.now(),
                "APROBADA", "Aprobada", new RepresentanteComiteReadModel(UtilUUID.generarNuevoUUID(), "Rep"));
        var paginaEsperada = PaginatedResult.of(List.of(evaluacion), 0, 10, 1L);
        when(evaluacionFichaPerfilQueryOutputPort.consultarPorAsesor(criteria)).thenReturn(paginaEsperada);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(paginaEsperada);
        verify(evaluacionFichaPerfilQueryOutputPort).consultarPorAsesor(criteria);
        verify(logger).debug(eq(EvaluacionFichaPerfilKey.LOG_CONSULTANDO_ASESOR), eq(0), eq(10),
                eq(criteria.tieneFiltros()), eq(criteria.tieneOrden()));
        verify(logger).debug(eq(EvaluacionFichaPerfilKey.LOG_CONSULTA_ASESOR_COMPLETADA), eq(1L), eq(0), eq(10));
    }
}
