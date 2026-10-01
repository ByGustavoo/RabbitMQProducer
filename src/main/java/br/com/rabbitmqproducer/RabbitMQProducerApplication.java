package br.com.rabbitmqproducer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class RabbitMQProducerApplication {

	static void main(String[] args) {
		SpringApplication.run(RabbitMQProducerApplication.class, args);
	}
}