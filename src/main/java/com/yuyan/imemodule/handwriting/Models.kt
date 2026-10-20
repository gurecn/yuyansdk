package com.yuyan.imemodule.handwriting

data class Stroke(
    val points: List<Point> = mutableListOf()
) {
    data class Point(
        val x: Float,
        val y: Float,
        val pressure: Float = 1.0f,
        val timestamp: Long = System.currentTimeMillis()
    )
}

data class Candidate(val text: String, val confidence: Float)
