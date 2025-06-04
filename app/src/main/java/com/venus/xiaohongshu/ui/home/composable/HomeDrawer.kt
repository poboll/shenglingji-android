package com.venus.xiaohongshu.ui.home.composable

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.venus.xiaohongshu.R
import com.venus.xiaohongshu.ui.settings.SettingsActivity

/**
 * Description:
 *
 * @author: poboll
 * @date: 2024/05/26
 */
@Composable
@Preview
fun HomeDrawer() {
    var showCommunityDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    Scaffold(
        modifier = Modifier.width(300.dp),
        containerColor = colorResource(R.color.theme_background_gray)
    ) {  innerPadding ->
        Column(
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding(), start = 8.dp)
        ) {
            Column(
                modifier = Modifier.width(250.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
            ) { 
                HorizontalIconTextWidget(
                    icon = R.drawable.icon_add_friend,
                    text = "发现好友"
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier.width(250.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
            ) {
                HorizontalIconTextWidget(
                    icon = R.drawable.icon_person_edit,
                    text = "创作者中心"
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier.width(250.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
            ) {
                HorizontalIconTextWidget(
                    icon = R.drawable.icon_caogao,
                    text = "我的草稿"
                )
                HorizontalIconTextWidget(
                    icon = R.drawable.icon_pinglun,
                    text = "我的评论"
                )
                HorizontalIconTextWidget(
                    icon = R.drawable.icon_clock,
                    text = "浏览记录"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier.width(250.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
            ) {
                HorizontalIconTextWidget(
                    icon = R.drawable.icon_order,
                    text = "订单"
                )
                HorizontalIconTextWidget(
                    icon = R.drawable.icon_shopping_car,
                    text = "购物车"
                )
                HorizontalIconTextWidget(
                    icon = R.drawable.icon_wallet,
                    text = "钱包"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier.width(250.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
            ) {
                HorizontalIconTextWidget(
                    icon = R.drawable.icon_leaf,
                    text = "社区公约",
                    onClick = { showCommunityDialog = true }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.width(250.dp)
            ) {
                VerticalIconTextWidget(
                    icon = R.drawable.icon_scan,
                    text = "扫一扫",
                    modifier = Modifier.weight(1f)
                )

                VerticalIconTextWidget(
                    icon = R.drawable.icon_scan,
                    text = "帮助与客服",
                    modifier = Modifier.weight(1f)
                )

                VerticalIconTextWidget(
                    icon = R.drawable.icon_setting,
                    text = "设置",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val intent = Intent(context, SettingsActivity::class.java)
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
    
    // 社区公约弹窗
    if (showCommunityDialog) {
        CommunityGuidelinesDialog(
            onDismiss = { showCommunityDialog = false }
        )
    }
}

@Composable
fun HorizontalIconTextWidget(
    icon: Int,
    text: String,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            )
    ) {
        AsyncImage(
            model = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = text,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun VerticalIconTextWidget(
    icon: Int,
    text: String,
    modifier: Modifier,
    onClick: (() -> Unit)? = null
) {
    Column (
        modifier = modifier
            .padding(16.dp)
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = text,
            fontSize = 10.sp
            
        )
    }
}

@Composable
fun CommunityGuidelinesDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .width(320.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
                // 标题
                Text(
                    text = "生灵集社区公约",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 内容
                Text(
                    text = "欢迎来到生灵集！为了营造一个和谐、友善的生物爱好者社区，请遵守以下公约：\n\n" +
                            "🌿 尊重自然，保护生物多样性\n" +
                            "📸 分享真实的生物观察记录\n" +
                            "🤝 友善交流，互相学习\n" +
                            "🚫 禁止发布虚假信息\n" +
                            "🌱 倡导环保理念，传播科学知识\n" +
                            "❤️ 关爱动植物，拒绝伤害行为\n\n" +
                            "让我们一起守护这个美丽的生物世界！",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Start
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "我已了解",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
            }
        }
    }
}