package com.project.shopapp.Configuration;

import com.project.shopapp.DTO.OrderDetailDTO;
import com.project.shopapp.Models.OrderDetail;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfiguration {
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}
