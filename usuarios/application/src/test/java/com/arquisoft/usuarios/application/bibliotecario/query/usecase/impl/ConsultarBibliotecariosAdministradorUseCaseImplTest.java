package com.arquisoft.usuarios.application.bibliotecario.query.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.usuarios.ConsultarBibliotecariosAdministradorKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.bibliotecario.query.criteria.BibliotecarioCriteria;
import com.arquisoft.usuarios.application.bibliotecario.query.readmodel.BibliotecarioReadModel;
import com.arquisoft.usuarios.application.bibliotecario.query.secondaryport.BibliotecarioQueryOutputPort;
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
class ConsultarBibliotecariosAdministradorUseCaseImplTest {

    @Mock
    private BibliotecarioQueryOutputPort bibliotecarioQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarBibliotecariosAdministradorUseCaseImpl useCase;

    @Test
    void debeRetornarPagina_cuandoHayBibliotecarios() {
        // Arrange
        var criteria = BibliotecarioCriteria.builder().pagina(0).tamanio(10).build();
        var vigente = new BibliotecarioReadModel(UtilUUID.generarNuevoUUID(), "1001", "Ana Ramirez",
                "ana.ramirez@uco.edu.co", "3000000000", "ACTIVO", true);
        var dadoDeBaja = new BibliotecarioReadModel(UtilUUID.generarNuevoUUID(), "1002", "Bruno Diaz",
                "bruno.diaz@uco.edu.co", "3000000001", "INACTIVO", false);
        var esperado = PaginatedResult.of(List.of(vigente, dadoDeBaja), 0, 10, 2L);
        when(bibliotecarioQueryOutputPort.consultarTodos(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(bibliotecarioQueryOutputPort, times(1)).consultarTodos(criteria);
    }

    @Test
    void debeRetornarPaginaVacia_cuandoNoHayBibliotecarios() {
        // Arrange
        var criteria = BibliotecarioCriteria.builder().pagina(0).tamanio(10).build();
        when(bibliotecarioQueryOutputPort.consultarTodos(criteria))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
        verify(bibliotecarioQueryOutputPort).consultarTodos(criteria);
    }

    @Test
    void debeRegistrarDebugDeEntradaYCierre_cuandoConsulta() {
        // Arrange
        var criteria = BibliotecarioCriteria.builder().pagina(1).tamanio(5).build();
        when(bibliotecarioQueryOutputPort.consultarTodos(any()))
                .thenReturn(PaginatedResult.of(List.of(), 1, 5, 7L));

        // Act
        useCase.ejecutar(criteria);

        // Assert
        verify(logger).debug(eq(ConsultarBibliotecariosAdministradorKey.LOG_CONSULTANDO),
                eq(1), eq(5), eq(false), eq(false));
        verify(logger).debug(eq(ConsultarBibliotecariosAdministradorKey.LOG_CONSULTA_COMPLETADA),
                eq(7L), eq(1), eq(5));
        verify(logger, never()).info(any(ClaveMensaje.class), any());
    }
}
