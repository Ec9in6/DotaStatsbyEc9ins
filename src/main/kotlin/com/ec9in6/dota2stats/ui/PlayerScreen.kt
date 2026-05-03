package com.ec9in6.dota2stats.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainContent(
    viewModel: PlayerViewModel,
    onThemeChange: (Color) -> Unit,
) {
    var showWelcome by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = showWelcome,
            exit = slideOutVertically(animationSpec = tween(500)) + fadeOut(animationSpec = tween(500)),
        ) {
            WelcomeScreen { showWelcome = false }
        }

        AnimatedVisibility(
            visible = !showWelcome,
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(500)) + fadeIn(animationSpec = tween(500)),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                ThemeSelector(onThemeChange)
                PlayerScreen(viewModel)
            }
        }
    }
}

@Composable
fun ThemeSelector(onThemeChange: (Color) -> Unit) {
    val themes =
        listOf(
            Color(0xFFD0BCFF),
            Color(0xFF81C784),
            Color(0xFF64B5F6),
            Color(0xFFE57373),
            Color(0xFFFFB74D),
        )

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("Палитра: ", style = MaterialTheme.typography.labelMedium)
        themes.forEach { color ->
            Box(
                modifier =
                    Modifier
                        .padding(horizontal = 6.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(color)
                        .clickable { onThemeChange(color) },
            )
        }
    }
}

@Composable
fun WelcomeScreen(onStartClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            "Dota Stats by Ec9ins",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Приложение для быстрого поиска статистики игроков.\nУзнай ранг, винрейт, команду и сигнатурных героев по Account ID.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onStartClick,
            modifier = Modifier.height(56.dp).fillMaxWidth(0.5f),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text("Начать", fontSize = 18.sp)
        }
    }
}

@Composable
fun PlayerScreen(viewModel: PlayerViewModel) {
    val state by viewModel.uiState.collectAsState()
    var searchId by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(0.8f),
        ) {
            OutlinedTextField(
                value = searchId,
                onValueChange = { searchId = it },
                label = { Text("Account ID") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = { viewModel.searchPlayer(searchId) },
                modifier = Modifier.height(56.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Search")
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        AnimatedContent(
            targetState = state,
            transitionSpec = {
                fadeIn(tween(400)) + scaleIn(initialScale = 0.9f) togetherWith fadeOut(tween(400))
            },
        ) { targetState ->
            when (targetState) {
                is PlayerScreenState.Idle -> Text("Введите ID для начала", color = Color.Gray)
                is PlayerScreenState.Loading -> CircularProgressIndicator()
                is PlayerScreenState.Error -> Text(targetState.message, color = MaterialTheme.colorScheme.error)
                is PlayerScreenState.Success -> PlayerCard(targetState, viewModel)
            }
        }
    }
}

@Composable
fun PlayerCard(
    state: PlayerScreenState.Success,
    viewModel: PlayerViewModel,
) {
    val profile = state.player.profile

    Card(
        modifier = Modifier.fillMaxWidth(0.9f),
        shape = RoundedCornerShape(24.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            state.avatar?.let {
                Image(
                    bitmap = it,
                    contentDescription = null,
                    modifier = Modifier.size(120.dp).clip(CircleShape),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val displayName = profile?.proName ?: profile?.personaName ?: "Unknown"
            Text(
                text = displayName,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
            )

            if (profile?.isPro == true || profile?.teamTag != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        text = "PRO ${if (profile.teamTag != null) "| ${profile.teamTag}" else ""}",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Регион: ${profile?.locCountryCode ?: "N/A"}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 20.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatItem("Победы", state.winLoss.win.toString(), Color(0xFF4CAF50))
                StatItem("Поражения", state.winLoss.lose.toString(), Color(0xFFF44336))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            ) {
                Text(
                    viewModel.getRankName(state.player.rankTier).uppercase(),
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            if (state.topHeroes.isNotEmpty()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 20.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                )
                Text(
                    "СИГНАТУРНЫЕ ГЕРОИ",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.primary,
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    state.topHeroes.forEach { hero ->
                        HeroStatItem(hero)
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    color: Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun HeroStatItem(hero: HeroStat) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 6.dp)) {
        Text(hero.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        Text("${hero.games} матчей", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            "${hero.winRate}% WR",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (hero.winRate >= 50) Color(0xFF4CAF50) else Color(0xFFF44336),
        )
    }
}
