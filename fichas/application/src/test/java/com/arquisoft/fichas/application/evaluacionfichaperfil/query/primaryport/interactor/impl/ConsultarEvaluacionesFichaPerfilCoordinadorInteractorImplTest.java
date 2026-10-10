package com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilCoordinadorCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model.ConsultarEvaluacionesFichaPerfilCoordinadorQuery;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilCoordinadorReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.usecase.ConsultarEvaluacionesFichaPerfilCoordinadorUseCase;
import com.arquisoft.fichas.application.representantecomite.query.readmodel.RepresentanteComiteReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEvaluacionesFichaPerfilCoordinadorInteractorImplTest {

    @Mock
    private ConsultarEvaluacionesFichaPerfilCoordinadorUseCase consultarEvaluacionesFichaPerfilCoordinadorUseCase;

    @Captor
    private ArgumentCaptor<EvaluacionFichaPerfilCoordinadorCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarEvaluacionesFichaPerfilCoordinadorInteractorImpl interactor;

    @Test
    void debeConvertirQueryYDelegarEnUseCase_cuandoEjecuta() {
        // Arrange
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionesFichaPerfilCoordinadorQuery.crear(fichaPerfil);
        var esperado = List.of(new EvaluacionFichaPerfilCoordinadorReadModel(
                UtilUUID.generarNuevoUUID(), fichaPerfil, Instant.now(), "APROBADA", "Aprobada",
                new RepresentanteComiteReadModel(UtilUUID.generarNuevoUUID(), "María Gómez")));
        when(consultarEvaluacionesFichaPerfilCoordinadorUseCase.ejecutar(
                any(EvaluacionFichaPerfilCoordinadorCriteria.class))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarEvaluacionesFichaPerfilCoordinadorUseCase).ejecutar(criteriaCaptor.capture());
        assertThat(criteriaCaptor.getValue().fichaPerfil()).isEqualTo(fichaPerfil);
    }
}
