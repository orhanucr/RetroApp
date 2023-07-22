package com.example.retroapp.presentation.detail

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.retroapp.R
import com.example.retroapp.navigation.ROUTE_HOME
import com.google.firebase.Timestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel?,
    isDetail:Boolean?, navController: NavHostController,
    noteId: String
) {
    LaunchedEffect(key1 = true){
        if (isDetail == true){
            viewModel?.getNote(noteId)!!
        }
    }
    val activity = LocalContext.current as? ComponentActivity
    val parentOptions = listOf("Teknik Karar Toplantısı", "Retro Toplantısı", "Cluster Toplantısı")
    val selectedOption =
        remember { mutableStateOf(parentOptions[0]) } //Seçilen toplantı türünü tutuyor
    val title = rememberSaveable() { mutableStateOf("") }
    val detail = rememberSaveable() { mutableStateOf("") }
    val selectedImageUris = remember {
        mutableStateOf<List<Uri>>(emptyList())
    }
    val contextForToast = LocalContext.current.applicationContext

    Scaffold(
        topBar = {
            TopBar(isDetail = isDetail ?: false, onBackClick = { navController.popBackStack() })
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .background(color = Color.White)
                .padding(contentPadding)
        ) {
            Column(
                modifier = Modifier
                    .align(CenterHorizontally)
                    .padding(20.dp, 5.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = CenterHorizontally
            ) {
                if (isDetail == true){
                    OutlinedTextField(
                        value = viewModel?.note!!.title,
                        onValueChange = { viewModel.onTitleChange(it) },
                        label = { Text("Title", color = Color.Black) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(1.dp)
                    )
                    Spacer(modifier = Modifier.height(7.dp))
                    selectedOption.value = viewModel.note.type
                    DisplaySpinner(selectedOption, parentOptions)

                    OutlinedTextField(
                        value = viewModel.note.description,
                        onValueChange = { viewModel.onDetailChange(it) },
                        label = { Text("Detail", color = Color.Black) },
                        modifier = Modifier
                            .fillMaxWidth(),
                        maxLines = 6,
                    )
                } else{
                    OutlinedTextField(
                        value = title.value,
                        onValueChange = { title.value = it },
                        label = { Text("Title", color = Color.Black) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(1.dp)
                    )
                    Spacer(modifier = Modifier.height(7.dp))
                    DisplaySpinner(selectedOption, parentOptions)

                    OutlinedTextField(
                        value = detail.value,
                        onValueChange = { detail.value = it },
                        label = { Text("Detail", color = Color.Black) },
                        modifier = Modifier
                            .fillMaxWidth(),
                        maxLines = 6,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .padding(0.5.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = CenterHorizontally
            ) {
                if (isDetail == true){
                    val selectedImages = arrayListOf<Uri>()
                    viewModel?.note!!.images?.forEach {
                        selectedImages.add(Uri.parse(it))
                    }
                    PickImageFromGallery(viewModel)
                    Button(
                        onClick = {
                                val images = arrayListOf<String>()
                                selectedImageUris.value.forEach { uri -> images.add(uri.toString()) }
                            Log.d("title", title.value)
                                viewModel.updateNote(
                                    viewModel.note.title,
                                    viewModel.note.description,
                                    viewModel.note.id,
                                    viewModel.note.images,
                                    selectedOption.value
                                ) {
                                    navController.navigate(ROUTE_HOME)
                                    Toast.makeText(
                                        contextForToast,
                                        "Note succesfully updated",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                        },
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth(1F),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.blue),
                            contentColor = Color.White
                        )
                    ) {
                        AnimatedVisibility(visible = isDetail) {
                            Text(text = "Update")
                        }
                        AnimatedVisibility(visible = !isDetail) {
                            Text(text = "Add")
                        }
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                } else{
                    PickImageFromGallery(viewModel)
                    Button(
                        onClick = {
                            if (title.value.isEmpty()) {
                                Toast.makeText(
                                    contextForToast,
                                    "Title cannot be empty",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else if (detail.value.isEmpty()) {
                                Toast.makeText(
                                    contextForToast,
                                    "Detail cannot be empty",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                val images = arrayListOf<String>()
                                selectedImageUris.value.forEach { uri -> images.add(uri.toString()) }
                                viewModel?.addNote(
                                    title.value,
                                    detail.value,
                                    images,
                                    Timestamp.now(),
                                    selectedOption.value,
                                    onComplete = {
                                        navController.navigate(ROUTE_HOME)
                                        Toast.makeText(
                                            contextForToast,
                                            "Note succesfully added",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    })
                            }
                        },
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth(1F),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.blue),
                            contentColor = Color.White
                        )
                    ) {
                        AnimatedVisibility(visible = isDetail!!) {
                            Text(text = "Update")
                        }
                        AnimatedVisibility(visible = !isDetail) {
                            Text(text = "Add")
                        }
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                }
            }
        }
        DisposableEffect(Unit) {
            val callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navController.navigate(ROUTE_HOME)

                }
            }

            activity?.onBackPressedDispatcher?.addCallback(callback)

            onDispose {
                callback.remove()
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplaySpinner(selectedOption: MutableState<String>, parentOptions: List<String>){
    val expandedState = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth(1F)
            .padding(1.dp)
            .clickable(onClick = { expandedState.value = true })
    ) {
        TextField(
            value = selectedOption.value,
            modifier = Modifier
                .fillMaxWidth(1F)
                .border(
                    0.5.dp, Color.DarkGray,
                    RoundedCornerShape(5.dp)
                ),
            onValueChange = {},
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Drop-down",
                    modifier = Modifier.clickable {
                        expandedState.value = !expandedState.value
                    }
                )
            },
            readOnly = true,
            textStyle = TextStyle.Default.copy(fontSize = 16.sp),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )
        DropdownMenu(
            expanded = expandedState.value,
            onDismissRequest = { expandedState.value = false },
            Modifier.background(Color.White)
        ) {
            parentOptions.forEach { option ->
                DropdownMenuItem(modifier = Modifier
                    .fillMaxWidth(1F),
                    onClick = {
                        selectedOption.value = option
                        expandedState.value = false
                        Log.d("Option", selectedOption.value)
                    }, text ={Text(text = option, fontSize = 16.sp, style = TextStyle.Default)})
                Divider()
            }
        }
    }
}
@Composable
fun PickImageFromGallery(viewModel: DetailViewModel?) {
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            val list = arrayListOf<String>()
            uris.forEach {
                list.add(it.toString())
            }
            viewModel?.onImagesChange(list)
        }
    )
    Row(modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.Start
    ) {
        LazyRow(
            modifier = Modifier
                .size(300.dp, 120.dp)
                .padding(2.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val listUri = arrayListOf<Uri>()
            if (viewModel != null) {
                viewModel.note.images?.forEach {
                    listUri.add(Uri.parse(it))
                }
            }
            items(listUri) { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp, 120.dp)
                        .padding(1.dp, 1.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
      Spacer(modifier = Modifier.width(5.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.gallery_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp, 32.dp)
                    .clickable {
                        multiplePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
            )
            Text(
                text = "Add Photo",
                textAlign = TextAlign.Start,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Black
                )
            )
        }
    }
}

@Composable
fun ClickableDetail(
    message: String,
) {
    val uriHandler = LocalUriHandler.current
    val styledMessage = textFormatter(
        text = message
    )
    ClickableText(
        text = styledMessage,
        style = MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current),
        modifier = Modifier.padding(16.dp),
        onClick = {
            styledMessage
                .getStringAnnotations(start = it, end = it)
                .firstOrNull()
                ?.let { annotation ->
                    when (annotation.tag) {
                        SymbolAnnotationType.LINK.name -> uriHandler.openUri(annotation.item)
                        else -> Unit
                    }
                }
        })
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(isDetail: Boolean, onBackClick: () -> Unit) {
    var textRes = R.string.add_screen
    if (isDetail) textRes = R.string.detail_screen

    TopAppBar(
        modifier = Modifier.background(Color.White),
        title = {
            Text(
                text = stringResource(textRes)
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    onBackClick()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )
}
@Preview(showSystemUi = true)
@Composable
fun PrevDetailScreen() {
    DetailScreen(null, isDetail = null, rememberNavController(), "")
}