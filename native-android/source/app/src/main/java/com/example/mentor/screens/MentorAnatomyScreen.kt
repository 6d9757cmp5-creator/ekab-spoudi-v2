package com.example.mentor.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mentor.models.AnatomyOrgan
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MentorAnatomyScreen(
    organs: List<AnatomyOrgan>
) {
    var isMaleModel by remember { mutableStateOf(true) }
    var selectedSystem by remember { mutableStateOf("Όλα") }
    var selectedOrgan by remember { mutableStateOf<AnatomyOrgan?>(organs.firstOrNull()) }

    var rotationX by remember { mutableFloatStateOf(0f) }
    var rotationY by remember { mutableFloatStateOf(0f) }
    var zoomScale by remember { mutableFloatStateOf(1f) }

    val filteredOrgans = remember(organs, isMaleModel, selectedSystem) {
        organs.filter { organ ->
            val matchesSex = when (organ.sexAvailability) {
                "Κοινό" -> true
                "Ανδρικό" -> isMaleModel
                "Γυναικείο" -> !isMaleModel
                else -> true
            }
            val matchesSys = if (selectedSystem == "Όλα") true else organ.system == selectedSystem
            matchesSex && matchesSys
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("mentor_anatomy_screen")
    ) {
        // Controls Header: Sex Switcher & System Filter
        Surface(tonalElevation = 2.dp, color = MaterialTheme.colorScheme.surface) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Sex switcher row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ανατομικό Μοντέλο:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    SingleChoiceSegmentedButtonRow {
                        SegmentedButton(
                            selected = isMaleModel,
                            onClick = { isMaleModel = true },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                        ) {
                            Text("Ανδρικό ♂", fontSize = 12.sp)
                        }
                        SegmentedButton(
                            selected = !isMaleModel,
                            onClick = { isMaleModel = false },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                        ) {
                            Text("Γυναικείο ♀", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // System Filter Chips
                val systems = listOf("Όλα", "Σκελετικό", "Σπλαχνικό", "Κυκλοφορικό", "Νευρικό")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(systems) { sys ->
                        val isSelected = selectedSystem == sys
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedSystem = sys },
                            label = { Text(sys, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }
        }

        // 3D Canvas Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(EkabNavyDark)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        rotationY += dragAmount.x * 0.6f
                        rotationX = (rotationX - dragAmount.y * 0.4f).coerceIn(-45f, 45f)
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val radY = Math.toRadians(rotationY.toDouble())
                val cosY = cos(radY).toFloat()
                val sinY = sin(radY).toFloat()

                // Draw background grid lines for 3D lab feel
                val gridColor = Color(0xFF1E3A4C)
                for (i in -4..4) {
                    val yLine = cy + i * 30f
                    drawLine(gridColor, Offset(0f, yLine), Offset(size.width, yLine), strokeWidth = 1f)
                }

                // Render 3D Body Wireframe
                val headX = cx + sinY * 10f
                val headY = cy - 80f * zoomScale
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF64B5F6), Color(0xFF1565C0).copy(alpha = 0.4f)),
                        center = Offset(headX, headY),
                        radius = 28f * zoomScale
                    ),
                    radius = 24f * zoomScale,
                    center = Offset(headX, headY)
                )

                // Torso
                val chestTopY = headY + 30f * zoomScale
                val pelvisY = cy + 40f * zoomScale
                drawLine(
                    color = Color(0xFF90CAF9),
                    start = Offset(headX, chestTopY),
                    end = Offset(cx, pelvisY),
                    strokeWidth = 4f * zoomScale
                )

                // Spine dots (AMSS and dorsal)
                for (s in 1..7) {
                    val sy = chestTopY + s * 14f * zoomScale
                    val sx = cx + (sinY * (s * 3f))
                    drawCircle(
                        color = Color(0xFFFFD54F),
                        radius = 3.5f * zoomScale,
                        center = Offset(sx, sy)
                    )
                }

                // Ribcage / Chest arcs
                for (r in 1..4) {
                    val ry = chestTopY + r * 16f * zoomScale
                    val rx = 40f * zoomScale * cosY
                    drawCircle(
                        color = Color(0xFF42A5F5).copy(alpha = 0.5f),
                        radius = rx.coerceAtLeast(15f),
                        center = Offset(cx, ry),
                        style = Stroke(width = 2f)
                    )
                }

                // Limbs
                // Arms
                drawLine(
                    color = Color(0xFF64B5F6),
                    start = Offset(cx - 35f * cosY, chestTopY + 10f),
                    end = Offset(cx - 65f * cosY, cy + 20f * zoomScale),
                    strokeWidth = 3f
                )
                drawLine(
                    color = Color(0xFF64B5F6),
                    start = Offset(cx + 35f * cosY, chestTopY + 10f),
                    end = Offset(cx + 65f * cosY, cy + 20f * zoomScale),
                    strokeWidth = 3f
                )

                // Pelvis ring
                drawCircle(
                    color = Color(0xFFFFB74D),
                    radius = 32f * zoomScale * cosY.coerceAtLeast(0.3f),
                    center = Offset(cx, pelvisY),
                    style = Stroke(width = 3f)
                )

                // Legs
                drawLine(
                    color = Color(0xFF64B5F6),
                    start = Offset(cx - 20f * cosY, pelvisY),
                    end = Offset(cx - 35f * cosY, cy + 110f * zoomScale),
                    strokeWidth = 4f
                )
                drawLine(
                    color = Color(0xFF64B5F6),
                    start = Offset(cx + 20f * cosY, pelvisY),
                    end = Offset(cx + 35f * cosY, cy + 110f * zoomScale),
                    strokeWidth = 4f
                )

                // Highlight selected organ marker
                val markerY = when (selectedOrgan?.id) {
                    "brain" -> headY
                    "heart" -> chestTopY + 25f * zoomScale
                    "lungs" -> chestTopY + 20f * zoomScale
                    "cervical-spine" -> chestTopY + 8f * zoomScale
                    "spleen" -> chestTopY + 45f * zoomScale
                    "liver" -> chestTopY + 42f * zoomScale
                    "pelvis" -> pelvisY
                    "uterus" -> pelvisY + 10f * zoomScale
                    else -> cy
                }
                val markerX = cx + (sinY * 18f)

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(EkabRed, Color.Transparent),
                        center = Offset(markerX, markerY),
                        radius = 24f
                    ),
                    radius = 18f,
                    center = Offset(markerX, markerY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 5f,
                    center = Offset(markerX, markerY)
                )
            }

            // Overlay 3D guidance & Reset
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "↻ Σύρετε για περιστροφή 360° (${rotationY.toInt()}°)",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilledTonalIconButton(
                        onClick = {
                            rotationX = 0f
                            rotationY = 0f
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.RestartAlt, contentDescription = "Επαναφορά", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Organ Details and Selection Card
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Selected Organ Clinical Card
            selectedOrgan?.let { organ ->
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = organ.name,
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${organ.latinName} · ${organ.system}",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (organ.sexAvailability == "Γυναικείο") EkabRedContainer else MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = organ.sexAvailability,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (organ.sexAvailability == "Γυναικείο") EkabOnRedContainer else MaterialTheme.colorScheme.onPrimaryContainer
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Ανατομική Θέση: ${organ.location}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Φυσιολογία: ${organ.physiologicalFunction}",
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(EkabRedContainer.copy(alpha = 0.5f))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(imageVector = Icons.Filled.Warning, contentDescription = null, tint = EkabRed, modifier = Modifier.size(16.dp))
                                        Text(
                                            text = "Συσχέτιση με Προνοσοκομειακό Τραύμα (ΕΚΑΒ):",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = EkabOnRedContainer
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = organ.clinicalTraumaCorrelation,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurface,
                                            lineHeight = 17.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Organ Chips
            item {
                Text(
                    text = "Επιλογή Ανατομικής Δομής (${filteredOrgans.size})",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            items(filteredOrgans) { org ->
                val isSelected = selectedOrgan?.id == org.id
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedOrgan = org }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = org.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = org.system, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        }
                        Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
