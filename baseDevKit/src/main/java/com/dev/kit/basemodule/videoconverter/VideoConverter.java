package com.dev.kit.basemodule.videoconverter;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Surface;

import com.dev.kit.basemodule.util.LogUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;

public class VideoConverter {
    private static final String TAG = "VideoConverter";

    // parameters for the video encoder
    private static final String OUTPUT_VIDEO_MIME_TYPE = "video/avc"; // H.264 Advanced Video Coding
    private static final int OUTPUT_VIDEO_FRAME_RATE = 25; // 15fps
    private static final int OUTPUT_VIDEO_IFRAME_INTERVAL = 10; // 10 seconds between I-frames
    private static final int OUTPUT_VIDEO_COLOR_FORMAT =
            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface;

    // parameters for the audio encoder
    private static final String OUTPUT_AUDIO_MIME_TYPE = "audio/mp4a-latm"; // Advanced Audio Coding
    private static final int OUTPUT_AUDIO_CHANNEL_COUNT = 2; // Must match the input stream.
    private static final int OUTPUT_AUDIO_BIT_RATE = 128 * 1024;
    //    private static final int OUTPUT_AUDIO_AAC_PROFILE = MediaCodecInfo.CodecProfileLevel.AACObjectHE;
    private static final int OUTPUT_AUDIO_SAMPLE_RATE_HZ = 44100; // Must match the input stream.
    /**
     * Whether to copy the video from the test video.
     */
    private boolean mCopyVideo = true;
    /**
     * Whether to copy the audio from the test video.
     */
    private boolean mCopyAudio = true;
    /**
     * Width of the output frames.
     */
    private int mWidth = -1;
    /**
     * Height of the output frames.
     */
    private int mHeight = -1;

    /**
     * bit rate of video
     */
    private int bitrate;
    /**
     * bit rate of audio
     */
    private int audioBitRate = OUTPUT_AUDIO_BIT_RATE;
    /**
     * channel count of audio
     */
    private int channelCount = OUTPUT_AUDIO_CHANNEL_COUNT;
    /**
     * sample rate of audio
     */
    private int sampleRate = OUTPUT_AUDIO_SAMPLE_RATE_HZ;

    /**
     * The path used as the input file.
     */
    private String sourcePath;

    /**
     * The destination file for the encoded output.
     */
    private String mOutputFile;

    /**
     * Let progress update once every second
     */
    private long lastTime;

    private MediaExtractor mVideoExtractor = null;
    private MediaExtractor mAudioExtractor = null;
    private InputSurface mInputSurface = null;
    private OutputSurface mOutputSurface = null;
    private MediaCodec mVideoDecoder = null;
    private MediaCodec mAudioDecoder = null;
    private MediaCodec mVideoEncoder = null;
    private MediaCodec mAudioEncoder = null;
    private MediaMuxer mMuxer = null;
    private HandlerThread mVideoDecoderHandlerThread;
    private CallbackHandler mVideoDecoderHandler;

    // We will get these from the decoders when notified of a format change.
    private MediaFormat mDecoderOutputVideoFormat = null;
    private MediaFormat mDecoderOutputAudioFormat = null;
    // We will get these from the encoders when notified of a format change.
    private MediaFormat mEncoderOutputVideoFormat = null;
    private MediaFormat mEncoderOutputAudioFormat = null;

    // We will determine these once we have the output format.
    private int mOutputVideoTrack = -1;
    private int mOutputAudioTrack = -1;
    // Whether things are done on the video side.
    private boolean mVideoExtractorDone = false;
    private boolean mVideoDecoderDone = false;
    private boolean mVideoEncoderDone = false;
    // Whether things are done on the audio side.
    private boolean mAudioExtractorDone = false;
    private boolean mAudioDecoderDone = false;
    private boolean mAudioEncoderDone = false;
    private LinkedList<Integer> mPendingAudioDecoderOutputBufferIndices;
    private LinkedList<MediaCodec.BufferInfo> mPendingAudioDecoderOutputBufferInfos;
    private LinkedList<Integer> mPendingAudioEncoderInputBufferIndices;

    private LinkedList<Integer> mPendingVideoEncoderOutputBufferIndices;
    private LinkedList<MediaCodec.BufferInfo> mPendingVideoEncoderOutputBufferInfos;
    private LinkedList<Integer> mPendingAudioEncoderOutputBufferIndices;
    private LinkedList<MediaCodec.BufferInfo> mPendingAudioEncoderOutputBufferInfos;

    private boolean mMuxing = false;

    private int mVideoExtractedFrameCount = 0;
    private int mVideoDecodedFrameCount = 0;
    private int mVideoEncodedFrameCount = 0;

    private int mAudioExtractedFrameCount = 0;
    private int mAudioDecodedFrameCount = 0;
    private int mAudioEncodedFrameCount = 0;

    private boolean useSoftware;

