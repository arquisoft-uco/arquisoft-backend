package com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model.ConsultarEvaluacionesFichaPerfilEstudianteQuery;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilEstudianteReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.usecase.ConsultarEvaluacionesFichaPerfilEstudianteUseCase;
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
class ConsultarEvaluacionesFichaPerfilEstudianteInteractorImplTest {

    @Mock
    private ConsultarEvaluacionesFichaPerfilEstudianteUseCase consultarEvaluacionesFichaPerfilEstudianteUseCase;

    @Captor
    private ArgumentCaptor<EvaluacionFichaPerfilEstudianteCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarEvaluacionesFichaPerfilEstudianteInteractorImpl interactor;

    @Test
    void debeConvertirQueryACriteriaYDelegarEnUseCase() {
        // Arrange
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionesFichaPerfilEstudianteQuery.crear(fichaPerfil, estudiante);
        var esperado = List.of(new EvaluacionFichaPerfilEstudianteReadModel(
                UtilUUID.generarNuevoUUID(), fichaPerfil, Instant.now(), "APROBADA", "Aprobada",
                new RepresentanteComiteReadModel(UtilUUID.generarNuevoUUID(), "María Gómez")));
        when(consultarEvaluacionesFichaPerfilEstudianteUseCase.ejecutar(
                any(EvaluacionFichaPerfilEstudianteCriteria.class))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarEvaluacionesFichaPerfilEstudianteUseCase).ejecutar(criteriaCaptor.capture());
        assertThat(criteriaCaptor.getValue().fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(criteriaCaptor.getValue().estudiante()).isEqualTo(estudiante);
    }
}
