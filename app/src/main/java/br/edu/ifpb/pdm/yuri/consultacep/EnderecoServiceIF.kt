package br.edu.ifpb.pdm.yuri.consultacep

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface EnderecoServiceIF {
    @GET
        ("ws/{cep}/json/")
    fun buscarEndereco(
        @Path
            ("cep") cep:String
    ): Call<Endereco>
}