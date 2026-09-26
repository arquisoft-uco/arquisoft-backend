package com.arquisoft.usuarios.application.asesorficha.query.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.usuarios.ConsultarAsesoresFichaVigentesKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.asesorficha.query.criteria.AsesorFichaVigenteCriteria;
import com.arquisoft.usuarios.application.asesorficha.query.readmodel.AsesorFichaVigenteReadModel;
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
class ConsultarAsesoresFichaVigentesUseCaseImplTest {

    @Mock
    private AsesorFichaQueryOutputPort asesorFichaQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarAsesoresFichaVigentesUseCaseImpl useCase;

    @Test
    void debeConsultarVigentesUnaVezYRegistrarDosDebug_cuandoHayResultados() {
        // Arrange
        var criteria = AsesorFichaVigenteCriteria.builder().pagina(1).tamanio(5).build();
        var esperado = PaginatedResult.of(
                List.of(new AsesorFichaVigenteReadModel(UtilUUID.generarNuevoUUID(), "1000", "Ana",
                        "ana@uco.edu.co", "3000000000", "ACTIVO")),
                1, 5, 6L);
        when(asesorFichaQueryOutputPort.consultarVigentes(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(asesorFichaQueryOutputPort, times(1)).consultarVigentes(criteria);
        verify(asesorFichaQueryOutputPort, never()).consultarTodos(any());
        verify(logger).debug(eq(ConsultarAsesoresFichaVigentesKey.LOG_CONSULTANDO),
                eq(1), eq(5), eq(false), eq(false));
        verify(logger).debug(eq(ConsultarAsesoresFichaVigentesKey.LOG_CONSULTA_COMPLETADA),
                eq(6L), eq(1), eq(5));
        verify(logger, never()).info(any(ClaveMensaje.class), any());
    }

    @Test
    void debeRetornarPaginaVacia_cuandoElPuertoNoEncuentraRegistros() {
        // Arrange
        var criteria = AsesorFichaVigenteCriteria.builder().pagina(0).tamanio(10).build();
        when(asesorFichaQueryOutputPort.consultarVigentes(criteria))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }
}
