package com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estadofichaperfil.query.criteria.EstadoFichaPerfilCoordinadorCriteria;
import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model.ConsultarEstadosFichaPerfilCoordinadorQuery;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.usecase.ConsultarEstadosFichaPerfilCoordinadorUseCase;
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
class ConsultarEstadosFichaPerfilCoordinadorInteractorImplTest {

    @Mock
    private ConsultarEstadosFichaPerfilCoordinadorUseCase consultarEstadosFichaPerfilCoordinadorUseCase;

    @Captor
    private ArgumentCaptor<EstadoFichaPerfilCoordinadorCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarEstadosFichaPerfilCoordinadorInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCaseConCriteria_cuandoRecibeQuery() {
        // Arrange
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var query = ConsultarEstadosFichaPerfilCoordinadorQuery.crear(fichaPerfil);
        var esperado = List.of(new EstadoFichaPerfilReadModel("EN_CONSTRUCCION", "En Construccion", Instant.now()));
        when(consultarEstadosFichaPerfilCoordinadorUseCase.ejecutar(any(EstadoFichaPerfilCoordinadorCriteria.class)))
                .thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarEstadosFichaPerfilCoordinadorUseCase).ejecutar(criteriaCaptor.capture());
        assertThat(criteriaCaptor.getValue().fichaPerfil()).isEqualTo(fichaPerfil);
    }
}
