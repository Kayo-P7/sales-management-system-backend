package com.commercial_management_system.backend.config.beansConfig;

import com.commercial_management_system.backend.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
public class AuditoriaConfig {


    @Bean
    //informa ao Spring que o responsável pela auditoria será representado por um Long
    public AuditorAware<Long> auditorAware(){
        /*
        *  @CreatedBy
        *  @Column(updatable = false)
        *  private String createdBy;
        *
        * faz com que esse campo seja preenchido, colocando o id do usario que criou
        * */
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if(authentication == null ||
                    !authentication.isAuthenticated() /* verifica se a autenticação está marcada como válida*/||
                    "anonymousUser".equals(authentication.getPrincipal())){
                return Optional.empty();
            }

            Object principal = authentication.getPrincipal();
            if(principal instanceof User user){
                return Optional.ofNullable(user.getId());
            }
            return Optional.empty();
        };
    }
}
