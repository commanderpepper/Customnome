package commanderpepper.customnome.feature.info

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun InfoScreen(modifier: Modifier = Modifier){
    Column {
        Text(text = "Customnome", fontSize = 20.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun InfoScreenPreview(){
    InfoScreen()
}