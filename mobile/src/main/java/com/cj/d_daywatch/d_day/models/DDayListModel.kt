package com.cj.d_daywatch.d_day.models

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cj.d_daywatch.d_day.ui.calculateForPreview
import com.cj.d_daywatch.frameworks.ui.convertIntToColor
import com.cj.d_daywatch.ui.theme.gray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

fun calculateDate(selectedDate: String, setOneDay: Boolean): Long {
    val dateFormatter = SimpleDateFormat("yyyy. MM. dd.", Locale.KOREA)
    val current = dateFormatter.parse(
        dateFormatter.format(Date())
    )

    val selected = dateFormatter.parse(selectedDate)

    if (current != null && selected != null) {
        val diff = current.time - selected.time

        return if (!setOneDay) TimeUnit.DAYS.convert(
            diff, TimeUnit.MILLISECONDS
        ) else TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1
    }

    return 0L
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DDayListModel(data: DDayDataModel, onClickStartSource: () -> Unit){
    Card(onClick = { onClickStartSource() }, colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
        Box(
            modifier = Modifier
                .width(250.dp)
                .height(250.dp)
                .padding(10.dp)
                .border(1.dp, gray, RoundedCornerShape(15.dp))
                .background(
                    if (data.backgroundType == 0) convertIntToColor(
                        data.backgroundColor ?: 0
                    ) else Color.Transparent,
                    RoundedCornerShape(15.dp)
                )
        ) {
            if (data.image != null && data.backgroundType == 1) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(data.image).build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(15.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(
                    Alignment.Center
                )
            ) {
                Text(text = data.title, color = if(data.textColor == 0) Color.White else Color.Black)

                AnimatedVisibility(visible = data.date != ""){
                    val diff = calculateDate(data.date, data.setBaseDayToOneDay)
                    Text(
                        text = if (diff >= 0) "D+${diff}" else "D${diff}",
                        color = if(data.textColor == 0) Color.White else Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

        }
    }

}

@Preview
@Composable
fun DDayListModelPreview(){
    DDayListModel(data = DDayDataModel("", "D-Day", "2024. 03. 01.", true, 0, null, 0, 0), onClickStartSource = {

    })
}