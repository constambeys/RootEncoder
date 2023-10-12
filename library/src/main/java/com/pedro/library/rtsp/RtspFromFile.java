/*
 * Copyright (C) 2021 pedroSG94.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pedro.library.rtsp;

import android.content.Context;
import android.media.MediaCodec;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.pedro.encoder.input.decoder.AudioDecoderInterface;
import com.pedro.encoder.input.decoder.VideoDecoderInterface;
import com.pedro.encoder.utils.CodecUtil;
import com.pedro.library.base.FromFileBase;
import com.pedro.library.util.streamclient.RtspStreamClient;
import com.pedro.library.util.streamclient.StreamClientListener;
import com.pedro.library.view.LightOpenGlView;
import com.pedro.library.view.OpenGlView;
import com.pedro.rtsp.rtsp.RtspClient;
import com.pedro.rtsp.rtsp.VideoCodec;
import com.pedro.rtsp.utils.ConnectCheckerRtsp;

import java.nio.ByteBuffer;

/**
 * More documentation see:
 * {@link com.pedro.library.base.FromFileBase}
 *
 * Created by pedro on 4/06/17.
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class RtspFromFile extends FromFileBase implements StreamClientListener {

  private final RtspClient rtspClient;
  private final RtspStreamClient streamClient;

  public RtspFromFile(ConnectCheckerRtsp connectCheckerRtsp,
      VideoDecoderInterface videoDecoderInterface, AudioDecoderInterface audioDecoderInterface) {
    super(videoDecoderInterface, audioDecoderInterface);
    rtspClient = new RtspClient(connectCheckerRtsp);
    streamClient = new RtspStreamClient(rtspClient, this);
  }

  public RtspFromFile(Context context, ConnectCheckerRtsp connectCheckerRtsp,
      VideoDecoderInterface videoDecoderInterface, AudioDecoderInterface audioDecoderInterface) {
    super(context, videoDecoderInterface, audioDecoderInterface);
    rtspClient = new RtspClient(connectCheckerRtsp);
    streamClient = new RtspStreamClient(rtspClient, this);
  }

  public RtspFromFile(OpenGlView openGlView, ConnectCheckerRtsp connectCheckerRtsp,
      VideoDecoderInterface videoDecoderInterface, AudioDecoderInterface audioDecoderInterface) {
    super(openGlView, videoDecoderInterface, audioDecoderInterface);
    rtspClient = new RtspClient(connectCheckerRtsp);
    streamClient = new RtspStreamClient(rtspClient, this);
  }

  public RtspFromFile(LightOpenGlView lightOpenGlView, ConnectCheckerRtsp connectCheckerRtsp,
      VideoDecoderInterface videoDecoderInterface, AudioDecoderInterface audioDecoderInterface) {
    super(lightOpenGlView, videoDecoderInterface, audioDecoderInterface);
    rtspClient = new RtspClient(connectCheckerRtsp);
    streamClient = new RtspStreamClient(rtspClient, this);
  }

  public RtspStreamClient getStreamClient() {
    return streamClient;
  }

  public void setVideoCodec(VideoCodec videoCodec) {
    recordController.setVideoMime(
        videoCodec == VideoCodec.H265 ? CodecUtil.H265_MIME : CodecUtil.H264_MIME);
    videoEncoder.setType(videoCodec == VideoCodec.H265 ? CodecUtil.H265_MIME : CodecUtil.H264_MIME);
  }

  @Override
  protected void prepareAudioRtp(boolean isStereo, int sampleRate) {
    rtspClient.setAudioInfo(sampleRate, isStereo);
  }

  @Override
  protected void startStreamRtp(String url) {
    rtspClient.setOnlyAudio(!videoEnabled);
    rtspClient.connect(url);
  }

  @Override
  protected void stopStreamRtp() {
    rtspClient.disconnect();
  }

  @Override
  protected void onSpsPpsVpsRtp(ByteBuffer sps, ByteBuffer pps, ByteBuffer vps) {
    rtspClient.setVideoInfo(sps, pps, vps);
  }

  @Override
  protected void getH264DataRtp(ByteBuffer h264Buffer, MediaCodec.BufferInfo info) {
    rtspClient.sendVideo(h264Buffer, info);
  }

  @Override
  protected void getAacDataRtp(ByteBuffer aacBuffer, MediaCodec.BufferInfo info) {
    rtspClient.sendAudio(aacBuffer, info);
  }

  @Override
  public void onRequestKeyframe() {
    requestKeyFrame();
  }
}

