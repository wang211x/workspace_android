package com.parfois.vmall;

import com.parfois.utils.PathUtils;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
public class MeFragment extends Fragment {
	private WebView me_wv;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_me, null);

		me_wv = (WebView) view.findViewById(R.id.me_wv);
		me_wv.loadUrl(PathUtils.personal);
		me_wv.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		me_wv.getSettings().setJavaScriptEnabled(true);
		
		return view;
	}
	
	
}
