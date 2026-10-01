package com.arquisoft.shared.message.respaldo;

import com.arquisoft.shared.message.CategoriaMensaje;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.ClavesCatalogo;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * El catálogo que responde cuando no hay otro.
 *
 * <p>Es lo que ve el cliente si Redis cae con la aplicación ya arrancada y la caché no tiene el
 * texto. Que degrade bien no es un detalle cosmético: un respaldo vacío convertiría un 422 legible
 * en una respuesta sin mensaje, y uno que devolviera la clave filtraría un identificador interno.
 */
class CatalogoMensajesRespaldoTest {

    private final CatalogoMensajesRespaldo respaldo = CatalogoMensajesRespaldo.porDefecto();

    @ParameterizedTest
    @EnumSource(CategoriaMensaje.class)
    @DisplayName("toda categoría tiene texto de respaldo no vacío")
    void debeTenerTexto_cuandoSeRecorreCadaCategoria(CategoriaMensaje categoria) {
        // Arrange
        ClaveMensaje clave = claveCon("app.dominio.objeto." + categoria.name().toLowerCase() + ".descripcion");

        // Act
        String texto = MensajesRespaldo.para(clave);

        // Assert
        assertThat(texto).isNotBlank();
    }

    @Test
    @DisplayName("una clave de log degrada a la propia clave")
    void debeDevolverLaClave_cuandoLaCategoriaEsLog() {
        // Act
        String texto = respaldo.obtener(FichaPerfilKey.LOG_REGISTRADA);

        // Assert
        assertThat(texto)
                .as("un log lo lee un operador, no un cliente: la clave es más útil que un genérico")
                .isEqualTo(FichaPerfilKey.LOG_REGISTRADA.clave());
    }

    @Test
    @DisplayName("ninguna clave que no sea de log expone su identificador")
    void debeNoExponerLaClave_cuandoLaCategoriaNoEsLog() {
        // Arrange
        List<ClaveMensaje> visiblesAlCliente = ClavesCatalogo.TODAS.stream()
                .filter(clave -> CategoriaMensaje.desde(clave.clave()) != CategoriaMensaje.LOG)
                .toList();

        // Act
        List<String> filtradas = visiblesAlCliente.stream()
                .filter(clave -> respaldo.obtener(clave).contains(clave.clave()))
                .map(ClaveMensaje::clave)
                .toList();

        // Assert
        assertThat(filtradas)
                .as("la clave es un identificador interno y viajaría en el cuerpo de la respuesta")
                .isEmpty();
    }

    @Test
    @DisplayName("formatear ignora los argumentos — el respaldo no tiene patrón que sustituir")
    void debeIgnorarLosArgumentos_cuandoSeFormatea() {
        // Act
        String texto = respaldo.formatear(FichaPerfilKey.ERROR_TITULO_DUPLICADO, "Mi proyecto");

        // Assert
        assertThat(texto).isEqualTo(respaldo.obtener(FichaPerfilKey.ERROR_TITULO_DUPLICADO));
    }

    @Test
    @DisplayName("no expone marcadores de formato al sustituir sin patrón")
    void debeNoExponerMarcadores_cuandoSeFormateaSinPatron() {
        // Act
        String texto = respaldo.formatear(FichaPerfilKey.ERROR_TITULO_DUPLICADO);

        // Assert
        assertThat(texto).doesNotContain("%s");
    }

    @Test
    @DisplayName("contiene es siempre falso — el respaldo no resuelve ninguna clave de verdad")
    void debeDevolverFalso_cuandoSePreguntaSiContiene() {
        // Act & Assert
        assertThat(respaldo.contiene(FichaPerfilKey.ERROR_TITULO_DUPLICADO)).isFalse();
        assertThat(respaldo.contiene(ValidadorKey.NO_EN_BLANCO)).isFalse();
    }

    @Test
    @DisplayName("una clave con cuarto segmento fuera del catálogo de categorías degrada a error")
    void debeDegradarAError_cuandoElCuartoSegmentoNoEsCategoria() {
        // Arrange
        // Hoy es el caso de app.infraestructura.consulta.tipo.* y de plantilla.asunto/cuerpo:
        // segmentos legítimos que no nombran una categoría. Ver CatalogoCargaTest, que fija la
        // lista de segmentos aceptados para que uno nuevo sea una decisión y no un descuido.
        ClaveMensaje conSegmentoPropio = claveCon("app.infraestructura.consulta.tipo.texto");

        // Act
        String texto = MensajesRespaldo.para(conSegmentoPropio);

        // Assert
        assertThat(texto).isEqualTo(MensajesRespaldo.ERROR);
    }

    @Test
    @DisplayName("una clave con forma inesperada degrada al texto de error, no a null")
    void debeDegradarAError_cuandoLaClaveNoTieneCuartoSegmento() {
        // Arrange
        ClaveMensaje malFormada = claveCon("app.dominio");

        // Act
        String texto = MensajesRespaldo.para(malFormada);

        // Assert
        assertThat(texto).isEqualTo(MensajesRespaldo.ERROR);
    }

    @Test
    @DisplayName("porDefecto devuelve siempre la misma instancia")
    void debeDevolverLaMismaInstancia_cuandoSePideVariasVeces() {
        // Act & Assert
        assertThat(CatalogoMensajesRespaldo.porDefecto()).isSameAs(respaldo);
    }

    private static ClaveMensaje claveCon(String texto) {
        return new ClaveMensaje() {
            @Override
            public String clave() {
                return texto;
            }

            @Override
            public int parametros() {
                return 0;
            }
        };
    }
}
