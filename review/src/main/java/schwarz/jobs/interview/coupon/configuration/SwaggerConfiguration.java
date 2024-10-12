package schwarz.jobs.interview.coupon.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI couponOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Coupon Application API")
                        .version("1.0")
                        .description("API documentation for the Coupon Application"));
    }
}

