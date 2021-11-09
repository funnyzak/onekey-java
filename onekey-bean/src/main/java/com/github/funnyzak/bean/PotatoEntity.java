package com.github.funnyzak.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.plugin.spring.boot.service.entity.DataBaseEntity;

import java.io.Serializable;

/**
 * @author silenceace@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PotatoEntity extends DataBaseEntity implements Serializable {
    private static final Long serialVersionUID = 1L;
    @Id
    Long id;
}
