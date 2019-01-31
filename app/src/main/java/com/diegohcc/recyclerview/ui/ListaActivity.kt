package com.diegohcc.recyclerview.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.diegohcc.recyclerview.R
import com.diegohcc.recyclerview.api.getPokemonAPI
import com.diegohcc.recyclerview.model.Pokemon
import com.diegohcc.recyclerview.model.PokemonResponse
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_lista.*

class ListaActivity : AppCompatActivity() {
    private var disposable: Disposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista)
        carregarDados()
    }

    private fun exibeNaLista(pokemons: List<Pokemon>) {
        rvPokemon.adapter = ListaPokemonAdapter(this, pokemons, {
            Toast.makeText(this, it.nome, Toast.LENGTH_LONG).show()
        })
        rvPokemon.layoutManager = LinearLayoutManager(this)

    }

    private fun exibeErro(t: Throwable) {
        Toast.makeText(this, t.message, Toast.LENGTH_LONG).show()
    }

    private fun carregarDados() {
        getPokemonAPI()
                .buscar(150)
                .subscribeOn(Schedulers.io()) //informa que vai trabalhar com io para nao blockar
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()) //manipular thread principal
                .subscribe(object : Observer<PokemonResponse> {
                    override fun onComplete() { //quando finaliza a chamada
                        Log.i("ListaActivity", "Complete")
                    }

                    override fun onSubscribe(d: Disposable) { //Quando entra no subscribe
                        disposable = d // para armazenar o observable e depois dar o dispose
                    }

                    override fun onNext(t: PokemonResponse) { //Quando der sucesso
                        exibeNaLista(t.pokemons)
                    }

                    override fun onError(e: Throwable) { // Erro na chamada
                        exibeErro(e)
                    }

                })

    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}

