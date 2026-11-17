package com.arquisoft.entregables.application.artefacto.query.primaryport.interactor.impl;

import com.arquisoft.entregables.application.artefacto.query.criteria.VistaPreviaPaqueteCriteria;
import com.arquisoft.entregables.application.artefacto.query.primaryport.model.ConsultarVistaPreviaPaqueteQuery;
import com.arquisoft.entregables.application.artefacto.query.readmodel.ArtefactoVistaPreviaReadModel;
import com.arquisoft.entregables.application.artefacto.query.usecase.ConsultarVistaPreviaPaqueteUseCase;
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
class ConsultarVistaPreviaPaqueteInteractorImplTest {

    @Mock
    private ConsultarVistaPreviaPaqueteUseCase consultarVistaPreviaPaqueteUseCase;

    @InjectMocks
    private ConsultarVistaPreviaPaqueteInteractorImpl interactor;

    @Test
    void debeConvertirAlCriteriaYDelegar_cuandoSeConsultaLaVistaPrevia() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var esperado = List.of(new ArtefactoVistaPreviaReadModel(UtilUUID.generarNuevoUUID(), "Doc", 1));
        when(consultarVistaPreviaPaqueteUseCase.ejecutar(new VistaPreviaPaqueteCriteria(proyecto, asesor)))
                .thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(ConsultarVistaPreviaPaqueteQuery.crear(proyecto, asesor));

        // Assert
        verify(consultarVistaPreviaPaqueteUseCase).ejecutar(new VistaPreviaPaqueteCriteria(proyecto, asesor));
        assertThat(resultado).isSameAs(esperado);
    }
}
