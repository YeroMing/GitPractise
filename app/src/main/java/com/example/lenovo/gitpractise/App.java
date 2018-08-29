package com.example.lenovo.gitpractise;

import android.app.Application;
import android.widget.Toast;

/**
 * 项目:趣租部落
 *
 * @author：location time：2018/8/29 18:06
 * description：
 */

public class App extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		ToastUtils.init(this);
	}
}
