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

    public static final class Comun {

        private Comun() {}

        public static final String RESP_401 = "No autenticado";
        public static final String RESP_403 = "Sin permisos para realizar esta acción";
    }

    public static final class Usuario {

        private Usuario() {}

        public static final String TAG_NAME = "Usuarios";
        public static final String TAG_DESCRIPTION = "Gestión de usuarios del sistema";

        public static final String REGISTRAR_SUMMARY = "Registrar información de un nuevo usuario";
        public static final String REGISTRAR_DESCRIPTION =
                "Registra un nuevo usuario en el proveedor de identidad (Keycloak) y persiste la "
                        + "información local con el identificador que Keycloak le asigna. Asigna los realm "
                        + "roles indicados y dispara el correo de establecimiento de contraseña. "
                        + "Exclusivo del rol administrador.";
        public static final String REGISTRAR_RESP_201 = "Usuario registrado — retorna el UUID asignado";
        public static final String REGISTRAR_RESP_400 = "Datos de entrada inválidos o rol no válido";
        public static final String REGISTRAR_RESP_422 = "Identificador, email o contacto ya registrados, o datos de dominio inválidos";
        public static final String REGISTRAR_RESP_503 = "No fue posible completar el registro; el servicio no está disponible temporalmente";

        public static final String MODIFICAR_SUMMARY = "Modificar información de un usuario existente";
        public static final String MODIFICAR_DESCRIPTION =
                "Modifica los datos personales enviados de un usuario activo y sincroniza el email y el "
                        + "nombre en el proveedor de identidad (Keycloak). Los roles enviados se agregan o, si "
                        + "fueron eliminados lógicamente, se reactivan; un rol ausente de la lista no se revoca. "
                        + "Solo cambian los campos presentes en el body. Exclusivo del rol administrador.";
        public static final String MODIFICAR_RESP_204 = "Usuario modificado";
        public static final String MODIFICAR_RESP_400 =
                "Identificador de usuario inválido, rol no válido o body sin ningún dato ni rol";
        public static final String MODIFICAR_RESP_422 =
                "Usuario inexistente o inactivo, datos ya usados por otro usuario, o rol ya vigente";
        public static final String MODIFICAR_RESP_503 =
                "No fue posible sincronizar con el proveedor de identidad; el servicio no está disponible temporalmente";
    }

    public static final class Estudiante {

        private Estudiante() {}

        public static final String REMOVER_SUMMARY = "Remover información de un estudiante";
        public static final String REMOVER_DESCRIPTION =
                "Da de baja lógica el rol estudiante de un usuario: marca la fecha de eliminación sin borrar "
                        + "la fila, revoca el realm role estudiante en Keycloak y notifica a los contextos "
                        + "que replican al estudiante. El usuario y sus demás roles no cambian. "
                        + "Exclusivo del rol administrador.";
        public static final String REMOVER_RESP_204 = "Estudiante removido";
        public static final String REMOVER_RESP_400 = "Identificador de usuario inválido";
        public static final String REMOVER_RESP_422 = "El usuario no tiene un rol estudiante vigente";
        public static final String REMOVER_RESP_503 =
                "No fue posible revocar el rol en el proveedor de identidad; el servicio no está disponible temporalmente";
    }

    public static final class Asesor {

        private Asesor() {}

        public static final String REMOVER_SUMMARY = "Remover información de un asesor";
        public static final String REMOVER_DESCRIPTION =
                "Da de baja lógica el rol asesor de un usuario: marca la fecha de eliminación sin borrar "
                        + "la fila, revoca el realm role asesor en Keycloak y notifica a los contextos "
                        + "que replican al asesor. El usuario y sus demás roles no cambian. "
                        + "Exclusivo del rol administrador.";
        public static final String REMOVER_RESP_204 = "Asesor removido";
        public static final String REMOVER_RESP_400 = "Identificador de usuario inválido";
        public static final String REMOVER_RESP_422 = "El usuario no tiene un rol asesor vigente";
        public static final String REMOVER_RESP_503 =
                "No fue posible revocar el rol en el proveedor de identidad; el servicio no está disponible temporalmente";
    }
}
