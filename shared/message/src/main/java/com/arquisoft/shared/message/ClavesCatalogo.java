package com.arquisoft.shared.message;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import com.arquisoft.shared.message.key.app.AlmacenamientoKey;
import com.arquisoft.shared.message.key.app.ConsultaKey;
import com.arquisoft.shared.message.key.app.HttpKey;
import com.arquisoft.shared.message.key.app.MensajeriaKey;
import com.arquisoft.shared.message.key.app.PaginacionKey;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.message.key.evaluaciones.ItemCualitativoJuradoKey;
import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;
import com.arquisoft.shared.message.key.fichas.EstadoFichaKey;
import com.arquisoft.shared.message.key.fichas.EstadoFichaPerfilKey;
import com.arquisoft.shared.message.key.fichas.EstudianteFichaPerfilKey;
import com.arquisoft.shared.message.key.fichas.EstudianteKey;
import com.arquisoft.shared.message.key.fichas.EvaluacionFichaPerfilKey;
import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;
import com.arquisoft.shared.message.key.fichas.MinioGuiaKey;
import com.arquisoft.shared.message.key.fichas.TipoItemKey;
import com.arquisoft.shared.message.key.fichas.UsuarioEspejoKey;
import com.arquisoft.shared.message.key.fichas.RepresentanteComiteKey;
import com.arquisoft.shared.message.key.fichas.RevisionItemKey;
import com.arquisoft.shared.message.key.notificaciones.ConsumidorKey;
import com.arquisoft.shared.message.key.notificaciones.EnvioNotificacionKey;
import com.arquisoft.shared.message.key.notificaciones.NotificacionKey;
import com.arquisoft.shared.message.key.notificaciones.PlantillaKey;
import com.arquisoft.shared.message.key.seguridad.AutenticacionKey;
import com.arquisoft.shared.message.key.seguridad.ConfiguracionKey;
import com.arquisoft.shared.message.key.seguridad.IniciarSesionKey;
import com.arquisoft.shared.message.key.seguridad.LimiteSolicitudesKey;
import com.arquisoft.shared.message.key.seguridad.RolKey;
import com.arquisoft.shared.message.key.seguridad.SesionKey;
import com.arquisoft.shared.message.key.seguridad.TokenInvalidadoKey;
import com.arquisoft.shared.message.key.seguridad.TokenKey;
import com.arquisoft.shared.message.key.usuarios.UsuarioKey;


/**
 * Registro explícito de los enums que componen el catálogo.
 *
 * <p>El arranque fail-fast necesita enumerar en producción todas las claves declaradas, para
 * pedírselas a Redis y abortar si falta alguna. Un escaneo de classpath en tiempo de ejecución es
 * frágil dentro de un {@code jar} y caro en el arranque, así que la lista es explícita: una línea
 * por enum.
 *
 * <p>El agujero de toda lista manual — alguien añade un enum y olvida registrarlo — lo cierra
 * {@code CatalogoCargaTest}, que escanea el paquete {@code key} sobre las clases compiladas y falla
 * el build si el registro no coincide con lo escaneado. La lista vive en producción; la garantía de
 * que está completa vive en el test.
 */
public final class ClavesCatalogo {

    private ClavesCatalogo() {}

    /**
     * Los enums registrados, uno por feature.
     *
     * <p>Es un {@code Set.of} y no un {@code List.of} a propósito: rechaza duplicados al inicializar
     * la clase, así que una línea repetida revienta en el arranque en vez de dejar un enum registrado
     * dos veces y otro sin registrar, en silencio.
     *
     * <p>Ese fallo ya ocurrió una vez, cuando cuatro nombres colisionaban por pares y había que
     * cualificarlos aquí: bastaba con que el "optimizar imports" del IDE colapsara un par para que dos
     * enums desaparecieran del registro. La defensa de verdad no era el {@code Set} sino renombrarlos
     * —{@code EnvioNotificacionKey} y {@code UsuarioEspejoKey}—, porque un nombre único no se puede
     * colapsar. Lo que queda aquí es la red por si alguien duplica una línea a mano.
     */
    public static final Set<Class<? extends ClaveMensaje>> ENUMS = Set.of(
            AlmacenamientoKey.class,
            ConsultaKey.class,
            HttpKey.class,
            MensajeriaKey.class,
            PaginacionKey.class,
            ValidadorKey.class,
            EstadoEvaluacionFichaKey.class,
            EstadoFichaKey.class,
            EstadoFichaPerfilKey.class,
            EstudianteFichaPerfilKey.class,
            EstudianteKey.class,
            EvaluacionFichaPerfilKey.class,
            ItemCualitativoJuradoKey.class,
            FichaPerfilKey.class,
            ItemFichaPerfilKey.class,
            MinioGuiaKey.class,
            TipoItemKey.class,
            RepresentanteComiteKey.class,
            RevisionItemKey.class,
            UsuarioEspejoKey.class,
            ConsumidorKey.class,
            EnvioNotificacionKey.class,
            NotificacionKey.class,
            PlantillaKey.class,
            AutenticacionKey.class,
            ConfiguracionKey.class,
            IniciarSesionKey.class,
            LimiteSolicitudesKey.class,
            RolKey.class,
            SesionKey.class,
            TokenInvalidadoKey.class,
            TokenKey.class,
            UsuarioKey.class
    );

    /** Todas las claves declaradas por los enums de {@link #ENUMS}. */
    public static final List<ClaveMensaje> TODAS = ENUMS.stream()
            .map(Class::getEnumConstants)
            .flatMap(Arrays::stream)
            .map(ClaveMensaje.class::cast)
            .toList();
}
