package com.example.atividade

class Estoque {
    companion object {
        private val listaProdutos = mutableListOf<Produto>()

        fun adicionarProduto(produto: Produto) {
            listaProdutos.add(produto)
        }

        fun getListaProdutos(): List<Produto> {
            return listaProdutos
        }

        fun calcularValorTotalEstoque(): Double {
            return listaProdutos.sumOf { it.preco * it.quantidadeEmEstoque }
        }

        fun calcularQuantidadeTotalProdutos(): Int {
            return listaProdutos.sumOf { it.quantidadeEmEstoque }
        }
    }
}
