package com.example.logistic_app.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.logistic_app.R
import com.example.logistic_app.data.model.Dispatch
import com.example.logistic_app.ui.components.MapPlaceholder
import com.example.logistic_app.ui.theme.*
import com.example.logistic_app.ui.viewmodel.AuthViewModel

@Composable
fun DispatchScreen(
    authViewModel: AuthViewModel,
    onConfirmDelivery: () -> Unit,
    onContactSupport: () -> Unit,
    onStopOver: () -> Unit,
    onReportDelay: () -> Unit,
    onExpandMap: () -> Unit
) {
    val user by authViewModel.user.collectAsState()
    val personnel by authViewModel.personnel.collectAsState()
    val activeDispatch by authViewModel.activeDispatch.collectAsState()
    val context = LocalContext.current
    
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var snapTrigger by remember { mutableIntStateOf(0) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasLocationPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    
    val displayName = personnel?.fullName ?: user?.displayName ?: user?.email?.substringBefore("@")?.split(".", "_", "-")?.joinToString(" ") { 
        it.replaceFirstChar { char -> char.uppercase() } 
    } ?: "User"
    
    val initials = personnel?.initials ?: displayName.split(" ").filter { it.isNotEmpty() }.take(2).map { it.first().uppercase() }.joinToString("")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBackground)
            .padding(16.dp)
    ) {
        // User Header
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            color = Color.Transparent
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(NavyBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(initials, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        displayName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = TextPrimary
                    )
                    Text(
                        "${personnel?.rank ?: "Active Driver"} • ${personnel?.username ?: user?.uid?.take(7)?.uppercase() ?: "DRV-001"}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                
                if (activeDispatch != null && activeDispatch?.status != "Pending") {
                    IconButton(
                        onClick = onContactSupport,
                        modifier = Modifier
                            .background(NavyBlue.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.Chat, contentDescription = "Support", tint = NavyBlue)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                IconButton(
                    onClick = { snapTrigger++ },
                    modifier = Modifier
                        .background(NavyBlue.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(Icons.Rounded.MyLocation, contentDescription = "Locate Me", tint = NavyBlue)
                }
            }
        }

        if (activeDispatch == null) {
            Text(
                "NO ACTIVE DISPATCH",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
            )
            MapPlaceholder(
                modifier = Modifier.weight(1f),
                text = "Waiting for admin dispatch...",
                showUserLocation = hasLocationPermission,
                snapToUserLocation = snapTrigger,
                onMapClick = onExpandMap
            )
        } else if (activeDispatch?.status == "Pending") {
            DispatchConfirmation(
                dispatch = activeDispatch!!,
                onAccept = { authViewModel.updateDispatchStatus("Ongoing") }
            )
        } else {
            ActiveDispatchContent(
                dispatch = activeDispatch!!,
                authViewModel = authViewModel,
                onConfirmDelivery = onConfirmDelivery,
                onStopOver = onStopOver,
                onReportDelay = onReportDelay,
                hasLocationPermission = hasLocationPermission,
                snapTrigger = snapTrigger,
                onExpandMap = onExpandMap
            )
        }
    }
}

@Composable
fun DispatchConfirmation(
    dispatch: Dispatch,
    onAccept: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = OngoingBlue.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_truck),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(OngoingBlue),
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "New Dispatch",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                
                Text(
                    "ID: ${dispatch.dispatchId}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OngoingBlue,
                    modifier = Modifier
                        .background(OngoingBlue.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .padding(top = 8.dp)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Column(
                    modifier = Modifier.fillMaxWidth(), 
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ModernInfoRow(label = "Target Destination", value = dispatch.location.label)
                    ModernInfoRow(label = "Assigned Truck", value = dispatch.truck)
                    ModernInfoRow(label = "Payload", value = "${dispatch.supplies.size} Item(s)")
                }
                
                Spacer(modifier = Modifier.height(40.dp))
                
                Button(
                    onClick = onAccept,
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(Icons.Rounded.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("ACCEPT DISPATCH", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
fun ModernInfoRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        Text(
            value, 
            color = TextPrimary, 
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
fun ColumnScope.ActiveDispatchContent(
    dispatch: Dispatch,
    authViewModel: AuthViewModel,
    onConfirmDelivery: () -> Unit,
    onStopOver: () -> Unit,
    onReportDelay: () -> Unit,
    hasLocationPermission: Boolean,
    snapTrigger: Int,
    onExpandMap: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = CircleShape,
                        color = OngoingBlueLight
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_truck),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(OngoingBlue),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Current Dispatch",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                }
                Surface(
                    color = if (dispatch.status == "Ongoing") OngoingBlue else ReportedOrange,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        dispatch.status.uppercase(),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Dispatch ID", color = TextSecondary, fontSize = 11.sp)
                    Text(dispatch.dispatchId, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Truck Unit", color = TextSecondary, fontSize = 11.sp)
                    Text(dispatch.truck, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EnhancedActionChip(
                    label = "Delivered",
                    color = DeliveredGreen,
                    bgColor = DeliveredGreenLight,
                    onClick = onConfirmDelivery,
                    modifier = Modifier.weight(1f)
                )
                EnhancedActionChip(
                    label = "Stop Over",
                    color = EmergencyRed,
                    bgColor = EmergencyRedLight,
                    onClick = onStopOver,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            EnhancedActionChip(
                label = "Report Delay",
                color = StopOverYellow,
                bgColor = StopOverYellowLight,
                onClick = onReportDelay,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.Navigation,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = TextSecondary
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text("TARGET: ${dispatch.location.label.uppercase()}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
    }

    MapPlaceholder(
        modifier = Modifier.weight(1f),
        text = dispatch.location.label,
        latitude = dispatch.location.lat,
        longitude = dispatch.location.lng,
        showUserLocation = hasLocationPermission,
        snapToUserLocation = snapTrigger,
        onMapClick = onExpandMap,
        onLocationUpdated = { lat, lng ->
            authViewModel.updateLiveLocation(lat, lng)
        }
    )
}

@Composable
fun EnhancedActionChip(
    label: String,
    color: Color,
    bgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.height(44.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = color
                )
            }
        }
    }
}
