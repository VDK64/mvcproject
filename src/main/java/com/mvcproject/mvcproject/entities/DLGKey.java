package com.mvcproject.mvcproject.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class DLGKey implements Serializable {
    @Column(name = "from_id", nullable = false)
    private Long fromId;
    @Column(name = "to_id", nullable = false)
    private Long toId;
}
