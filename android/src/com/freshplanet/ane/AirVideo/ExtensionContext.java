//////////////////////////////////////////////////////////////////////////////////////
//
//  Copyright 2012 Freshplanet (http://freshplanet.com | opensource@freshplanet.com)
//  
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//  
//    http://www.apache.org/licenses/LICENSE-2.0
//  
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//  
//////////////////////////////////////////////////////////////////////////////////////

package com.freshplanet.ane.AirVideo;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.freshplanet.ane.AirVideo.functions.BufferVideosFunction;
import com.freshplanet.ane.AirVideo.functions.HidePlayerFunction;
import com.freshplanet.ane.AirVideo.functions.LoadVideoFunction;
import com.freshplanet.ane.AirVideo.functions.PauseVideoFunction;
import com.freshplanet.ane.AirVideo.functions.PlayVideoFunction;
import com.freshplanet.ane.AirVideo.functions.SetControlStyleFunction;
import com.freshplanet.ane.AirVideo.functions.SetViewDimensionsFunction;
import com.freshplanet.ane.AirVideo.functions.ShowPlayerFunction;

public class ExtensionContext extends FREContext implements OnCompletionListener
{
	private VideoView _videoView = null;
	private ViewGroup _videoContainer = null;
	
	@Override
	public void dispose() {}

	@Override
	public Map<String, FREFunction> getFunctions()
	{
		Map<String, FREFunction> functions = new HashMap<String, FREFunction>();
		
		functions.put("showPlayer", new ShowPlayerFunction());
		functions.put("hidePlayer", new HidePlayerFunction());
		functions.put("loadVideo", new LoadVideoFunction());
		
		functions.put("bufferVideos", new BufferVideosFunction());
		functions.put("playVideo", new PlayVideoFunction());
		functions.put("setControlStyle", new SetControlStyleFunction());
		functions.put("setViewDimensions", new SetViewDimensionsFunction());
		functions.put("pauseCurrentVideo", new PauseVideoFunction());
		return functions;
	}
	
	public ViewGroup getRootContainer()
	{
		return (ViewGroup)((ViewGroup)getActivity().findViewById(android.R.id.content)).getChildAt(0);
	}
	
	private FrameLayout.LayoutParams videoLayoutParams;
	
	public ViewGroup getVideoContainer()
	{
		if (_videoContainer == null)
		{
			_videoContainer = new FrameLayout(getActivity());
			if (videoLayoutParams == null)
			{
				videoLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			}
			_videoContainer.addView(getVideoView(), videoLayoutParams);
		}
		
		return _videoContainer;
	}
	
	public VideoView getVideoView()
	{
		if (_videoView == null)
		{
			_videoView = new VideoView(getActivity());
			_videoView.setZOrderOnTop(true);
			_videoView.setMediaController(new MediaController(getActivity()));
			_videoView.setOnCompletionListener(this);
		}
		
		return _videoView;
	}
	
	private HashMap<String, InputStream> videosData = null;
	
	public void setStreamAtPosition(InputStream stream, int position)
	{
		if (videosData == null)
		{
			videosData = new HashMap<String, InputStream>();
		}
		videosData.put(Integer.toString(position), stream);
	}

	public InputStream getStreamAtPosition(int position)
	{
		if (videosData == null)
		{
			return null;
		}
		return videosData.get(Integer.toString(position));
	}
	
	public void onCompletion(MediaPlayer mp)
	{
		dispatchStatusEventAsync("PLAYBACK_DID_FINISH", "OK");
	}
	
	public void setStyle(int style)
	{
		if (style == 1)
		{
			getVideoView().setMediaController(null);
			getVideoView().setClickable(false);
		} else
		{
			getVideoView().setMediaController(new MediaController(getActivity()));
			getVideoView().setClickable(true);
		}
	}
	
	public void setViewDimensions(double x, double y, double width, double height)
	{
		videoLayoutParams = new FrameLayout.LayoutParams((int) width, (int)height);
		videoLayoutParams.leftMargin = (int) x;
		videoLayoutParams.topMargin = (int) y;
		getVideoView().setLayoutParams(videoLayoutParams);
	}
	
	public void pauseVideo()
	{
		getVideoView().stopPlayback();
	}
	
}
