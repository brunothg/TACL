/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.ui.misc

import androidx.compose.foundation.pager.PagerState

/**
 * Utilities for the pager.
 */
object PagerUtils {

    /**
     * Checks if the current page is the first page ([currentPage] = 0).
     */
    fun PagerState.isFirstPage(): Boolean {
        return currentPage == 0
    }

    /**
     * Checks if the current page is the last page ([currentPage] = [pageCount] - 1).
      */
    fun PagerState.isLastPage(): Boolean {
        return currentPage == pageCount - 1
    }

    /**
     * Animate going to the next page.
     */
    suspend fun PagerState.animateScrollToNextPage() {
        if (!isLastPage()) {
            animateScrollToPage(currentPage + 1)
        }
    }

    /**
     * Animate going to the previous page.
     */
    suspend fun PagerState.animateScrollToPreviousPage() {
        if (!isFirstPage()) {
            animateScrollToPage(currentPage - 1)
        }
    }

    /**
     * Go to the next page.
     */
    suspend fun PagerState.scrollToNextPage() {
        if (!isLastPage()) {
            scrollToPage(currentPage + 1)
        }
    }

    /**
     * Go to the previous page.
     */
    suspend fun PagerState.scrollToPreviousPage() {
        if (!isFirstPage()) {
            scrollToPage(currentPage - 1)
        }
    }

}