package com.ytrewqwert.yetanotherjnovelreader

import kotlinx.coroutines.Job

/**
 * A container for a single Job, where storing a new Job cancels the old Job
 */
class JobHolder {
    var job: Job? = null
        set(value) {
            field?.cancel()
            field = value
        }
}