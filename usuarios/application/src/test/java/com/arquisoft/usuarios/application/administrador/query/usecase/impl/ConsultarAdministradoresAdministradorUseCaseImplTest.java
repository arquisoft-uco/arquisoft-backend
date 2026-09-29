package com.arquisoft.usuarios.application.administrador.query.usecase.impl;

import com.arquisoft.usuarios.application.administrador.query.criteria.AdministradorCriteria;
import com.arquisoft.usuarios.application.administrador.query.readmodel.AdministradorReadModel;
import com.arquisoft.usuarios.application.administrador.query.secondaryport.AdministradorQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.usuarios.ConsultarAdministradoresAdministradorKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.util.UtilUUID;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarAdministradoresAdministradorUseCaseImplTest {

    @Mock
    private AdministradorQueryOutputPort administradorQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarAdministradoresAdministradorUseCaseImpl useCase;

    @Test
    void debeDelegarEnElPuertoYRetornarSuResultado() {
        // Arrange
        var criteria = AdministradorCriteria.builder().pagina(0).tamanio(10).build();
        var esperado = PaginatedResult.of(
                List.of(new AdministradorReadModel(UtilUUID.generarNuevoUUID(), "1000", "Ana", "ana@uco.edu.co",
                        "3000000000", "ACTIVO", true)),
                0, 10, 1L);
        when(administradorQueryOutputPort.consultarTodos(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(administradorQueryOutputPort).consultarTodos(criteria);
    }

    @Test
    void debeRetornarPaginaVacia_cuandoElPuertoNoEncuentraRegistros() {
        // Arrange
        var criteria = AdministradorCriteria.builder().pagina(0).tamanio(10).build();
        when(administradorQueryOutputPort.consultarTodos(any())).thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeRegistrarDebugEntradaYCierre_sinInfo() {
        // Arrange
        var criteria = AdministradorCriteria.builder().pagina(0).tamanio(10).build();
        when(administradorQueryOutputPort.consultarTodos(any())).thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        // Act
        useCase.ejecutar(criteria);

        // Assert
        verify(logger).debug(eq(ConsultarAdministradoresAdministradorKey.LOG_CONSULTANDO),
                eq(criteria.getPagina()), eq(criteria.getTamanio()), eq(criteria.tieneFiltros()), eq(criteria.tieneOrden()));
        verify(logger).debug(eq(ConsultarAdministradoresAdministradorKey.LOG_CONSULTA_COMPLETADA),
                eq(0L), eq(criteria.getPagina()), eq(criteria.getTamanio()));
        verify(logger, never()).info(any(ClaveMensaje.class), any());
    }
}
