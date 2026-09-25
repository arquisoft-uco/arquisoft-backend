package com.arquisoft.usuarios.application.asesorficha.query.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.usuarios.ConsultarAsesoresFichaAdministradorKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.asesorficha.query.criteria.AsesorFichaCriteria;
import com.arquisoft.usuarios.application.asesorficha.query.readmodel.AsesorFichaReadModel;
import com.arquisoft.usuarios.application.asesorficha.query.secondaryport.AsesorFichaQueryOutputPort;
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
class ConsultarAsesoresFichaAdministradorUseCaseImplTest {

    @Mock
    private AsesorFichaQueryOutputPort asesorFichaQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarAsesoresFichaAdministradorUseCaseImpl useCase;

    @Test
    void debeConsultarTodosUnaVezYRegistrarDosDebug_cuandoHayResultados() {
        // Arrange
        var criteria = AsesorFichaCriteria.builder().pagina(0).tamanio(10).build();
        var esperado = PaginatedResult.of(
                List.of(new AsesorFichaReadModel(UtilUUID.generarNuevoUUID(), "1000", "Ana", "ana@uco.edu.co",
                        "3000000000", "INACTIVO", false)),
                0, 10, 1L);
        when(asesorFichaQueryOutputPort.consultarTodos(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(asesorFichaQueryOutputPort, times(1)).consultarTodos(criteria);
        verify(asesorFichaQueryOutputPort, never()).consultarVigentes(any());
        verify(logger).debug(eq(ConsultarAsesoresFichaAdministradorKey.LOG_CONSULTANDO),
                eq(0), eq(10), eq(false), eq(false));
        verify(logger).debug(eq(ConsultarAsesoresFichaAdministradorKey.LOG_CONSULTA_COMPLETADA),
                eq(1L), eq(0), eq(10));
        verify(logger, never()).info(any(ClaveMensaje.class), any());
    }

    @Test
    void debeRetornarPaginaVacia_cuandoElPuertoNoEncuentraRegistros() {
        // Arrange
        var criteria = AsesorFichaCriteria.builder().pagina(0).tamanio(10).build();
        when(asesorFichaQueryOutputPort.consultarTodos(criteria))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }
}
