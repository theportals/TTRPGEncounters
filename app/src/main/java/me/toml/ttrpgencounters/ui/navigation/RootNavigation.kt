package me.toml.ttrpgencounters.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.*
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import me.toml.ttrpgencounters.R
import me.toml.ttrpgencounters.ui.screens.*
import me.toml.ttrpgencounters.ui.viewmodels.RootNavigationViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RootNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val viewModel: RootNavigationViewModel = viewModel()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            if (currentDestination?.hierarchy?.none { it.route == Routes.launchNavigation.route || it.route == Routes.splashScreen.route } == true) {
                TopAppBar {
                    val dest = currentDestination.route
                    when (dest) {
                        Routes.encounters.route, Routes.characters.route -> {
                            IconButton(onClick = { scope.launch { scaffoldState.drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu button")
                            }
                        }
                        else -> {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        }
                    }
                    Text(text = "TTRPG Encounters")
                }
            }
        },
        drawerContent = {
            if (currentDestination?.hierarchy?.none { it.route == Routes.launchNavigation.route || it.route == Routes.splashScreen.route } == true) {
                DropdownMenuItem(onClick = {
                    scope.launch {
                        scaffoldState.drawerState.snapTo(DrawerValue.Closed)
                    }
                    navController.navigate(Routes.encountersNavigation.route) {
                        popUpTo(0)
                    }
                }) {
                    Text(text = "Encounters")
                }

                DropdownMenuItem(onClick = {
                    scope.launch {
                        scaffoldState.drawerState.snapTo(DrawerValue.Closed)
                    }
                    navController.navigate(Routes.charactersNavigation.route) {
                        popUpTo(0)
                    }
                }) {
                    Text(text = "Characters")
                }

                DropdownMenuItem(onClick = {
                    viewModel.signOutUser()
                    scope.launch {
                        scaffoldState.drawerState.snapTo(DrawerValue.Closed)
                    }
                    navController.navigate(Routes.launchNavigation.route) {
                        popUpTo(0)
                    }
                }) {
                    Icon(Icons.Default.ExitToApp, "Logout")
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Logout")
                }
            }
        },
        floatingActionButton = {
            if (currentDestination?.route == Routes.encounters.route) {
                FloatingActionButton(onClick = { navController.navigate(Routes.editEncounter.route) }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add encounter")
                }
            }
            if (currentDestination?.route == Routes.characters.route) {
                FloatingActionButton(onClick = { navController.navigate(Routes.editCharacter.route) }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add character")
                }
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxHeight()) {
//            Image(
//                painterResource(id = R.drawable.parchmentbackground),
//                contentDescription = "Parchment background",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier.matchParentSize()
//            )
            NavHost(
                navController = navController,
                startDestination = Routes.splashScreen.route,
                modifier = Modifier.padding(paddingValues = it)
            ) {
                navigation(
                    route = Routes.launchNavigation.route,
                    startDestination = Routes.launch.route
                ) {
                    composable(route = Routes.launch.route) { LaunchScreen(navController) }
                    composable(route = Routes.signIn.route) { SignInScreen(navController) }
                    composable(route = Routes.signUp.route) { SignUpScreen(navController) }
                }
                navigation(
                    route = Routes.encountersNavigation.route,
                    startDestination = Routes.encounters.route
                ) {
                    composable(
                        route = "editencounter?id={id}",
                        arguments = listOf(navArgument("id") { defaultValue = "new" })
                    ) { navBackStackEntry ->
                        EncountersModificationScreen(
                            navController,
                            navBackStackEntry.arguments?.get("id").toString()
                        )
                    }
                    composable(
                        route = Routes.runEncounter.route + "?id={id}",
                        arguments = listOf(navArgument("id") { defaultValue = "null" })
                    ) { navBackStackEntry ->
                        RunEncounterScreen(
                            navController,
                            navBackStackEntry.arguments?.get("id").toString()
                        )
                    }
                    composable(route = Routes.encounters.route) { EncountersScreen(navController) }
                }
                navigation(
                    route = Routes.charactersNavigation.route,
                    startDestination = Routes.characters.route
                ) {
                    composable(
                        route = "editcharacter?id={id}",
                        arguments = listOf(navArgument("id") { defaultValue = "new" })
                    ) { navBackStackEntry ->
                        CharactersModificationScreen(
                            navController,
                            navBackStackEntry.arguments?.get("id").toString()
                        )
                    }
                    composable(route = Routes.characters.route) { CharactersScreen(navController) }
                }
                composable(route = Routes.splashScreen.route) { SplashScreen(navController) }
            }
        }
    }
}