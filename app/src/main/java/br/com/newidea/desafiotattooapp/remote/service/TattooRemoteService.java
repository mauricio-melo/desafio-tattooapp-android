package br.com.newidea.desafiotattooapp.remote.service;

import br.com.newidea.desafiotattooapp.dto.request.TattooRequestDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Maur on 05/12/2017.
 */

public interface TattooRemoteService {

    @POST("/api/tattoo")
    Call<Void> save(@Body TattooRequestDTO jobRequestDTO);
}
