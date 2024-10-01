package br.edu.ifpb.pdm.yuri.consultacep

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.ifpb.pdm.yuri.consultacep.ui.theme.ConsultaCEPTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConsultaCEPTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ConsultaCEPScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ConsultaCEPScreen(modifier: Modifier = Modifier) {
    var cep by remember { mutableStateOf("") }
    var endereco by remember { mutableStateOf<Endereco?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = cep,
            onValueChange = { cep = it },
            label = { Text("Digite o CEP") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                isLoading = true
                coroutineScope.launch(Dispatchers.IO) {
                    buscarEndereco(cep) { result ->
                        isLoading = false
                        if (result != null) {
                            endereco = result
                        } else {
                            Toast.makeText(context, "Erro ao buscar o CEP", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buscar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Text("Buscando...")
        } else if (endereco != null) {
            endereco?.let {
                Text("CEP: ${it.cep}")
                Text("Logradouro: ${it.logradouro}")
                Text("Bairro: ${it.bairro}")
                Text("Cidade: ${it.localidade}")
                Text("Estado: ${it.uf}")
            }
        }
    }
}

fun buscarEndereco(cep: String, callback: (Endereco?) -> Unit) {
    val service = RetrofitClient.enderecoService
    val call = service.buscarEndereco(cep)

    call.enqueue(object : Callback<Endereco> {
        override fun onResponse(call: Call<Endereco>, response: Response<Endereco>) {
            if (response.isSuccessful) {
                callback(response.body())
            } else {
                callback(null)
            }
        }

        override fun onFailure(call: Call<Endereco>, t: Throwable) {
            callback(null)
        }
    })
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ConsultaCEPTheme {
        ConsultaCEPScreen()
    }
}
