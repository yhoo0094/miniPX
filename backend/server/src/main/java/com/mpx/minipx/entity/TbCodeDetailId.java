package com.mpx.minipx.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Embeddable
@Data
@EqualsAndHashCode
public class TbCodeDetailId implements Serializable {

    @Column(name = "CODE_GROUP")
    private String codeGroup;

    @Column(name = "CODE_DETAIL")
    private String codeDetail;
}
