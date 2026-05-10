package br.com.cafeteria.demo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("DemoApplication - Contexto")
class DemoApplicationTests {

	@Test
	@DisplayName("Deve carregar o contexto da aplicação")
	void contextLoads() {
		// Se o contexto Spring carregar sem erros, o teste passa
	}
}