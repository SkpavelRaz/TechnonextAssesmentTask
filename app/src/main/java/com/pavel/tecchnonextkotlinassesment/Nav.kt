package com.pavel.tecchnonextkotlinassesment

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pavel.tecchnonextkotlinassesment.data.local.PostEntity
import com.pavel.tecchnonextkotlinassesment.ui.auth.AuthViewModel
import com.pavel.tecchnonextkotlinassesment.ui.auth.LoginScreen
import com.pavel.tecchnonextkotlinassesment.ui.auth.RegisterScreen
import com.pavel.tecchnonextkotlinassesment.ui.favorites.FavoritesScreen
import com.pavel.tecchnonextkotlinassesment.ui.favorites.FavouritesViewModel
import com.pavel.tecchnonextkotlinassesment.ui.posts.PostsScreen
import com.pavel.tecchnonextkotlinassesment.ui.posts.PostsViewModel
import com.pavel.tecchnonextkotlinassesment.ui.theme.TecchnoNextKotlinAssesmentTheme
import kotlinx.coroutines.flow.StateFlow

sealed class Route(val route: String)  {
    data object Register: Route("register")
    data object Login: Route("login")
    data object Posts: Route("posts")
    data object Favourites: Route("favourites")
}

@Composable
fun AppNav() {
    TecchnoNextKotlinAssesmentTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val navController = rememberNavController()
            NavGraph(navController)
        }
    }
}

@Composable
fun NavGraph(navController: NavHostController){
    NavHost(navController=navController, startDestination = Route.Login.route)
    {
        composable(Route.Register.route){
            val vm= hiltViewModel<AuthViewModel>()
            RegisterScreen(
                stateFlow = vm.registerState,
                onRegister ={e,p,cp->
                    vm.register(e,p,cp){ success->
                        if (success) navController.navigate(Route.Login.route) { popUpTo(
                            Route.Register.route) { inclusive = true } }
                    }
                } ,
                onGoToLogin = {navController.navigate(Route.Login.route)}
            )
        }

        composable(Route.Login.route) {
            val vm = hiltViewModel<AuthViewModel>()
            LoginScreen(
                stateFlow = vm.loginState,
                onLogin = { e, p ->
                    vm.login(e, p) { success ->
                        if (success) navController.navigate(Route.Posts.route) { popUpTo(
                           Route.Login.route) { inclusive = true } }
                    }
                },
                onGoToRegister = { navController.navigate(Route.Register.route) }
            )
        }

        composable(Route.Posts.route) {
            val vm = hiltViewModel<PostsViewModel>()
            PostsScreen(
                pager = vm.pager,
                searchQueryFlow = vm.searchQuery,
                searchResultsFlow = vm.searchResults,
                onSearch = vm::updateSearch,
                onToggleFav = vm::toggleFavourite,
                onOpenFavourites = { navController.navigate(Route.Favourites.route) },
                favouritesFlow = vm.favouriteIds,
            )
        }

        composable(Route.Favourites.route) {
            val vm = hiltViewModel<FavouritesViewModel>()
            FavoritesScreen(
                postsFlow = vm.favouritePosts,
                onBack = { navController.popBackStack() },
                onToggleFav = vm::toggleFavourite
            )
        }
    }

}