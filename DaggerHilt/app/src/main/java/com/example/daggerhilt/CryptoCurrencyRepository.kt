package com.example.daggerhilt

interface CryptocurrencyRepository {
    fun getCryptoCurrency(): List<Cryptocurrency>
}
