package com.venus.xiaohongshu.ui.publish.album

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.venus.xiaohongshu.R
import com.venus.xiaohongshu.ui.publish.viewmodel.PublishViewModel

/**
 * Description:
 *
 * @author: poboll
 * @date: 2024/05/24
 */
@Composable
fun AlbumPage(
    topPadding: Dp,
    vm: PublishViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(top = topPadding)
    ) { 
        AlbumTopBar()

        vm.albumController?.let {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3)
            ) {
                itemsIndexed(items = it.albumList) { index, item ->
                    var selected by remember {
                        mutableStateOf(false)
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .aspectRatio(1f)
                            .padding(1.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        AsyncImage(
                            model = item,
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                        RadioButton(
                            selected = selected,
                            onClick = {
                                selected = !selected
                                if (selected) {
                                    vm.addSelect(item)
                                } else {
                                    vm.removeSelect(item)
                                }
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = colorResource(R.color.theme_red),
                                unselectedColor = colorResource(R.color.theme_text_gray)
                            )
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun AlbumTopBar() {
    val context = LocalContext.current
    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
    ) {
        val (closeRef, titleRef) = remember { createRefs() }
        AsyncImage(
            model = R.drawable.icon_close_white,
            contentDescription = null,
            modifier = Modifier.constrainAs(closeRef) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .size(24.dp)
                .clickable {
                    (context as Activity).finish()
                }
        )
        Text(
            text = "全部",
            color = Color.White,
            modifier = Modifier.constrainAs(titleRef) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
        )
    }
}