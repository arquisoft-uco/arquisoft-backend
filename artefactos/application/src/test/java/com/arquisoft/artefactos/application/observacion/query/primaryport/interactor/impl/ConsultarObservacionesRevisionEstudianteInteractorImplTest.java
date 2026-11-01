package com.arquisoft.artefactos.application.observacion.query.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.observacion.query.criteria.ObservacionEstudianteCriteria;
import com.arquisoft.artefactos.application.observacion.query.primaryport.model.ConsultarObservacionesRevisionEstudianteQuery;
import com.arquisoft.artefactos.application.observacion.query.readmodel.ObservacionReadModel;
import com.arquisoft.artefactos.application.observacion.query.usecase.ConsultarObservacionesRevisionEstudianteUseCase;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarObservacionesRevisionEstudianteInteractorImplTest {

    @Mock
    private ConsultarObservacionesRevisionEstudianteUseCase consultarObservacionesRevisionEstudianteUseCase;

    @InjectMocks
    private ConsultarObservacionesRevisionEstudianteInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElCriteria_cuandoEjecuta() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarObservacionesRevisionEstudianteQuery.crear(revision, estudiante);
        List<ObservacionReadModel> esperado = List.of();
        when(consultarObservacionesRevisionEstudianteUseCase.ejecutar(
                new ObservacionEstudianteCriteria(revision, estudiante))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarObservacionesRevisionEstudianteUseCase)
                .ejecutar(new ObservacionEstudianteCriteria(revision, estudiante));
    }
}
