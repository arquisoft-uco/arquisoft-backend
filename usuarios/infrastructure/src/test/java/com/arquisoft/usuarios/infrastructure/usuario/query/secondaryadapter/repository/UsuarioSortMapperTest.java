package com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.repository;

import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.usuarios.application.usuario.query.criteria.UsuarioCriteria;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioSortMapperTest {

    @Test
    void debeTraducirCamposOrdenables_cuandoCampoValido() {
        // Act
        var identificador = UsuarioSortMapper.traducir("identificador");
        var nombre = UsuarioSortMapper.traducir("nombre");
        var email = UsuarioSortMapper.traducir("email");

        // Assert
        assertThat(identificador).isEqualTo("identificador");
        assertThat(nombre).isEqualTo("nombre");
        assertThat(email).isEqualTo("email");
    }

    @Test
    void debeRetornarNulo_cuandoCampoNoOrdenable() {
        // Act
        var esEstudiante = UsuarioSortMapper.traducir("esEstudiante");
        var esRepresentanteComite = UsuarioSortMapper.traducir("esRepresentanteComite");
        var vigente = UsuarioSortMapper.traducir("vigente");
        var inexistente = UsuarioSortMapper.traducir("campoInexistente");

        // Assert
        assertThat(esEstudiante).isNull();
        assertThat(esRepresentanteComite).isNull();
        assertThat(vigente).isNull();
        assertThat(inexistente).isNull();
    }

    @Test
    void debeResolverRuta_soloParaLosCamposQueElCriteriaDeclaraOrdenables() {
        for (var campo : UsuarioCriteria.Campo.values()) {
            // Act
            var ruta = UsuarioSortMapper.traducir(campo.getClave());

            // Assert
            assertThat(UsuarioCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("El campo '%s' es ordenable en el Criteria si y solo si el SortMapper le resuelve una ruta",
                            campo.getClave())
                    .isEqualTo(UtilObjeto.noEsNulo(ruta));
        }
    }
}
