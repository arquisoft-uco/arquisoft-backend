package com.arquisoft.solicitudes.infrastructure.solicitud.query.secondaryadapter.repository;

import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.solicitudes.application.solicitud.query.criteria.SolicitudCriteria;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.solicitudes.infrastructure.destinatario.command.secondaryadapter.entity.DestinatarioJpaEntity;
import com.arquisoft.solicitudes.infrastructure.remitente.command.secondaryadapter.entity.RemitenteJpaEntity;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SolicitudRegistroModificacionUsuariosQueryOutputAdapterTest {

    private static final String TIPO_NOVEDAD = "NOVEDAD_PARA_EL_COORDINADOR";
    private static final String TIPO_REGISTRO = "REGISTRO_Y_MODIFICACION_DE_USUARIOS";

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
        entityManager.persist(TipoSolicitudJpaEntity.builder().id(TIPO_REGISTRO)
                .nombre("Registro y modificación de Usuarios").descripcion("desc").build());
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

    @Test
    void debeRetornarSoloLasSolicitudesRegistroModificacionUsuariosDelDestinatario_cuandoElCriteriaFuerzaTipoYDestinatario() {
        // Arrange
        var admin = sembrarDestinatario("Admin", "ADM-1", "admin@uco.edu.co");
        var otroAdmin = sembrarDestinatario("Otro", "ADM-2", "otro@uco.edu.co");
        var ana = sembrarRemitente("Ana", "COORD-1", "ana@uco.edu.co");
        sembrarSolicitud(ana, admin, TIPO_NOVEDAD,
                LocalDateTime.of(2026, 3, 1, 10, 0).toInstant(ZoneOffset.UTC), "es novedad");
        sembrarSolicitud(ana, admin, TIPO_REGISTRO,
                LocalDateTime.of(2026, 3, 2, 10, 0).toInstant(ZoneOffset.UTC), "es registro");
        sembrarSolicitud(ana, otroAdmin, TIPO_REGISTRO,
                LocalDateTime.of(2026, 3, 3, 10, 0).toInstant(ZoneOffset.UTC), "registro ajeno");
        entityManager.flush();
        entityManager.clear();

        var criteria = SolicitudCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.grupo(FiltroConector.AND, List.of(
                        NodoFiltro.predicado("destinatarioUsuarioId", FiltroOperador.ES,
                                admin.getUsuarioId().toString()),
                        NodoFiltro.predicado("tipoSolicitudId", FiltroOperador.ES, TIPO_REGISTRO))))
                .build();

        // Act
        var resultado = adapter.consultar(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(SolicitudReadModel::mensajeSolicitud)
                .containsExactly("es registro");
    }
}
