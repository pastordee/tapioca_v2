package com.tapioca_v2.plg.tapioca_v2

interface VideoGeneratorServiceInterface {
    fun writeVideofile(processing: HashMap<String,HashMap<String,Any>>, result: Result, activity: Activity, eventSink: EventChannel.EventSink);
}

class VideoGeneratorService(
    private val composer: Mp4Composer
) : VideoGeneratorServiceInterface {
    override fun writeVideofile(processing: HashMap<String,HashMap<String,Any>>, result: Result, activity: Activity, eventSink: EventChannel.EventSink ) {
        val filters: MutableList<GlFilter> = mutableListOf()
        try {
            processing.forEach { (k, v) ->
                when (k) {
                    "Filter" -> {
                        val passFilter = Filter(v)
                        val filter = GlColorBlendFilter(passFilter)
                        filters.add(filter)
                    }
                    "TextOverlay" -> {
                        val textOverlay = TextOverlay(v)
                        filters.add(GlTextOverlayFilter(textOverlay))
                    }
                    "ImageOverlay" -> {
                        val imageOverlay = ImageOverlay(v)
                        filters.add(GlImageOverlayFilter(imageOverlay))
                    }
                }
            }
        } catch (e: Exception){
            println(e)
            activity.runOnUiThread(Runnable {
                result.error("processing_data_invalid", "Processing data is invalid.", null)
            })
        }
        composer.filter(GlFilterGroup( filters))
            .videoFormatMimeType(VideoFormatMimeType.HEVC)
            .listener(object : Mp4Composer.Listener {
                override fun onProgress(progress: Double) {
                    println("onProgress = " + progress)
                    activity.runOnUiThread(Runnable {
                        eventSink.success(progress)
                    })
                }

                override fun onCompleted() {
                    activity.runOnUiThread(Runnable {
                        result.success(null)
                    })
                }

                override  fun onCanceled() {
                    activity.runOnUiThread(Runnable {
                        result.error("video_processing_canceled", "Video processing is canceled.", null)
                    })
                }

                override fun onFailed(exception: Exception) {
                    println(exception);
                    activity.runOnUiThread(Runnable {
                        result.error("video_processing_failed", "video processing is failed.", null)

                    })
                }
            }).start()
    }
}

