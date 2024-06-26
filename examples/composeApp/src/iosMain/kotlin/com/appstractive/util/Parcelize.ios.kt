package com.appstractive.util

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlin.annotation.AnnotationRetention.BINARY
import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.PROPERTY

@Retention(BINARY)
@Target(CLASS)
actual annotation class CommonParcelize

@Target(CLASS, PROPERTY)
@Repeatable
@Retention(SOURCE)
actual annotation class CommonTypeParceler<T, P : CommonParceler<in T>>

actual interface CommonParcelable

actual interface CommonParceler<T>

actual object ImmutableListParceler : CommonParceler<ImmutableList<*>>

actual object ImmutableSetParceler : CommonParceler<ImmutableSet<*>>
