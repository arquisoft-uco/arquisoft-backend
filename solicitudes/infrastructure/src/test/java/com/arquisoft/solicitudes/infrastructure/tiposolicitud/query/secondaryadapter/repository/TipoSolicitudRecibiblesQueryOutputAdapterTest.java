package com.arquisoft.solicitudes.infrastructure.tiposolicitud.query.secondaryadapter.repository;

import com.arquisoft.solicitudes.application.tiposolicitud.query.readmodel.TipoSolicitudReadModel;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.solicitudes.infrastructure.security.SolicitudesRoles;
import com.arquisoft.solicitudes.infrastructure.tiposolicitud.command.secondaryadapter.entity.TipoSolicitudJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// tipo_solicitud_rol_recibida no tiene @Entity de comando, asi que Hibernate no la genera con ddl-auto. Se crea
// aqui con el DDL de la migracion V20261104013837 (sin FK) para ejercitar el @Subselect real.
@DataJpaTest
class TipoSolicitudRecibiblesQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TipoSolicitudQueryRepository repository;

    private TipoSolicitudQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new TipoSolicitudQueryOutputAdapter(repository);
        entityManager.getEntityManager().createNativeQuery("""
                CREATE TABLE IF NOT EXISTS tipo_solicitud_rol_recibida (
                    tipo_solicitud_id VARCHAR(60) NOT NULL,
                    rol               VARCHAR(60) NOT NULL,
                    PRIMARY KEY (tipo_solicitud_id, rol))
                """).executeUpdate();
        sembrarCatalogo();
    }

    @Test
    void debeRetornarTresTiposOrdenadosPorId_cuandoElRolEsCoordinador() {
        // Act
        var resultado = adapter.consultarRecibiblesPorRoles(List.of(SolicitudesRoles.Negocio.COORDINADOR));

        // Assert
        assertThat(resultado)
                .extracting(TipoSolicitudReadModel::id)
                .containsExactly(
                        TipoSolicitud.AMPLIACION_DE_PLAZO.getId(),
                        TipoSolicitud.CAMBIO_DE_ASESOR.getId(),
                        TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId());
    }

    @Test
    void debeRetornarSoloNovedadAsesor_cuandoElRolEsAsesor() {
        // Act
        var resultado = adapter.consultarRecibiblesPorRoles(List.of(SolicitudesRoles.Negocio.ASESOR));

        // Assert
        assertThat(resultado)
                .extracting(TipoSolicitudReadModel::id)
                .containsExactly(TipoSolicitud.NOVEDAD_PARA_EL_ASESOR.getId());
    }

    @Test
    void debeRetornarSoloRegistroDeUsuarios_cuandoElRolEsAdministrador() {
        // Act
        var resultado = adapter.consultarRecibiblesPorRoles(List.of(SolicitudesRoles.Negocio.ADMINISTRADOR));

        // Assert
        assertThat(resultado)
                .extracting(TipoSolicitudReadModel::id)
                .containsExactly(TipoSolicitud.REGISTRO_Y_MODIFICACION_DE_USUARIOS.getId());
    }

    @Test
    void debeRetornarLosCincoSinDuplicados_cuandoVariosRolesSeSolapan() {
        // Act
        var resultado = adapter.consultarRecibiblesPorRoles(List.of(
                SolicitudesRoles.Negocio.COORDINADOR,
                SolicitudesRoles.Negocio.ASESOR,
                SolicitudesRoles.Negocio.ADMINISTRADOR,
                SolicitudesRoles.Negocio.COORDINADOR));

        // Assert
        assertThat(resultado)
                .extracting(TipoSolicitudReadModel::id)
                .containsExactly(
                        TipoSolicitud.AMPLIACION_DE_PLAZO.getId(),
                        TipoSolicitud.CAMBIO_DE_ASESOR.getId(),
                        TipoSolicitud.NOVEDAD_PARA_EL_ASESOR.getId(),
                        TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId(),
                        TipoSolicitud.REGISTRO_Y_MODIFICACION_DE_USUARIOS.getId());
    }

    @Test
    void debeRetornarVacio_cuandoLaListaDeRolesEstaVacia() {
        // Act
        var resultado = adapter.consultarRecibiblesPorRoles(List.of());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeRetornarVacio_cuandoElRolEsEstudiante() {
        // Act
        var resultado = adapter.consultarRecibiblesPorRoles(List.of(SolicitudesRoles.Negocio.ESTUDIANTE));

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeProyectarTodasLasColumnas_cuandoLeeUnTipo() {
        // Act
        var resultado = adapter.consultarRecibiblesPorRoles(List.of(SolicitudesRoles.Negocio.ADMINISTRADOR));

        // Assert
        assertThat(resultado).singleElement().satisfies(tipo -> {
            assertThat(tipo.id()).isEqualTo(TipoSolicitud.REGISTRO_Y_MODIFICACION_DE_USUARIOS.getId());
            assertThat(tipo.nombre()).isEqualTo("Registro y modificacion de Usuarios");
            assertThat(tipo.descripcion()).isEqualTo("Solicitud para el registro o modificacion de usuarios");
        });
    }

    private void sembrarCatalogo() {
        persistirTipo(TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR, "Novedad para el Coordinador", "Novedad coordinador");
        persistirTipo(TipoSolicitud.NOVEDAD_PARA_EL_ASESOR, "Novedad para el Asesor", "Novedad asesor");
        persistirTipo(TipoSolicitud.CAMBIO_DE_ASESOR, "Cambio de Asesor", "Solicitud para modificar el asesor");
        persistirTipo(TipoSolicitud.AMPLIACION_DE_PLAZO, "Ampliacion de Plazo", "Solicitud para extender el plazo");
        persistirTipo(TipoSolicitud.REGISTRO_Y_MODIFICACION_DE_USUARIOS, "Registro y modificacion de Usuarios",
                "Solicitud para el registro o modificacion de usuarios");
        entityManager.flush();

        asignarRol(TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR, SolicitudesRoles.Negocio.COORDINADOR);
        asignarRol(TipoSolicitud.NOVEDAD_PARA_EL_ASESOR, SolicitudesRoles.Negocio.ASESOR);
        asignarRol(TipoSolicitud.CAMBIO_DE_ASESOR, SolicitudesRoles.Negocio.COORDINADOR);
        asignarRol(TipoSolicitud.AMPLIACION_DE_PLAZO, SolicitudesRoles.Negocio.COORDINADOR);
        asignarRol(TipoSolicitud.REGISTRO_Y_MODIFICACION_DE_USUARIOS, SolicitudesRoles.Negocio.ADMINISTRADOR);
    }

    private void persistirTipo(TipoSolicitud tipo, String nombre, String descripcion) {
        entityManager.persist(TipoSolicitudJpaEntity.builder()
                .id(tipo.getId())
                .nombre(nombre)
                .descripcion(descripcion)
                .build());
    }

    private void asignarRol(TipoSolicitud tipo, String rol) {
        entityManager.getEntityManager()
                .createNativeQuery("INSERT INTO tipo_solicitud_rol_recibida (tipo_solicitud_id, rol) VALUES (?1, ?2)")
                .setParameter(1, tipo.getId())
                .setParameter(2, rol)
                .executeUpdate();
    }
}
