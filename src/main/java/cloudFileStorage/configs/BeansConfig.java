package cloudFileStorage.configs;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;


@Configuration
public class BeansConfig {

//    @Bean
//    public SpringSecurityDialect springSecurityDialect(){
//        return new SpringSecurityDialect();
//    }
    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }
}
