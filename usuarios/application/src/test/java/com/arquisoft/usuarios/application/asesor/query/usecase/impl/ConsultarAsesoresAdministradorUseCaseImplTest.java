package com.arquisoft.usuarios.application.asesor.query.usecase.impl;

import com.arquisoft.usuarios.application.asesor.query.criteria.AsesorCriteria;
import com.arquisoft.usuarios.application.asesor.query.readmodel.AsesorReadModel;
import com.arquisoft.usuarios.application.asesor.query.secondaryport.AsesorQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.usuarios.ConsultarAsesoresAdministradorKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarAsesoresAdministradorUseCaseImplTest {

    @Mock
    private AsesorQueryOutputPort asesorQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarAsesoresAdministradorUseCaseImpl useCase;

    @Test
    void debeDelegarEnElPuertoYRetornarSuResultado() {
        // Arrange
        var criteria = AsesorCriteria.builder().pagina(0).tamanio(10).build();
        var esperado = PaginatedResult.of(
                List.of(new AsesorReadModel(UUID.randomUUID(), "1000", "Ana", "ana@uco.edu.co",
                        "3000000000", "ACTIVO", true)),
                0, 10, 1L);
        when(asesorQueryOutputPort.consultarTodos(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(asesorQueryOutputPort).consultarTodos(criteria);
    }

    @Test
    void debeRetornarPaginaVacia_cuandoElPuertoNoEncuentraRegistros() {
        // Arrange
        var criteria = AsesorCriteria.builder().pagina(0).tamanio(10).build();
        when(asesorQueryOutputPort.consultarTodos(any())).thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeRegistrarDebugEntradaYCierre_sinInfo() {
        // Arrange
        var criteria = AsesorCriteria.builder().pagina(0).tamanio(10).build();
        when(asesorQueryOutputPort.consultarTodos(any())).thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        // Act
        useCase.ejecutar(criteria);

        // Assert
        verify(logger).debug(eq(ConsultarAsesoresAdministradorKey.LOG_CONSULTANDO),
                eq(criteria.getPagina()), eq(criteria.getTamanio()), eq(criteria.tieneFiltros()), eq(criteria.tieneOrden()));
        verify(logger).debug(eq(ConsultarAsesoresAdministradorKey.LOG_CONSULTA_COMPLETADA),
                eq(0L), eq(criteria.getPagina()), eq(criteria.getTamanio()));
        verify(logger, never()).info(any(ClaveMensaje.class), any());
    }
}
