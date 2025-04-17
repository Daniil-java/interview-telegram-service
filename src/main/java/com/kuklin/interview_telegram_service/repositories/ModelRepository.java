package com.kuklin.interview_telegram_service.repositories;

import com.kuklin.interview_telegram_service.entities.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {

//    @Query(value = "SELECT * FROM models m WHERE m.model_name = ?1", nativeQuery = true)
//    @Query(value = "SELECT m FROM Model m WHERE m.modelName = :modelName")
    @Query(value = "SELECT m FROM Model m WHERE m.modelName = :model")
    Optional<Model> findByModelName(String model);

}
