package com.cj.d_daywatch.presentation.frameworks.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import com.cj.d_daywatch.R
import com.cj.d_daywatch.presentation.frameworks.helper.DDayHelper
import com.cj.d_daywatch.presentation.frameworks.models.DDayListModel
import com.cj.d_daywatch.presentation.theme.DDayWatchTheme


@Composable
fun MainView(){
    val context = LocalContext.current
    val helper = DDayHelper(context)
    var showProgress by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {
        helper.getList()

        showProgress = false
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.surface) {
        if(showProgress){
            CircularProgressIndicator()
        } else{
            if(!DDayHelper.dDayList.isEmpty()){
                LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.padding(10.dp)) {
                    items(DDayHelper.dDayList){dDay ->
                        DDayListModel(data = dDay) {

                        }
                    }
                }
            } else{
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Rounded.Add, contentDescription = null, tint = Color.White)
                    Text(text = stringResource(id = R.string.TXT_ADD_D_DAY_BY_PHONE), color = Color.White, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Preview(device = "id:wearos_small_round")
@Composable
fun MainViewPreview(){
    MainView()
}