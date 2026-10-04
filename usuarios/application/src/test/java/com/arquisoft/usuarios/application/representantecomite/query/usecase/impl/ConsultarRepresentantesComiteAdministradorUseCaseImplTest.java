package com.arquisoft.usuarios.application.representantecomite.query.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.usuarios.ConsultarRepresentantesComiteAdministradorKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.representantecomite.query.criteria.RepresentanteComiteCriteria;
import com.arquisoft.usuarios.application.representantecomite.query.readmodel.RepresentanteComiteReadModel;
import com.arquisoft.usuarios.application.representantecomite.query.secondaryport.RepresentanteComiteQueryOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarRepresentantesComiteAdministradorUseCaseImplTest {

    @Mock
    private RepresentanteComiteQueryOutputPort representanteComiteQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarRepresentantesComiteAdministradorUseCaseImpl useCase;

    @Test
    void debeConsultarTodosUnaVezYRegistrarDosDebug_cuandoHayResultados() {
        // Arrange
        var criteria = RepresentanteComiteCriteria.builder().pagina(0).tamanio(10).build();
        var esperado = PaginatedResult.of(
                List.of(new RepresentanteComiteReadModel(UtilUUID.generarNuevoUUID(), "1000", "Ana",
                        "ana@uco.edu.co", "3000000000", "ACTIVO", false)),
                0, 10, 1L);
        when(representanteComiteQueryOutputPort.consultarTodos(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(representanteComiteQueryOutputPort, times(1)).consultarTodos(criteria);
        verify(representanteComiteQueryOutputPort, never()).consultarVigentes(any());
        verify(logger).debug(eq(ConsultarRepresentantesComiteAdministradorKey.LOG_CONSULTANDO),
                eq(0), eq(10), eq(false), eq(false));
        verify(logger).debug(eq(ConsultarRepresentantesComiteAdministradorKey.LOG_CONSULTA_COMPLETADA),
                eq(1L), eq(0), eq(10));
        verify(logger, never()).info(any(ClaveMensaje.class), any());
    }

    @Test
    void debeRetornarPaginaVacia_cuandoElPuertoNoEncuentraRegistros() {
        // Arrange
        var criteria = RepresentanteComiteCriteria.builder().pagina(0).tamanio(10).build();
        when(representanteComiteQueryOutputPort.consultarTodos(criteria))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }
}
