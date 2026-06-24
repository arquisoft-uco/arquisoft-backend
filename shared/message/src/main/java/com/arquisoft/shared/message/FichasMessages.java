package com.arquisoft.shared.message;

public final class FichasMessages {

    private FichasMessages() {}

    // ─────────────────────────────────────────────────────────────────────────
    // FichaPerfil
    // ─────────────────────────────────────────────────────────────────────────

    public static final class FichaPerfil {

        private FichaPerfil() {}

        // Campos
        public static final String CAMPO_ID              = "id";
        public static final String CAMPO_TITULO          = "tituloProyecto";
        public static final String CAMPO_ASESOR_FICHA_ID = "asesorFichaId";

        // Límites
        public static final int TITULO_MAX = 100;
        public static final int ESTUDIANTES_MAX = 3;

        // Códigos de error
        public static final String ID_REQUERIDO            = "FICHA_ID_REQUERIDO";
        public static final String TITULO_REQUERIDO        = "FICHA_TITULO_REQUERIDO";
        public static final String TITULO_DEMASIADO_LARGO  = "FICHA_TITULO_DEMASIADO_LARGO";
        public static final String ASESOR_REQUERIDO        = "FICHA_ASESOR_REQUERIDO";
        public static final String FICHA_TITULO_DUPLICADO  = "FICHA_TITULO_DUPLICADO";
        public static final String ASESOR_NO_ENCONTRADO    = "ASESOR_NO_ENCONTRADO";

        // Mensajes de error
        public static final String TITULO_DUPLICADO        = "El título ya existe: %s";
        public static final String ASESOR_NO_ENCONTRADO_MSG = "Asesor Ficha no encontrado: %s";
        public static final String TITULO_REQUERIDO_MSG    = "El título del proyecto es obligatorio";
        public static final String ASESOR_REQUERIDO_MSG    = "El asesor ficha es obligatorio";

        // Logs
        public static final String LOG_REGISTRADA            = "Ficha de perfil registrada — id={}";
        public static final String LOG_GUARDADA              = "FichaPerfil guardada: id={}";
        public static final String LOG_CONSULTANDO           = "Consultando fichas de perfil — pagina={}, tamanio={}";
        public static final String LOG_CONSULTA_COMPLETADA   = "Consulta fichas-perfil completada — total={}, pagina={}, tamanio={}";
        public static final String LOG_ORDENAMIENTO_INVALIDO  = "Campo de ordenamiento inválido: {}";
        public static final String LOG_USO_INVALIDO_API_ORDEN = "Uso inválido de la API de acceso a datos al ordenar: {}";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // AsesorFicha
    // ─────────────────────────────────────────────────────────────────────────

    public static final class AsesorFicha {

        private AsesorFicha() {}

        // Campos
        public static final String CAMPO_ID            = "id";
        public static final String CAMPO_IDENTIFICADOR = "identificador";
        public static final String CAMPO_NOMBRE        = "nombre";
        public static final String CAMPO_EMAIL         = "email";

        // (Sin códigos ni logs por ahora — el aggregate solo expone rebuild.)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Usuario (espejo de seguridad consumido vía AMQP)
    // ─────────────────────────────────────────────────────────────────────────

    public static final class Usuario {

        private Usuario() {}

        // Logs
        public static final String LOG_USUARIO_CREADO_RECIBIDO =
                "[FICHAS] UsuarioCreado recibido: usuarioId={} email={} rol={}";
        public static final String LOG_REGISTRADO_ESPEJO_SIMULADO =
                "[FICHAS] Usuario registrado en espejo (simulado): usuarioId={} email={} rol={}";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MinIO (guías de fichas)
    // ─────────────────────────────────────────────────────────────────────────

    public static final class MinioGuia {

        private MinioGuia() {}

