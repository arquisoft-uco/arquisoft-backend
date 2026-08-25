package com.arquisoft.shared.message.annotation;

/**
 * Textos de documentación OpenAPI del contexto {@code usuarios}.
 *
 * <p>Mismo criterio que {@link FichasApiMessages}: el texto va incrustado y no al catálogo de Redis,
 * porque la especificación OpenAPI se construye una vez al arrancar y no vuelve a consultarse. Ver
 * allí el razonamiento completo.
 */
public final class UsuariosApiMessages {

    private UsuariosApiMessages() {}

    public static final class Usuario {

        private Usuario() {}

        public static final String TAG_NAME = "Usuarios";
        public static final String TAG_DESCRIPTION = "Gestion de usuarios del sistema";

        public static final String CREAR_SUMMARY = "Crear usuario";
        public static final String CREAR_DESCRIPTION =
                "Crea un nuevo usuario en el sistema con el email y rol indicados. "
                        + "Publica el evento UsuarioCreadoEvent para que otros contextos registren "
                        + "al usuario en sus bases de datos espejo.";
        public static final String CREAR_RESP_201 = "Usuario creado exitosamente";
        public static final String CREAR_RESP_400 = "Datos invalidos (email mal formado, rol nulo)";
        public static final String CREAR_RESP_401 = "No autenticado";
        public static final String CREAR_RESP_403 = "Sin permisos — se requiere rol administrador";
    }

    public static final class RepresentanteComiteCurriculum {

        private RepresentanteComiteCurriculum() {}

        public static final String TAG_NAME = "Representantes del Comite Curriculum";
        public static final String TAG_DESCRIPTION = "Operaciones para gestionar representantes del comite curriculum";

        public static final String AGREGAR_SUMMARY = "Agregar un nuevo representante del comite curriculum";
        public static final String AGREGAR_DESCRIPTION = "Registra un usuario existente como representante del comite curriculum";
        public static final String AGREGAR_RESP_201 = "Representante agregado exitosamente";
        public static final String AGREGAR_RESP_400 = "Solicitud invalida";
        public static final String AGREGAR_RESP_401 = "No autenticado";
        public static final String AGREGAR_RESP_403 = "Acceso denegado";
        public static final String AGREGAR_RESP_422 = "Usuario no encontrado o ya es representante";
    }
}
