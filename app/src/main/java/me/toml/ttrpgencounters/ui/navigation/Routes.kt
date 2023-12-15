package me.toml.ttrpgencounters.ui.navigation

data class Screen(val route: String)

object Routes {
    val launchNavigation = Screen("launchnavigation")
    val encountersNavigation = Screen("encountersnavigation")
    val launch = Screen("launch")
    val signIn = Screen("signin")
    val signUp = Screen("signup")
    val encounters = Screen("encounters")
    val editEncounter = Screen("editencounter")
    val splashScreen = Screen("splashscreen")
    val charactersNavigation = Screen("charactersnavigation")
    val characters = Screen("characters")
    val editCharacter = Screen("editcharacter")
    val runEncounter = Screen("runencounter")
}