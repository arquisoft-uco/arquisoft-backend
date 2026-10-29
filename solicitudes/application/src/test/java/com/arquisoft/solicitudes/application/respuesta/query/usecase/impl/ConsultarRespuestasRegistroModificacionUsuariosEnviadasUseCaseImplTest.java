package com.arquisoft.solicitudes.application.respuesta.query.usecase.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.destinatario.query.readmodel.DestinatarioReadModel;
import com.arquisoft.solicitudes.application.remitente.query.readmodel.RemitenteReadModel;
import com.arquisoft.solicitudes.application.respuesta.query.criteria.RespuestaCriteria;
import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.solicitudes.application.respuesta.query.secondaryport.RespuestaQueryOutputPort;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.RespuestaKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarRespuestasRegistroModificacionUsuariosEnviadasUseCaseImplTest {

    @Mock
    private RespuestaQueryOutputPort respuestaQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarRespuestasRegistroModificacionUsuariosEnviadasUseCaseImpl useCase;

    private static RespuestaCriteria criteria() {
        return RespuestaCriteria.builder().pagina(0).tamanio(10).build();
    }

    private static RespuestaReadModel respuesta() {
        var remitente = new RemitenteReadModel(UtilUUID.generarNuevoUUID(), "EST-1", "Ana", "ana@uco.edu.co");
        var destinatario = new DestinatarioReadModel(
                UtilUUID.generarNuevoUUID(), "ADMIN-1", "Administrador", "admin@uco.edu.co");
        var solicitud = new SolicitudReadModel(UtilUUID.generarNuevoUUID(), "un registro de usuario",
                Instant.now(), "REGISTRO_Y_MODIFICACION_DE_USUARIOS", "Registro y modificación de usuarios",
                remitente, destinatario);
        return new RespuestaReadModel(UtilUUID.generarNuevoUUID(), "contenido de la respuesta",
                Instant.now(), "EN_REVISION", "En revisión", solicitud);
    }

    @Test
    void debeConsultarElPuertoYRetornarElResultado_cuandoElCriteriaEsValido() {
        // Arrange
        var criteria = criteria();
        var esperado = PaginatedResult.of(List.of(respuesta()), 0, 10, 1L);
        when(respuestaQueryOutputPort.consultar(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        assertThat(resultado.getContent()).hasSize(1);
        verify(respuestaQueryOutputPort, times(1)).consultar(criteria);
        verify(logger).debug(eq(RespuestaKey.LOG_CONSULTANDO_REGISTRO_MODIFICACION_USUARIOS_ENVIADAS),
                eq(0), eq(10), eq(criteria.tieneFiltros()), eq(criteria.tieneOrden()));
        verify(logger).debug(eq(RespuestaKey.LOG_CONSULTA_REGISTRO_MODIFICACION_USUARIOS_ENVIADAS_COMPLETADA),
                eq(1L), eq(0), eq(10));
    }
}
