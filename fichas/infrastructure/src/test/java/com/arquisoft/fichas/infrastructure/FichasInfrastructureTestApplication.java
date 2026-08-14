package com.arquisoft.fichas.infrastructure;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication
@EntityScan({"com.arquisoft.fichas.application", "com.arquisoft.fichas.infrastructure"})
public class FichasInfrastructureTestApplication {
}
