package com.aimfaraway.process;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		startDaemon(this, this.getPackageName());

	}

	// 守护进程是否在运行中，不是android app进程，不能通过ActivityManager获取到进程，通过ps命令获取
	private boolean isdaemonLive() {
		String processName = "/data/data/" + getPackageName() + "/chkproc";
		Process process = null;
		DataInputStream inputStream = null;
		DataOutputStream outputStream = null;
		DataInputStream erro = null;
		try {
			process = Runtime.getRuntime().exec("sh"); // 获得shell.
			inputStream = new DataInputStream(process.getInputStream());
			outputStream = new DataOutputStream(process.getOutputStream());
			erro = new DataInputStream(process.getInputStream());

			outputStream.writeBytes("ps | grep " + getPackageName() + " \n");
			outputStream.writeBytes("exit\n");
			outputStream.flush();
			process.waitFor();
			String line = null;
			while ((line = inputStream.readLine()) != null) {
				Log.i("hhd2", "output : " + line);
				if (line != null && line.contains(processName))
					return true;
			}

		} catch (Exception e) {
			return false;
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;

	}

	public void startDaemon(Context context, String packageName) {
		if (isdaemonLive()) {
			return;
		}
		// String result = CmdExcUtil.execCmd("ps");
		// if(!result.contains("chkproc")){
		Log.d("hhd2", "*********chkproc*************");
		String path = "/data/data/" + packageName;
		String cmd1 = path + "/lib/libchkproc.so";
		String cmd2 = path + "/chkproc";
		String cmd3 = "chmod 777 " + cmd2;
		String cmd4 = "dd if=" + cmd1 + " of=" + cmd2;
		String cmd5 = path + "/chkproc -p " + packageName + " -a "
				+ packageName
				+ " -c com.aimfaraway.process.HanderActivity -i 10";
		// if(!new File(cmd2).exists()){
		execCommand(cmd4, packageName); // 拷贝lib/libtest.so到上一层目录,同时命名为test.
		execCommand(cmd3, packageName); // 改变test的属性,让其变为可执行
		// }
		execCommand(cmd5, packageName); // 执行test程序.
		// }
	}

	public static boolean execCommand(String command, String packageName) {
		Process process = null;
		DataInputStream inputStream = null;
		DataOutputStream outputStream = null;
		DataInputStream erro = null;
		try {
			process = Runtime.getRuntime().exec("sh"); // 获得shell.
			inputStream = new DataInputStream(process.getInputStream());
			outputStream = new DataOutputStream(process.getOutputStream());
			erro = new DataInputStream(process.getInputStream());

			outputStream.writeBytes("cd /data/data/" + packageName + "\n"); // 保证在command在自己的数据目录里执行,才有权限写文件到当前目录

			outputStream.writeBytes(command + " \n");
			outputStream.writeBytes("exit\n");
			outputStream.flush();
			process.waitFor();
			String line = null;
			Log.i("hhd2", command);
			while ((line = inputStream.readLine()) != null) {
				Log.i("hhd2", "output : " + line);
			}

			while ((line = erro.readLine()) != null) {
				Log.i("hhd2", "error : " + line);
			}

		} catch (Exception e) {
			return false;
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.e("demo", "service onDestroy");
	}

}
