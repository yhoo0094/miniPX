package com.mpx.minipx.repository;

import com.mpx.minipx.entity.TbUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TbUserRepository extends JpaRepository<TbUser, String> {
	TbUser findByUserId(String userId);
}
