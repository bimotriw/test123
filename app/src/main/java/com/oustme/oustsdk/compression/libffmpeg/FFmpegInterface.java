package com.oustme.oustsdk.compression.libffmpeg;

import com.oustme.oustsdk.compression.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.oustme.oustsdk.compression.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.util.Map;


@SuppressWarnings("unused")
interface FFmpegInterface {

    /**
     * Load binary to the device according to archituecture. This also updates FFmpeg binary if the binary on device have old version.
     * @param ffmpegLoadBinaryResponseHandler {@link FFmpegLoadBinaryResponseHandler}
     * @throws FFmpegNotSupportedException
     */
    void loadBinary(FFmpegLoadBinaryResponseHandler ffmpegLoadBinaryResponseHandler) throws FFmpegNotSupportedException;

    /**
     * Executes a command
     * @param environvenmentVars Environment variables
     * @param cmd command to execute
     * @param ffmpegExecuteResponseHandler {@link FFmpegExecuteResponseHandler}
     * @throws FFmpegCommandAlreadyRunningException
     */
    void execute(Map<String, String> environvenmentVars, String[] cmd, FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) throws FFmpegCommandAlreadyRunningException;

    /**
     * Executes a command
     * @param cmd command to execute
     * @param ffmpegExecuteResponseHandler {@link FFmpegExecuteResponseHandler}
     * @throws FFmpegCommandAlreadyRunningException
     */
    void execute(String[] cmd, FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) throws FFmpegCommandAlreadyRunningException;

    /**
     * Tells FFmpeg version currently on device
     * @return FFmpeg version currently on device
     * @throws FFmpegCommandAlreadyRunningException
     */
    String getDeviceFFmpegVersion() throws FFmpegCommandAlreadyRunningException;

    /**
     * Tells FFmpeg version shipped with current library
     * @return FFmpeg version shipped with Library
     */
    String getLibraryFFmpegVersion();

    /**
     * Checks if FFmpeg command is Currently running
     * @return true if FFmpeg command is running
     */
    boolean isFFmpegCommandRunning();

    /**
     * Kill Running FFmpeg process
     * @return true if process is killed successfully
     */
    boolean killRunningProcesses();

    /**
     * Timeout for FFmpeg process, should be minimum of 10 seconds
     * @param timeout in milliseconds
     */
    void setTimeout(long timeout);

}
