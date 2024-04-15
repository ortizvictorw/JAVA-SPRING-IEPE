package com.vicode.api.apirest.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vicode.api.apirest.Entities.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