    public VideoConverter() {
        useSoftware = useSoftware();
    }

//    /**
//     * Sets the test to copy the video stream.
//     */
//    private void setCopyVideo() {
//        mCopyVideo = true;
//    }
//
//    /**
//     * Sets the test to copy the video stream.
//     */
//    private void setCopyAudio() {
//        mCopyAudio = true;
//    }

    /**
     * Sets the desired frame size.
     */
    private void setSize(int originalWidth, int originalHeight) {
        int resultWidth, resultHeight;
        if (originalWidth > originalHeight && originalWidth > 960) {
            resultWidth = 960;
            float scale = originalWidth * 1f / 960;
            resultHeight = (int) Math.floor(originalHeight / scale);
        } else if (originalWidth <= originalHeight && originalHeight > 960) {
            resultHeight = 960;
            float scale = originalHeight * 1f / 960;
            resultWidth = (int) Math.floor(originalWidth / scale);
        } else {
            resultWidth = originalWidth;
            resultHeight = originalHeight;
        }
        if (resultWidth % 16 != 0) resultWidth += resultWidth % 16;
        if (resultHeight % 16 != 0) resultHeight += resultHeight % 16;
        mWidth = resultWidth;
        mHeight = resultHeight;
        bitrate = (resultWidth / 2) * (resultHeight / 2) * 10;
    }

    /**
     * Sets the name of the output file based on the other parameters.
     * <p>
     * <p>Must be called after {@link #setSize(int, int)}.
     */
    private void setOutputFile(String outputFile) {
        this.mOutputFile = outputFile;
    }

