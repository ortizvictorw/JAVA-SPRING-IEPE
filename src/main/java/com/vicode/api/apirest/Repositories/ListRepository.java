package com.vicode.api.apirest.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vicode.api.apirest.Entities.ListEntity;

public interface ListRepository extends JpaRepository<ListEntity, Long> {

}
