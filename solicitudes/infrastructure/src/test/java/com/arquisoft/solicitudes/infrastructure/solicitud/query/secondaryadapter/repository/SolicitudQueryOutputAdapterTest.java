package com.arquisoft.solicitudes.infrastructure.solicitud.query.secondaryadapter.repository;

import com.arquisoft.solicitudes.application.solicitud.query.criteria.SolicitudCriteria;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.solicitudes.infrastructure.destinatario.command.secondaryadapter.entity.DestinatarioJpaEntity;
import com.arquisoft.solicitudes.infrastructure.remitente.command.secondaryadapter.entity.RemitenteJpaEntity;
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
class SolicitudQueryOutputAdapterTest {

    private static final String TIPO_NOVEDAD = "NOVEDAD_PARA_EL_COORDINADOR";
    private static final String TIPO_CAMBIO = "CAMBIO_DE_ASESOR";

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SolicitudQueryRepository repository;

    private SolicitudQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new SolicitudQueryOutputAdapter(repository, new SolicitudJpaSpecification());

        entityManager.persist(TipoSolicitudJpaEntity.builder()
                .id(TIPO_NOVEDAD).nombre("Novedad para el Coordinador").descripcion("desc").build());
        entityManager.persist(TipoSolicitudJpaEntity.builder()
                .id(TIPO_CAMBIO).nombre("Cambio de Asesor").descripcion("desc").build());
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

    private void sembrarSolicitud(RemitenteJpaEntity remitente, DestinatarioJpaEntity destinatario,
            String tipo, Instant fecha, String mensaje) {
        entityManager.persist(SolicitudJpaEntity.builder()
                .id(UUID.randomUUID())
                .remitente(remitente)
                .destinatario(destinatario)
                .tipoSolicitud(entityManager.find(TipoSolicitudJpaEntity.class, tipo))
                .fechaCreacion(fecha)
                .mensajeSolicitud(mensaje)
                .build());
    }

    private void sincronizar() {
        entityManager.flush();
        entityManager.clear();
    }

