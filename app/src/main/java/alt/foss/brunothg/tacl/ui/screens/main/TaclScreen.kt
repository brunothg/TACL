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
import alt.foss.brunothg.tacl.bl.filter.CallFilter
import alt.foss.brunothg.tacl.bl.permission.AppPermissions
import alt.foss.brunothg.tacl.bl.repository.CallFilterRepository
import alt.foss.brunothg.tacl.misc.ID
import alt.foss.brunothg.tacl.misc.LoadState
import alt.foss.brunothg.tacl.misc.LoadState.Loading.valueOrNull
import alt.foss.brunothg.tacl.ui.components.misc.BackButton
import alt.foss.brunothg.tacl.ui.components.misc.LoadingIndicator
import alt.foss.brunothg.tacl.ui.components.permission.PermissionIndicator
import alt.foss.brunothg.tacl.ui.components.filter.CallFilterEditor
import alt.foss.brunothg.tacl.ui.components.filter.CallFilterEditorUiState
import alt.foss.brunothg.tacl.ui.components.filter.CallFilterEditorUiState.Companion.toUiState
import alt.foss.brunothg.tacl.ui.components.filter.CallFilterListItem
import alt.foss.brunothg.tacl.ui.components.permission.rememberPermissionRequestLauncher
import alt.foss.brunothg.tacl.ui.misc.Delayed
import alt.foss.brunothg.tacl.ui.misc.PreviewUtils.withPreviewFallback
import alt.foss.brunothg.tacl.ui.theme.TaclPreview
import android.os.Parcelable
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/**
 * Selection state for a call filter.
 */
@Parcelize
sealed interface CallFilterSelection : Parcelable {
    /**
     * No filter selected.
     */
    @Parcelize
    data object None : CallFilterSelection

    /**
     * Edit an existing filter.
     *
     * @property id The ID of the filter.
     */
    @Parcelize
    data class Edit(val id: ID) : CallFilterSelection

    /**
     * Create a new filter.
     */
    @Parcelize
    data object New : CallFilterSelection
}

/**
 * UI state for the Tacl screen.
 */
@Immutable
data class TaclUiState(
    val callFilters: LoadState<List<CallFilter>> = LoadState.Loading,
    val selection: CallFilterSelection = CallFilterSelection.None,
    val editorState: CallFilterEditorUiState? = null,
) {

    fun getSelectedCalFilter(selection: CallFilterSelection): CallFilter? {
        return when (selection) {
            is CallFilterSelection.Edit -> {
                callFilters.valueOrNull()?.find { filter -> filter.id == selection.id }
            }

            else -> null
        }
    }

}

/**
 * ViewModel for the Tacl screen.
 */
interface TaclViewModel {
    /**
     * The current UI state.
     */
    val uiState: StateFlow<TaclUiState>

    /**
     * Update the current selection.
     */
    fun updateSelection(selection: CallFilterSelection)

    /**
     * Update the editor state.
     */
    fun updateEditorState(editorState: CallFilterEditorUiState)

    /**
     * Save a call filter
     */
    fun saveCallFilter(callFilter: CallFilter)

    /**
     * Delete a call filter
     */
    fun deleteCallFilter(callFilter: CallFilter)

}

