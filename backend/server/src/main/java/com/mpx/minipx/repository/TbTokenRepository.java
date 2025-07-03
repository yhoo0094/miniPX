package com.mpx.minipx.repository;

import com.mpx.minipx.entity.TbToken;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TbTokenRepository extends JpaRepository<TbToken, String> {
	Optional<TbToken> findByUserId(String userId);
	void deleteByUserId(String userId);
}
