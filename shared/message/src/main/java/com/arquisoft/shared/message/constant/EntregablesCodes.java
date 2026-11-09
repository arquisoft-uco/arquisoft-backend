package com.arquisoft.shared.message.constant;

public final class EntregablesCodes {

    private EntregablesCodes() {}

    public static final class EstadoEntregable {

        private EstadoEntregable() {}

        public static final String NO_ENCONTRADO = "ESTADO_ENTREGABLE_NO_ENCONTRADO";
    }

    public static final class EntregableProyectoGrado {

        private EntregableProyectoGrado() {}

        public static final String PROYECTO_GRADO_REQUERIDO = "ENTREGABLES_ENTREGABLE_PROYECTO_GRADO_REQUERIDO";
        public static final String ASESOR_REQUERIDO = "ENTREGABLES_ENTREGABLE_PROYECTO_GRADO_ASESOR_REQUERIDO";
        public static final String ULTIMA_VERSION_INVALIDA =
                "ENTREGABLES_ENTREGABLE_PROYECTO_GRADO_ULTIMA_VERSION_INVALIDA";
        public static final String ID_REQUERIDO = "ENTREGABLES_ENTREGABLE_PROYECTO_GRADO_ID_REQUERIDO";
        public static final String ESTADO_ENTREGABLE_REQUERIDO =
                "ENTREGABLES_ENTREGABLE_PROYECTO_GRADO_ESTADO_ENTREGABLE_REQUERIDO";
        public static final String ESTADO_ENTREGABLE_INVALIDO =
                "ENTREGABLES_ENTREGABLE_PROYECTO_GRADO_ESTADO_ENTREGABLE_INVALIDO";
        public static final String NO_ENCONTRADO = "ENTREGABLES_ENTREGABLE_PROYECTO_GRADO_NO_ENCONTRADO";
        public static final String ASESOR_NO_PROPIETARIO =
                "ENTREGABLES_ENTREGABLE_PROYECTO_GRADO_ASESOR_NO_PROPIETARIO";
        public static final String ESTADO_DESTINO_NO_PERMITIDO =
                "ENTREGABLES_ENTREGABLE_PROYECTO_GRADO_ESTADO_DESTINO_NO_PERMITIDO";
        public static final String CIERRE_NO_PERMITIDO = "ENTREGABLES_ENTREGABLE_PROYECTO_GRADO_CIERRE_NO_PERMITIDO";
        public static final String ESTADO_TERMINAL = "ENTREGABLES_ENTREGABLE_PROYECTO_GRADO_ESTADO_TERMINAL";
        public static final String ESTUDIANTE_REQUERIDO = "ENTREGABLES_ENTREGABLE_PROYECTO_GRADO_ESTUDIANTE_REQUERIDO";
        public static final String VIGENTE_YA_EXISTE = "ENTREGABLES_ENTREGABLE_PROYECTO_GRADO_VIGENTE_YA_EXISTE";
    }

    public static final class PaqueteEntregable {

        private PaqueteEntregable() {}

        public static final String ID_REQUERIDO = "ENTREGABLES_PAQUETE_ENTREGABLE_ID_REQUERIDO";
        public static final String ENTREGABLE_PROYECTO_GRADO_REQUERIDO =
                "ENTREGABLES_PAQUETE_ENTREGABLE_ENTREGABLE_PROYECTO_GRADO_REQUERIDO";
        public static final String BUCKET_REQUERIDO = "ENTREGABLES_PAQUETE_ENTREGABLE_BUCKET_REQUERIDO";
        public static final String CLAVE_OBJETO_REQUERIDA = "ENTREGABLES_PAQUETE_ENTREGABLE_CLAVE_OBJETO_REQUERIDA";
        public static final String NOMBRE_ARCHIVO_REQUERIDO = "ENTREGABLES_PAQUETE_ENTREGABLE_NOMBRE_ARCHIVO_REQUERIDO";
        public static final String TAMANO_BYTES_INVALIDO = "ENTREGABLES_PAQUETE_ENTREGABLE_TAMANO_BYTES_INVALIDO";
        public static final String GENERADO_EN_REQUERIDO = "ENTREGABLES_PAQUETE_ENTREGABLE_GENERADO_EN_REQUERIDO";
        public static final String ACTOR_REQUERIDO = "ENTREGABLES_PAQUETE_ENTREGABLE_ACTOR_REQUERIDO";
        public static final String ROLES_REQUERIDOS = "ENTREGABLES_PAQUETE_ENTREGABLE_ROLES_REQUERIDOS";
        public static final String ACCESO_DENEGADO = "ENTREGABLES_PAQUETE_ENTREGABLE_ACCESO_DENEGADO";
        public static final String EN_PREPARACION = "ENTREGABLES_PAQUETE_ENTREGABLE_EN_PREPARACION";
    }

    public static final class ProyectoGrado {

        private ProyectoGrado() {}

        public static final String ID_REQUERIDO = "ENTREGABLES_PROYECTO_GRADO_ID_REQUERIDO";
        public static final String NOMBRE_REQUERIDO = "ENTREGABLES_PROYECTO_GRADO_NOMBRE_REQUERIDO";
        public static final String NO_ENCONTRADO = "ENTREGABLES_PROYECTO_GRADO_NO_ENCONTRADO";
        public static final String ESTUDIANTE_NO_PERTENECE = "ENTREGABLES_PROYECTO_GRADO_ESTUDIANTE_NO_PERTENECE";
        public static final String FINALIZADO = "ENTREGABLES_PROYECTO_GRADO_FINALIZADO";
        public static final String ESTADO_PROYECTO_NO_ENCONTRADO = "ENTREGABLES_ESTADO_PROYECTO_NO_ENCONTRADO";
    }

    public static final class Asesor {

        private Asesor() {}

        public static final String ID_REQUERIDO = "ENTREGABLES_ASESOR_ID_REQUERIDO";
        public static final String IDENTIFICADOR_REQUERIDO = "ENTREGABLES_ASESOR_IDENTIFICADOR_REQUERIDO";
        public static final String NOMBRE_REQUERIDO = "ENTREGABLES_ASESOR_NOMBRE_REQUERIDO";
        public static final String EMAIL_REQUERIDO = "ENTREGABLES_ASESOR_EMAIL_REQUERIDO";
        public static final String OCURRIDO_EN_REQUERIDO = "ENTREGABLES_ASESOR_OCURRIDO_EN_REQUERIDO";
        public static final String NO_VIGENTE = "ENTREGABLES_ASESOR_NO_VIGENTE";
    }

    public static final class EstudianteProyectoGrado {

        private EstudianteProyectoGrado() {}

        public static final String PROYECTO_GRADO_REQUERIDO = "ENTREGABLES_ESTUDIANTE_PROYECTO_GRADO_PROYECTO_GRADO_REQUERIDO";
        public static final String ESTUDIANTES_REQUERIDOS = "ENTREGABLES_ESTUDIANTE_PROYECTO_GRADO_ESTUDIANTES_REQUERIDOS";
        public static final String ESTUDIANTE_REQUERIDO = "ENTREGABLES_ESTUDIANTE_PROYECTO_GRADO_ESTUDIANTE_REQUERIDO";
        public static final String OCURRIDO_EN_REQUERIDO = "ENTREGABLES_ESTUDIANTE_PROYECTO_GRADO_OCURRIDO_EN_REQUERIDO";
        public static final String ESTUDIANTE_DUPLICADO = "ENTREGABLES_ESTUDIANTE_PROYECTO_GRADO_ESTUDIANTE_DUPLICADO";
    }
}
