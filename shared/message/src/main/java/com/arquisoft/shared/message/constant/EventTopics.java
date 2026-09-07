package com.arquisoft.shared.message.constant;

public final class EventTopics {

    private EventTopics() {}

    public static final class Fichas {

        private Fichas() {}

        public static final String FICHA_PERFIL_ASESOR_CAMBIADO =
                "fichas.ficha_perfil.asesor_cambiado";

        public static final String FICHA_PERFIL_REGISTRADA =
                "fichas.ficha_perfil.registrada";

        public static final String ESTUDIANTES_FICHA_PERFIL_ASIGNADOS =
                "fichas.estudiante_ficha_perfil.asignados";

        public static final String REVISION_ITEM_AGREGADO =
                "fichas.revision_item.agregado";
    }

    public static final class Usuarios {

        private Usuarios() {}

        public static final String USUARIO_CREADO = "usuarios.usuario.creado";
    }

    public static final class Proyectos {

        private Proyectos() {}

        public static final String ESTUDIANTE_PROYECTO_ASIGNADO = "proyectos.estudiante_proyecto.asignado";

        public static final String ESTUDIANTE_PROYECTO_DESTITUIDO = "proyectos.estudiante_proyecto.destituido";
    }

    public static final class Entregables {

        private Entregables() {}

        public static final String ENTREGABLE_PROYECTO_GRADO_GENERADO =
                "entregables.entregable_proyecto_grado.generado";
    }
}
