package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.finder.AsesorDeEvaluacionAsesorQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.criteria.EvaluacionCuantitativaAsesorCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.readmodel.EvaluacionCuantitativaAsesorReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.secondaryport.EvaluacionCuantitativaAsesorQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.validator.ConsultarEvaluacionesCuantitativasAsesorValidator;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.readmodel.ItemCuantitativoAsesorReadModel;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaAsesorKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEvaluacionesCuantitativasAsesorUseCaseImplTest {

    @Mock
    private AsesorDeEvaluacionAsesorQueryFinder asesorDeEvaluacionAsesorQueryFinder;

    @Mock
    private ConsultarEvaluacionesCuantitativasAsesorValidator validator;

    @Mock
    private EvaluacionCuantitativaAsesorQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEvaluacionesCuantitativasAsesorUseCaseImpl useCase;

    private static EvaluacionCuantitativaAsesorReadModel readModel(String item, int puntaje) {
        return new EvaluacionCuantitativaAsesorReadModel(
                UtilUUID.generarNuevoUUID(),
                puntaje,
                new ItemCuantitativoAsesorReadModel(
                        UtilUUID.generarNuevoUUID(), item, "desc", UtilUUID.generarNuevoUUID(), 5));
    }

    @Test
    void debeConsultarEnOrden_cuandoElAccesoEsValido() {
        // Arrange
        var criteria = new EvaluacionCuantitativaAsesorCriteria(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        var esperado = List.of(readModel("Claridad", 4), readModel("Rigor", 3), readModel("Tono", 5));
        when(asesorDeEvaluacionAsesorQueryFinder.obtener(criteria.evaluacionAsesor())).thenReturn(criteria.asesor());
        when(queryOutputPort.consultar(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var orden = inOrder(asesorDeEvaluacionAsesorQueryFinder, validator, queryOutputPort);
        orden.verify(asesorDeEvaluacionAsesorQueryFinder).obtener(criteria.evaluacionAsesor());
        orden.verify(validator).validar(criteria.evaluacionAsesor(), criteria.asesor(), criteria.asesor());
        orden.verify(queryOutputPort).consultar(criteria);
        verify(asesorDeEvaluacionAsesorQueryFinder, times(1)).obtener(any());
        verify(queryOutputPort, times(1)).consultar(any());
        verify(logger).debug(eq(EvaluacionCuantitativaAsesorKey.LOG_CONSULTANDO), eq(criteria.evaluacionAsesor()));
        verify(logger).debug(eq(EvaluacionCuantitativaAsesorKey.LOG_CONSULTA_COMPLETADA), eq(3));
    }

    @Test
    void debeRetornarListaVacia_cuandoLaEvaluacionNoTieneCuantitativas() {
        // Arrange
        var criteria = new EvaluacionCuantitativaAsesorCriteria(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        when(asesorDeEvaluacionAsesorQueryFinder.obtener(criteria.evaluacionAsesor())).thenReturn(criteria.asesor());
        when(queryOutputPort.consultar(criteria)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
