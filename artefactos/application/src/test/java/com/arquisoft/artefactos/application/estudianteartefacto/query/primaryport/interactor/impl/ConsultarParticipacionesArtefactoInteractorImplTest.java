package com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.query.criteria.ParticipacionArtefactoCriteria;
import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.model.ConsultarParticipacionesArtefactoQuery;
import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.ParticipacionArtefactoReadModel;
import com.arquisoft.artefactos.application.estudianteartefacto.query.usecase.ConsultarParticipacionesArtefactoUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarParticipacionesArtefactoInteractorImplTest {

    @Mock
    private ConsultarParticipacionesArtefactoUseCase consultarParticipacionesArtefactoUseCase;

    @InjectMocks
    private ConsultarParticipacionesArtefactoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElCriteria_cuandoEjecuta() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(1, 5, List.of(), null);
        var query = ConsultarParticipacionesArtefactoQuery.crear(estudiante, criterio);
        var esperado = PaginatedResult.<ParticipacionArtefactoReadModel>of(List.of(), 1, 5, 0L);
        when(consultarParticipacionesArtefactoUseCase.ejecutar(any(ParticipacionArtefactoCriteria.class)))
                .thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        var captor = ArgumentCaptor.forClass(ParticipacionArtefactoCriteria.class);
        verify(consultarParticipacionesArtefactoUseCase).ejecutar(captor.capture());
        assertThat(resultado).isSameAs(esperado);
        assertThat(captor.getValue().getPagina()).isEqualTo(1);
        assertThat(captor.getValue().getTamanio()).isEqualTo(5);
        assertThat(captor.getValue().getRaiz()).isNotNull();
    }
}