    private void initialization(String sourcePath, String outputPath, CompressProgressListener listener) {
        setOutputFile(outputPath);
        this.sourcePath = sourcePath;
        this.listener = listener;

        MediaExtractor extractor = new MediaExtractor();
        try {
            extractor.setDataSource(sourcePath);
        } catch (IOException e) {
            LogUtil.e(TAG, "can not get MediaExtractor");
            extractor = null;
        }
        if (extractor != null) {
            int numTracks = extractor.getTrackCount();
            int index = -1;
            for (int i = 0; i < numTracks; i++) {
                MediaFormat format = extractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith("audio/")) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                MediaFormat mf = extractor.getTrackFormat(index);
                if (mf.containsKey(MediaFormat.KEY_BIT_RATE)) {
                    int br = mf.getInteger(MediaFormat.KEY_BIT_RATE);
                    audioBitRate = br <= 0 ? audioBitRate : br;
                }
                if (mf.containsKey(MediaFormat.KEY_SAMPLE_RATE)) {
                    sampleRate = mf.getInteger(MediaFormat.KEY_SAMPLE_RATE);
                }
                if (mf.containsKey(MediaFormat.KEY_CHANNEL_COUNT)) {
                    channelCount = mf.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
                }
            }
            LogUtil.w(TAG, "original data : bit rate = " + audioBitRate + " , smapleRate = " + sampleRate + " , channelCount = " + channelCount);
            extractor.release();
        }

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(sourcePath);
        duration = Long.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) * 1000;
        int rotation = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
        int width = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        setSize(width, height);

        if (rotation == 90) {
            int temp = mHeight;
            mHeight = mWidth;
            mWidth = temp;
        } else if (rotation == 270) {
            int temp = mHeight;
            mHeight = mWidth;
            mWidth = temp;
        }

        Log.i(TAG, "width = " + width + ", height = " + height + ", rW = " + mWidth + ", rH = " + mHeight + ", rotate = " + rotation + "bit_rate = " + bitrate);
    }


    private CompressProgressListener listener;

    public interface CompressProgressListener {
        void onProgress(float percent);
    }

    /**
     * source's duration
     */
    private long duration;

    /**
     * Tests encoding and subsequently decoding video from frames generated into a buffer.
     * <p>
     * We encode several frames of a video test pattern using MediaCodec, then decode the output
     * with MediaCodec and do some simple checks.
     */
    public boolean extractDecodeEditEncodeMux(String sourcePath, String outputPath, CompressProgressListener listener) throws Exception {
        initialization(sourcePath, outputPath, listener);

        // Exception that may be thrown during release.
        Exception exception = null;

        mDecoderOutputVideoFormat = null;
        mDecoderOutputAudioFormat = null;
        mEncoderOutputVideoFormat = null;
        mEncoderOutputAudioFormat = null;

        mOutputVideoTrack = -1;
        mOutputAudioTrack = -1;
        mVideoExtractorDone = false;
        mVideoDecoderDone = false;
        mVideoEncoderDone = false;
        mAudioExtractorDone = false;
        mAudioDecoderDone = false;
        mAudioEncoderDone = false;
        mPendingAudioDecoderOutputBufferIndices = new LinkedList<>();
        mPendingAudioDecoderOutputBufferInfos = new LinkedList<>();
        mPendingAudioEncoderInputBufferIndices = new LinkedList<>();
        mPendingVideoEncoderOutputBufferIndices = new LinkedList<>();
        mPendingVideoEncoderOutputBufferInfos = new LinkedList<>();
        mPendingAudioEncoderOutputBufferIndices = new LinkedList<>();
        mPendingAudioEncoderOutputBufferInfos = new LinkedList<>();
        mMuxing = false;
        mVideoExtractedFrameCount = 0;
        mVideoDecodedFrameCount = 0;
        mVideoEncodedFrameCount = 0;
        mAudioExtractedFrameCount = 0;
        mAudioDecodedFrameCount = 0;
        mAudioEncodedFrameCount = 0;

        MediaCodecInfo videoCodecInfo = selectCodec(OUTPUT_VIDEO_MIME_TYPE);
        if (videoCodecInfo == null) {
            // Don't fail CTS if they don't have an AVC codec (not here, anyway).
            LogUtil.e(TAG, "Unable to find an appropriate codec for " + OUTPUT_VIDEO_MIME_TYPE);
            return false;
        }
        LogUtil.d(TAG, "odevideo found cc: " + videoCodecInfo.getName());

        MediaCodecInfo audioCodecInfo = selectCodec(OUTPUT_AUDIO_MIME_TYPE);
        if (audioCodecInfo == null) {
            // Don't fail CTS if they don't have an AAC codec (not here, anyway).
            LogUtil.e(TAG, "Unable to find an appropriate codec for " + OUTPUT_AUDIO_MIME_TYPE);
            return false;
        }
        LogUtil.d(TAG, "audio found codec: " + audioCodecInfo.getName());

        try {
            // Creates a muxer but do not start or add tracks just yet.
            mMuxer = createMuxer();

            if (mCopyVideo) {
                mVideoExtractor = createExtractor();
                int videoInputTrack = getAndSelectVideoTrackIndex(mVideoExtractor);
//                assertTrue("missing video track in test video", videoInputTrack != -1);
                MediaFormat inputFormat = mVideoExtractor.getTrackFormat(videoInputTrack);

                // We avoid the device-specific limitations on width and height by using values
                // that are multiples of 16, which all tested devices seem to be able to handle.
                MediaFormat outputVideoFormat =
                        MediaFormat.createVideoFormat(OUTPUT_VIDEO_MIME_TYPE, mWidth, mHeight);

                // Set some properties. Failing to specify some of these can cause the MediaCodec
                // configure() call to throw an unhelpful exception.
                outputVideoFormat.setInteger(
                        MediaFormat.KEY_COLOR_FORMAT, OUTPUT_VIDEO_COLOR_FORMAT);
                outputVideoFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);
                outputVideoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, OUTPUT_VIDEO_FRAME_RATE);
                outputVideoFormat.setInteger(
                        MediaFormat.KEY_I_FRAME_INTERVAL, OUTPUT_VIDEO_IFRAME_INTERVAL);
                LogUtil.d(TAG, "video format: " + outputVideoFormat);

                // Create a MediaCodec for the desired codec, then configure it as an encoder with
                // our desired properties. Request a Surface to use for input.
                AtomicReference<Surface> inputSurfaceReference = new AtomicReference<>();
                mVideoEncoder = createVideoEncoder(
                        videoCodecInfo, outputVideoFormat, inputSurfaceReference);
                mInputSurface = new InputSurface(inputSurfaceReference.get());
                mInputSurface.makeCurrent();
                // Create a MediaCodec for the decoder, based on the extractor's format.
                mOutputSurface = new OutputSurface();
                mVideoDecoder = createVideoDecoder(inputFormat, mOutputSurface.getSurface());
                mInputSurface.releaseEGLContext();
            }

            if (mCopyAudio) {
                mAudioExtractor = createExtractor();
                int audioInputTrack = getAndSelectAudioTrackIndex(mAudioExtractor);
//                assertTrue("missing audio track in test video", audioInputTrack != -1);
                MediaFormat inputFormat = mAudioExtractor.getTrackFormat(audioInputTrack);

                MediaFormat outputAudioFormat =
                        MediaFormat.createAudioFormat(
                                OUTPUT_AUDIO_MIME_TYPE, sampleRate,
                                channelCount);
                outputAudioFormat.setInteger(MediaFormat.KEY_BIT_RATE, audioBitRate);
//                outputAudioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, OUTPUT_AUDIO_AAC_PROFILE);

                // Create a MediaCodec for the desired codec, then configure it as an encoder with
                // our desired properties. Request a Surface to use for input.
                mAudioEncoder = createAudioEncoder(audioCodecInfo, outputAudioFormat);
                // Create a MediaCodec for the decoder, based on the extractor's format.
                mAudioDecoder = createAudioDecoder(inputFormat);
            }

            awaitEncode();
        } finally {
            LogUtil.d(TAG, "releasing extractor, decoder, encoder, and muxer");
            // Try to release everything we acquired, even if one of the releases fails, in which
            // case we save the first exception we got and re-throw at the end (unless something
            // other exception has already been thrown). This guarantees the first exception thrown
            // is reported as the cause of the error, everything is (attempted) to be released, and
            // all other exceptions appear in the logs.
            try {
                if (mVideoExtractor != null) {
                    mVideoExtractor.release();
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "error while releasing videoExtractor");
                e.printStackTrace();
                if (exception == null) {
                    exception = e;
                }
            }
            try {
                if (mAudioExtractor != null) {
                    mAudioExtractor.release();
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "error while releasing audioExtractor");
                e.printStackTrace();
                if (exception == null) {
                    exception = e;
                }
            }
            try {
                if (mVideoDecoder != null) {
                    mVideoDecoder.stop();
                    mVideoDecoder.release();
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "error while releasing videoDecoder");
                e.printStackTrace();
                if (exception == null) {
                    exception = e;
                }
            }
            try {
                if (mOutputSurface != null) {
                    mOutputSurface.release();
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "error while releasing outputSurface");
                e.printStackTrace();
                if (exception == null) {
                    exception = e;
                }
            }
            try {
                if (mVideoEncoder != null) {
                    mVideoEncoder.stop();
                    mVideoEncoder.release();
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "error while releasing videoEncoder");
                e.printStackTrace();
                if (exception == null) {
                    exception = e;
                }
            }
            try {
                if (mAudioDecoder != null) {
                    mAudioDecoder.stop();
                    mAudioDecoder.release();
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "error while releasing audioDecoder");
                e.printStackTrace();
                if (exception == null) {
                    exception = e;
                }
            }
            try {
                if (mAudioEncoder != null) {
                    mAudioEncoder.stop();
                    mAudioEncoder.release();
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "error while releasing audioEncoder");
                e.printStackTrace();
                if (exception == null) {
                    exception = e;
                }
            }
            try {
                if (mMuxer != null) {
                    mMuxer.stop();
                    mMuxer.release();
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "error while releasing muxer");
                e.printStackTrace();
                if (exception == null) {
                    exception = e;
                }
            }
            try {
                if (mInputSurface != null) {
                    mInputSurface.release();
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "error while releasing inputSurface");
                e.printStackTrace();
                if (exception == null) {
                    exception = e;
                }
            }
            if (mVideoDecoderHandlerThread != null) {
                mVideoDecoderHandlerThread.quitSafely();
            }
            mVideoExtractor = null;
            mAudioExtractor = null;
            mOutputSurface = null;
            mInputSurface = null;
            mVideoDecoder = null;
            mAudioDecoder = null;
            mVideoEncoder = null;
            mAudioEncoder = null;
            mMuxer = null;
            mVideoDecoderHandlerThread = null;
        }
        if (exception != null) {
            throw exception;
        }
        return mAudioExtractorDone && mVideoExtractorDone;
    }

    /**
     * Creates an extractor that reads its frames from {@link #sourcePath}.
     */
    private MediaExtractor createExtractor() throws IOException {
        MediaExtractor extractor;
        extractor = new MediaExtractor();
        extractor.setDataSource(sourcePath);
        return extractor;
    }

    /**
     * Creates a decoder for the given format, which outputs to the given surface.
     *
     * @param inputFormat the format of the stream to decode
     * @param surface     into which to decode the frames
     */
    private MediaCodec createVideoDecoder(MediaFormat inputFormat, Surface surface) {
        mVideoDecoderHandlerThread = new HandlerThread("DecoderThread");
        mVideoDecoderHandlerThread.start();
        mVideoDecoderHandler = new CallbackHandler(mVideoDecoderHandlerThread.getLooper());
        MediaCodec.Callback callback = new MediaCodec.Callback() {
            public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException exception) {
                LogUtil.e(TAG, "video decoder error() ");
            }

            public void onOutputFormatChanged(MediaCodec codec, @NonNull MediaFormat format) {
                mDecoderOutputVideoFormat = codec.getOutputFormat();
                LogUtil.d(TAG, "video decoder: onOutputFormatChanged(): "
                        + mDecoderOutputVideoFormat);
            }

            public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
                if (isCancel) return;
                // Extract video from file and feed to decoder.
                // We feed packets regardless of whether the muxer is set up or not.
                // If the muxer isn't set up yet, the encoder output will be queued up,
                // finally blocking the decoder as well.
                ByteBuffer decoderInputBuffer = codec.getInputBuffer(index);
                while (!mVideoExtractorDone) {
                    int size = mVideoExtractor.readSampleData(decoderInputBuffer, 0);
                    long presentationTime = mVideoExtractor.getSampleTime();
                    if (size >= 0) {
                        codec.queueInputBuffer(
                                index,
                                0,
                                size,
                                presentationTime,
                                mVideoExtractor.getSampleFlags());
                    }
                    mVideoExtractorDone = !mVideoExtractor.advance();
                    if (mVideoExtractorDone) {
                        LogUtil.d(TAG, "video extractor: EOS");
                        codec.queueInputBuffer(
                                index,
                                0,
                                0,
                                0,
                                MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    }
                    mVideoExtractedFrameCount++;
                    logState();
                    if (size >= 0)
                        break;
                }
            }

            @Override
            public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
                if (isCancel) return;
                if ((info.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    LogUtil.d(TAG, "video decoder: codec config buffer");
                    codec.releaseOutputBuffer(index, false);
                    return;
                }
                try {
                    boolean render = info.size != 0;
                    codec.releaseOutputBuffer(index, render);
                    if (render) {
                        mInputSurface.makeCurrent();
                        LogUtil.d(TAG, "output surface: await new image");
                        mOutputSurface.awaitNewImage();
                        // Edit the frame and send it to the encoder.
                        LogUtil.d(TAG, "output surface: draw image");
                        mOutputSurface.drawImage();
                        mInputSurface.setPresentationTime(
                                info.presentationTimeUs * 1000);
                        LogUtil.d(TAG, "input surface: swap buffers");
                        mInputSurface.swapBuffers();
                        LogUtil.d(TAG, "video encoder: notified of new frame");
                        mInputSurface.releaseEGLContext();
                    }
                    if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        LogUtil.d(TAG, "video decoder: EOS");
                        mVideoDecoderDone = true;
                        mVideoEncoder.signalEndOfInputStream();
                    }
                    mVideoDecodedFrameCount++;
                    logState();
                } catch (Exception e) {
                    e.printStackTrace();
                    cancel();
                }
            }
        };
