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
import alt.foss.brunothg.tacl.bl.log.FilterLogEntry
import alt.foss.brunothg.tacl.bl.repository.FilterLogRepository
import alt.foss.brunothg.tacl.misc.LoadState
import alt.foss.brunothg.tacl.ui.components.log.LogEntryListItem
import alt.foss.brunothg.tacl.ui.components.misc.BackButton
import alt.foss.brunothg.tacl.ui.components.misc.LoadingIndicator
import alt.foss.brunothg.tacl.ui.components.misc.LoadingListItem
import alt.foss.brunothg.tacl.ui.misc.Delayed
import alt.foss.brunothg.tacl.ui.misc.PreviewUtils.withPreviewFallback
import alt.foss.brunothg.tacl.ui.theme.TaclPreview
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/**
 * UI state for the log screen.
 */
@Immutable
data class LogUiState(
    val logEntries: LoadState<Flow<PagingData<FilterLogEntry>>> = LoadState.Loading,
)

/**
 * ViewModel for the log screen.
 */
interface LogViewModel {
    /**
     * The current UI state.
     */
    val uiState: StateFlow<LogUiState>

    /**
     * Clears the whole log.
     */
    fun clearLog()
}

@HiltViewModel
class HiltLogViewModel @Inject constructor(
    private val logRepository: FilterLogRepository,
) : ViewModel(), LogViewModel {

    private val _logEntries = logRepository.getLogEntriesPaged()
        .cachedIn(viewModelScope)

    override val uiState: StateFlow<LogUiState> = combine(
        flowOf(Unit),
    ) { _ ->
        LogUiState(
            logEntries = LoadState.Loaded(_logEntries),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LogUiState(),
    )

    override fun clearLog() {
        Log.d("HiltLogViewModel", "clearLog")
        viewModelScope.launch {
            logRepository.clearLog()
        }
    }
}

/**
 * Screen displaying the call filter log.
 *
 * @param navigation The screen navigation.
 */
@Composable
fun LogScreen(
    navigation: ScreenNavigation,
    viewModel: LogViewModel = withPreviewFallback(
        { hiltViewModel<HiltLogViewModel>() },
        { buildPreviewLogViewModel() }
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val onClearLog = {
        Log.d("LogScreen", "onClearLog")
        viewModel.clearLog()
    }

    Scaffold(
        topBar = {
            LogTopBar(
                onBack = navigation.onBack,
                onClearLog = onClearLog,
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding),
        ) {
            when (val logEntriesState = uiState.logEntries) {
                is LoadState.Loading -> {
                    Delayed(200.milliseconds) {
                        LoadingIndicator()
                    }
                }

                is LoadState.Loaded -> {
                    LogListPane(
                        pagedItems = logEntriesState.value,
                    )
                }
            }
        }
    }
}

@Composable
private fun LogTopBar(
    onClearLog: () -> Unit,
    onBack: (() -> Unit)? = null,
) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.screen_log_label))
        },
        navigationIcon = {
            if (onBack != null) {
                BackButton(
                    onBack = onBack,
                )
            }
        },
        actions = {
            var menuExpanded by remember { mutableStateOf(false) }
            IconButton(
                onClick = {
                    menuExpanded = !menuExpanded
                },
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_m3_more_vert),
                    contentDescription = stringResource(R.string.misc_action_open_menu)
                )
            }
            LogTopBarDropdownMenu(
                expanded = menuExpanded,
                onDismiss = {
                    menuExpanded = false
                },
                onClearLog = onClearLog,
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
    )
}

@Composable
private fun LogTopBarDropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onClearLog: (() -> Unit)?
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            text = {
                Text(stringResource(R.string.misc_action_delete_sweep))
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_m3_delete_sweep),
                    contentDescription = stringResource(R.string.misc_action_delete_sweep),
                )
            },
            onClick = {
                onDismiss()
                onClearLog?.invoke()
            },
            enabled = onClearLog != null,
        )
    }
}

@Composable
private fun LogListPane(
    pagedItems: Flow<PagingData<FilterLogEntry>>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val items = pagedItems.collectAsLazyPagingItems()

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = contentPadding,
    ) {
        if (items.itemCount <= 0) {
            item {
                LogListPaneNoItem(
                    modifier = Modifier
                        .fillParentMaxSize()
                )
            }
        } else {
            items(
                count = items.itemCount,
                key = items.itemKey { it.id },
            ) { index ->
                val item = items[index]
                LogListPaneItem(
                    logEntry = item
                )
            }
        }
    }
}

@Composable
private fun LogListPaneNoItem(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.screen_log_no_data_message),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LogListPaneItem(
    logEntry: FilterLogEntry?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        if (logEntry != null) {
            LogEntryListItem(
                logEntry = logEntry,
            )
        } else {
            LoadingListItem()
        }
        HorizontalDivider()
    }
}

@Composable
fun buildPreviewLogViewModel(): LogViewModel {
    return object : LogViewModel {
        override val uiState: StateFlow<LogUiState> = MutableStateFlow(
            LogUiState()
        )

        override fun clearLog() {
            error("Preview only")
        }
    }
}

@PreviewLightDark
@Composable
fun PreviewLogScreen() {
    TaclPreview {
        LogScreen(
            navigation = buildPreviewScreenNavigation(),
        )
    }
}
