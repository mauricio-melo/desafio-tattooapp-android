package br.com.newidea.desafiotattooapp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonDeserialize(builder = TattooResponseDTO.Builder.class)
public class TattooResponseDTO implements Serializable {

    private String imageUrl;
    private Long id;
    private String descricao;
    private String localCorpo;
    private String estilo;
    private int numeroSessoes;
    private BigDecimal valor;


    public static class Builder {
        private String imageUrl;
        private Long id;
        private String descricao;
        private String localCorpo;
        private String estilo;
        private int numeroSessoes;
        private BigDecimal valor;

        @JsonProperty("id")
        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        @JsonProperty("imageUrl")
        public Builder setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        @JsonProperty("valor")
        public Builder setValor(BigDecimal valor) {
            this.valor = valor;
            return this;
        }

        @JsonProperty("numeroSessoes")
        public Builder setNumeroSessoes(int numeroSessoes) {
            this.numeroSessoes = numeroSessoes;
            return this;
        }

        @JsonProperty("descricao")
        public Builder setDescricao(String descricao) {
            this.descricao = descricao;
            return this;
        }

        @JsonProperty("estilo")
        public Builder setEstilo(String estilo) {
            this.estilo = estilo;
            return this;
        }

        @JsonProperty("localCorpo")
        public Builder setLocalCorpo(String localCorpo) {
            this.localCorpo = localCorpo;
            return this;
        }


        public TattooResponseDTO build() {
            final TattooResponseDTO tattooResponseDTO = new TattooResponseDTO();
            tattooResponseDTO.setId(id);
            tattooResponseDTO.setDescricao(descricao);
            tattooResponseDTO.setEstilo(estilo);
            tattooResponseDTO.setImageUrl(imageUrl);
            tattooResponseDTO.setLocalCorpo(localCorpo);
            tattooResponseDTO.setNumeroSessoes(numeroSessoes);
            tattooResponseDTO.setValor(valor);

            return tattooResponseDTO;
        }
    }
}
