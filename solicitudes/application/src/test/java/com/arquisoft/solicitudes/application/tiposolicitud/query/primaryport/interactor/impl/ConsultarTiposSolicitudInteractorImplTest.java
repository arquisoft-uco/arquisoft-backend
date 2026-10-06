package com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.tiposolicitud.query.criteria.TipoSolicitudCriteria;
import com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.model.ConsultarTiposSolicitudQuery;
import com.arquisoft.solicitudes.application.tiposolicitud.query.readmodel.TipoSolicitudReadModel;
import com.arquisoft.solicitudes.application.tiposolicitud.query.usecase.ConsultarTiposSolicitudUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarTiposSolicitudInteractorImplTest {

    @Mock
    private ConsultarTiposSolicitudUseCase consultarTiposSolicitudUseCase;

    @InjectMocks
    private ConsultarTiposSolicitudInteractorImpl interactor;

    @Test
    void debeConvertirQueryACriteria_yDelegarEnUseCase() {
        // Arrange
        var query = ConsultarTiposSolicitudQuery.crear(Set.of("CAMBIO_DE_ASESOR"));
        var resultadoEsperado = List.of(
                new TipoSolicitudReadModel("CAMBIO_DE_ASESOR", "Cambio de Asesor",
                        "Solicitud para modificar el asesor"));
        when(consultarTiposSolicitudUseCase.ejecutar(any(TipoSolicitudCriteria.class))).thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        var captor = ArgumentCaptor.forClass(TipoSolicitudCriteria.class);
        verify(consultarTiposSolicitudUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().tipos()).containsExactly("CAMBIO_DE_ASESOR");
        assertThat(resultado).isSameAs(resultadoEsperado);
    }
}
