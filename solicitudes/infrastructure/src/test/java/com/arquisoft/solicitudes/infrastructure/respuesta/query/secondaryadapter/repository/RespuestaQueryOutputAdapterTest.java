package com.arquisoft.solicitudes.infrastructure.respuesta.query.secondaryadapter.repository;

import com.arquisoft.solicitudes.application.respuesta.query.criteria.RespuestaCriteria;
import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.solicitudes.infrastructure.destinatario.command.secondaryadapter.entity.DestinatarioJpaEntity;
import com.arquisoft.solicitudes.infrastructure.remitente.command.secondaryadapter.entity.RemitenteJpaEntity;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.secondaryadapter.entity.EstadoRespuestaJpaEntity;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.secondaryadapter.entity.RespuestaJpaEntity;
import com.arquisoft.solicitudes.infrastructure.solicitud.command.secondaryadapter.entity.SolicitudJpaEntity;
import com.arquisoft.solicitudes.infrastructure.tiposolicitud.command.secondaryadapter.entity.TipoSolicitudJpaEntity;
import com.arquisoft.solicitudes.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RespuestaQueryOutputAdapterTest {

    private static final String TIPO_NOVEDAD = "NOVEDAD_PARA_EL_COORDINADOR";
    private static final String TIPO_CAMBIO = "CAMBIO_DE_ASESOR";
    private static final String ESTADO_EN_REVISION = "EN_REVISION";
    private static final String ESTADO_APROBADA = "APROBADA";

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
        entityManager.persist(EstadoRespuestaJpaEntity.builder()
                .id(ESTADO_APROBADA).nombre("Aprobada").descripcion("desc").build());
        entityManager.flush();
    }

    private RemitenteJpaEntity sembrarRemitente(String nombre, String identificador, String email) {
        var usuario = UsuarioJpaEntity.builder()
                .id(UUID.randomUUID()).nombre(nombre).identificador(identificador).email(email)
                .ocurridoEn(Instant.now()).build();
        entityManager.persist(usuario);
        var remitente = RemitenteJpaEntity.builder()
                .id(UUID.randomUUID()).usuarioId(usuario.getId()).build();
        entityManager.persist(remitente);
        return remitente;
    }

    private DestinatarioJpaEntity sembrarDestinatario(String nombre, String identificador, String email) {
        var usuario = UsuarioJpaEntity.builder()
                .id(UUID.randomUUID()).nombre(nombre).identificador(identificador).email(email)
                .ocurridoEn(Instant.now()).build();
        entityManager.persist(usuario);
        var destinatario = DestinatarioJpaEntity.builder()
                .id(UUID.randomUUID()).usuarioId(usuario.getId()).build();
        entityManager.persist(destinatario);
        return destinatario;
    }

    private SolicitudJpaEntity sembrarSolicitud(RemitenteJpaEntity remitente,
            DestinatarioJpaEntity destinatario, String tipo, Instant fecha, String mensaje) {
        var solicitud = SolicitudJpaEntity.builder()
                .id(UUID.randomUUID())
                .remitente(remitente)
                .destinatario(destinatario)
                .tipoSolicitud(entityManager.find(TipoSolicitudJpaEntity.class, tipo))
                .fechaCreacion(fecha)
                .mensajeSolicitud(mensaje)
                .build();
        entityManager.persist(solicitud);
        return solicitud;
    }

    private void sembrarRespuesta(SolicitudJpaEntity solicitud, String estadoId,
            LocalDateTime fechaRespuesta, String contenido) {
        entityManager.persist(RespuestaJpaEntity.builder()
                .id(UUID.randomUUID())
                .solicitudId(solicitud.getId())
                .fechaRespuesta(fechaRespuesta)
                .contenido(contenido)
                .estadoRespuesta(entityManager.find(EstadoRespuestaJpaEntity.class, estadoId))
                .build());
    }

    private void sincronizar() {
        entityManager.flush();
        entityManager.clear();
    }

    private static RespuestaCriteria porRemitente(UUID remitenteUsuarioId) {
        return RespuestaCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("remitenteUsuarioId", FiltroOperador.ES,
                        remitenteUsuarioId.toString()))
                .build();
    }

    @Test
    void debeRetornarSoloLasRespuestasDelEstudianteRemitente_yProyectarSolicitudCompleta() {
        // Arrange
        var ana = sembrarRemitente("Ana Estudiante", "EST-1", "ana@uco.edu.co");
        var beto = sembrarRemitente("Beto Estudiante", "EST-2", "beto@uco.edu.co");
        var coord = sembrarDestinatario("Coordinadora Uno", "COORD-1", "coord1@uco.edu.co");
        var solicitudAna = sembrarSolicitud(ana, coord, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 3, 1, 10, 0).toInstant(ZoneOffset.UTC), "novedad de Ana");
        var solicitudBeto = sembrarSolicitud(beto, coord, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 3, 2, 10, 0).toInstant(ZoneOffset.UTC), "novedad de Beto");
        sembrarRespuesta(solicitudAna, ESTADO_EN_REVISION,
                LocalDateTime.of(2026, 3, 3, 8, 0), "respuesta para Ana");
        sembrarRespuesta(solicitudBeto, ESTADO_APROBADA,
                LocalDateTime.of(2026, 3, 4, 8, 0), "respuesta para Beto");
        sincronizar();

        // Act
        PaginatedResult<RespuestaReadModel> resultado = adapter.consultar(porRemitente(ana.getUsuarioId()));

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        RespuestaReadModel leida = resultado.getContent().get(0);
        assertThat(leida.contenido()).isEqualTo("respuesta para Ana");
        assertThat(leida.estadoRespuestaId()).isEqualTo(ESTADO_EN_REVISION);
        assertThat(leida.estadoRespuestaNombre()).isEqualTo("En revisión");
        assertThat(leida.solicitud().id()).isEqualTo(solicitudAna.getId());
        assertThat(leida.solicitud().mensajeSolicitud()).isEqualTo("novedad de Ana");
        assertThat(leida.solicitud().tipoSolicitudId()).isEqualTo(TIPO_NOVEDAD);
        assertThat(leida.solicitud().tipoSolicitudNombre()).isEqualTo("Novedad para el Coordinador");
        assertThat(leida.solicitud().remitente().identificador()).isEqualTo("EST-1");
        assertThat(leida.solicitud().remitente().nombre()).isEqualTo("Ana Estudiante");
        assertThat(leida.solicitud().remitente().email()).isEqualTo("ana@uco.edu.co");
        assertThat(leida.solicitud().destinatario().identificador()).isEqualTo("COORD-1");
        assertThat(leida.solicitud().destinatario().nombre()).isEqualTo("Coordinadora Uno");
        assertThat(leida.solicitud().destinatario().email()).isEqualTo("coord1@uco.edu.co");
    }

    @Test
    void debeExcluirRespuestasDeSolicitudesDeOtroTipo_cuandoElCriteriaFuerzaElTipoYRemitente() {
        // Arrange
        var ana = sembrarRemitente("Ana", "EST-1", "ana@uco.edu.co");
        var coord = sembrarDestinatario("Coord", "COORD-1", "coord@uco.edu.co");
        var solicitudNovedad = sembrarSolicitud(ana, coord, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 3, 1, 10, 0).toInstant(ZoneOffset.UTC), "es novedad");
        var solicitudCambio = sembrarSolicitud(ana, coord, TIPO_CAMBIO,
                LocalDateTime.of(2026, 3, 2, 10, 0).toInstant(ZoneOffset.UTC), "es cambio");
        sembrarRespuesta(solicitudNovedad, ESTADO_EN_REVISION,
                LocalDateTime.of(2026, 3, 3, 8, 0), "sobre novedad");
        sembrarRespuesta(solicitudCambio, ESTADO_EN_REVISION,
                LocalDateTime.of(2026, 3, 4, 8, 0), "sobre cambio");
        sincronizar();

        RespuestaCriteria criteria = RespuestaCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.grupo(FiltroConector.AND, List.of(
                        NodoFiltro.predicado("remitenteUsuarioId", FiltroOperador.ES,
                                ana.getUsuarioId().toString()),
                        NodoFiltro.predicado("tipoSolicitudId", FiltroOperador.ES, TIPO_NOVEDAD))))
                .build();

        // Act
        PaginatedResult<RespuestaReadModel> resultado = adapter.consultar(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(RespuestaReadModel::contenido)
                .containsExactly("sobre novedad");
    }

    @Test
    void debeFiltrarPorDestinatarioUsuarioId_cuandoElCriteriaTraeEsePredicado() {
        // Arrange
        var ana = sembrarRemitente("Ana", "EST-1", "ana@uco.edu.co");
        var coordUno = sembrarDestinatario("Coordinadora Uno", "COORD-1", "coord1@uco.edu.co");
        var coordDos = sembrarDestinatario("Coordinador Dos", "COORD-2", "coord2@uco.edu.co");
        var solicitudUno = sembrarSolicitud(ana, coordUno, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 3, 1, 10, 0).toInstant(ZoneOffset.UTC), "para coord uno");
        var solicitudDos = sembrarSolicitud(ana, coordDos, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 3, 2, 10, 0).toInstant(ZoneOffset.UTC), "para coord dos");
        sembrarRespuesta(solicitudUno, ESTADO_EN_REVISION,
                LocalDateTime.of(2026, 3, 3, 8, 0), "respuesta para coord uno");
        sembrarRespuesta(solicitudDos, ESTADO_EN_REVISION,
                LocalDateTime.of(2026, 3, 4, 8, 0), "respuesta para coord dos");
        sincronizar();

        RespuestaCriteria criteria = RespuestaCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("destinatarioUsuarioId", FiltroOperador.ES,
                        coordUno.getUsuarioId().toString()))
                .build();

        // Act
        PaginatedResult<RespuestaReadModel> resultado = adapter.consultar(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(RespuestaReadModel::contenido)
                .containsExactly("respuesta para coord uno");
    }

    @Test
    void debeFiltrarPorContenido_cuandoElCriteriaTraeEsePredicado() {
        // Arrange
        var ana = sembrarRemitente("Ana", "EST-1", "ana@uco.edu.co");
        var coord = sembrarDestinatario("Coord", "COORD-1", "coord@uco.edu.co");
        var solicitud1 = sembrarSolicitud(ana, coord, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 3, 1, 10, 0).toInstant(ZoneOffset.UTC), "m1");
        var solicitud2 = sembrarSolicitud(ana, coord, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 3, 2, 10, 0).toInstant(ZoneOffset.UTC), "m2");
        sembrarRespuesta(solicitud1, ESTADO_EN_REVISION,
                LocalDateTime.of(2026, 3, 3, 8, 0), "aprobado con observaciones");
        sembrarRespuesta(solicitud2, ESTADO_EN_REVISION,
                LocalDateTime.of(2026, 3, 4, 8, 0), "rechazado por incompleto");
        sincronizar();

        RespuestaCriteria criteria = RespuestaCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.grupo(FiltroConector.AND, List.of(
                        NodoFiltro.predicado("remitenteUsuarioId", FiltroOperador.ES,
                                ana.getUsuarioId().toString()),
                        NodoFiltro.predicado("contenido", FiltroOperador.CONTIENE, "observaciones"))))
                .build();

        // Act
        PaginatedResult<RespuestaReadModel> resultado = adapter.consultar(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(RespuestaReadModel::contenido)
                .containsExactly("aprobado con observaciones");
    }

    @Test
    void debeFiltrarPorEstadoRespuestaId_cuandoElCriteriaTraeEsePredicado() {
        // Arrange
        var ana = sembrarRemitente("Ana", "EST-1", "ana@uco.edu.co");
        var coord = sembrarDestinatario("Coord", "COORD-1", "coord@uco.edu.co");
        var solicitud1 = sembrarSolicitud(ana, coord, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 3, 1, 10, 0).toInstant(ZoneOffset.UTC), "m1");
        var solicitud2 = sembrarSolicitud(ana, coord, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 3, 2, 10, 0).toInstant(ZoneOffset.UTC), "m2");
        sembrarRespuesta(solicitud1, ESTADO_EN_REVISION,
                LocalDateTime.of(2026, 3, 3, 8, 0), "en revision todavia");
        sembrarRespuesta(solicitud2, ESTADO_APROBADA,
                LocalDateTime.of(2026, 3, 4, 8, 0), "ya aprobada");
        sincronizar();

        RespuestaCriteria criteria = RespuestaCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.grupo(FiltroConector.AND, List.of(
                        NodoFiltro.predicado("remitenteUsuarioId", FiltroOperador.ES,
                                ana.getUsuarioId().toString()),
                        NodoFiltro.predicado("estadoRespuestaId", FiltroOperador.ES, ESTADO_APROBADA))))
                .build();

        // Act
        PaginatedResult<RespuestaReadModel> resultado = adapter.consultar(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(RespuestaReadModel::contenido)
                .containsExactly("ya aprobada");
    }

    @Test
    void debeOrdenarPorFechaRespuestaDescendente_cuandoElCriteriaLoPide() {
        // Arrange
        var ana = sembrarRemitente("Ana", "EST-1", "ana@uco.edu.co");
        var coord = sembrarDestinatario("Coord", "COORD-1", "coord@uco.edu.co");
        var solicitud1 = sembrarSolicitud(ana, coord, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 1, 10, 10, 0).toInstant(ZoneOffset.UTC), "s1");
        var solicitud2 = sembrarSolicitud(ana, coord, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 5, 20, 10, 0).toInstant(ZoneOffset.UTC), "s2");
        sembrarRespuesta(solicitud1, ESTADO_EN_REVISION,
                LocalDateTime.of(2026, 1, 15, 8, 0), "vieja");
        sembrarRespuesta(solicitud2, ESTADO_EN_REVISION,
                LocalDateTime.of(2026, 5, 25, 8, 0), "nueva");
        sincronizar();

        RespuestaCriteria criteria = RespuestaCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("remitenteUsuarioId", FiltroOperador.ES,
                        ana.getUsuarioId().toString()))
                .ordenamiento(List.of(SortOrder.of("fechaRespuesta", SortDirection.DESC)))
                .build();

        // Act
        PaginatedResult<RespuestaReadModel> resultado = adapter.consultar(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(RespuestaReadModel::contenido)
                .containsExactly("nueva", "vieja");
    }

    @Test
    void debeOrdenarPorNombreDelDestinatarioAscendente_cuandoElCriteriaLoPide() {
        // Arrange
        var ana = sembrarRemitente("Ana", "EST-1", "ana@uco.edu.co");
        var zulma = sembrarDestinatario("Zulma", "COORD-1", "zulma@uco.edu.co");
        var alba = sembrarDestinatario("Alba", "COORD-2", "alba@uco.edu.co");
        var solicitudZulma = sembrarSolicitud(ana, zulma, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 1, 10, 10, 0).toInstant(ZoneOffset.UTC), "para zulma");
        var solicitudAlba = sembrarSolicitud(ana, alba, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 5, 20, 10, 0).toInstant(ZoneOffset.UTC), "para alba");
        sembrarRespuesta(solicitudZulma, ESTADO_EN_REVISION,
                LocalDateTime.of(2026, 1, 15, 8, 0), "z");
        sembrarRespuesta(solicitudAlba, ESTADO_EN_REVISION,
                LocalDateTime.of(2026, 5, 25, 8, 0), "a");
        sincronizar();

        RespuestaCriteria criteria = RespuestaCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("remitenteUsuarioId", FiltroOperador.ES,
                        ana.getUsuarioId().toString()))
                .ordenamiento(List.of(SortOrder.of("destinatarioNombre", SortDirection.ASC)))
                .build();

        // Act
        PaginatedResult<RespuestaReadModel> resultado = adapter.consultar(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(r -> r.solicitud().destinatario().nombre())
                .containsExactly("Alba", "Zulma");
    }

    @Test
    void debePaginar_cuandoElCriteriaPideLaSegundaPagina() {
        // Arrange
        var ana = sembrarRemitente("Ana", "EST-1", "ana@uco.edu.co");
        var coord = sembrarDestinatario("Coord", "COORD-1", "coord@uco.edu.co");
        for (int i = 0; i < 3; i++) {
            var solicitud = sembrarSolicitud(ana, coord, TIPO_NOVEDAD,
                    LocalDateTime.of(2026, 3, 1 + i, 10, 0).toInstant(ZoneOffset.UTC), "m" + i);
            sembrarRespuesta(solicitud, ESTADO_EN_REVISION,
                    LocalDateTime.of(2026, 3, 1 + i, 8, 0), "r" + i);
        }
        sincronizar();

        RespuestaCriteria criteria = RespuestaCriteria.builder().pagina(1).tamanio(2)
                .raiz(NodoFiltro.predicado("remitenteUsuarioId", FiltroOperador.ES,
                        ana.getUsuarioId().toString()))
                .ordenamiento(List.of(SortOrder.of("fechaRespuesta", SortDirection.ASC)))
                .build();

        // Act
        PaginatedResult<RespuestaReadModel> resultado = adapter.consultar(criteria);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getPage()).isEqualTo(1);
        assertThat(resultado.getSize()).isEqualTo(2);
        assertThat(resultado.getTotalElements()).isEqualTo(3L);
    }

    @Test
    void debeRetornarVacio_cuandoNoHayRespuestasParaElEstudiante() {
        // Act
        PaginatedResult<RespuestaReadModel> resultado = adapter.consultar(porRemitente(UUID.randomUUID()));

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }
}
