package com.main_service.mainService.Repos;


import com.main_service.mainService.Models.Conversion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ConversionRepository extends JpaRepository<Conversion, Long> {

}
