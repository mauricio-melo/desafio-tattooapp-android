package br.com.newidea.desafiotattooapp.dto.request;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Maur on 05/12/2017.
 */

@Data
@Builder
public class TattooRequestDTO {

    private String imageUrl;
    private Long id;
    private String descricao;
    private String localCorpo;
    private String estilo;
    private int numeroSessoes;
    private BigDecimal valor;
    private String imageFileName;
    private String base64ByteImagem;
}