        // Logs
        public static final String LOG_UPLOAD_URL   = "GET /fichas/minio/guia/upload-url — bucket={}, key={}";
        public static final String LOG_DOWNLOAD_URL = "GET /fichas/minio/guia/download-url — bucket={}, key={}";
        public static final String LOG_DELETE       = "DELETE /fichas/minio/guia/objeto — bucket={}, key={}";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Estudiante (réplica local)
    // ─────────────────────────────────────────────────────────────────────────

    public static final class Estudiante {

        private Estudiante() {}

        // Códigos de error
        public static final String ESTUDIANTE_NO_ENCONTRADO = "ESTUDIANTE_NO_ENCONTRADO";

        // Mensajes de error
        public static final String NO_ENCONTRADO = "No se encontró el estudiante con id: %s";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // EstudianteFichaPerfil (relación)
    // ─────────────────────────────────────────────────────────────────────────

    public static final class EstudianteFichaPerfil {

        private EstudianteFichaPerfil() {}

        // Campos
        public static final String CAMPO_ID              = "id";
        public static final String CAMPO_FICHA_PERFIL_ID = "fichaPerfilId";
        public static final String CAMPO_ESTUDIANTE_ID   = "estudianteId";

        // Códigos de error
        public static final String ID_REQUERIDO                 = "ESTUDIANTE_FICHA_PERFIL_ID_REQUERIDO";
        public static final String FICHA_PERFIL_ID_REQUERIDO    = "ESTUDIANTE_FICHA_PERFIL_FICHA_ID_REQUERIDO";
        public static final String ESTUDIANTE_ID_REQUERIDO      = "ESTUDIANTE_FICHA_PERFIL_ESTUDIANTE_ID_REQUERIDO";
        public static final String ESTUDIANTE_DUPLICADO         = "ESTUDIANTE_DUPLICADO";
        public static final String LIMITE_ESTUDIANTES_EXCEDIDO  = "LIMITE_ESTUDIANTES_EXCEDIDO";

        // Mensajes de error
        public static final String DUPLICADO        = "El estudiante ya está asignado a esta ficha: %s";
        public static final String LIMITE_EXCEDIDO  = "No se pueden asignar más de %d estudiantes a una ficha";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // EstadoFichaPerfil (trazabilidad)
    // ─────────────────────────────────────────────────────────────────────────

    public static final class EstadoFichaPerfil {

        private EstadoFichaPerfil() {}

        // Campos
        public static final String CAMPO_ID                  = "id";
        public static final String CAMPO_FICHA_PERFIL_ID     = "fichaPerfilId";
        public static final String CAMPO_ESTADO_FICHA        = "estadoFicha";
        public static final String CAMPO_FECHA_ACTUALIZACION = "fechaActualizacion";

        // Códigos de error
        public static final String ID_REQUERIDO                    = "ESTADO_FICHA_PERFIL_ID_REQUERIDO";
        public static final String FICHA_PERFIL_ID_REQUERIDO       = "ESTADO_FICHA_PERFIL_FICHA_PERFIL_ID_REQUERIDO";
        public static final String ESTADO_FICHA_REQUERIDO          = "ESTADO_FICHA_PERFIL_ESTADO_FICHA_REQUERIDO";
        public static final String FECHA_ACTUALIZACION_REQUERIDA   = "ESTADO_FICHA_PERFIL_FECHA_ACTUALIZACION_REQUERIDA";

        // Logs
        public static final String LOG_CREADO = "Estado ficha perfil creado — id={}, fichaPerfilId={}, estadoFicha={}";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // EstadoFicha (catálogo)
    // ─────────────────────────────────────────────────────────────────────────

    public static final class EstadoFicha {

        private EstadoFicha() {}

        // Códigos de error
        public static final String ESTADO_NO_ENCONTRADO = "ESTADO_FICHA_NO_ENCONTRADO";

        // Mensajes de error
        public static final String NOMBRE_NO_ENCONTRADO_MENSAJE = "No se encontró el estado: %s";
    }
}
