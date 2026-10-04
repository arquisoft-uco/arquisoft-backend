package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoficha.command.secondaryadapter.entity.EstadoFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.command.secondaryadapter.entity.EstadoFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.secondaryadapter.entity.FichaPerfilJpaEntity;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.FiltroConector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// EXCEPCION DE COBERTURA (JaCoCo 75%): esta clase queda deshabilitada porque H2 no soporta la
// sintaxis JOIN LATERAL usada por el @Subselect de FichaPerfilJpaQueryEntity
// (fichas/infrastructure/.../fichaperfil/query/secondaryadapter/repository/FichaPerfilJpaQueryEntity.java).
// El LATERAL es intencional en Postgres: resuelve el top-1-por-particion (ultimo estado de cada ficha)
// sin materializar el conjunto completo como forzaria un ROW_NUMBER() OVER (...). Hoy no existe un
// indice (ficha_perfil_id, fecha_actualizacion DESC): solo uk_trazabilidad_ficha_estado, que lleva
// estado_ficha_id en medio, asi que Postgres ordena las pocas filas de estado de cada ficha.
// No se reescribe production para acomodar H2 ni se baja
// el umbral global de cobertura: es una excepcion puntual y justificada de esta clase concreta.
// Los tests quedan escritos y listos para validacion manual/CI contra Postgres real (mismo precedente
// que FichaPerfilEstudianteQueryRepositoryTest).
@DataJpaTest
class FichaPerfilQueryOutputAdapterEstadoTest {

    private static final String MOTIVO_H2 = "H2 no soporta JOIN LATERAL usado por @Subselect en FichaPerfilJpaQueryEntity "
            + "(intencional en Postgres para top-1-por-particion via index scan). Validar manualmente contra Postgres.";

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FichaPerfilQueryRepository fichaPerfilRepository;

