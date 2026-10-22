/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.ui.screens.setup

import alt.foss.brunothg.tacl.R
import alt.foss.brunothg.tacl.bl.repository.AppSettingsRepository
import alt.foss.brunothg.tacl.misc.LoadState
import alt.foss.brunothg.tacl.misc.LoadState.Loading.valueOrNull
import alt.foss.brunothg.tacl.ui.components.misc.LoadingIndicator
import alt.foss.brunothg.tacl.ui.misc.Delayed
import alt.foss.brunothg.tacl.ui.misc.PagerUtils.animateScrollToNextPage
import alt.foss.brunothg.tacl.ui.misc.PagerUtils.animateScrollToPreviousPage
import alt.foss.brunothg.tacl.ui.misc.PagerUtils.isFirstPage
import alt.foss.brunothg.tacl.ui.misc.PagerUtils.isLastPage
import alt.foss.brunothg.tacl.ui.misc.PreviewUtils.withPreviewFallback
import alt.foss.brunothg.tacl.ui.theme.TaclPreview
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/**
 * UI state for the setup process.
 *
 * @property pages The list of pages.
 * @property isReady True if the setup is ready for the next step.
 */
@Immutable
data class SetupUiState(
    val pages: LoadState<List<SetupPage>> = LoadState.Loading,
    val isReady: Boolean = false,
)

/**
 * ViewModel for the setup process.
 */
interface SetupViewModel {
    /**
     * The current UI state.
     */
    val uiState: StateFlow<SetupUiState>

    /**
     * Marks the setup as ready (or not) for the next step.
     */
    fun onReady(ready: Boolean)

    /**
     * Marks the setup for the current version as done.
     */
    fun onDone()

}

@HiltViewModel
class HiltSetupViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
) : ViewModel(), SetupViewModel {

    private val _readyState: MutableStateFlow<Boolean> = MutableStateFlow(
        false
    )

    private val _pages: Flow<List<SetupPage>> = appSettingsRepository.setupVersion
        .take(1)
        .map { version ->
            SetupPages.filter { page -> version < page.version }
        }

    override val uiState: StateFlow<SetupUiState> = combine(
        _pages,
        _readyState,
    ) { pages, ready ->
        SetupUiState(
            pages = LoadState.Loaded(pages),
            isReady = ready,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SetupUiState(),
    )

    override fun onReady(ready: Boolean) {
        _readyState.value = ready
    }

    override fun onDone() {
        Log.d("SetupViewModel", "onDone")
        viewModelScope.launch {
            val maxVersion = SetupPages.maxOfOrNull { it.version } ?: 0
            appSettingsRepository.setSetupVersion(maxVersion)
        }

    }

}

/**
 * Scaffold for the setup process containing the pager and navigation.
 *
 */
@Composable
fun SetupScaffold(
    viewModel: SetupViewModel = withPreviewFallback(
        { hiltViewModel<HiltSetupViewModel>() },
        { buildPreviewSetupViewModel() }
    ),
    onDone: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState {
        uiState.pages.valueOrNull()?.size ?: 0
    }
    val onReady: (ready: Boolean, pageIndex: Int) -> Unit = { ready, _ ->
        viewModel.onReady(ready)
    }
    val onDone: () -> Unit = {
        onDone()
        viewModel.onDone()
    }

    when (val pagesState = uiState.pages) {
        is LoadState.Loading -> {
            Delayed(200.milliseconds) {
                LoadingIndicator()
            }
        }

        is LoadState.Loaded -> {
            val pages = pagesState.value

            LaunchedEffect(pages) {
                if (pages.isEmpty()) {
                    onDone()
                }
            }

            if (pages.isNotEmpty()) {
                SetupSurface(
                    state = uiState,
                    pagerState = pagerState,
                    onReady = onReady,
                    onDone = onDone,
                )
            }
        }
    }

}

@Composable
private fun SetupSurface(
    state: SetupUiState,
    pagerState: PagerState,
    onDone: () -> Unit,
    onReady: (ready: Boolean, pageIndex: Int) -> Unit,
) {
    Surface(
        Modifier
            .fillMaxSize(),
    ) {
        Column(
            Modifier
                .fillMaxSize(),
        ) {
            SetupPager(
                state = state,
                pagerState = pagerState,
                onReady = onReady,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
            )

            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                    .navigationBarsPadding()
                    .fillMaxWidth(),
            ) {
                PagerNavButtons(
                    state = state,
                    pagerState = pagerState,
                    onDone = onDone,
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                )
                PagerProgress(
                    pagerState = pagerState,
                    modifier = Modifier
                        .fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun PagerNavButtons(
    state: SetupUiState,
    pagerState: PagerState,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val canGoBack = { !pagerState.isFirstPage() }
    val canGoNext = { state.isReady }
    val nextPage: () -> Unit = {
        if (canGoNext()) {
            if (pagerState.isLastPage()) {
                onDone()
            } else {
                coroutineScope.launch {
                    pagerState.animateScrollToNextPage()
                }
            }
        }

    }
    val previousPage: () -> Unit = {
        if (canGoBack()) {
            coroutineScope.launch {
                pagerState.animateScrollToPreviousPage()
            }
        }
    }


    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        if (canGoBack()) {
            OutlinedIconButton(
                onClick = previousPage
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_m3_arrow_back),
                    contentDescription = stringResource(R.string.misc_action_back),
                )
            }
        } else {
            Spacer(
                modifier = Modifier
                    .width(1.dp)
            )
        }

        FilledIconButton(
            enabled = canGoNext(),
            onClick = nextPage
        ) {
            if (pagerState.isLastPage()) {
                Icon(
                    painter = painterResource(R.drawable.ic_m3_check),
                    contentDescription = stringResource(R.string.misc_action_done),
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_m3_arrow_forward),
                    contentDescription = stringResource(R.string.misc_action_next),
                )
            }
        }
    }
}

@Composable
private fun PagerProgress(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    val pageCount = { pagerState.pageCount }
    val currentPageIndex = { pagerState.currentPage }

    if (pageCount() > 0) {
        LinearProgressIndicator(
            progress = {
                (currentPageIndex() + 1).toFloat() / pageCount()
            },
            modifier = modifier,
        )
    }
}


@Composable
private fun SetupPager(
    state: SetupUiState,
    pagerState: PagerState,
    onReady: (ready: Boolean, pageIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pages = state.pages.valueOrNull() ?: emptyList()
    val currentPageIndex = { pagerState.currentPage }

    HorizontalPager(
        state = pagerState,
        userScrollEnabled = state.isReady,
        modifier = modifier,
    ) { pageIndex ->
        val page = pages[pageIndex]
        val pageState = PageUiState(
            isActive = currentPageIndex() == pageIndex,
            onReady = { done ->
                onReady(
                    done,
                    pageIndex,
                )
            },
        )

        page.content(
            pageState
        )
    }
}

@Composable
fun buildPreviewSetupViewModel(): SetupViewModel {
    return object : SetupViewModel {
        override val uiState: StateFlow<SetupUiState> = MutableStateFlow(
            SetupUiState()
        )

        override fun onReady(ready: Boolean) {
            error("Preview only")
        }

        override fun onDone() {
            error("Preview only")
        }
    }
}

@PreviewLightDark
@Composable
fun PreviewSetupScaffold() {
    TaclPreview {
        SetupScaffold()
    }
}


