package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.InviteViewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.ErrorComponent
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.LoadingComponent

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@OptIn( ExperimentalGetImage::class)
@Composable
fun InviteQRScannerBottomSheet(
    showBottomSheet: Boolean,
    onDismiss: () -> Unit,
    navController: NavController,
    viewModel: InviteViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var scanned by remember { mutableStateOf(false) }
    var showSuccessSheet by remember { mutableStateOf(false) }
    var scannedInviteId by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val successSheetState = rememberModalBottomSheetState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                Toast.makeText(context, "Permesso fotocamera negato", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(showBottomSheet) {
        if (showBottomSheet) {
            // Reset states when opening
            scanned = false
            showSuccessSheet = false
            scannedInviteId = ""

            // Check camera permission
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    // Main QR Scanner Bottom Sheet
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            dragHandle = null,

                    windowInsets = WindowInsets(0), // Rimuove gli insets automatici
            modifier = Modifier
                .fillMaxHeight(0.95f) // Oc
        ) {
            Box(modifier = Modifier
                .fillMaxSize(),
                contentAlignment = Alignment.Center

//                .fillMaxWidth()
//                .height(400.dp) // o Dimensione desiderata
//                .padding(horizontal = 16.dp)
            ) {


                // Camera Preview
                AndroidView(
                    modifier = Modifier
                        .size(500.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)

                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()

                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val scanner = BarcodeScanning.getClient()
                            val analysis = ImageAnalysis.Builder()
                                .build()
                                .also {
                                    it.setAnalyzer(
                                        ContextCompat.getMainExecutor(ctx)
                                    ) { imageProxy ->
                                        val mediaImage = imageProxy.image
                                        if (mediaImage != null && !scanned) {
                                            val image = InputImage.fromMediaImage(
                                                mediaImage,
                                                imageProxy.imageInfo.rotationDegrees
                                            )
                                            scanner.process(image)
                                                .addOnSuccessListener { barcodes ->
                                                    for (barcode in barcodes) {
                                                        barcode.rawValue?.let { inviteId ->
                                                            if (!scanned) {
                                                                scanned = true
                                                                scannedInviteId = inviteId
                                                                showSuccessSheet = true
                                                                Log.d("DEBUG", "ID invite scan: $inviteId")
                                                            }
                                                        }
                                                    }
                                                }
                                                .addOnFailureListener {
                                                    Log.e("QRCode", "Errore", it)
                                                }
                                                .addOnCompleteListener {
                                                    imageProxy.close()
                                                }
                                        } else {
                                            imageProxy.close()
                                        }
                                    }
                                }

                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    analysis
                                )
                            } catch (exc: Exception) {
                                Log.e("CameraX", "Use case binding failed", exc)
                            }

                        }, ContextCompat.getMainExecutor(ctx))

                        previewView
                    }
                )


                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box (
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 8.dp)

                    ) {
                        TextButton(
                            modifier = Modifier.align(Alignment.CenterStart),
                            onClick = { onDismiss() },

                            ) {
                            Text(
                                text = "Annulla",
                                color = Color(0xFF0058CC),
                                fontSize = 10.sp
                            )
                        }

                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = "Inquadra il codice QR",
                            color = Color(0xFF0058CC),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,

                            )
                    }


                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Overlay scuro con buco per il QR
                        Canvas(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            val canvasWidth = size.width
                            val canvasHeight = size.height

                            // Dimensioni del "buco" per il QR code
                            val scanAreaSize = 300.dp.toPx()
                            val scanAreaLeft = (canvasWidth - scanAreaSize) / 2
                            val scanAreaTop = (canvasHeight - scanAreaSize) / 2

                            // Disegna overlay scuro
                            drawRect(
                                color = Color.White,
                                size = Size(canvasWidth, canvasHeight)
                            )

                            // "Buca" il rettangolo per il QR code
                            drawRoundRect(
                                color = Color.Transparent,
                                topLeft = Offset(scanAreaLeft, scanAreaTop),
                                size = Size(scanAreaSize, scanAreaSize),
                                cornerRadius = CornerRadius(16.dp.toPx()),
                                blendMode = BlendMode.Clear
                            )
                        }

                        }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(bottom = 70.dp)
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = "Inquadra il codice sul computer del tuo medico!",
                            color = Color(0xFF666666),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }

                    }


                }
            }
        }
    if (showSuccessSheet) {
        Dialog(
            onDismissRequest = {
                showSuccessSheet = false
                scanned = false
                scannedInviteId = ""
            }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                tonalElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icona di successo
                    Surface(
                        modifier = Modifier.size(64.dp),
                        color = Color(0xFF4CAF50),
                        shape = RoundedCornerShape(32.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✓",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Titolo
                    Text(
                        text = "Codice QR riconosciuto!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Messaggio
                    Text(
                        text = "Il codice è stato scansionato con successo.\nProsegui con la registrazione.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Pulsanti
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Pulsante Annulla
                        TextButton(
                            onClick = {
                                showSuccessSheet = false
                                scanned = false
                                scannedInviteId = ""
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Annulla",
                                color = Color(0xFF666666),
                                fontSize = 16.sp
                            )
                        }

                        // Pulsante Continua
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    showSuccessSheet = false
                                    // onDismiss()
                                    kotlinx.coroutines.delay(100) // attende 100ms (puoi anche fare delay(1) per un frame)
                                    navController.navigate("register/$scannedInviteId") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }

//                                navController.navigate("register/$scannedInviteId") {
//                                    popUpTo("login") {
//                                        inclusive = true
//                                    }
//                                }
//
//                                showSuccessSheet = false
//                                onDismiss() // Chiude anche il bottom sheet principale
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0058CC),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Continua",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Stato di caricamento/errore (se necessario)
                    when {
                        uiState.isLoading -> {
                          LoadingComponent()
                        }

                        uiState.error != null -> {
                            ErrorComponent(error = uiState.error.toString())

                        }
                    }
                }
            }
        }
    }
}


