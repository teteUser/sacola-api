package me.dio.sacola.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Embeddable
public class ItemDto {

    private Long idProduto;
    private Integer quantidade;
    private Long idSacola;

}
