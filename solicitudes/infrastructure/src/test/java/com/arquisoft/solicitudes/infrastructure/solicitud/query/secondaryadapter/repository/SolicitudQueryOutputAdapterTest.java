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

    private void sembrarSolicitud(UUID destinatarioUsuario, String tipo, String remitenteNombre,
            String remitenteIdentificador, String remitenteEmail, Instant fecha, String mensaje) {
        var usuarioRemitente = UsuarioJpaEntity.builder()
                .id(UUID.randomUUID())
                .identificador(remitenteIdentificador)
                .nombre(remitenteNombre)
                .email(remitenteEmail)
                .build();
        entityManager.persist(usuarioRemitente);

        var remitente = RemitenteJpaEntity.builder()
                .id(UUID.randomUUID()).usuarioId(usuarioRemitente.getId()).build();
        entityManager.persist(remitente);

        var destinatario = DestinatarioJpaEntity.builder()
                .id(UUID.randomUUID()).usuarioId(destinatarioUsuario).build();
        entityManager.persist(destinatario);

        entityManager.persist(SolicitudJpaEntity.builder()
                .id(UUID.randomUUID())
                .destinatario(destinatario)
                .remitente(remitente)
                .tipoSolicitud(entityManager.find(TipoSolicitudJpaEntity.class, tipo))
                .fechaCreacion(fecha)
                .mensajeSolicitud(mensaje)
                .build());
        entityManager.flush();
        entityManager.clear();
    }

    private static SolicitudCriteria filtroDestinatario(UUID destinatarioUsuario) {
        return SolicitudCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("destinatarioUsuarioId", FiltroOperador.ES,
                        destinatarioUsuario.toString()))
                .build();
    }

    @Test
    void debeRetornarSoloLasSolicitudesDelCoordinador_yProyectarTipoYRemitente() {
        // Arrange
        UUID coordinador = UUID.randomUUID();
        UUID otroCoordinador = UUID.randomUUID();
        sembrarSolicitud(coordinador, TIPO_NOVEDAD, "Ana Estudiante", "EST-1",
                "ana@uco.edu.co", LocalDateTime.of(2026, 3, 1, 10, 0).toInstant(ZoneOffset.UTC), "novedad de Ana");
        sembrarSolicitud(otroCoordinador, TIPO_NOVEDAD, "Beto Estudiante", "EST-2",
                "beto@uco.edu.co", LocalDateTime.of(2026, 3, 2, 10, 0).toInstant(ZoneOffset.UTC), "novedad de Beto");

        // Act
        PaginatedResult<SolicitudReadModel> resultado = adapter.consultar(filtroDestinatario(coordinador));

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        SolicitudReadModel leida = resultado.getContent().get(0);
        assertThat(leida.mensajeSolicitud()).isEqualTo("novedad de Ana");
        assertThat(leida.tipoSolicitudId()).isEqualTo(TIPO_NOVEDAD);
        assertThat(leida.tipoSolicitudNombre()).isEqualTo("Novedad para el Coordinador");
        assertThat(leida.remitente().identificador()).isEqualTo("EST-1");
        assertThat(leida.remitente().nombre()).isEqualTo("Ana Estudiante");
        assertThat(leida.remitente().email()).isEqualTo("ana@uco.edu.co");
        assertThat(leida.remitente().usuarioId()).isNotNull();
    }

    @Test
    void debeExcluirLasSolicitudesDeOtroTipo_cuandoElCriteriaFuerzaElTipo() {
        // Arrange
        UUID coordinador = UUID.randomUUID();
        sembrarSolicitud(coordinador, TIPO_NOVEDAD, "Ana", "EST-1",
                "ana@uco.edu.co", LocalDateTime.of(2026, 3, 1, 10, 0).toInstant(ZoneOffset.UTC), "es novedad");
        sembrarSolicitud(coordinador, TIPO_CAMBIO, "Ana", "EST-1",
                "ana@uco.edu.co", LocalDateTime.of(2026, 3, 2, 10, 0).toInstant(ZoneOffset.UTC), "es cambio");

        SolicitudCriteria criteria = SolicitudCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.grupo(FiltroConector.AND, List.of(
                        NodoFiltro.predicado("destinatarioUsuarioId", FiltroOperador.ES,
                                coordinador.toString()),
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
        UUID coordinador = UUID.randomUUID();
        sembrarSolicitud(coordinador, TIPO_NOVEDAD, "Ana Ramirez", "EST-1",
                "ana@uco.edu.co", LocalDateTime.of(2026, 3, 1, 10, 0).toInstant(ZoneOffset.UTC), "de Ana");
        sembrarSolicitud(coordinador, TIPO_NOVEDAD, "Luis Perez", "EST-2",
                "luis@uco.edu.co", LocalDateTime.of(2026, 3, 2, 10, 0).toInstant(ZoneOffset.UTC), "de Luis");

        SolicitudCriteria criteria = SolicitudCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.grupo(FiltroConector.AND, List.of(
                        NodoFiltro.predicado("destinatarioUsuarioId", FiltroOperador.ES,
                                coordinador.toString()),
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
        UUID coordinador = UUID.randomUUID();
        sembrarSolicitud(coordinador, TIPO_NOVEDAD, "Ana", "EST-1",
                "ana@uco.edu.co", LocalDateTime.of(2026, 1, 10, 10, 0).toInstant(ZoneOffset.UTC), "vieja");
        sembrarSolicitud(coordinador, TIPO_NOVEDAD, "Luis", "EST-2",
                "luis@uco.edu.co", LocalDateTime.of(2026, 5, 20, 10, 0).toInstant(ZoneOffset.UTC), "nueva");

        SolicitudCriteria criteria = SolicitudCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("destinatarioUsuarioId", FiltroOperador.ES,
                        coordinador.toString()))
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
    void debeOrdenarPorNombreDelRemitenteAscendente_cuandoElCriteriaLoPide() {
        // Arrange
        UUID coordinador = UUID.randomUUID();
        sembrarSolicitud(coordinador, TIPO_NOVEDAD, "Zulma", "EST-1",
                "zulma@uco.edu.co", LocalDateTime.of(2026, 1, 10, 10, 0).toInstant(ZoneOffset.UTC), "z");
        sembrarSolicitud(coordinador, TIPO_NOVEDAD, "Ana", "EST-2",
                "ana@uco.edu.co", LocalDateTime.of(2026, 5, 20, 10, 0).toInstant(ZoneOffset.UTC), "a");

        SolicitudCriteria criteria = SolicitudCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("destinatarioUsuarioId", FiltroOperador.ES,
                        coordinador.toString()))
                .ordenamiento(List.of(SortOrder.of("remitenteNombre", SortDirection.ASC)))
                .build();

        // Act
        PaginatedResult<SolicitudReadModel> resultado = adapter.consultar(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(r -> r.remitente().nombre())
                .containsExactly("Ana", "Zulma");
    }

    @Test
    void debePaginar_cuandoElCriteriaPideLaSegundaPagina() {
        // Arrange
        UUID coordinador = UUID.randomUUID();
        for (int i = 0; i < 3; i++) {
            sembrarSolicitud(coordinador, TIPO_NOVEDAD, "Rem " + i, "EST-" + i,
                    "rem" + i + "@uco.edu.co", LocalDateTime.of(2026, 3, 1 + i, 10, 0).toInstant(ZoneOffset.UTC), "m" + i);
        }

        SolicitudCriteria criteria = SolicitudCriteria.builder().pagina(1).tamanio(2)
                .raiz(NodoFiltro.predicado("destinatarioUsuarioId", FiltroOperador.ES,
                        coordinador.toString()))
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
    void debeRetornarVacio_cuandoNoHaySolicitudesParaElCoordinador() {
        // Act
        PaginatedResult<SolicitudReadModel> resultado = adapter.consultar(
                filtroDestinatario(UUID.randomUUID()));

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }
}
