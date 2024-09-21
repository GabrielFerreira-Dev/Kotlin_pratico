package com.example.atividade

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.atividade.Produto
import com.google.gson.Gson

// Companion object para armazenar a lista de produtos
class ProdutoManager {
    companion object {
        val listaProdutos = mutableListOf<Produto>()
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "cadastro") {
                composable("cadastro") { CadastroProdutoScreen(navController) }
                composable("lista") { ListaProdutosScreen(navController) }
                composable("detalhes/{produtoJson}") { backStackEntry ->
                    val produtoJson = backStackEntry.arguments?.getString("produtoJson") ?: ""
                    DetalhesProdutoScreen(produtoJson, navController)
                }
                composable("estatisticas") { EstatisticasScreen(navController) }
            }
        }
    }
}

@Composable
fun CadastroProdutoScreen(navController: NavHostController) {
    var nome by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Cadastro de Produtos", fontSize = 25.sp)
        TextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome do Produto") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoria") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = preco,
            onValueChange = { preco = it },
            label = { Text("Preço") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = quantidade,
            onValueChange = { quantidade = it },
            label = { Text("Quantidade em estoque") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val precoValue = preco.toDoubleOrNull() ?: -1.0
                val quantidadeValue = quantidade.toIntOrNull() ?: -1

                if (nome.isEmpty() || categoria.isEmpty() || precoValue < 0 || quantidadeValue < 1) {
                    Toast.makeText(context, "Todos os campos são obrigatórios e devem ser válidos. Preço >= 0 e Quantidade >= 1.", Toast.LENGTH_SHORT).show()
                } else {
                    // Produto válido, adicionando ao estoque
                    val produto = Produto(
                        nome = nome,
                        categoria = categoria,
                        preco = precoValue,
                        quantidadeEmEstoque = quantidadeValue
                    )
                    Estoque.adicionarProduto(produto)
                    Toast.makeText(context, "Produto cadastrado com sucesso!", Toast.LENGTH_SHORT).show()

                    nome = ""
                    categoria = ""
                    preco = ""
                    quantidade = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cadastrar")
        }


        Spacer(modifier = Modifier.height(25.dp))


        Button(
            onClick = {
                navController.navigate("lista")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Listar produtos")
        }

    }
}

@Composable
fun ListaProdutosScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Lista de Produtos")

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(Estoque.getListaProdutos()) { produto ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${produto.nome} (${produto.quantidadeEmEstoque} unidades)")

                    Button(onClick = {
                        val produtoJson = Gson().toJson(produto)
                        navController.navigate("detalhes/$produtoJson")
                    }) {
                        Text("Detalhes")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = {
                navController.navigate("estatisticas")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Estatísticas")
        }
    }
}


@Composable
fun DetalhesProdutoScreen(produtoJson: String, navController: NavController) {
    val produto = Gson().fromJson(produtoJson, Produto::class.java)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Nome: ${produto.nome}")
        Text("Categoria: ${produto.categoria}")
        Text("Preço: R$ ${produto.preco}")
        Text("Quantidade em Estoque: ${produto.quantidadeEmEstoque} unidades")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Voltar")
        }
    }
}

@Composable
fun EstatisticasScreen(navController: NavHostController) {
    val valorTotal = Estoque.calcularValorTotalEstoque()
    val quantidadeTotal = Estoque.calcularQuantidadeTotalProdutos()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Estatísticas do Estoque")

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Valor Total do Estoque: R$ ${"%.2f".format(valorTotal)}")
        Text(text = "Quantidade Total de Produtos: $quantidadeTotal unidades")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Voltar")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewCadastro() {
    CadastroProdutoScreen(rememberNavController())
}

@Preview(showBackground = true)
@Composable
fun PreviewLista() {
    ListaProdutosScreen(rememberNavController())
}
