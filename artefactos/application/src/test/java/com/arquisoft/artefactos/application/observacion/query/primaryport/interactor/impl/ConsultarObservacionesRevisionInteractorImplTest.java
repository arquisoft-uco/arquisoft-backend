package com.arquisoft.artefactos.application.observacion.query.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.observacion.query.criteria.ObservacionCriteria;
import com.arquisoft.artefactos.application.observacion.query.primaryport.model.ConsultarObservacionesRevisionQuery;
import com.arquisoft.artefactos.application.observacion.query.readmodel.ObservacionReadModel;
import com.arquisoft.artefactos.application.observacion.query.usecase.ConsultarObservacionesRevisionUseCase;
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
class ConsultarObservacionesRevisionInteractorImplTest {

    @Mock
    private ConsultarObservacionesRevisionUseCase consultarObservacionesRevisionUseCase;

    @InjectMocks
    private ConsultarObservacionesRevisionInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElCriteria_cuandoEjecuta() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var query = ConsultarObservacionesRevisionQuery.crear(revision, asesor);
        List<ObservacionReadModel> esperado = List.of();
        when(consultarObservacionesRevisionUseCase.ejecutar(new ObservacionCriteria(revision, asesor)))
                .thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarObservacionesRevisionUseCase).ejecutar(new ObservacionCriteria(revision, asesor));
    }
}
