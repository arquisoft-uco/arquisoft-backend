package com.arquisoft.shared.message.constant;

public final class EventTopics {

    private EventTopics() {}

    public static final class Fichas {

        private Fichas() {}

        public static final String FICHA_PERFIL_ASESOR_CAMBIADO =
                "fichas.ficha_perfil.asesor_cambiado";

        public static final String FICHA_PERFIL_REGISTRADA =
                "fichas.ficha_perfil.registrada";
    }

    public static final class Usuarios {

        private Usuarios() {}

        public static final String USUARIO_CREADO = "usuarios.usuario.creado";
    }
}