@HiltViewModel
class HiltTaclViewModel @Inject constructor(
    private val callFilterRepository: CallFilterRepository,
) : ViewModel(), TaclViewModel {

    private val _callFilters = callFilterRepository.callFilters
        .map { LoadState.Loaded(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LoadState.Loading,
        )
    private val _selection = MutableStateFlow<CallFilterSelection>(CallFilterSelection.None)
    private val _editorState = MutableStateFlow<CallFilterEditorUiState?>(null)

    override val uiState: StateFlow<TaclUiState> = combine(
        _callFilters,
        _selection,
        _editorState,
    ) { callFilters, selection, editorState ->
        TaclUiState(
            callFilters = callFilters,
            selection = selection,
            editorState = editorState,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TaclUiState(),
    )

    override fun updateSelection(selection: CallFilterSelection) {
        Log.d("HiltTaclViewModel", "updateSelection: $selection")
        if (_selection.value == selection) {
            return
        }

        _selection.value = selection
        _editorState.value = when (selection) {
            is CallFilterSelection.None -> null
            is CallFilterSelection.New -> CallFilterEditorUiState.buildDefault()
            is CallFilterSelection.Edit -> {
                _callFilters.value
                    .valueOrNull()
                    ?.find { it.id == selection.id }
                    ?.toUiState()
            }
        }
    }

    override fun updateEditorState(editorState: CallFilterEditorUiState) {
        Log.d("HiltTaclViewModel", "updateEditorState: $editorState")
        _editorState.value = editorState
    }

    override fun saveCallFilter(callFilter: CallFilter) {
        Log.d("HiltTaclViewModel", "saveCallFilter: $callFilter")
        viewModelScope.launch {
            callFilterRepository.saveCallFilter(callFilter)
        }
    }

    override fun deleteCallFilter(callFilter: CallFilter) {
        Log.d("HiltTaclViewModel", "deleteCallFilter: $callFilter")
        viewModelScope.launch {
            callFilterRepository.deleteCallFilter(callFilter)
        }
    }

}


/**
 * Screen displaying the call filters and their configuration.
 *
 * @param viewModel The view model.
 * @param navigation The screen navigation.
 */
@Composable
fun TaclScreen(
    navigation: ScreenNavigation,
    viewModel: TaclViewModel = withPreviewFallback(
        { hiltViewModel<HiltTaclViewModel>() },
        { buildPreviewTaclViewModel() }
    ),
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listNavigator = rememberListDetailPaneScaffoldNavigator<CallFilterSelection>()
    val selection = listNavigator.currentDestination?.contentKey ?: CallFilterSelection.None

    LaunchedEffect(selection) {
        viewModel.updateSelection(selection)
    }

    val onSelect: (CallFilterSelection) -> Unit = {
        Log.d("TaclScreen", "onSelect: $it")
        coroutineScope.launch {
            when (it) {
                is CallFilterSelection.None -> {
                    listNavigator.navigateTo(
                        ListDetailPaneScaffoldRole.List,
                        it,
                    )
                }

                is CallFilterSelection.Edit,
                is CallFilterSelection.New -> {
                    listNavigator.navigateTo(
                        ListDetailPaneScaffoldRole.Detail,
                        it,
                    )
                }
            }
        }

    }
    val onDelete: (CallFilterSelection) -> Unit = { deleteSelection ->
        Log.d("TaclScreen", "onDelete: $deleteSelection")
        uiState.getSelectedCalFilter(deleteSelection)
            ?.also { filter ->
                viewModel.deleteCallFilter(filter)
            }
        coroutineScope.launch {
            listNavigator.navigateBack()
        }
    }
    val onSave: (CallFilter) -> Unit = {
        Log.d("TaclScreen", "onSave: $it")
        viewModel.saveCallFilter(it)
        coroutineScope.launch {
            listNavigator.navigateBack()
        }
    }

    NavigableListDetailPaneScaffold(
        navigator = listNavigator,
        listPane = {
            AnimatedPane {
                TaclListPaneScaffold(
                    state = uiState,
                    selection = selection,
                    onSelect = onSelect,
                    onDelete = onDelete,
                    onBack = navigation.onBack,
                )
            }
        },
        detailPane = {
            AnimatedPane {
                TaclDetailPaneScaffold(
                    state = uiState,
                    selection = selection,
                    onSelect = onSelect,
                    onSave = onSave,
                    onDelete = onDelete,
                    onChangeEditorState = viewModel::updateEditorState,
                )
            }
        },
    )

}

@Composable
private fun TaclListPaneScaffold(
    state: TaclUiState,
    selection: CallFilterSelection,
    onSelect: (CallFilterSelection) -> Unit,
    onDelete: (CallFilterSelection) -> Unit,
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TaclListPaneTopBar(
                onBack = onBack,
            )
        },
        floatingActionButton = {
            TaclListPaneFAB(
                onClick = {
                    onSelect(CallFilterSelection.New)
                }
            )
        },
        modifier = modifier,
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding),
        ) {
            when (val callFiltersState = state.callFilters) {
                is LoadState.Loading -> {
                    Delayed(200.milliseconds) {
                        LoadingIndicator()
                    }
                }

                is LoadState.Loaded -> {
                    TaclListPane(
                        items = callFiltersState.value,
                        selection = selection,
                        onSelect = onSelect,
                        onDelete = onDelete,
                        contentPadding = PaddingValues(
                            bottom = (56 /*FAB*/ + (16 * 2 /*FAB padding*/)).dp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun TaclListPaneFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_m3_add),
            contentDescription = stringResource(R.string.misc_action_add),
        )
    }
}

@Composable
private fun TaclListPaneTopBar(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
) {
    val callScreeningRole = AppPermissions.CallScreeningRole.definition
    val callScreeningRoleRequest = rememberPermissionRequestLauncher(
        permissionDefinition = callScreeningRole,
    )

    TopAppBar(
        title = {
            Text(stringResource(R.string.screen_tacl_label))
        },
        navigationIcon = {
            if (onBack != null) {
                BackButton(
                    onBack = onBack,
                )
            }
        },
        actions = {
            PermissionIndicator(
                permissionDefinitions = setOf(callScreeningRole)
            ) { granted ->
                val granted = granted ?: false

                IconButton(
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = if (granted)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline
                    ),
                    onClick = {
                        callScreeningRoleRequest.invoke()
                    }
                ) {

                    Icon(
                        painter = painterResource(R.drawable.ic_m3_power_settings_new),
                        contentDescription = stringResource(R.string.screen_tacl_on_off_action_description),
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = modifier,
    )
}

@Composable
private fun TaclListPane(
    items: List<CallFilter>,
    selection: CallFilterSelection,
    onSelect: (CallFilterSelection) -> Unit,
    onDelete: (CallFilterSelection) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = contentPadding,
    ) {
        if (items.isEmpty()) {
            item {
                TaclListPaneNoItem(
                    modifier = Modifier
                        .fillParentMaxSize()
                )
            }
        } else {
            items(
                items = items,
                key = { it.id.value }
            ) { item ->
                TaclListPaneItem(
                    item = item,
                    selection = selection,
                    onSelect = onSelect,
                    onDelete = onDelete,
                )
            }
        }
    }
}

@Composable
private fun TaclListPaneItem(
    item: CallFilter,
    selection: CallFilterSelection,
    onSelect: (CallFilterSelection) -> Unit,
    onDelete: (CallFilterSelection) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isItemSelected = item.id == (selection as? CallFilterSelection.Edit)?.id
    val dismissState = rememberSwipeToDismissBoxState()

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false, // TODO use for enable/disable action
        backgroundContent = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                    else -> Color.Transparent
                }
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_m3_delete),
                    contentDescription = stringResource(R.string.misc_action_delete),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        onDismiss = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete(CallFilterSelection.Edit(item.id))
            }
        },
        modifier = modifier,
    ) {
        Column {
            CallFilterListItem(
                callFilter = item,
                modifier = Modifier
                    .background(
                        if (isItemSelected)
                            MaterialTheme.colorScheme.secondaryContainer
                        else
                            MaterialTheme.colorScheme.surface
                    )
                    .clickable {
                        onSelect(CallFilterSelection.Edit(item.id))
                    },
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun TaclListPaneNoItem(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.screen_tacl_no_data_message),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TaclDetailPaneScaffold(
    state: TaclUiState,
    selection: CallFilterSelection,
    onSelect: (CallFilterSelection) -> Unit,
    onDelete: (CallFilterSelection) -> Unit,
    onSave: (CallFilter) -> Unit,
    onChangeEditorState: (CallFilterEditorUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedItem = state.getSelectedCalFilter(selection)
    val editorState = state.editorState

    Scaffold(
        topBar = {
            TaclDetailPaneTopBar(
                selection = selection,
                onSelect = onSelect,
                onDelete = onDelete,
            )
        },
        floatingActionButton = {
            TaclDetailPaneFAB(
                editorState = editorState,
                onSave = onSave,
            )
        },
        modifier = modifier,
    ) { padding ->
        if (editorState == null) {
            TaclDetailPaneNoItem(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            )
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        bottom = if (selectedItem != null)
                            (56 /*FAB*/ + (16 * 2 /*FAB padding*/)).dp
                        else
                            0.dp
                    ),
            ) {
                TaclDetailPane(
                    editorState = editorState,
                    onChangeEditorState = onChangeEditorState,
                )
            }
        }
    }
}

@Composable
private fun TaclDetailPaneFAB(
    editorState: CallFilterEditorUiState?,
    onSave: (CallFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    val editorIsSavable = editorState?.isValid

    if (editorIsSavable != null) {
        FloatingActionButton(
            onClick = {
                if (editorIsSavable) {
                    editorState.toDomain().also { callFilter ->
                        onSave(callFilter)
                    }
                }
            },
            modifier = modifier
                .alpha(if (editorIsSavable) 1f else 0.5f),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_m3_save),
                contentDescription = stringResource(R.string.misc_action_save),
            )
        }
    }
}

@Composable
private fun TaclDetailPaneTopBar(
    selection: CallFilterSelection,
    onSelect: (CallFilterSelection) -> Unit,
    onDelete: (CallFilterSelection) -> Unit,
    modifier: Modifier = Modifier,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val onBack: (() -> Unit)? = selection.let {
        if (it !is CallFilterSelection.None) ({
            onSelect(CallFilterSelection.None)
        }) else null
    }
    val onDelete: (() -> Unit)? = selection.let {
        if (it is CallFilterSelection.Edit) ({
            onDelete(it)
        }) else null
    }

    TopAppBar(
        title = {
            Text(stringResource(R.string.screen_tacl_editor_label))
        },
        navigationIcon = {
            if (onBack != null) {
                BackButton(
                    onBack = onBack,
                )
            }
        },
        actions = {
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
            TaclDetailPaneTopBarDropdownMenu(
                expanded = menuExpanded,
                onDismiss = {
                    menuExpanded = false
                },
                onDelete = onDelete,
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = modifier,
    )
}

@Composable
private fun TaclDetailPaneTopBarDropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onDelete: (() -> Unit)?
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            text = {
                Text(stringResource(R.string.misc_action_delete))
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_m3_delete),
                    contentDescription = stringResource(R.string.misc_action_delete),
                )
            },
            onClick = {
                onDismiss()
                onDelete?.invoke()
            },
            enabled = onDelete != null,
        )
    }
}

@Composable
private fun TaclDetailPane(
    editorState: CallFilterEditorUiState,
    onChangeEditorState: (CallFilterEditorUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    CallFilterEditor(
        state = editorState,
        onChange = { newState ->
            onChangeEditorState(newState)
        },
        modifier = modifier
            .padding(8.dp),
    )
}

@Composable
private fun TaclDetailPaneNoItem(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.screen_tacl_no_selection_message),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun buildPreviewTaclViewModel(): TaclViewModel {
    return object : TaclViewModel {
        override val uiState: StateFlow<TaclUiState> = MutableStateFlow(
            TaclUiState()
        )

        override fun updateSelection(selection: CallFilterSelection) {
            error("Preview only")
        }

        override fun updateEditorState(editorState: CallFilterEditorUiState) {
            error("Preview only")
        }

        override fun saveCallFilter(callFilter: CallFilter) {
            error("Preview only")
        }

        override fun deleteCallFilter(callFilter: CallFilter) {
            error("Preview only")
        }
    }
}

@PreviewLightDark
@Composable
fun PreviewTaclScreen() {
    TaclPreview {
        TaclScreen(
            navigation = buildPreviewScreenNavigation(),
        )
    }
}
