package com.artemklymenko.mycryptotracker.crypto.presentation.coin_detail.components

import android.graphics.RectF
import com.artemklymenko.mycryptotracker.crypto.presentation.coin_detail.DataPoint

class BezierConfiguration {

    private var firstControlPoints: List<DataPoint?> = emptyList()
    private var secondControlPoints: List<DataPoint?> = emptyList()

    fun configureControlPoints(data: List<DataPoint>, bounds: RectF): List<DataPoint> {
        val segments = data.size - 1

        if (segments == 1) {
            val p0 = data[0]
            val p3 = data[1]
            return listOf(p0, p3) // Straight line
        } else if (segments > 1) {
            val ad = MutableList(segments) { 0f }
            val d = MutableList(segments) { 0f }
            val bd = MutableList(segments) { 0f }
            val rhsArray = MutableList(segments) { DataPoint(0f, 0f, "") }

            for (i in 0 until segments) {
                val p0 = data[i]
                val p3 = data[i + 1]
                when (i) {
                    0 -> {
                        bd[i] = 0f
                        d[i] = 2f
                        ad[i] = 1f
                        rhsArray[i] = DataPoint(
                            x = p0.x + 2 * p3.x,
                            y = p0.y + 2 * p3.y,
                            xLabel = ""
                        )
                    }

                    segments - 1 -> {
                        bd[i] = 2f
                        d[i] = 7f
                        ad[i] = 0f
                        rhsArray[i] = DataPoint(
                            x = 8 * p0.x + p3.x,
                            y = 8 * p0.y + p3.y,
                            xLabel = ""
                        )
                    }

                    else -> {
                        bd[i] = 1f
                        d[i] = 4f
                        ad[i] = 1f
                        rhsArray[i] = DataPoint(
                            x = 4 * p0.x + 2 * p3.x,
                            y = 4 * p0.y + 2 * p3.y,
                            xLabel = ""
                        )
                    }
                }
            }

            return thomasAlgorithm(bd, d, ad, rhsArray, segments, data)
        }

        firstControlPoints = firstControlPoints.map { cp ->
            cp?.let { clampToBounds(it, bounds.left, bounds.right, bounds.top, bounds.bottom) }
        }
        secondControlPoints = secondControlPoints.map { cp ->
            cp?.let { clampToBounds(it, bounds.left, bounds.right, bounds.top, bounds.bottom) }
        }

        return (firstControlPoints + secondControlPoints).filterNotNull()
    }

    private fun clampToBounds(point: DataPoint, leftX: Float, rightX: Float, topY: Float, bottomY: Float): DataPoint {
        return point.copy(
            x = point.x.coerceIn(leftX, rightX),
            y = point.y.coerceIn(topY, bottomY)
        )
    }

    private fun thomasAlgorithm(
        bd: List<Float>,
        d: List<Float>,
        ad: MutableList<Float>,
        rhsArray: MutableList<DataPoint>,
        segments: Int,
        data: List<DataPoint>
    ): List<DataPoint> {
        val controlPoints = mutableListOf<DataPoint>()
        val solutionSet1 = MutableList<DataPoint?>(segments) { null }

        // Forward elimination
        ad[0] = ad[0] / d[0]
        rhsArray[0] = rhsArray[0].copy(
            x = rhsArray[0].x / d[0],
            y = rhsArray[0].y / d[0]
        )

        for (i in 1 until segments - 1) {
            val denominator = d[i] - bd[i] * ad[i - 1]
            ad[i] /= denominator
            rhsArray[i] = rhsArray[i].copy(
                x = (rhsArray[i].x - bd[i] * rhsArray[i - 1].x) / denominator,
                y = (rhsArray[i].y - bd[i] * rhsArray[i - 1].y) / denominator
            )
        }

        // Backward substitution
        rhsArray[segments - 1] = rhsArray[segments - 1].copy(
            x = (rhsArray[segments - 1].x - bd[segments - 1] * rhsArray[segments - 2].x) /
                    (d[segments - 1] - bd[segments - 1] * ad[segments - 2]),
            y = (rhsArray[segments - 1].y - bd[segments - 1] * rhsArray[segments - 2].y) /
                    (d[segments - 1] - bd[segments - 1] * ad[segments - 2])
        )
        solutionSet1[segments - 1] = rhsArray[segments - 1]

        for (i in (0 until segments - 1).reversed()) {
            solutionSet1[i] = rhsArray[i].copy(
                x = rhsArray[i].x - ad[i] * (solutionSet1[i + 1]?.x ?: 0f),
                y = rhsArray[i].y - ad[i] * (solutionSet1[i + 1]?.y ?: 0f)
            )
        }

        firstControlPoints = solutionSet1

        // Calculate second control points
        for (i in 0 until segments) {
            if (i == segments - 1) {
                val p3 = data[i + 1]
                val p1 = firstControlPoints[i] ?: continue
                secondControlPoints += DataPoint(
                    x = (p3.x + p1.x) / 2f,
                    y = (p3.y + p1.y) / 2f,
                    xLabel = ""
                )
            } else {
                val p3 = data[i + 1]
                val p1 = firstControlPoints[i + 1] ?: continue
                secondControlPoints += DataPoint(
                    x = 2 * p3.x - p1.x,
                    y = 2 * p3.y - p1.y,
                    xLabel = ""
                )
            }
        }

        // Combine control points
        for (i in 0 until segments) {
            val firstCP = firstControlPoints[i] ?: continue
            val secondCP = secondControlPoints[i] ?: continue
            controlPoints += firstCP
            controlPoints += secondCP
        }

        return controlPoints
    }
}