//         Create the decoder on a different thread, in order to have the callbacks there.
//         This makes sure that the blocking waiting and rendering in onOutputBufferAvailable
//         won't block other callbacks (e.g. blocking encoder output callbacks), which
//         would otherwise lead to the transcoding pipeline to lock up.
//
//         Since API 23, we could just do setCallback(callback, mVideoDecoderHandler) instead
//         of using a custom Handler and passing a message to create the MediaCodec there.
//
//         When the callbacks are received on a different thread, the updating of the variables
//         that are used for state logging (mVideoExtractedFrameCount, mVideoDecodedFrameCount,
//         mVideoExtractorDone and mVideoDecoderDone) should ideally be synchronized properly
//         against accesses from other threads, but that is left out for brevity since it's
//         not essential to the actual transcoding.
        mVideoDecoderHandler.create(false, getMimeTypeFor(inputFormat), callback);
        MediaCodec decoder = mVideoDecoderHandler.getCodec();
        decoder.configure(inputFormat, surface, null, 0);
        decoder.start();
        return decoder;
    }


    /**
     * Creates an encoder for the given format using the specified codec, taking input from a
     * surface.
     * <p>
     * <p>The surface to use as input is stored in the given reference.
     *
     * @param codecInfo        of the codec to use
     * @param format           of the stream to be produced
     * @param surfaceReference to store the surface to use as input
     */
    private MediaCodec createVideoEncoder(
            MediaCodecInfo codecInfo,
            MediaFormat format,
            AtomicReference<Surface> surfaceReference) throws IOException {
        MediaCodec encoder;
        if (useSoftware) {
            encoder = MediaCodec.createByCodecName("OMX.google.h264.encoder");
        } else {
            encoder = MediaCodec.createByCodecName(codecInfo.getName());
        }
        encoder.setCallback(new MediaCodec.Callback() {
            public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException exception) {
                LogUtil.e(TAG, "video encoder error() ");
            }

            public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
                LogUtil.d(TAG, "video encoder: onOutputFormatChanged()");
                if (mOutputVideoTrack >= 0) {
                    fail("video encoder changed its output format again?");
                }
                mEncoderOutputVideoFormat = codec.getOutputFormat();
                setupMuxer();
            }

            public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
            }

            public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
                LogUtil.d(TAG, "video encoder: onOutputBufferAvailable()");
                if (isCancel) return;
                muxVideo(index, info);
            }
        });
        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        // Must be called before start() is.
        surfaceReference.set(encoder.createInputSurface());
        encoder.start();
        return encoder;
    }


    /**
     * Creates a decoder for the given format.
     *
     * @param inputFormat the format of the stream to decode
     */
    private MediaCodec createAudioDecoder(MediaFormat inputFormat) throws IOException {
        MediaCodec decoder = MediaCodec.createDecoderByType(getMimeTypeFor(inputFormat));
        decoder.setCallback(new MediaCodec.Callback() {
            public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException exception) {
                LogUtil.e(TAG, "audio decoder error() ");
            }

            public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
                mDecoderOutputAudioFormat = codec.getOutputFormat();
                LogUtil.d(TAG, "audio decoder: onOutputFormatChanged(): "
                        + mDecoderOutputAudioFormat);
            }

            public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
                if (isCancel) return;
                ByteBuffer decoderInputBuffer = codec.getInputBuffer(index);
                while (!mAudioExtractorDone) {
                    int size = mAudioExtractor.readSampleData(decoderInputBuffer, 0);
                    long presentationTime = mAudioExtractor.getSampleTime();
                    LogUtil.i(TAG, "audio extractor: ronInputBufferAvailable() presentationTime = " + presentationTime);
                    if (size >= 0) {
                        codec.queueInputBuffer(
                                index,
                                0,
                                size,
                                presentationTime,
                                mAudioExtractor.getSampleFlags());
                    }
                    mAudioExtractorDone = !mAudioExtractor.advance();
                    if (mAudioExtractorDone) {
                        LogUtil.d(TAG, "audio extractor: EOS");
                        codec.queueInputBuffer(
                                index,
                                0,
                                0,
                                0,
                                MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    }
                    mAudioExtractedFrameCount++;
                    logState();
                    if (size >= 0)
                        break;
                }
            }

            public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
                LogUtil.d(TAG, "audio decoder: onOutputBufferAvailable()");
                if (isCancel) return;
                ByteBuffer decoderOutputBuffer = codec.getOutputBuffer(index);
                if ((info.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    LogUtil.d(TAG, "audio decoder: codec config buffer");
                    codec.releaseOutputBuffer(index, false);
                    return;
                }
                LogUtil.w(TAG, "audio decoder: onOutputBufferAvailable() presentationTimeUs = "
                        + info.presentationTimeUs);
                mPendingAudioDecoderOutputBufferIndices.add(index);
                mPendingAudioDecoderOutputBufferInfos.add(info);
                mAudioDecodedFrameCount++;
                logState();
                tryEncodeAudio();
            }
        });
        decoder.configure(inputFormat, null, null, 0);
        decoder.start();
        return decoder;
    }


    /**
     * Creates an encoder for the given format using the specified codec.
     *
     * @param codecInfo of the codec to use
     * @param format    of the stream to be produced
     */
    private MediaCodec createAudioEncoder(MediaCodecInfo codecInfo, MediaFormat format) throws IOException {
        MediaCodec encoder = MediaCodec.createByCodecName(codecInfo.getName());
        encoder.setCallback(new MediaCodec.Callback() {
            public void onError(MediaCodec codec, MediaCodec.CodecException exception) {
                LogUtil.e(TAG, "audio encoder error() ");
            }

            public void onOutputFormatChanged(MediaCodec codec, MediaFormat format) {
                LogUtil.d(TAG, "audio encoder: onOutputFormatChanged()");
                if (mOutputAudioTrack >= 0) {
                    fail("audio encoder changed its output format again?");
                }

                mEncoderOutputAudioFormat = codec.getOutputFormat();
                setupMuxer();
            }

            public void onInputBufferAvailable(MediaCodec codec, int index) {
                LogUtil.d(TAG, "audio encoder: onInputBufferAvailable() buffer: " + index);
                if (isCancel) return;
                mPendingAudioEncoderInputBufferIndices.add(index);
                tryEncodeAudio();
            }

            public void onOutputBufferAvailable(MediaCodec codec, int index, MediaCodec.BufferInfo info) {
                LogUtil.d(TAG, "audio encoder: onOutputBufferAvailable() ");
                if (isCancel) return;
                muxAudio(index, info);
            }
        });
        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        encoder.start();
        return encoder;
    }

    // No need to have synchronization around this, since both audio encoder and
    // decoder callbacks are on the same thread.
    private void tryEncodeAudio() {
        if (mPendingAudioEncoderInputBufferIndices.size() == 0 ||
                mPendingAudioDecoderOutputBufferIndices.size() == 0)
            return;
        try {
            int decoderIndex = mPendingAudioDecoderOutputBufferIndices.poll();
            int encoderIndex = mPendingAudioEncoderInputBufferIndices.poll();
            MediaCodec.BufferInfo info = mPendingAudioDecoderOutputBufferInfos.poll();

            ByteBuffer encoderInputBuffer = mAudioEncoder.getInputBuffer(encoderIndex);
            int size = info.size;
            long presentationTime = info.presentationTimeUs;
            LogUtil.d(TAG, "audio decoder: pending buffer for time " + presentationTime);
            LogUtil.w(TAG, "tryEncodeAudio() enIndex = " + encoderIndex + " , deIndex = " + decoderIndex);
            if (size >= 0) {
                ByteBuffer decoderOutputBuffer = mAudioDecoder.getOutputBuffer(decoderIndex).duplicate();
                decoderOutputBuffer.position(info.offset);
                decoderOutputBuffer.limit(info.offset + size);
                encoderInputBuffer.position(0);
                encoderInputBuffer.put(decoderOutputBuffer);

                mAudioEncoder.queueInputBuffer(
                        encoderIndex,
                        0,
                        size,
                        presentationTime,
                        info.flags);
            }
            mAudioDecoder.releaseOutputBuffer(decoderIndex, false);
            if ((info.flags
                    & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                LogUtil.d(TAG, "audio decoder: EOS");
                mAudioDecoderDone = true;
            }
            logState();
        } catch (Exception e) {
            e.printStackTrace();
            cancel();
        }
    }


    private void setupMuxer() {
        if (!mMuxing
                && (!mCopyAudio || mEncoderOutputAudioFormat != null)
                && (!mCopyVideo || mEncoderOutputVideoFormat != null)) {
            if (mCopyVideo) {
                LogUtil.d(TAG, "muxer: adding video track.");
                mOutputVideoTrack = mMuxer.addTrack(mEncoderOutputVideoFormat);
            }
            if (mCopyAudio) {
                LogUtil.d(TAG, "muxer: adding audio track.");
                mOutputAudioTrack = mMuxer.addTrack(mEncoderOutputAudioFormat);
            }
            LogUtil.d(TAG, "muxer: starting");
            mMuxer.start();
            mMuxing = true;

            MediaCodec.BufferInfo info;
            while ((info = mPendingVideoEncoderOutputBufferInfos.poll()) != null) {
                int index = mPendingVideoEncoderOutputBufferIndices.poll().intValue();
                muxVideo(index, info);
            }
            while ((info = mPendingAudioEncoderOutputBufferInfos.poll()) != null) {
                int index = mPendingAudioEncoderOutputBufferIndices.poll().intValue();
                muxAudio(index, info);
            }
        }
    }

    private void muxVideo(int index, MediaCodec.BufferInfo info) {
        if (!mMuxing) {
            mPendingVideoEncoderOutputBufferIndices.add(new Integer(index));
            mPendingVideoEncoderOutputBufferInfos.add(info);
            return;
        }
        ByteBuffer encoderOutputBuffer = mVideoEncoder.getOutputBuffer(index);
        if ((info.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
            LogUtil.d(TAG, "muxVideo: codec config buffer");
            // Simply ignore codec config buffers.
            mVideoEncoder.releaseOutputBuffer(index, false);
            return;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime > 1000) {
            lastTime = currentTime;
            if (info.presentationTimeUs > 0) {
                float per = 1f * info.presentationTimeUs / duration * 100;
                listener.onProgress(per);
            }
            if (useSoftware) {
                Bundle params = new Bundle();
                params.putInt(MediaCodec.PARAMETER_KEY_REQUEST_SYNC_FRAME, 0);
                mVideoEncoder.setParameters(params);
            }
        }
//        LogUtil.d(TAG, "muxVideo: presentationTimeUs = "
//                + info.presentationTimeUs + " , duration = " + duration);
        if (info.size != 0) {
            mMuxer.writeSampleData(
                    mOutputVideoTrack, encoderOutputBuffer, info);
        }
        mVideoEncoder.releaseOutputBuffer(index, false);
        mVideoEncodedFrameCount++;
        if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
            LogUtil.d(TAG, "muxVideo: EOS");
            synchronized (this) {
                mVideoEncoderDone = true;
                notifyAll();
            }
        }
        logState();
    }


    private void muxAudio(int index, MediaCodec.BufferInfo info) {
        if (!mMuxing) {
            mPendingAudioEncoderOutputBufferIndices.add(new Integer(index));
            mPendingAudioEncoderOutputBufferInfos.add(info);
            return;
        }
        ByteBuffer encoderOutputBuffer = mAudioEncoder.getOutputBuffer(index);
        if ((info.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
            LogUtil.d(TAG, "muxAudio: codec config buffer");
            // Simply ignore codec config buffers.
            mAudioEncoder.releaseOutputBuffer(index, false);
            return;
        }
        {
            LogUtil.d(TAG, "muxAudio: presentationTimeUs = " + info.presentationTimeUs);
        }
        if (info.size != 0) {
            mMuxer.writeSampleData(
                    mOutputAudioTrack, encoderOutputBuffer, info);
        }
        mAudioEncoder.releaseOutputBuffer(index, false);
        mAudioEncodedFrameCount++;
        if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
            LogUtil.d(TAG, "muxAudio: EOS");
            synchronized (this) {
                mAudioEncoderDone = true;
                notifyAll();
            }
        }
        logState();
    }

    /**
     * Creates a muxer to write the encoded frames.
     * <p>
     * <p>The muxer is not started as it needs to be started only after all streams have been added.
     */
    private MediaMuxer createMuxer() throws IOException {
        return new MediaMuxer(mOutputFile, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
    }

    private int getAndSelectVideoTrackIndex(MediaExtractor extractor) {
        for (int index = 0; index < extractor.getTrackCount(); ++index) {
            {
                LogUtil.d(TAG, "format for track " + index + " is "
                        + getMimeTypeFor(extractor.getTrackFormat(index)));
            }
            if (isVideoFormat(extractor.getTrackFormat(index))) {
                extractor.selectTrack(index);
                return index;
            }
        }
        return -1;
    }

    private int getAndSelectAudioTrackIndex(MediaExtractor extractor) {
        for (int index = 0; index < extractor.getTrackCount(); ++index) {
            {
                LogUtil.d(TAG, "format for track " + index + " is "
                        + getMimeTypeFor(extractor.getTrackFormat(index)));
            }
            if (isAudioFormat(extractor.getTrackFormat(index))) {
                extractor.selectTrack(index);
                return index;
            }
        }
        return -1;
    }

    private void logState() {
        LogUtil.w(TAG, String.format(
                "loop: "
                        + "V(%b){"
                        + "extracted:%d(done:%b) "
                        + "decoded:%d(done:%b) "
                        + "encoded:%d(done:%b)} "

                        + "A(%b){"
                        + "extracted:%d(done:%b) "
                        + "decoded:%d(done:%b) "
                        + "encoded:%d(done:%b) "

                        + "muxing:%b(V:%d,A:%d)",

                mCopyVideo,
                mVideoExtractedFrameCount, mVideoExtractorDone,
                mVideoDecodedFrameCount, mVideoDecoderDone,
                mVideoEncodedFrameCount, mVideoEncoderDone,

                mCopyAudio,
                mAudioExtractedFrameCount, mAudioExtractorDone,
                mAudioDecodedFrameCount, mAudioDecoderDone,
                mAudioEncodedFrameCount, mAudioEncoderDone,

                mMuxing, mOutputVideoTrack, mOutputAudioTrack));
    }

    private void awaitEncode() {
        synchronized (this) {
            while ((mCopyVideo && !mVideoEncoderDone) || (mCopyAudio && !mAudioEncoderDone)) {
                try {
                    wait();
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }
    }

    private boolean isVideoFormat(MediaFormat format) {
        return getMimeTypeFor(format).startsWith("video/");
    }

    private boolean isAudioFormat(MediaFormat format) {
        return getMimeTypeFor(format).startsWith("audio/");
    }

    private String getMimeTypeFor(MediaFormat format) {
        return format.getString(MediaFormat.KEY_MIME);
    }


    /**
     * Returns the first codec capable of encoding the specified MIME type, or null if no match was
     * found.
     */
    private MediaCodecInfo selectCodec(String mimeType) {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);

            if (!codecInfo.isEncoder()) {
                continue;
            }

            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(mimeType)) {
                    return codecInfo;
                }
            }
        }
        return null;
    }

    static class CallbackHandler extends Handler {
        CallbackHandler(Looper l) {
            super(l);
        }

        private MediaCodec mCodec;
        private boolean mEncoder;
        private MediaCodec.Callback mCallback;
        private String mMime;
        private boolean mSetDone;

        @Override
        public void handleMessage(Message msg) {
            try {
                mCodec = mEncoder ? MediaCodec.createEncoderByType(mMime) : MediaCodec.createDecoderByType(mMime);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            mCodec.setCallback(mCallback);
            synchronized (this) {
                mSetDone = true;
                notifyAll();
            }
        }

        void create(boolean encoder, String mime, MediaCodec.Callback callback) {
            mEncoder = encoder;
            mMime = mime;
            mCallback = callback;
            mSetDone = false;
            sendEmptyMessage(0);
            synchronized (this) {
                while (!mSetDone) {
                    try {
                        wait();
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }
        }

        MediaCodec getCodec() {
            return mCodec;
        }
    }

    private void fail(String errorMsg) {
        LogUtil.e(TAG, errorMsg);
    }

    private boolean isCancel;

    public void cancel() {
        isCancel = true;
        Log.w(TAG, "You are canceling the video converting task");

        synchronized (this) {
            Log.w(TAG, "Going to set video and audio status to done, and await the other threads");
            mVideoEncoderDone = true;
            mAudioEncoderDone = true;
            mVideoDecoderDone = true;
            mAudioDecoderDone = true;
            notifyAll();
        }
    }

    /**
     * 是否使用软编码。
     * 默认情况，视频的编码，解码都是使用hardware，目前发现华为的硬件编码出的视频在iOS和OS X上播放有一半绿屏,
     * 网上查了相关内容，因为视频的 1 frame 里包含了多个 slice, 而iOS和OS X只支持 1 frame 里包含 1 slice的视频，
     * 多个slice的导致不能正确解析，所以绿屏，而Android默认都支持。
     *
     * @return true: use software, false: use hardware
     */
    public static boolean useSoftware() {
        return Build.MANUFACTURER.equalsIgnoreCase("HUAWEI");
    }
}
