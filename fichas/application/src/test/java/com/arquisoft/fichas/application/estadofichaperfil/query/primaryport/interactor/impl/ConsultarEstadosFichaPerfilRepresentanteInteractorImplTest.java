package com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estadofichaperfil.query.criteria.EstadoFichaPerfilRepresentanteCriteria;
import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model.ConsultarEstadosFichaPerfilRepresentanteQuery;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.usecase.ConsultarEstadosFichaPerfilRepresentanteUseCase;
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
class ConsultarEstadosFichaPerfilRepresentanteInteractorImplTest {

    @Mock
    private ConsultarEstadosFichaPerfilRepresentanteUseCase consultarEstadosFichaPerfilRepresentanteUseCase;

    @Captor
    private ArgumentCaptor<EstadoFichaPerfilRepresentanteCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarEstadosFichaPerfilRepresentanteInteractorImpl interactor;

    @Test
    void debeConvertirQueryEnCriteriaYDelegar_cuandoSeEjecuta() {
        // Arrange
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var representanteComite = UtilUUID.generarNuevoUUID();
        var query = ConsultarEstadosFichaPerfilRepresentanteQuery.crear(fichaPerfil, representanteComite);
        var esperado = List.of(new EstadoFichaPerfilReadModel("EN_CONSTRUCCION", "En Construccion", Instant.now()));
        when(consultarEstadosFichaPerfilRepresentanteUseCase.ejecutar(any(EstadoFichaPerfilRepresentanteCriteria.class)))
                .thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarEstadosFichaPerfilRepresentanteUseCase).ejecutar(criteriaCaptor.capture());
        assertThat(criteriaCaptor.getValue().fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(criteriaCaptor.getValue().representanteComite()).isEqualTo(representanteComite);
    }
}
