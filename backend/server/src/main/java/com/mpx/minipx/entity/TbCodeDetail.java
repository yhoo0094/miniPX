package com.mpx.minipx.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "TB_CODE_DETAIL")
@Data
public class TbCodeDetail {
    
    @EmbeddedId
    @JsonProperty("id")
    private TbCodeDetailId id;
    
    @Column(name = "CODE_DETAIL_NM")
	@JsonProperty("codeDetailNm")
    private String codeDetailNm;
    
    @Column(name = "MODIFY_YN")
	@JsonProperty("modifyYn")
    private String modifyYn;  
    
    @Column(name = "DETAIL_ORDER")
	@JsonProperty("detailOrder")
    private String detailOrder;  
    
    public String getCodeGroup() {
        return id != null ? id.getCodeGroup() : null;
    }

    public String getCodeDetail() {
        return id != null ? id.getCodeDetail() : null;
    }    
}