package com.arquisoft.solicitudes.infrastructure.respuesta.query.secondaryadapter.repository;

import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.respuesta.query.criteria.RespuestaCriteria;
import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.solicitudes.infrastructure.destinatario.command.secondaryadapter.entity.DestinatarioJpaEntity;
import com.arquisoft.solicitudes.infrastructure.remitente.command.secondaryadapter.entity.RemitenteJpaEntity;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.secondaryadapter.entity.EstadoRespuestaJpaEntity;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.secondaryadapter.entity.RespuestaJpaEntity;
import com.arquisoft.solicitudes.infrastructure.solicitud.command.secondaryadapter.entity.SolicitudJpaEntity;
import com.arquisoft.solicitudes.infrastructure.tiposolicitud.command.secondaryadapter.entity.TipoSolicitudJpaEntity;
import com.arquisoft.solicitudes.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RespuestaQueryOutputAdapterCambioAsesorTest {

    private static final String TIPO_NOVEDAD = "NOVEDAD_PARA_EL_COORDINADOR";
    private static final String TIPO_CAMBIO = "CAMBIO_DE_ASESOR";
    private static final String ESTADO_EN_REVISION = "EN_REVISION";

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RespuestaQueryRepository repository;

    private RespuestaQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new RespuestaQueryOutputAdapter(repository, new RespuestaJpaSpecification());

        entityManager.persist(TipoSolicitudJpaEntity.builder()
                .id(TIPO_NOVEDAD).nombre("Novedad para el Coordinador").descripcion("desc").build());
        entityManager.persist(TipoSolicitudJpaEntity.builder()
                .id(TIPO_CAMBIO).nombre("Cambio de Asesor").descripcion("desc").build());
        entityManager.persist(EstadoRespuestaJpaEntity.builder()
                .id(ESTADO_EN_REVISION).nombre("En revisión").descripcion("desc").build());
        entityManager.flush();
    }

    private UsuarioJpaEntity sembrarUsuario(String nombre, String identificador, String email) {
        var usuario = UsuarioJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).nombre(nombre).identificador(identificador).email(email)
                .ocurridoEn(Instant.now()).build();
        entityManager.persist(usuario);
        return usuario;
    }

    private RemitenteJpaEntity sembrarRemitente(String nombre, String identificador, String email) {
        var usuario = sembrarUsuario(nombre, identificador, email);
        var remitente = RemitenteJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).usuarioId(usuario.getId()).build();
        entityManager.persist(remitente);
        return remitente;
    }

    private DestinatarioJpaEntity sembrarDestinatario(String nombre, String identificador, String email) {
        var usuario = sembrarUsuario(nombre, identificador, email);
        var destinatario = DestinatarioJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).usuarioId(usuario.getId()).build();
        entityManager.persist(destinatario);
        return destinatario;
    }

    private SolicitudJpaEntity sembrarSolicitud(RemitenteJpaEntity remitente,
            DestinatarioJpaEntity destinatario, String tipo, Instant fecha, String mensaje) {
        var solicitud = SolicitudJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .remitente(remitente)
                .destinatario(destinatario)
                .tipoSolicitud(entityManager.find(TipoSolicitudJpaEntity.class, tipo))
                .fechaCreacion(fecha)
                .mensajeSolicitud(mensaje)
                .build();
        entityManager.persist(solicitud);
        return solicitud;
    }

    private void sembrarRespuesta(SolicitudJpaEntity solicitud, Instant fechaRespuesta, String contenido) {
        entityManager.persist(RespuestaJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .solicitudId(solicitud.getId())
                .fechaRespuesta(fechaRespuesta)
                .contenido(contenido)
                .estadoRespuesta(entityManager.find(EstadoRespuestaJpaEntity.class, ESTADO_EN_REVISION))
                .build());
    }

    @Test
    void debeRetornarSoloRespuestasDeCambioDeAsesorDelCoordinadorDestinatario_cuandoElCriteriaFuerzaDestinatarioYTipo() {
        // Arrange
        var ana = sembrarRemitente("Ana", "EST-1", "ana@uco.edu.co");
        var coordUno = sembrarDestinatario("Coord Uno", "COORD-1", "coord1@uco.edu.co");
        var coordDos = sembrarDestinatario("Coord Dos", "COORD-2", "coord2@uco.edu.co");
        var fecha = LocalDateTime.of(2026, 3, 1, 10, 0).toInstant(ZoneOffset.UTC);
        var fechaRespuesta = LocalDateTime.of(2026, 3, 3, 8, 0).toInstant(ZoneOffset.UTC);
        var cambioUno = sembrarSolicitud(ana, coordUno, TIPO_CAMBIO, fecha, "cambio uno");
        var cambioDos = sembrarSolicitud(ana, coordDos, TIPO_CAMBIO, fecha, "cambio dos");
        var novedadUno = sembrarSolicitud(ana, coordUno, TIPO_NOVEDAD, fecha, "novedad uno");
        sembrarRespuesta(cambioUno, fechaRespuesta, "respuesta cambio uno");
        sembrarRespuesta(cambioDos, fechaRespuesta, "respuesta cambio dos");
        sembrarRespuesta(novedadUno, fechaRespuesta, "respuesta novedad uno");
        entityManager.flush();
        entityManager.clear();

        var criteria = RespuestaCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.grupo(FiltroConector.AND, List.of(
                        NodoFiltro.predicado("destinatarioUsuarioId", FiltroOperador.ES,
                                coordUno.getUsuarioId().toString()),
                        NodoFiltro.predicado("tipoSolicitudId", FiltroOperador.ES, TIPO_CAMBIO))))
                .build();

        // Act
        var resultado = adapter.consultar(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(RespuestaReadModel::contenido)
                .containsExactly("respuesta cambio uno");
        assertThat(resultado.getContent().get(0).solicitud().tipoSolicitudId()).isEqualTo(TIPO_CAMBIO);
    }
}
