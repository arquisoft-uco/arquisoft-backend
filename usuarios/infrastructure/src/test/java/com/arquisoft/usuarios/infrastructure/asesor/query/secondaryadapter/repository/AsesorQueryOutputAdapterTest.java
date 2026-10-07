package com.arquisoft.usuarios.infrastructure.asesor.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.asesor.query.criteria.AsesorVigenteCriteria;
import com.arquisoft.usuarios.application.asesor.query.readmodel.AsesorVigenteReadModel;
import com.arquisoft.usuarios.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@DataJpaTest
class AsesorQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AsesorVigenteQueryRepository asesorVigenteQueryRepository;

    private AsesorQueryOutputAdapter adapter;

    private UUID vigenteUno;
    private UUID vigenteDos;
    private UUID dadoDeBaja;
    private UUID inactivoPeroVigente;

    @BeforeEach
    void setUp() {
        adapter = new AsesorQueryOutputAdapter(
                asesorVigenteQueryRepository,
                new AsesorVigenteJpaSpecification());

        vigenteUno = persistirAsesor("1001", "Ana Ramirez", "ana.ramirez@uco.edu.co", "ACTIVO", null);
        vigenteDos = persistirAsesor("1002", "Bruno Diaz", "bruno.diaz@uco.edu.co", "ACTIVO", null);
        dadoDeBaja = persistirAsesor("1003", "Carla Vidal", "carla.vidal@uco.edu.co", "ACTIVO", Instant.now());
        inactivoPeroVigente = persistirAsesor("1004", "Dario Leon", "dario.leon@uco.edu.co", "INACTIVO", null);
        entityManager.flush();

        var usuarioSinAsesor = UUID.randomUUID();
        entityManager.persist(UsuarioJpaEntity.builder()
                .id(usuarioSinAsesor)
                .identificador("1005")
                .nombre("Elena Roa")
                .email("elena.roa@uco.edu.co")
                .contacto("3000000005")
                .estadoId("ACTIVO")
                .build());
        entityManager.flush();
    }

    @Test
    void debeExcluirLosDadosDeBaja_cuandoConsultaVigentesSinFiltro() {
        // Act
        var resultado = adapter.consultarVigentes(
                AsesorVigenteCriteria.builder().pagina(0).tamanio(10).build());

        // Assert
        assertThat(resultado.getContent()).hasSize(3);
        assertThat(resultado.getContent())
                .extracting(AsesorVigenteReadModel::id)
                .containsExactlyInAnyOrder(vigenteUno, vigenteDos, inactivoPeroVigente);
    }

    @Test
    void debeIncluirUnUsuarioInactivo_cuandoSuAsesorSigueVigente() {
        // La vigencia se decide por asesor.eliminado_en, nunca por usuario.estado_id
        // Act
        var resultado = adapter.consultarVigentes(
                AsesorVigenteCriteria.builder().pagina(0).tamanio(10).build());

        // Assert
        assertThat(resultado.getContent())
                .extracting(AsesorVigenteReadModel::id)
                .contains(inactivoPeroVigente);
    }

    @Test
    void debeExcluirUnUsuarioActivo_cuandoSuAsesorFueDadoDeBaja() {
        // Act
        var resultado = adapter.consultarVigentes(
                AsesorVigenteCriteria.builder().pagina(0).tamanio(10).build());

        // Assert
        assertThat(resultado.getContent())
                .extracting(AsesorVigenteReadModel::id)
                .doesNotContain(dadoDeBaja);
    }

    @Test
    void debeFiltrarPorEmail_cuandoConsultaVigentes() {
        // Arrange
        var criteria = AsesorVigenteCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("email", FiltroOperador.CONTIENE, "bruno"))
                .build();

        // Act
        var resultado = adapter.consultarVigentes(criteria);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).id()).isEqualTo(vigenteDos);
    }

    @Test
    void debeFiltrarPorEstadoSinIncluirBajas_cuandoConsultaVigentesConEstadoActivo() {
        // Arrange — dadoDeBaja también es ACTIVO: el filtro por estado se suma a la vigencia
        var criteria = AsesorVigenteCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("estado", FiltroOperador.ES, "ACTIVO"))
                .build();

        // Act
        var resultado = adapter.consultarVigentes(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(AsesorVigenteReadModel::id, AsesorVigenteReadModel::estado)
                .containsExactlyInAnyOrder(tuple(vigenteUno, "ACTIVO"), tuple(vigenteDos, "ACTIVO"));
    }

    private UUID persistirAsesor(String identificador, String nombre, String email,
                                  String estado, Instant eliminadoEn) {
        var usuarioId = UUID.randomUUID();
        entityManager.persist(UsuarioJpaEntity.builder()
                .id(usuarioId)
                .identificador(identificador)
                .nombre(nombre)
                .email(email)
                .contacto("3000000000")
                .estadoId(estado)
                .build());
        entityManager.persist(AsesorJpaEntity.builder()
                .usuarioId(usuarioId)
                .eliminadoEn(eliminadoEn)
                .build());
        return usuarioId;
    }
}