    private FichaPerfilQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new FichaPerfilQueryOutputAdapter(
                fichaPerfilRepository,
                new FichaPerfilJpaSpecification()
        );
    }

    @Test
    @Disabled(MOTIVO_H2)
    void debeFiltrarPorEstadoActual_cuandoOperadorEs() {
        // Arrange
        persistirFichaConEstado("Ficha aprobada", "DOC-100", "Ana Ramirez", "ana10@soyuco.edu.co",
                "APROBADA", "Aprobada");
        persistirFichaConEstado("Ficha en construccion", "DOC-101", "Juan Salazar", "juan10@soyuco.edu.co",
                "EN_CONSTRUCCION", "En Construccion");
        entityManager.flush();

        var criteria = FichaPerfilCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("estadoFicha", FiltroOperador.ES, "APROBADA"))
                .build();

        // Act
        var resultado = adapter.consultarTodas(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(FichaPerfilReadModel::tituloProyecto)
                .containsExactly("Ficha aprobada");
    }

    @Test
    @Disabled(MOTIVO_H2)
    void debeUsarSoloElUltimoEstado_cuandoLaFichaCambioDeEstado() {
        // Arrange
        var ficha = persistirFichaConEstado("Ficha que cambio", "DOC-110", "Ana Ramirez", "ana11@soyuco.edu.co",
                "EN_CONSTRUCCION", "En Construccion");
        sembrarEstado(ficha.getId(), "APROBADA", "Aprobada", Instant.now().plusSeconds(3600));
        entityManager.flush();

        var porEstadoViejo = FichaPerfilCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("estadoFicha", FiltroOperador.ES, "EN_CONSTRUCCION"))
                .build();
        var porEstadoActual = FichaPerfilCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("estadoFicha", FiltroOperador.ES, "APROBADA"))
                .build();
        var sinFiltro = FichaPerfilCriteria.builder().pagina(0).tamanio(10).build();

        // Act
        var conEstadoViejo = adapter.consultarTodas(porEstadoViejo);
        var conEstadoActual = adapter.consultarTodas(porEstadoActual);
        var todas = adapter.consultarTodas(sinFiltro);

        // Assert
        assertThat(conEstadoViejo.getContent()).isEmpty();
        assertThat(conEstadoActual.getContent()).hasSize(1);
        assertThat(todas.getContent()).hasSize(1);
        assertThat(todas.getTotalElements()).isEqualTo(1L);
    }

    @Test
    @Disabled(MOTIVO_H2)
    void debeFiltrarPorVariosEstados_cuandoOperadorIn() {
        // Arrange
        persistirFichaConEstado("Uno", "DOC-120", "Ana Ramirez", "ana12@soyuco.edu.co", "EN_CONSTRUCCION", "En Construccion");
        persistirFichaConEstado("Dos", "DOC-121", "Juan Salazar", "juan12@soyuco.edu.co",
                "DISPONIBLE_PARA_EVALUACION", "Disponible Para Evaluacion");
        persistirFichaConEstado("Tres", "DOC-122", "Marco Vidal", "marco12@soyuco.edu.co", "APROBADA", "Aprobada");
        entityManager.flush();

        var criteria = FichaPerfilCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicadoMultivalor("estadoFicha", FiltroOperador.IN,
                        List.of("EN_CONSTRUCCION", "DISPONIBLE_PARA_EVALUACION")))
                .build();

        // Act
        var resultado = adapter.consultarTodas(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(FichaPerfilReadModel::tituloProyecto)
                .containsExactlyInAnyOrder("Uno", "Dos");
    }

    @Test
    @Disabled(MOTIVO_H2)
    void debeExcluirElEstado_cuandoOperadorNoEs() {
        // Arrange
        persistirFichaConEstado("Rechazada", "DOC-130", "Ana Ramirez", "ana13@soyuco.edu.co", "NO_APROBADA", "No Aprobada");
        persistirFichaConEstado("Aprobada", "DOC-131", "Juan Salazar", "juan13@soyuco.edu.co", "APROBADA", "Aprobada");
        entityManager.flush();

        var criteria = FichaPerfilCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("estadoFicha", FiltroOperador.NO_ES, "NO_APROBADA"))
                .build();

        // Act
        var resultado = adapter.consultarTodas(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(FichaPerfilReadModel::tituloProyecto)
                .containsExactly("Aprobada");
    }

    @Test
    @Disabled(MOTIVO_H2)
    void debeFiltrarPorTexto_cuandoOperadorContiene() {
        // Arrange
        persistirFichaConEstado("Aprobada", "DOC-140", "Ana Ramirez", "ana14@soyuco.edu.co", "APROBADA", "Aprobada");
        persistirFichaConEstado("Con observaciones", "DOC-141", "Juan Salazar", "juan14@soyuco.edu.co",
                "APROBADA_CON_OBSERVACIONES", "Aprobada Con Observaciones");
        persistirFichaConEstado("Rechazada", "DOC-142", "Marco Vidal", "marco14@soyuco.edu.co", "NO_APROBADA", "No Aprobada");
        entityManager.flush();

        var criteria = FichaPerfilCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("estadoFicha", FiltroOperador.EMPIEZA_CON, "APROB"))
                .build();

        // Act
        var resultado = adapter.consultarTodas(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(FichaPerfilReadModel::tituloProyecto)
                .containsExactlyInAnyOrder("Aprobada", "Con observaciones");
    }

    @Test
    @Disabled(MOTIVO_H2)
    void debeDevolverVacio_cuandoElValorDeEstadoNoEstaEnElCatalogo() {
        // Arrange
        persistirFichaConEstado("Aprobada", "DOC-150", "Ana Ramirez", "ana15@soyuco.edu.co", "APROBADA", "Aprobada");
        entityManager.flush();

        var criteria = FichaPerfilCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("estadoFicha", FiltroOperador.ES, "XYZ"))
                .build();

        // Act
        var resultado = adapter.consultarTodas(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    @Disabled(MOTIVO_H2)
    void debeCombinarEstadoConTituloProyecto_cuandoGrupoAnd() {
        // Arrange
        persistirFichaConEstado("Plataforma web", "DOC-160", "Ana Ramirez", "ana16@soyuco.edu.co", "APROBADA", "Aprobada");
        persistirFichaConEstado("Plataforma movil", "DOC-161", "Juan Salazar", "juan16@soyuco.edu.co",
                "EN_CONSTRUCCION", "En Construccion");
        persistirFichaConEstado("Otro tema", "DOC-162", "Marco Vidal", "marco16@soyuco.edu.co", "APROBADA", "Aprobada");
        entityManager.flush();

        var criteria = FichaPerfilCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(NodoFiltro.grupo(FiltroConector.AND, List.of(
                        NodoFiltro.predicado("estadoFicha", FiltroOperador.ES, "APROBADA"),
                        NodoFiltro.predicado("tituloProyecto", FiltroOperador.CONTIENE, "Plataforma"))))
                .build();

        // Act
        var resultado = adapter.consultarTodas(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(FichaPerfilReadModel::tituloProyecto)
                .containsExactly("Plataforma web");
    }

    @Test
    @Disabled(MOTIVO_H2)
    void debeDevolverEstadoEnElReadModel_cuandoLaFichaTieneEstado() {
        // Arrange
        persistirFichaConEstado("Aprobada", "DOC-170", "Ana Ramirez", "ana17@soyuco.edu.co", "APROBADA", "Aprobada");
        entityManager.flush();

        // Act
        var resultado = adapter.consultarTodas(FichaPerfilCriteria.builder().pagina(0).tamanio(10).build());

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        var estado = resultado.getContent().get(0).estado();
        assertThat(estado.id()).isEqualTo("APROBADA");
        assertThat(estado.nombre()).isEqualTo("Aprobada");
        assertThat(estado.fechaActualizacion()).isNotNull();
    }

    @Test
    @Disabled(MOTIVO_H2)
    void debeIncluirSoloFichasConEstado_cuandoNoHayFiltro() {
        // Arrange
        persistirFichaConEstado("Con estado", "DOC-180", "Ana Ramirez", "ana18@soyuco.edu.co", "APROBADA", "Aprobada");
        var asesorSinEstado = AsesorFichaJpaEntity.builder()
                .id(UUID.randomUUID())
                .identificador("DOC-181")
                .nombre("Juan Salazar")
                .email("juan18@soyuco.edu.co")
                .ocurridoEn(Instant.now())
                .build();
        entityManager.persist(asesorSinEstado);
        entityManager.persist(FichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .tituloProyecto("Sin estado")
                .asesorFicha(asesorSinEstado)
                .build());
        entityManager.flush();

        // Act
        var resultado = adapter.consultarTodas(FichaPerfilCriteria.builder().pagina(0).tamanio(10).build());

        // Assert
        assertThat(resultado.getContent())
                .extracting(FichaPerfilReadModel::tituloProyecto)
                .containsExactly("Con estado");
    }

    private FichaPerfilJpaEntity persistirFichaConEstado(String titulo, String identificador, String nombre,
                                                         String email, String estadoId, String estadoNombre) {
        var asesor = AsesorFichaJpaEntity.builder()
                .id(UUID.randomUUID())
                .identificador(identificador)
                .nombre(nombre)
                .email(email)
                .ocurridoEn(Instant.now())
                .build();
        entityManager.persist(asesor);

        var ficha = FichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .tituloProyecto(titulo)
                .asesorFicha(asesor)
                .build();
        entityManager.persist(ficha);
        sembrarEstado(ficha.getId(), estadoId, estadoNombre, Instant.now());
        return ficha;
    }

    private void sembrarEstado(UUID fichaId, String estadoId, String estadoNombre, Instant fecha) {
        var estado = entityManager.find(EstadoFichaJpaEntity.class, estadoId);
        if (estado == null) {
            estado = entityManager.persist(EstadoFichaJpaEntity.builder()
                    .id(estadoId)
                    .nombre(estadoNombre)
                    .descripcion(estadoNombre)
                    .build());
        }
        entityManager.persist(EstadoFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaId)
                .estadoFicha(estado)
                .fechaActualizacion(fecha)
                .build());
    }
}
