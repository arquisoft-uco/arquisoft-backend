package com.arquisoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

// nameGenerator = FQN: el monolito modular tiene varios contextos con clases @Component de nombre
// simple repetido (p. ej. UsuarioCreadoConsumer en fichas y en solicitudes, RegistrarUsuarioUseCaseImpl,
// UsuarioCommandOutputAdapter). El generador por defecto los nombra por nombre simple y colisionan al
// escanear com.arquisoft.**. FQN da a cada bean un nombre unico (su clase completa). No afecta a los
// beans de metodo @Bean (DataSources, EMFs, Flyways, exchanges, ObjectMappers), que conservan su
// nombre de metodo — y son los unicos referenciados por @Qualifier en el repo.
@SpringBootApplication(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
public class ArquisoftApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArquisoftApplication.class, args);
    }
}
