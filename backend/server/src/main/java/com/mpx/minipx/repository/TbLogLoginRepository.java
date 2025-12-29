package com.mpx.minipx.repository;

import com.mpx.minipx.entity.TbLogLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TbLogLoginRepository
extends JpaRepository<TbLogLogin, Long>, JpaSpecificationExecutor<TbLogLogin> {
}