    private static SolicitudCriteria porDestinatario(UUID destinatarioUsuarioId) {
        return SolicitudCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("destinatarioUsuarioId", FiltroOperador.ES,
                        destinatarioUsuarioId.toString()))
                .build();
    }

    private static SolicitudCriteria porRemitente(UUID remitenteUsuarioId) {
        return SolicitudCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("remitenteUsuarioId", FiltroOperador.ES,
                        remitenteUsuarioId.toString()))
                .build();
    }

    // --- HU-091: lado destinatario (recibidas) ---

    @Test
    void debeRetornarSoloLasSolicitudesDelDestinatario_yProyectarRemitenteYDestinatario() {
        // Arrange
        var coord = sembrarDestinatario("Coordinadora Uno", "COORD-1", "coord1@uco.edu.co");
        var otroCoord = sembrarDestinatario("Coordinadora Dos", "COORD-2", "coord2@uco.edu.co");
        var ana = sembrarRemitente("Ana Estudiante", "EST-1", "ana@uco.edu.co");
        var beto = sembrarRemitente("Beto Estudiante", "EST-2", "beto@uco.edu.co");
        sembrarSolicitud(ana, coord, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 3, 1, 10, 0).toInstant(ZoneOffset.UTC), "novedad de Ana");
        sembrarSolicitud(beto, otroCoord, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 3, 2, 10, 0).toInstant(ZoneOffset.UTC), "novedad de Beto");
        sincronizar();

        // Act
        PaginatedResult<SolicitudReadModel> resultado = adapter.consultar(
                porDestinatario(coord.getUsuarioId()));

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        SolicitudReadModel leida = resultado.getContent().get(0);
        assertThat(leida.mensajeSolicitud()).isEqualTo("novedad de Ana");
        assertThat(leida.tipoSolicitudId()).isEqualTo(TIPO_NOVEDAD);
        assertThat(leida.tipoSolicitudNombre()).isEqualTo("Novedad para el Coordinador");
        assertThat(leida.remitente().identificador()).isEqualTo("EST-1");
        assertThat(leida.remitente().nombre()).isEqualTo("Ana Estudiante");
        assertThat(leida.remitente().email()).isEqualTo("ana@uco.edu.co");
        assertThat(leida.remitente().usuarioId()).isEqualTo(ana.getUsuarioId());
        assertThat(leida.destinatario().identificador()).isEqualTo("COORD-1");
        assertThat(leida.destinatario().nombre()).isEqualTo("Coordinadora Uno");
        assertThat(leida.destinatario().email()).isEqualTo("coord1@uco.edu.co");
        assertThat(leida.destinatario().usuarioId()).isEqualTo(coord.getUsuarioId());
    }

    @Test
    void debeExcluirLasSolicitudesDeOtroTipo_cuandoElCriteriaFuerzaElTipoYDestinatario() {
        // Arrange
        var coord = sembrarDestinatario("Coord", "COORD-1", "coord@uco.edu.co");
        var ana = sembrarRemitente("Ana", "EST-1", "ana@uco.edu.co");
        sembrarSolicitud(ana, coord, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 3, 1, 10, 0).toInstant(ZoneOffset.UTC), "es novedad");
        sembrarSolicitud(ana, coord, TIPO_CAMBIO,
                LocalDateTime.of(2026, 3, 2, 10, 0).toInstant(ZoneOffset.UTC), "es cambio");
        sincronizar();

        SolicitudCriteria criteria = SolicitudCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.grupo(FiltroConector.AND, List.of(
                        NodoFiltro.predicado("destinatarioUsuarioId", FiltroOperador.ES,
                                coord.getUsuarioId().toString()),
                        NodoFiltro.predicado("tipoSolicitudId", FiltroOperador.ES, TIPO_NOVEDAD))))
                .build();

        // Act
        PaginatedResult<SolicitudReadModel> resultado = adapter.consultar(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(SolicitudReadModel::mensajeSolicitud)
                .containsExactly("es novedad");
    }

    @Test
    void debeFiltrarPorNombreDelRemitente_cuandoElCriteriaTraeEsePredicado() {
        // Arrange
        var coord = sembrarDestinatario("Coord", "COORD-1", "coord@uco.edu.co");
        var ana = sembrarRemitente("Ana Ramirez", "EST-1", "ana@uco.edu.co");
        var luis = sembrarRemitente("Luis Perez", "EST-2", "luis@uco.edu.co");
        sembrarSolicitud(ana, coord, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 3, 1, 10, 0).toInstant(ZoneOffset.UTC), "de Ana");
        sembrarSolicitud(luis, coord, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 3, 2, 10, 0).toInstant(ZoneOffset.UTC), "de Luis");
        sincronizar();

        SolicitudCriteria criteria = SolicitudCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.grupo(FiltroConector.AND, List.of(
                        NodoFiltro.predicado("destinatarioUsuarioId", FiltroOperador.ES,
                                coord.getUsuarioId().toString()),
                        NodoFiltro.predicado("remitenteNombre", FiltroOperador.CONTIENE, "Ramirez"))))
                .build();

        // Act
        PaginatedResult<SolicitudReadModel> resultado = adapter.consultar(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(r -> r.remitente().nombre())
                .containsExactly("Ana Ramirez");
    }

    @Test
    void debeOrdenarPorFechaCreacionDescendente_cuandoElCriteriaLoPide() {
        // Arrange
        var coord = sembrarDestinatario("Coord", "COORD-1", "coord@uco.edu.co");
        var ana = sembrarRemitente("Ana", "EST-1", "ana@uco.edu.co");
        var luis = sembrarRemitente("Luis", "EST-2", "luis@uco.edu.co");
        sembrarSolicitud(ana, coord, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 1, 10, 10, 0).toInstant(ZoneOffset.UTC), "vieja");
        sembrarSolicitud(luis, coord, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 5, 20, 10, 0).toInstant(ZoneOffset.UTC), "nueva");
        sincronizar();

        SolicitudCriteria criteria = SolicitudCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("destinatarioUsuarioId", FiltroOperador.ES,
                        coord.getUsuarioId().toString()))
                .ordenamiento(List.of(SortOrder.of("fechaCreacion", SortDirection.DESC)))
                .build();

        // Act
        PaginatedResult<SolicitudReadModel> resultado = adapter.consultar(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(SolicitudReadModel::mensajeSolicitud)
                .containsExactly("nueva", "vieja");
    }

    @Test
    void debePaginar_cuandoElCriteriaPideLaSegundaPagina() {
        // Arrange
        var coord = sembrarDestinatario("Coord", "COORD-1", "coord@uco.edu.co");
        for (int i = 0; i < 3; i++) {
            var rem = sembrarRemitente("Rem " + i, "EST-" + i, "rem" + i + "@uco.edu.co");
            sembrarSolicitud(rem, coord, TIPO_NOVEDAD,
                    LocalDateTime.of(2026, 3, 1 + i, 10, 0).toInstant(ZoneOffset.UTC), "m" + i);
        }
        sincronizar();

        SolicitudCriteria criteria = SolicitudCriteria.builder().pagina(1).tamanio(2)
                .raiz(NodoFiltro.predicado("destinatarioUsuarioId", FiltroOperador.ES,
                        coord.getUsuarioId().toString()))
                .ordenamiento(List.of(SortOrder.of("fechaCreacion", SortDirection.ASC)))
                .build();

        // Act
        PaginatedResult<SolicitudReadModel> resultado = adapter.consultar(criteria);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getPage()).isEqualTo(1);
        assertThat(resultado.getSize()).isEqualTo(2);
        assertThat(resultado.getTotalElements()).isEqualTo(3L);
    }

    @Test
    void debeRetornarVacio_cuandoNoHaySolicitudesParaElActor() {
        // Act
        PaginatedResult<SolicitudReadModel> resultado = adapter.consultar(
                porDestinatario(UUID.randomUUID()));

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeOrdenarPorNombreDelRemitenteAscendente_cuandoElCriteriaLoPide() {
        // Arrange
        var coord = sembrarDestinatario("Coord", "COORD-1", "coord@uco.edu.co");
        var zulma = sembrarRemitente("Zulma", "EST-1", "zulma@uco.edu.co");
        var alba = sembrarRemitente("Alba", "EST-2", "alba@uco.edu.co");
        sembrarSolicitud(zulma, coord, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 1, 10, 10, 0).toInstant(ZoneOffset.UTC), "z");
        sembrarSolicitud(alba, coord, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 5, 20, 10, 0).toInstant(ZoneOffset.UTC), "a");
        sincronizar();

        SolicitudCriteria criteria = SolicitudCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("destinatarioUsuarioId", FiltroOperador.ES,
                        coord.getUsuarioId().toString()))
                .ordenamiento(List.of(SortOrder.of("remitenteNombre", SortDirection.ASC)))
                .build();

        // Act
        PaginatedResult<SolicitudReadModel> resultado = adapter.consultar(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(r -> r.remitente().nombre())
                .containsExactly("Alba", "Zulma");
    }

    // --- HU-096: lado remitente (enviadas) ---

    @Test
    void debeRetornarSoloLasSolicitudesDelRemitente_yProyectarDestinatario() {
        // Arrange
        var ana = sembrarRemitente("Ana Estudiante", "EST-1", "ana@uco.edu.co");
        var beto = sembrarRemitente("Beto Estudiante", "EST-2", "beto@uco.edu.co");
        var coord = sembrarDestinatario("Coordinadora Uno", "COORD-1", "coord1@uco.edu.co");
        sembrarSolicitud(ana, coord, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 3, 1, 10, 0).toInstant(ZoneOffset.UTC), "de Ana");
        sembrarSolicitud(beto, coord, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 3, 2, 10, 0).toInstant(ZoneOffset.UTC), "de Beto");
        sincronizar();

        // Act
        PaginatedResult<SolicitudReadModel> resultado = adapter.consultar(porRemitente(ana.getUsuarioId()));

        // Assert
        assertThat(resultado.getContent())
                .extracting(SolicitudReadModel::mensajeSolicitud)
                .containsExactly("de Ana");
        SolicitudReadModel leida = resultado.getContent().get(0);
        assertThat(leida.remitente().usuarioId()).isEqualTo(ana.getUsuarioId());
        assertThat(leida.destinatario().usuarioId()).isEqualTo(coord.getUsuarioId());
        assertThat(leida.destinatario().identificador()).isEqualTo("COORD-1");
        assertThat(leida.destinatario().nombre()).isEqualTo("Coordinadora Uno");
        assertThat(leida.destinatario().email()).isEqualTo("coord1@uco.edu.co");
    }

    @Test
    void debeExcluirLasSolicitudesDeOtroTipo_cuandoElCriteriaFuerzaTipoYRemitente() {
        // Arrange
        var ana = sembrarRemitente("Ana", "EST-1", "ana@uco.edu.co");
        var coord = sembrarDestinatario("Coord", "COORD-1", "coord@uco.edu.co");
        sembrarSolicitud(ana, coord, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 3, 1, 10, 0).toInstant(ZoneOffset.UTC), "es novedad");
        sembrarSolicitud(ana, coord, TIPO_CAMBIO,
                LocalDateTime.of(2026, 3, 2, 10, 0).toInstant(ZoneOffset.UTC), "es cambio");
        sincronizar();

        SolicitudCriteria criteria = SolicitudCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.grupo(FiltroConector.AND, List.of(
                        NodoFiltro.predicado("remitenteUsuarioId", FiltroOperador.ES,
                                ana.getUsuarioId().toString()),
                        NodoFiltro.predicado("tipoSolicitudId", FiltroOperador.ES, TIPO_NOVEDAD))))
                .build();

        // Act
        PaginatedResult<SolicitudReadModel> resultado = adapter.consultar(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(SolicitudReadModel::mensajeSolicitud)
                .containsExactly("es novedad");
    }

    @Test
    void debeFiltrarPorNombreDelDestinatario_cuandoElCriteriaTraeEsePredicado() {
        // Arrange
        var ana = sembrarRemitente("Ana", "EST-1", "ana@uco.edu.co");
        var coordRamirez = sembrarDestinatario("Carla Ramirez", "COORD-1", "carla@uco.edu.co");
        var coordPerez = sembrarDestinatario("Luis Perez", "COORD-2", "luis@uco.edu.co");
        sembrarSolicitud(ana, coordRamirez, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 3, 1, 10, 0).toInstant(ZoneOffset.UTC), "para Ramirez");
        sembrarSolicitud(ana, coordPerez, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 3, 2, 10, 0).toInstant(ZoneOffset.UTC), "para Perez");
        sincronizar();

        SolicitudCriteria criteria = SolicitudCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.grupo(FiltroConector.AND, List.of(
                        NodoFiltro.predicado("remitenteUsuarioId", FiltroOperador.ES,
                                ana.getUsuarioId().toString()),
                        NodoFiltro.predicado("destinatarioNombre", FiltroOperador.CONTIENE, "Ramirez"))))
                .build();

        // Act
        PaginatedResult<SolicitudReadModel> resultado = adapter.consultar(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(r -> r.destinatario().nombre())
                .containsExactly("Carla Ramirez");
    }

    @Test
    void debeOrdenarPorNombreDelDestinatarioAscendente_cuandoElCriteriaLoPide() {
        // Arrange
        var ana = sembrarRemitente("Ana", "EST-1", "ana@uco.edu.co");
        var zulma = sembrarDestinatario("Zulma", "COORD-1", "zulma@uco.edu.co");
        var alba = sembrarDestinatario("Alba", "COORD-2", "alba@uco.edu.co");
        sembrarSolicitud(ana, zulma, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 1, 10, 10, 0).toInstant(ZoneOffset.UTC), "z");
        sembrarSolicitud(ana, alba, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 5, 20, 10, 0).toInstant(ZoneOffset.UTC), "a");
        sincronizar();

        SolicitudCriteria criteria = SolicitudCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("remitenteUsuarioId", FiltroOperador.ES,
                        ana.getUsuarioId().toString()))
                .ordenamiento(List.of(SortOrder.of("destinatarioNombre", SortDirection.ASC)))
                .build();

        // Act
        PaginatedResult<SolicitudReadModel> resultado = adapter.consultar(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(r -> r.destinatario().nombre())
                .containsExactly("Alba", "Zulma");
    }
}
