/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.ui.screens.main

import alt.foss.brunothg.tacl.R
import alt.foss.brunothg.tacl.misc.ID
import alt.foss.brunothg.tacl.misc.LoadState
import alt.foss.brunothg.tacl.ui.components.BackButton
import alt.foss.brunothg.tacl.ui.components.FilterBar
import alt.foss.brunothg.tacl.ui.components.LoadingIndicator
import alt.foss.brunothg.tacl.ui.misc.Delayed
import alt.foss.brunothg.tacl.ui.theme.TaclPreview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.style.LibraryActionBadges
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryBadges
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/**
 * UI state for libraries.
 */
@Immutable
data class LibrariesUiState(
    val searchQuery: String = "",
    val rawLibraries: LoadState<Libs> = LoadState.Loading,
) {
    val filteredLibraries: LoadState<Libs> = if (searchQuery.isBlank()) {
        rawLibraries
    } else {
        when (val librariesState = rawLibraries) {
            is LoadState.Loading -> LoadState.Loading
            is LoadState.Loaded -> LoadState.Loaded(
                librariesState.value.copy(
                    libraries = librariesState.value.libraries.filter { library ->
                        library.name.contains(searchQuery, ignoreCase = true)
                                || library.developers.any {
                            it.name?.contains(
                                searchQuery,
                                ignoreCase = true
                            ) ?: false
                        }
                    })
            )
        }
    }
}

/**
 * ViewModel for libraries.
 */
interface LibrariesViewModel {
    /**
     * The current UI state.
     */
    val uiState: StateFlow<LibrariesUiState>

    /**
     * Update the search query.
     *
     * @param query The new search query.
     */
    fun onSearchQueryChange(query: String)

    /**
     * Update the raw libraries.
     *
     * @param libraries The new raw libraries.
     */
    fun onRawLibrariesChange(libraries: LoadState<Libs>)
}

/**
 * Hilt ViewModel for libraries.
 */
@HiltViewModel
class HiltLibrariesViewModel @Inject constructor(
) : ViewModel(), LibrariesViewModel {

    private val _searchQuery: MutableStateFlow<String> = MutableStateFlow(
        ""
    )
    private val _rawLibraries: MutableStateFlow<LoadState<Libs>> = MutableStateFlow(
        LoadState.Loading
    )

    override val uiState: StateFlow<LibrariesUiState> = combine(
        _searchQuery,
        _rawLibraries,
    ) { searchQuery, rawLibraries ->
        LibrariesUiState(
            searchQuery = searchQuery,
            rawLibraries = rawLibraries,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LibrariesUiState()
    )

    override fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    override fun onRawLibrariesChange(libraries: LoadState<Libs>) {
        _rawLibraries.value = libraries
    }
}

@Composable
fun rememberLibrariesViewModel(): LibrariesViewModel {
    val viewModel: LibrariesViewModel = hiltViewModel<HiltLibrariesViewModel>()
    val libraries by produceLibraries(R.raw.aboutlibraries)

    LaunchedEffect(libraries) {
        viewModel.onRawLibrariesChange(
            when (val currentLibraries = libraries) {
                is Libs -> LoadState.Loaded(currentLibraries)
                else -> LoadState.Loading
            }
        )
    }

    return viewModel
}

/**
 * Screen displaying the used libraries.
 *
 * @param viewModel The UI view model.
 * @param navigation The screen navigation.
 */
@Composable
fun LibrariesScreen(
    viewModel: LibrariesViewModel = rememberLibrariesViewModel(),
    navigation: ScreenNavigation,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            LibrariesTopBar(
                onBack = navigation.onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            FilterBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                value = uiState.searchQuery,
                onValueChange = {
                    viewModel.onSearchQueryChange(it)
                },
            )

            when (val librariesState = uiState.filteredLibraries) {
                is LoadState.Loading -> {
                    Delayed(200.milliseconds) {
                        LoadingIndicator()
                    }
                }

                is LoadState.Loaded -> {
                    LibrariesContainer(
                        libraries = librariesState.value,
                        badges = LibraryBadges(
                            description = true
                        ),
                        actionLabels = LibraryActionBadges(
                            source = stringResource(R.string.screen_libraries_action_source),
                            website = stringResource(R.string.screen_libraries_action_website),
                            sponsor = stringResource(R.string.screen_libraries_action_sponsor),
                            viewLicense = stringResource(R.string.screen_libraries_action_license),
                        ),
                        licenseDialogConfirmText = stringResource(R.string.screen_libraries_confirm),
                        modifier = Modifier
                            .fillMaxSize(),
                    )
                }
            }
        }

    }
}

@Composable
private fun LibrariesTopBar(
    onBack: (() -> Unit)? = null,
) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.screen_libraries_label))
        },
        navigationIcon = {
            if (onBack != null) {
                BackButton(
                    onBack = onBack,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
    )
}

@Composable
fun buildPreviewLibrariesViewModel(): LibrariesViewModel {
    val licenses = setOf(
        License(
            name = "Preview License",
            url = null,
            hash = ID.uuid().value,
        )
    )
    val libraries = (1..50).map { i ->
        Library(
            uniqueId = ID.uuid().value,
            artifactVersion = "1.0.0",
            name = "Preview library $i",
            description = "Preview library $i description",
            website = null,
            developers = emptyList(),
            organization = null,
            scm = null,
            licenses = licenses,
        )
    }
    val rawLibraries = Libs(
        libraries = libraries,
        licenses = licenses,
    )

    return object : LibrariesViewModel {
        override val uiState: StateFlow<LibrariesUiState> = MutableStateFlow(
            LibrariesUiState(
                rawLibraries = LoadState.Loaded(rawLibraries)
            )
        )

        override fun onSearchQueryChange(query: String) {
            error("Preview only")
        }

        override fun onRawLibrariesChange(libraries: LoadState<Libs>) {
            error("Preview only")
        }
    }
}

@PreviewLightDark
@Composable
fun PreviewLibrariesScreen() {
    TaclPreview {
        LibrariesScreen(
            viewModel = buildPreviewLibrariesViewModel(),
            navigation = buildPreviewScreenNavigation(),
        )
    }
}
