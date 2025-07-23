package com.mpx.minipx.repository;

import com.mpx.minipx.entity.TbCodeDetail;
import com.mpx.minipx.entity.TbCodeDetailId;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TbCodeDetailRepository extends JpaRepository<TbCodeDetail, TbCodeDetailId> {
	List<TbCodeDetail> findByIdCodeGroup(String codeGroup);
}
