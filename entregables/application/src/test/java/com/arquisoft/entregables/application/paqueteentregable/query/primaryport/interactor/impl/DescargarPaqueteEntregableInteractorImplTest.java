package com.arquisoft.entregables.application.paqueteentregable.query.primaryport.interactor.impl;

import com.arquisoft.entregables.application.paqueteentregable.query.criteria.DescargaPaqueteEntregableCriteria;
import com.arquisoft.entregables.application.paqueteentregable.query.primaryport.model.DescargarPaqueteEntregableQuery;
import com.arquisoft.entregables.application.paqueteentregable.query.primaryport.model.RolConsultaPaqueteEntregable;
import com.arquisoft.entregables.application.paqueteentregable.query.readmodel.EnlaceDescargaPaqueteEntregableReadModel;
import com.arquisoft.entregables.application.paqueteentregable.query.usecase.DescargarPaqueteEntregableUseCase;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DescargarPaqueteEntregableInteractorImplTest {

    @Mock
    private DescargarPaqueteEntregableUseCase descargarPaqueteEntregableUseCase;

    @InjectMocks
    private DescargarPaqueteEntregableInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElCriteriaDeLosRoles() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var actor = UtilUUID.generarNuevoUUID();
        var query = DescargarPaqueteEntregableQuery.crear(entregable, actor,
                List.of(RolConsultaPaqueteEntregable.ASESOR));
        var criteria = new DescargaPaqueteEntregableCriteria(entregable, actor, true, false);
        var esperado = new EnlaceDescargaPaqueteEntregableReadModel("https://url", Instant.now());
        when(descargarPaqueteEntregableUseCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        verify(descargarPaqueteEntregableUseCase).ejecutar(criteria);
        assertThat(resultado).isSameAs(esperado);
    }
}
